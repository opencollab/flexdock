/*
 * Created on Feb 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Titlebar {
	public void addAction(Action action);
	public Action getAction(String key);
	public void removeAction(Action action);
	public void removeAction(String key);
	public Action[] getActions();
	public void paint(Graphics g);
	public void setText(String text);
	public String getText();
	public void setIcon(Icon icon);
	public Icon getIcon();
	public int getPreferredHeight();
	public void setFocused(boolean b);
	public boolean isFocused();
	public void setUI(TitlebarUI ui);
	public TitlebarUI getUI();
}
