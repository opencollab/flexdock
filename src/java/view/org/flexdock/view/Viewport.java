/*
 * Created on Mar 4, 2005
 */
package org.flexdock.view;

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.defaults.StandardBorderManager;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.view.tracking.ViewListener;
import org.flexdock.view.tracking.ViewTracker;

/**
 * @author Christopher Butler
 */
public class Viewport extends DefaultDockingPort {
	protected HashSet blockedRegions;
	
	static {
		DockingManager.setDockingStrategy(Viewport.class, ViewDockingStrategy.getInstance());
	}

	public Viewport() {
		this(null);
	}
	
	public Viewport(String portId) {
		super(portId);
		blockedRegions = new HashSet(5);
		setBorderManager(new StandardBorderManager());
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
		return !dockable.getDockingProperties().isTerritoryBlocked(region).booleanValue();
	}
	
	public boolean dock(Dockable dockable) {
		return dock(dockable, DockingPort.CENTER_REGION);
	}
	
	protected JTabbedPane createTabbedPane() {
		JTabbedPane pane = super.createTabbedPane();
		pane.addChangeListener(ViewListener.getInstance());
		return pane;
	}
	

    public Set getViewset() {
    	// return ALL views, recursing to maximum depth
    	return getViewset(-1, 0);
    }
    
    public Set getViewset(int depth) {
    	// return all views, including subviews up to the specified depth
    	return getViewset(depth, 0);
    }
    
    protected Set getViewset(int depth, int level) {
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
        
        // if we have a split-layout, then we need to decide whether to get the child
        // viewSets.  If 'depth' is less then zero, then it's implied we want to recurse
        // to get ALL child viewsets no matter how deep.  If 'depth' is greater than or 
        // equal to zero, we only want to go as deep as the specified depth.
        if (c instanceof JSplitPane && (depth<0 || level <= depth)) {
            JSplitPane pane = (JSplitPane) c;
            Component sub1 = pane.getLeftComponent();
            Component sub2 = pane.getRightComponent();

            if(sub1 instanceof Viewport)
            	set.addAll(((Viewport)sub1).getViewset(depth, level+1));
            
            if(sub2 instanceof Viewport)
            	set.addAll(((Viewport)sub2).getViewset(depth, level+1));
        }
       
        if(c instanceof View) {
            set.add(c);
        }
        return set;
    }



	
	public void dockingComplete(DockingEvent evt) {
		Object src = evt.getSource();
		if(!(src instanceof View) || !isShowing() || evt.getNewDockingPort()!=this)
			return;

		ViewTracker.requestViewActivation((View)src);
	}
}
