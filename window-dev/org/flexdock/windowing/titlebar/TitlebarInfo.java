/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.titlebar;

import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.Icon;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TitlebarInfo {
	public Component titlebar;
	public String text;
	public Icon icon;
	public AbstractButton[] buttons;
	public boolean inFocus;
	
	public TitlebarInfo(Component titlebar) {
		this.titlebar = titlebar;
	}
	
	public TitlebarInfo(Component titlebar, String text, Icon icon, AbstractButton[] buttons, boolean inFocus) {
		this.titlebar = titlebar;
		this.text = text;
		this.icon = icon;
		this.buttons = buttons;
		this.inFocus = inFocus;
	}
}
