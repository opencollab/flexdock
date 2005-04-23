/*
 * Created on Mar 1, 2005
 */
package org.flexdock.view;

import javax.swing.Action;
import javax.swing.JButton;

import org.flexdock.plaf.PlafManager;

/**
 * @author Christopher Butler
 */
public class Button extends JButton {
	
	public Button(Action action) {
		setAction(action);
	}
	
	public void updateUI() {
		setUI(PlafManager.getUI(this));
	}
}
