/*
 * Created on Mar 4, 2005
 */
package org.flexdock.view.viewport;

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.defaults.StandardBorderManager;
import org.flexdock.docking.defaults.SubComponentProvider;
import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public class Viewport extends DefaultDockingPort {
	private SubComponentProvider defaultSubDocker;
	protected ViewportTracker tracker;
	protected boolean acceptsFocus;
	protected HashSet blockedRegions;
	
	public Viewport() {
		tracker = ViewportTracker.getInstance();
		blockedRegions = new HashSet(5);
		setAcceptsFocus(true);
		setBorderManager(new StandardBorderManager());
	}

	public boolean isAcceptsFocus() {
		return isShowing() && acceptsFocus;
	}

	public void setAcceptsFocus(boolean acceptsFocus) {
		this.acceptsFocus = acceptsFocus;
	}
	
	public void setRegionBlocked(String region, boolean b) {
		if(isValidDockingRegion(region)) {
			if(b)
				blockedRegions.add(region);
			else
				blockedRegions.remove(region);
		}
	}
	
	public boolean isDockingAllowed(String region, Component comp) {
		// if we're already blocked, then no need to interrogate 
		// the components in this dockingport
		boolean blocked = !super.isDockingAllowed(region, comp);
		if(blocked)
			return false;
		
		// check to see if the region itself has been blocked for some reason
		if(blockedRegions.contains(region))
			return false;
		
		// by default, allow docking in non-CENTER regions
		if(!DockingPort.CENTER_REGION.equals(region))
			return true;
		
		// allow docking in the CENTER if there's nothing already there, 
		// of if there's no Dockable associated with the component there
		Dockable dockable = getCenterDockable();
		if(dockable==null)
			return true;
		
		// otherwise, only allow docking in the CENTER if the dockable 
		// doesn't mind
		return !dockable.isTerritorial(null);
	}
	
	public void dock(Dockable dockable) {
		dock(dockable, DockingPort.CENTER_REGION);
	}
	
	protected SubComponentProvider getDefaultSubdocker() {
		if(defaultSubDocker==null)
			defaultSubDocker = new SubDocker();
		return defaultSubDocker;
	}
	
	public Set getViewset() {
		Component c = getDockedComponent();
	
		if(c instanceof JTabbedPane) {
			JTabbedPane tabs = (JTabbedPane)c;
			int len = tabs.getTabCount();
			HashSet set = new HashSet(len);
			for(int i=0; i<len; i++) {
				c = tabs.getComponentAt(i);
				if(c instanceof View)
					set.add(c);
			}
			return set;
		}
		
		HashSet set = new HashSet(1);
		if(c instanceof View) {
			set.add(c);
		}
		return set;
	}
	
	public void requestActivation(View view) {
		Component c = getDockedComponent();
		View targetView = c instanceof JTabbedPane? null: view;
		tracker.activate(this, targetView);
	}
	
	protected class SubDocker extends DefaultDockingPort.DefaultComponentProvider implements Cloneable { 

		public DockingPort createChildPort() {
			return new Viewport();
		}

		public JSplitPane createSplitPane(String region) {
			return new Splitter(region);
		}

		
		public double getInitialDividerLocation(JSplitPane splitPane, Component controller) {
			if(splitPane instanceof Splitter) {
				if(controller instanceof Splitter)
					controller = ((Splitter)controller).getController();
				
				Dockable dockable = DockingManager.getRegisteredDockable(controller);
				if(dockable!=null) {
					Splitter splitter = (Splitter)splitPane;
					RegionChecker rc = getRegionChecker();
					float prefSize = rc.getSiblingSize(dockable.getDockable(), splitter.region);
					return splitter.controllerInTopLeft? 1f-prefSize: prefSize;
				}
			}
			return super.getInitialDividerLocation(splitPane, controller);
		}

		public JTabbedPane createTabbedPane() {
			JTabbedPane pane = super.createTabbedPane();
			pane.addMouseListener(tracker);
			return pane;
		}
	}
	
	protected class Splitter extends JSplitPane {
		protected boolean dividerLocDetermined;
		protected String region;
		protected boolean controllerInTopLeft;
		
		protected Splitter(String region) {
			this.region = region;
			// the controlling item is in the topLeft if our new item (represented 
			// by the "region" string) is in the SOUTH or EAST.
			controllerInTopLeft = DockingPort.SOUTH_REGION.equals(region) || DockingPort.EAST_REGION.equals(region);
		}
		
		protected boolean isDividerSizeProperlyDetermined() {
			if(getDividerLocation()!=0)
				return true;
			return dividerLocDetermined;
		}
		
		protected Component getController() {
			Component c = controllerInTopLeft? getLeftComponent(): getRightComponent();
			if(c instanceof DockingPort)
				c = ((DockingPort)c).getDockedComponent();
			return c;
		}
		
		public void doLayout() {
			// if they setup the docking configuration while the application
			// was first starting up, then the dividerLocation was calculated before 
			// the container tree was visible, sized, validated, etc, so it'll be 
			// stuck at zero. in that case, redetermine the divider location now that 
			// we have valid container bounds with which to work.
			if(!isDividerSizeProperlyDetermined()) {
				// make sure this can only run once so we don't get a StackOverflow
				dividerLocDetermined = true;
				// reset
				Component controller = getController();
				resolveSplitDividerLocation(controller);
			}
			// continue the layout
			super.doLayout();
		}
	}


}
