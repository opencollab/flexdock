/*
 * Created on Mar 14, 2005
 */
package org.flexdock.docking;

import java.awt.Component;

import javax.swing.JSplitPane;

import org.flexdock.docking.drag.DragToken;

/**
 * @author Christopher Butler
 */
public interface DockingStrategy {
	public void dock(Dockable dockable, DockingPort port, String region);
	public void dock(Dockable dockable, DockingPort port, String region, DragToken token);
	public void undock(Dockable dockable);
	
	public DockingPort createDockingPort(DockingPort base);
	public JSplitPane createSplitPane(DockingPort base, String region);
	public int getInitialDividerLocation(DockingPort port, JSplitPane splitPane, Component controller);
}
