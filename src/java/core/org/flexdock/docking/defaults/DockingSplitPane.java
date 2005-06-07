/*
 * Created on Mar 24, 2005
 */
package org.flexdock.docking.defaults;

import java.awt.Component;

import javax.swing.JSplitPane;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.DockingStrategy;
import org.flexdock.util.DockingConstants;

/**
 * @author Christopher Butler
 */
public class DockingSplitPane extends JSplitPane implements DockingConstants {
	protected DockingPort dockingPort;
	protected String region;
	protected boolean dividerLocDetermined;
	protected boolean controllerInTopLeft;
	
	
	public DockingSplitPane(DockingPort port, String region) {
		this.region = region;
		this.dockingPort = port;
		// the controlling item is in the topLeft if our new item (represented
		// by the "region" string) is in the SOUTH or EAST.
		controllerInTopLeft = SOUTH_REGION.equals(region) || EAST_REGION.equals(region);
		
		// set the proper resize weight
		int weight = controllerInTopLeft? 1: 0;
		setResizeWeight(weight);
	}

	protected boolean isDividerSizeProperlyDetermined() {
		if (getDividerLocation() != 0)
			return true;
		return dividerLocDetermined;
	}

	public Component getController() {
		Component c = controllerInTopLeft ? getLeftComponent() : getRightComponent();
		if (c instanceof DockingPort)
			c = ((DockingPort) c).getDockedComponent();
		return c;
	}
	
	public String getRegion() {
		return region;
	}
	
	public boolean isElderTopLeft() {
		return controllerInTopLeft;
	}


	public void doLayout() {
		// if they setup the docking configuration while the application
		// was first starting up, then the dividerLocation was calculated before
		// the container tree was visible, sized, validated, etc, so it'll be
		// stuck at zero. in that case, redetermine the divider location now
		// that we have valid container bounds with which to work.
		if (!isDividerSizeProperlyDetermined()) {
			// make sure this can only run once so we don't get a StackOverflow
			dividerLocDetermined = true;
			Component controller = getController();
			resetDividerLocation();
		}
		// continue the layout
		super.doLayout();
	}
	
	protected void resetDividerLocation() {
		DockingStrategy strategy = DockingManager.getDockingStrategy(dockingPort);
		int loc = strategy.getInitialDividerLocation(dockingPort, this, getController());
		setDividerLocation(loc);
	}
	
	public void cleanup() {
		dockingPort = null;
	}
}
