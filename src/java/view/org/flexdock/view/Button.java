/*
 * Created on Mar 1, 2005
 */
package org.flexdock.view;

import javax.swing.Action;
import javax.swing.JToggleButton;

import org.flexdock.plaf.PlafManager;

/**
 * @author Christopher Butler
 */
public class Button extends JToggleButton {
	
	public Button(Action action) {
		setAction(action);
	}
	
	public void updateUI() {
		setUI(PlafManager.getUI(this));
	}
}
