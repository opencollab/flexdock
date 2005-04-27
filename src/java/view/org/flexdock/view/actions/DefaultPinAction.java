/*
 * Created on Apr 26, 2005
 */
package org.flexdock.view.actions;

import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public class DefaultPinAction extends ViewAction {

	public DefaultPinAction() {
		
	}
	
	public void actionPerformed(View view) {
		System.out.println("pinning : " + view);
	}
}
