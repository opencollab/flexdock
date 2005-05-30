/*
 * Created on Apr 26, 2005
 */
package org.flexdock.view.actions;

import java.awt.event.ActionEvent;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public class DefaultCloseAction extends ViewAction {

	public DefaultCloseAction() {
		
	}
	
	public void actionPerformed(View view, ActionEvent evt) {
		if(DockingManager.isDocked((Dockable)view)) {
			DockingManager.undock(view);
		} else if (view.isMinimized()) {
			DockingManager.getMinimizeManager().close(view);
		}
	}
}
