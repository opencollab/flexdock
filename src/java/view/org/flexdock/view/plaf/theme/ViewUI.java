/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.plaf.theme;

import java.awt.Graphics;

import javax.swing.JComponent;

import org.flexdock.view.plaf.FlexViewComponentUI;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewUI extends FlexViewComponentUI {
	
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

	public void initializeCreationParameters() {

	}
	
	public String getPreferredTitlebarUI() {
		return creationParameters.getString(UIFactory.TITLEBAR_KEY);
	}
}
