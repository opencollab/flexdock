/*
 * Created on Apr 28, 2005
 */
package org.flexdock.dockbar.restore;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

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
	private transient String stringForm;
	private DockingPort rootPort;
	private ArrayList nodes;
	private String dockableId;
	private String siblingId;
	private boolean tabbed;

	
	public static DockingPath create(String dockableId) {
		Dockable dockable = findDockable(dockableId);
		return create(dockable);
	}
	
	public static DockingPath create(Dockable dockable) {
		if(dockable==null || !isDocked(dockable))
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
		
		path.initialize();
		return path;
	}
	
	private static SplitNode createNode(DockingPort port, JSplitPane split) {
		int orientation = split.getOrientation();
		boolean topLeft = split.getLeftComponent()==port? true: false;
		
		int region = 0;
		String siblingId = null;
		if(topLeft) {
			region = orientation==JSplitPane.VERTICAL_SPLIT? TOP: LEFT;
			siblingId = getSiblingId(split.getRightComponent());
		}
		else {
			region = orientation==JSplitPane.VERTICAL_SPLIT? BOTTOM: RIGHT;
			siblingId = getSiblingId(split.getLeftComponent());
		}
		
		int size = orientation==JSplitPane.VERTICAL_SPLIT? split.getHeight(): split.getWidth();
		int divLoc = split.getDividerLocation();
		float percentage = (float)divLoc / (float)size;
		
		return new SplitNode(orientation, region, percentage, siblingId);
	}
	
	private static String getSiblingId(Component c) {
		if(c instanceof DockingPort)
			c = ((DockingPort)c).getDockedComponent();
		
		Dockable dockable = findDockable(c);
		return dockable==null? null: dockable.getPersistentId();
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
	
	public static boolean restore(Dockable dockable) {
		DockingPath path = getRestorePath(dockable);
		return path==null? false: path.restore();
	}
	
	
	private DockingPath(Dockable dockable) {
		dockableId = dockable.getPersistentId();
		siblingId = findSiblingId(dockable);
		tabbed = dockable.getDockable().getParent() instanceof JTabbedPane;
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
	
	private void initialize() {
		Collections.reverse(nodes);
	}
	
	private String findSiblingId(Dockable dockable) {
		Component comp = dockable.getDockable();
		JSplitPane split = comp.getParent() instanceof JSplitPane? (JSplitPane)comp.getParent(): null;
		if(split==null)
			return null;
		
		Component sibling = split.getLeftComponent();
		if(comp==sibling)
			sibling = split.getRightComponent();
		
		Dockable d = findDockable(sibling);
		return d==null? null: d.getPersistentId();
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
	
	public boolean restore() {
		Dockable dockable = findDockable(dockableId);
		if(dockable==null || isDocked(dockable))
			return false;
		
		String region = DockingPort.CENTER_REGION;
		if(nodes.size()==0) {
			return dockFullPath(dockable, rootPort, region);
		}
		
		DockingPort port = rootPort;
		for(Iterator it=nodes.iterator(); it.hasNext();) {
			SplitNode node = (SplitNode)it.next();
			Component comp = port.getDockedComponent();
			region = getRegion(node, comp);
			
			JSplitPane splitPane = comp instanceof JSplitPane? (JSplitPane)comp: null;
			// path was broken.  we have no SplitPane, or the SplitPane doesn't 
			// match the orientation of the current node, meaning the path was 
			// altered at this point.
			if(splitPane==null || splitPane.getOrientation()!=node.getOrientation()) {
				return dockBrokenPath(dockable, port, region, node);
			}
			
			// assume there is a transient sub-dockingPort in the split pane
			comp = node.getRegion()==LEFT || node.getRegion()==TOP? splitPane.getLeftComponent(): splitPane.getRightComponent();
			port = (DockingPort)comp;
			
			// move on to the next node
		}
		
		return dockFullPath(dockable, port, region);
	}
	
	
	
	private boolean dockBrokenPath(Dockable dockable, DockingPort port, String region, SplitNode ctrlNode) {
		Component current = port.getDockedComponent();
		if(current instanceof JSplitPane) {
			return dockExtendedPath(dockable, port, region, ctrlNode);
		}
		
		if(current instanceof JTabbedPane) {
			return dock(dockable, port, DockingPort.CENTER_REGION, null);
		}
		
		Dockable embedded = findDockable(current);
		if(embedded==null || tabbed) {
			return dock(dockable, port, DockingPort.CENTER_REGION, null);
		}
		
		String embedId = embedded.getPersistentId();
		SplitNode lastNode = getLastNode();
		if(embedId.equals(lastNode.getSiblingId())) {
			region = getRegion(lastNode, current);
			ctrlNode = lastNode;
		}
		
		return dock(dockable, port, region, ctrlNode);
	}
	
	private boolean dockFullPath(Dockable dockable, DockingPort port, String region) {
		// the docking layout was altered since the last time our dockable we embedded within
		// it, and we were able to fill out the full docking path.  this means there is already
		// something within the target dockingPort where we expect to dock our dockable.  
		
		// first, check to see if we need to use a tabbed layout
		Component current = port.getDockedComponent();
		if(current instanceof JTabbedPane) {
			return dock(dockable, port, DockingPort.CENTER_REGION, null);
		}

		// check to see if we dock outside the current port or outside of it
		Dockable docked = findDockable(current);
		if(docked!=null) {
			Component comp = dockable.getDockable();
			if(port.isDockingAllowed(DockingPort.CENTER_REGION, comp)) {
				return dock(dockable, port, DockingPort.CENTER_REGION, null);
			}
			DockingPort superPort = (DockingPort)SwingUtilities.getAncestorOfClass(DockingPort.class, (Component)port);
			if(superPort!=null)
				port = superPort;
			return dock(dockable, port, region, getLastNode());
		}
		
		// if we were't able to dock above, then the path changes means our current path
		// does not extend all the way down into to docking layout.  try to determine 
		// an extended path and dock into it
		return dockExtendedPath(dockable, port, region, getLastNode());
	}
	
	private boolean dockExtendedPath(Dockable dockable, DockingPort port, String region, SplitNode ctrlNode) {
		Component docked = port.getDockedComponent();
		
		// if 'docked' is not a split pane, then I don't know what it is.  let's print a
		// stacktrace and see who sends in an error report.
		if(!(docked instanceof JSplitPane)) {
			new Throwable().printStackTrace();
			return false;
		}
		
		
		SplitNode lastNode = getLastNode();
		String lastSibling = lastNode==null? null: lastNode.getSiblingId();
		
		Set dockables = port.getDockables();
		for(Iterator it=dockables.iterator(); lastSibling!=null && it.hasNext();) {
			Dockable d = (Dockable)it.next();
			if(d.getPersistentId().equals(lastSibling)) {
				DockingPort embedPort = d.getDockingPort();
				String embedRegion = getRegion(lastNode, d.getDockable());
				return dock(dockable, embedPort, embedRegion, ctrlNode);
			}
		}
		
		
		return dock(dockable, port, region, ctrlNode);
	}
	
	

	
	
	
	
	private String getRegion(SplitNode node, Component dockedComponent) {
		if(dockedComponent==null)
			return DockingPort.CENTER_REGION;
		return DockingUtility.getRegion(node.getRegion());
	}
	
	private SplitNode getLastNode() {
		return nodes.size()==0? null: (SplitNode)nodes.get(nodes.size()-1);
	}
	
	private boolean dock(Dockable dockable, DockingPort port, String region, SplitNode ctrlNode) {
		boolean ret = DockingManager.dock(dockable, port, region);
		if(tabbed || ctrlNode==null)
			return ret;
		
		final float percent = ctrlNode.getPercentage();
		final Component docked = dockable.getDockable();
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						resizeSplitPane(docked, percent);
					}
				};
				EventQueue.invokeLater(r);
			}
		};
		t.start();
		return ret;
	}
	
	private void resizeSplitPane(Component comp, float percentage) {
		Container parent = comp.getParent();
		Container grandParent = parent==null? null: parent.getParent();
		if(!(grandParent instanceof JSplitPane))
			return;
		
		JSplitPane split = (JSplitPane)grandParent;
//		int splitSize = split.getOrientation()==DockingConstants.VERTICAL? split.getHeight(): split.getWidth();
//		int divLoc = (int)(percentage * (float)splitSize);
		split.setDividerLocation(percentage);
	}
	
	private static Dockable findDockable(Component c) {
		return DockingManager.getRegisteredDockable(c);
	}
	
	private static Dockable findDockable(String id) {
		return DockingManager.getRegisteredDockable(id);
	}
	
	private static boolean isDocked(Dockable dockable) {
		return DockingManager.isDocked(dockable);
	}
}
