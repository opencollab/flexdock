/*
 * Created on Apr 26, 2005
 */
package org.flexdock.view.actions;

import java.awt.event.ActionEvent;

import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public class DefaultPinAction extends ViewAction {

	public DefaultPinAction() {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		View view = getView(e);
		
		System.out.println("pinning : " + view);
	}
}
