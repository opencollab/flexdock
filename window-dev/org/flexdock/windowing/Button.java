/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing;

import javax.swing.JButton;

import org.flexdock.windowing.plaf.PlafManager;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Button extends JButton {
	public void updateUI() {
		setUI(PlafManager.getUI(this));
	}
}
