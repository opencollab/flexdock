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
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultRegionChecker;
import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.MinimizationManager;

/**
 * @author Christopher Butler
 */
public class DockingUtility implements DockingConstants {
	
	public static DockingPort getParentDockingPort(Dockable d) {
		return d==null? null: getParentDockingPort(d.getDockable());
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
			if(DockingPort.NORTH_REGION.equals(region))
				region = DockingPort.WEST_REGION;
			else if(DockingPort.SOUTH_REGION.equals(region))
				region = DockingPort.EAST_REGION;
		}
		else {
			if(DockingPort.WEST_REGION.equals(region))
				region = DockingPort.NORTH_REGION;
			else if(DockingPort.EAST_REGION.equals(region))
				region = DockingPort.SOUTH_REGION;
		}
		return region;
	}
	
	public static String flipRegion(String region) {
		if(!DockingManager.isValidDockingRegion(region) || DockingPort.CENTER_REGION.equals(region))
			return DockingPort.CENTER_REGION;
		
		if(DockingPort.NORTH_REGION.equals(region))
			return DockingPort.SOUTH_REGION;
		
		if(DockingPort.SOUTH_REGION.equals(region))
			return DockingPort.NORTH_REGION;
		
		if(DockingPort.EAST_REGION.equals(region))
			return DockingPort.WEST_REGION;
		
		return DockingPort.EAST_REGION;
	}

	
	public static boolean isAxisEquivalent(String region, String otherRegion) {
		if(!DockingManager.isValidDockingRegion(region) || !DockingManager.isValidDockingRegion(otherRegion))
			return false;
		
		if(region.equals(otherRegion))
			return true;
		
		if(DockingPort.CENTER_REGION.equals(region))
			return false;
		
		if(DockingPort.NORTH_REGION.equals(region))
			return DockingPort.WEST_REGION.equals(otherRegion);
		if(DockingPort.SOUTH_REGION.equals(region))
			return DockingPort.EAST_REGION.equals(otherRegion);
		if(DockingPort.EAST_REGION.equals(region))
			return DockingPort.SOUTH_REGION.equals(otherRegion);
		if(DockingPort.WEST_REGION.equals(region))
			return DockingPort.NORTH_REGION.equals(otherRegion);
		
		return false;
	}
	
	public static String getRegion(int regionType) {
		switch(regionType) {
			case LEFT:
				return DockingPort.WEST_REGION;
			case RIGHT:
				return DockingPort.EAST_REGION;
			case TOP:
				return DockingPort.NORTH_REGION;
			case BOTTOM:
				return DockingPort.SOUTH_REGION;
			case CENTER:
				return DockingPort.CENTER_REGION;
			default:
				return DockingPort.UNKNOWN_REGION;
		}
	}
	
	public static boolean isMinimized(Dockable dockable) {
		if(dockable==null)
			return false;
		
		DockingState info = getDockingState(dockable);
		return info==null? false: info.isMinimized();
	}
	
	public static int getMinimizedEdge(Dockable dockable) {
		int defaultEdge = MinimizationManager.UNSPECIFIED_LAYOUT_EDGE;
		DockingState info = getDockingState(dockable);
		return info==null? defaultEdge: info.getDockbarEdge();
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
		if(size==UNSPECIFIED_SIBLING_PREF || DockingPort.CENTER_REGION.equals(region) || !DockingManager.isValidDockingRegion(region))
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
		int size = split.getOrientation()==JSplitPane.HORIZONTAL_SPLIT? split.getWidth(): split.getHeight(); 
		
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
		
		Component comp = dockable.getDockable();
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
	
}
