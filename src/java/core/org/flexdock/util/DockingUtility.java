/*
 * Created on Mar 14, 2005
 */
package org.flexdock.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultRegionChecker;
import org.flexdock.docking.props.DockableProps;
import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.MinimizationManager;

/**
 * @author Christopher Butler
 */
public class DockingUtility implements DockingConstants {
	
	public static DockingPort getParentDockingPort(Dockable d) {
		return d==null? null: getParentDockingPort(d.getComponent());
	}

	public static DockingPort getParentDockingPort(Component comp) {
		DockingPort port = comp==null? null: (DockingPort)SwingUtilities.getAncestorOfClass(DockingPort.class, comp);
		if(port==null)
			return null;
			
		return port.isParentDockingPort(comp)? port: null;
	}
	
	public static boolean isSubport(DockingPort dockingPort) {
		return dockingPort==null? false: SwingUtilities.getAncestorOfClass(DockingPort.class, (Component)dockingPort)!=null;
	}
	
	public static DockingPort findDockingPort(Container c, Point p) {
		if(c==null || p==null)
			return null;
		
		Component deepestComponent = SwingUtilities.getDeepestComponentAt(c, p.x, p.y);
		if (deepestComponent == null)
			return null;
	
		// we're assured here that the deepest component is both a Component and DockingPort in
		// this case, so we're okay to return here.
		if (deepestComponent instanceof DockingPort)
			return (DockingPort) deepestComponent;
	
		// getAncestorOfClass() will either return a null or a Container that is also an instance of
		// DockingPort.  Since Container is a subclass of Component, we're fine in returning both
		// cases.
		return (DockingPort) SwingUtilities.getAncestorOfClass(DockingPort.class, deepestComponent);
	}
	
	public static String translateRegion(JSplitPane splitPane, String region) {
		if(splitPane==null || !DockingManager.isValidDockingRegion(region))
			return null;
		
		boolean horizontal = splitPane.getOrientation()==JSplitPane.HORIZONTAL_SPLIT;
		if(horizontal) {
			if(NORTH_REGION.equals(region))
				region = WEST_REGION;
			else if(SOUTH_REGION.equals(region))
				region = EAST_REGION;
		}
		else {
			if(WEST_REGION.equals(region))
				region = NORTH_REGION;
			else if(EAST_REGION.equals(region))
				region = SOUTH_REGION;
		}
		return region;
	}
	
	public static String flipRegion(String region) {
		if(!DockingManager.isValidDockingRegion(region) || CENTER_REGION.equals(region))
			return CENTER_REGION;
		
		if(NORTH_REGION.equals(region))
			return SOUTH_REGION;
		
		if(SOUTH_REGION.equals(region))
			return NORTH_REGION;
		
		if(EAST_REGION.equals(region))
			return WEST_REGION;
		
		return EAST_REGION;
	}

	
	public static boolean isAxisEquivalent(String region, String otherRegion) {
		if(!DockingManager.isValidDockingRegion(region) || !DockingManager.isValidDockingRegion(otherRegion))
			return false;
		
		if(region.equals(otherRegion))
			return true;
		
		if(CENTER_REGION.equals(region))
			return false;
		
		if(NORTH_REGION.equals(region))
			return WEST_REGION.equals(otherRegion);
		if(SOUTH_REGION.equals(region))
			return EAST_REGION.equals(otherRegion);
		if(EAST_REGION.equals(region))
			return SOUTH_REGION.equals(otherRegion);
		if(WEST_REGION.equals(region))
			return NORTH_REGION.equals(otherRegion);
		
		return false;
	}
	
	public static boolean isRegionTopLeft(String region) {
		return NORTH_REGION.equals(region) || WEST_REGION.equals(region);
	}
	
	public static String getRegion(int regionType) {
		switch(regionType) {
			case LEFT:
				return WEST_REGION;
			case RIGHT:
				return EAST_REGION;
			case TOP:
				return NORTH_REGION;
			case BOTTOM:
				return SOUTH_REGION;
			case CENTER:
				return CENTER_REGION;
			default:
				return UNKNOWN_REGION;
		}
	}
	
	public static boolean isMinimized(Dockable dockable) {
		if(dockable==null)
			return false;
		
		DockingState info = getDockingState(dockable);
		return info==null? false: info.isMinimized();
	}
	
	public static int getMinimizedEdge(Dockable dockable) {
		int defaultEdge = MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT;
		DockingState info = getDockingState(dockable);
		return info==null? defaultEdge: info.getMinimizedConstraint();
	}
	
	private static DockingState getDockingState(Dockable dockable) {
		return DockingManager.getLayoutManager().getDockingState(dockable);
	}
	

	public static boolean dockRelative(Dockable parent, Dockable sibling, String relativeRegion) {
		return dockRelative(parent, sibling, relativeRegion, UNSPECIFIED_SIBLING_PREF);
	}
	
	public static boolean dockRelative(Dockable parent, Dockable sibling, String relativeRegion, float ratio) {
		if(parent==null)
			throw new IllegalArgumentException("'parent' cannot be null");
		if(sibling==null)
			throw new IllegalArgumentException("'sibling' cannot be null");
		
		if(!DockingManager.isValidDockingRegion(relativeRegion))
			throw new IllegalArgumentException("'" + relativeRegion + "' is not a valid docking region.");

		// set the sibling preference
		setSiblingPreference(parent, relativeRegion, ratio);
		
		DockingPort port = parent.getDockingPort();
		if(port!=null)
			return DockingManager.dock(sibling, port, relativeRegion);

		return false;
	}
	
	private static void setSiblingPreference(Dockable src, String region, float size) {
		if(size==UNSPECIFIED_SIBLING_PREF || CENTER_REGION.equals(region) || !DockingManager.isValidDockingRegion(region))
			return;
		
		size = DefaultRegionChecker.validateSiblingSize(size);
		src.getDockingProperties().setSiblingSize(region, size);
	}
	
	public static boolean isFloating(Dockable dockable) {
		DockingState info = getDockingState(dockable);
		return info==null? false: info.isFloating();
	}
	
	public static boolean isEmbedded(Dockable dockable) {
		return DockingManager.isDocked(dockable) && !isFloating(dockable);
	}
	
	public static void resizeSplitPane(final JSplitPane split, float dividerProportion) {
		if(split==null)
			return;
		
		dividerProportion = Math.max(0f, dividerProportion);
		final float percent = Math.min(1f, dividerProportion);
		int size = SwingUtility.getSplitPaneSize(split); 
		
		if(split.isVisible() && size>0 && EventQueue.isDispatchThread()) {
			split.setDividerLocation(dividerProportion);
			split.validate();
			return;
		}
		
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						resizeSplitPane(split, percent);
					}
				};
				EventQueue.invokeLater(r);
			}
		};
		t.start();
			
	}

	public static void setSplitProportion(DockingPort port, float proportion) {
		if(port==null)
			return;
		
		Component comp = port.getDockedComponent();
		if(comp instanceof JSplitPane)
			resizeSplitPane((JSplitPane)comp, proportion);
	}
	
	public static void setSplitProportion(Dockable dockable, float proportion) {
		if(dockable==null)
			return;
		
		Component comp = dockable.getComponent();
		Container parent = comp.getParent();
		if(parent instanceof JTabbedPane) {
			parent = parent.getParent();
		}
		if(!(parent instanceof DockingPort))
			return;
		
		Container grandParent = parent.getParent();
		if(grandParent instanceof JSplitPane)
			resizeSplitPane((JSplitPane)grandParent, proportion);
	}
	
	public static String getTabText(Dockable dockable) {
		DockableProps props = dockable==null? null: dockable.getDockingProperties();
		return props==null? null: props.getDockableDesc();
	}
	
	public static boolean isDockable(Object obj) {
		if(obj==null)
			return false;
		
		if(obj instanceof Dockable)
			return true;
		
		if(obj instanceof Component) {
			Component comp = (Component)obj;
			return SwingUtility.getClientProperty(comp, Dockable.DOCKABLE_INDICATOR)==Boolean.TRUE;
		}
		return false;
	}
}
