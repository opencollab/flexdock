/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view;

import javax.swing.Action;
import javax.swing.JButton;

import org.flexdock.view.plaf.PlafManager;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Button extends JButton {
	
	public Button(Action action) {
		setAction(action);
	}
	
	public void updateUI() {
		setUI(PlafManager.getUI(this));
	}
}
