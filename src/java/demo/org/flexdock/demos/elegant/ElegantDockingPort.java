package org.flexdock.demos.elegant;

import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.defaults.StandardBorderManager;


public class ElegantDockingPort extends DefaultDockingPort {
	public ElegantDockingPort() {
		this(null);
	}
	
	public ElegantDockingPort(String id) {
		super(id);
		setComponentProvider(new ChildComponentDelegate());
		setBorderManager(new StandardBorderManager(new ShadowBorder()));
	}
	
	public void add(ElegantPanel view) {
		dock(view.getDockable(), CENTER_REGION); 
	}
}
