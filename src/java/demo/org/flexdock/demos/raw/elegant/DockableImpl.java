package org.flexdock.demos.raw.elegant;

import java.awt.Component;

import javax.swing.JComponent;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DockableAdapter;

public class DockableImpl extends DockableAdapter {
	private ElegantPanel panel;
	private JComponent dragInitiator;
	
	public DockableImpl(ElegantPanel dockable, JComponent dragInit, String id) {
		super(id);
		if(dockable==null)
			new IllegalArgumentException(
			"Cannot create DockableImpl with a null DockablePanel.");
		if(dragInit==null)
			new IllegalArgumentException(
			"Cannot create DockableImpl with a null drag initiator.");
			
		panel = dockable;
		dragInitiator = dragInit;
		setTabText(panel.getTitle());
		getDragSources().add(dragInit);
		getFrameDragSources().add(dockable.getTitlebar());
		DockingManager.registerDockable(this);
	}

	public Component getDockable() {
		return panel;
	}
}
