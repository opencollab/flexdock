/*
 * Created on Apr 26, 2005
 */
package org.flexdock.view.actions;

import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public class DefaultCloseAction extends ViewAction {

	public DefaultCloseAction() {
		
	}
	
	public void actionPerformed(View view) {
		System.out.println("closing: " + view);
	}
}
