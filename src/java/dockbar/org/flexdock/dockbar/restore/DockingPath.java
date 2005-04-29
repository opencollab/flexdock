/*
 * Created on Apr 28, 2005
 */
package org.flexdock.dockbar.restore;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JSplitPane;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.util.DockingConstants;
import org.flexdock.util.DockingUtility;

/**
 * @author Christopher Butler
 */
public class DockingPath implements DockingConstants {
	public static final String RESTORE_PATH_KEY = "DockingPath.RESTORE_PATH_KEY"; 
	
	private ArrayList nodes;
	private String dockableId;
	private DockingPort rootPort;
	private transient String stringForm;
	
	public static DockingPath create(String dockableId) {
		Dockable dockable = DockingManager.getRegisteredDockable(dockableId);
		return create(dockable);
	}
	
	public static DockingPath create(Dockable dockable) {
		if(dockable==null || !DockingManager.isDocked(dockable))
			return null;
		
		DockingPath path = new DockingPath(dockable);
		Component comp = dockable.getDockable();

		Container parent = comp.getParent();
		while(!isDockingRoot(parent)) {
			if(parent instanceof DockingPort) {
				DockingPort port = (DockingPort)parent;
				JSplitPane split = (JSplitPane)parent.getParent();
				SplitNode node = createNode(port, split);
				path.addNode(node);
			}
			parent = parent.getParent();
		}
		if(isDockingRoot(parent))
			path.setRootPort((DockingPort)parent);
		
		path.reverse();
		return path;
	}
	
	private static SplitNode createNode(DockingPort port, JSplitPane split) {
		int orientation = split.getOrientation();
		boolean topLeft = split.getLeftComponent()==port? true: false;
		
		int region = 0;
		if(topLeft) {
			region = orientation==JSplitPane.VERTICAL_SPLIT? TOP: LEFT;
		}
		else {
			region = orientation==JSplitPane.VERTICAL_SPLIT? BOTTOM: RIGHT;
		}
		
		int size = orientation==JSplitPane.VERTICAL_SPLIT? split.getHeight(): split.getWidth();
		int divLoc = split.getDividerLocation();
		float percentage = (float)divLoc / (float)size;
		
		return new SplitNode(orientation, region, percentage);
	}

	private static boolean isDockingRoot(Container c) {
		return c instanceof DockingPort && !((DockingPort)c).isTransient();
	}

	public static DockingPath getRestorePath(Dockable dockable) {
		Object obj = dockable==null? null: dockable.getClientProperty(RESTORE_PATH_KEY);
		return obj instanceof DockingPath? (DockingPath)obj: null;
	}
	
	public static void setRestorePath(Dockable dockable, DockingPath restorePath) {
		if(dockable==null || restorePath==null)
			return;
		dockable.putClientProperty(RESTORE_PATH_KEY, restorePath);
	}
	
	public static void setRestorePath(Dockable dockable) {
		DockingPath path  = create(dockable);
		setRestorePath(dockable, path);
	}	
	
	public static void restore(Dockable dockable) {
		DockingPath path = getRestorePath(dockable);
		if(path!=null)
			path.restore();
	}
	
	
	private DockingPath(Dockable dockable) {
		dockableId = dockable.getPersistentId();
		nodes = new ArrayList();
	}

	public String getDockableId() {
		return dockableId;
	}

	public List getNodes() {
		return nodes;
	}

	public DockingPort getRootPort() {
		return rootPort;
	}
	
	private void setRootPort(DockingPort rootPort) {
		this.rootPort = rootPort;
	}
	
	private void addNode(SplitNode node) {
		nodes.add(node);
	}
	
	private void reverse() {
		Collections.reverse(nodes);
	}
	
	public String toString() {
		if(stringForm==null) {
			StringBuffer sb = new StringBuffer("/RootPort[id=").append(rootPort.getPersistentId()).append("]");
			for(Iterator it=nodes.iterator(); it.hasNext();) {
				SplitNode node = (SplitNode)it.next();
				sb.append("/").append(node.toString());
			}
			sb.append("/Dockable[id=").append(dockableId).append("]");
			stringForm = sb.toString();
		}
		return stringForm;
	}
	
	public void restore() {
		Dockable dockable = DockingManager.getRegisteredDockable(dockableId);
		if(dockable==null || DockingManager.isDocked(dockable))
			return;
		
		String region = DockingPort.CENTER_REGION;
		if(nodes.size()==0) {
			DockingManager.dock(dockable, rootPort, region);
			return;
		}
		
		DockingPort port = rootPort;
		for(Iterator it=nodes.iterator(); it.hasNext();) {
			SplitNode node = (SplitNode)it.next();
			Component comp = port.getDockedComponent();
			region = getRegion(node, comp);
			
			// path was broken.  we don't have a JSplitPane to go with
			// the current node.
			if(!(comp instanceof JSplitPane)) {
				DockingManager.dock(dockable, port, region);
				return;
			}
			
			// path was broken.  SplitPane doesn't match the orientation
			// of the current node, meaning the path was altered at this point.
			JSplitPane splitPane = (JSplitPane)comp;
			if(splitPane.getOrientation()!=node.getOrientation()) {
				DockingManager.dock(dockable, port, region);
				return;				
			}
			
			// assume there is a transient sub-dockingPort in the split pane
			comp = node.getRegion()==LEFT || node.getRegion()==TOP? splitPane.getLeftComponent(): splitPane.getRightComponent();
			port = (DockingPort)comp;
			
			// move on to the next node
		}
		
		DockingManager.dock(dockable, port, region);
	}
	
	private String getRegion(SplitNode node, Component dockedComponent) {
		if(dockedComponent==null)
			return DockingPort.CENTER_REGION;
		return DockingUtility.getRegion(node.getRegion());
	}
}
