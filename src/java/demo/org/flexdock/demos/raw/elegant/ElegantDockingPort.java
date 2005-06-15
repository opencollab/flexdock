package org.flexdock.demos.raw.elegant;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.defaults.StandardBorderManager;


public class ElegantDockingPort extends DefaultDockingPort implements DockingConstants {
	public ElegantDockingPort() {
		this(null);
	}
	
	public ElegantDockingPort(String id) {
		super(id);
		setBorderManager(new StandardBorderManager(new ShadowBorder()));
	}
	
	public void add(ElegantPanel view) {
		dock(view.getDockable(), CENTER_REGION); 
	}
}
