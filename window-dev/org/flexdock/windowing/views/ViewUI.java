/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.views;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewUI extends ComponentUI {
	protected String preferredTitlebarUI;
	
	public void installUI(JComponent c) {
		// TODO Auto-generated method stub
		super.installUI(c);
	}

	public void paint(Graphics g, JComponent c) {
		// TODO Auto-generated method stub
		super.paint(g, c);
	}

	public void uninstallUI(JComponent c) {
		// TODO Auto-generated method stub
		super.uninstallUI(c);
	}

	
	public String getPreferredTitlebarUI() {
		return preferredTitlebarUI;
	}

	public void setPreferredTitlebarUI(String preferredTitlebarUI) {
		this.preferredTitlebarUI = preferredTitlebarUI;
	}
}
