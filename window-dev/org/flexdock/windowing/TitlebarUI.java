/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing;

import java.awt.Graphics;

import org.flexdock.windowing.plaf.UIResource;
import org.flexdock.windowing.titlebar.TitlebarInfo;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface TitlebarUI {
	public void setUIResource(UIResource resource);
	public UIResource getUIResource();
	public void layoutButtons(TitlebarInfo info);
	public void paint(Graphics g, TitlebarInfo info);
	public int getDefaultHeight();
}
