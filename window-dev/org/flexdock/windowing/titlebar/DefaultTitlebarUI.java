/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.titlebar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;

import org.flexdock.windowing.TitlebarUI;
import org.flexdock.windowing.plaf.TitlebarConstants;
import org.flexdock.windowing.plaf.UIResource;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DefaultTitlebarUI implements TitlebarUI, TitlebarConstants {
	public static final int MINIMUM_HEIGHT = 12;
	
	protected UIResource resource;
	protected int defaultHeight = -1;
	
	public void paint(Graphics g, TitlebarInfo info) {
		paintBackground(g, info);
		paintIcon(g, info);
		paintTitle(g, info);
	}
	
	protected void paintBackground(Graphics g, TitlebarInfo info) {
		int w = info.titlebar.getWidth();
		int h = info.titlebar.getHeight();

		Color c = getBackgroundColor(info.inFocus);
		g.setColor(c);
		g.fillRect(0, 0, w, h);
	}
	
	protected void paintTitle(Graphics g, TitlebarInfo info) {
		if(info.text==null)
			return;
		
		Font font = info.titlebar.getFont();
		int xMargin = getIconMargin(info.icon);
		int x = info.icon==null? xMargin: info.icon.getIconWidth() + (2*xMargin);
		int y = info.titlebar.getHeight()/2 + font.getSize()/2 - 1;
		
		Color c = getFontColor(info.inFocus);
		g.setColor(c);
		g.drawString(info.text, x, y);
	}
	
	protected void paintIcon(Graphics g, TitlebarInfo info) {
		if(info.icon==null)
			return;
		
		Icon icon = info.icon;
		int x = 2;
		int y = info.titlebar.getHeight()/2 - icon.getIconHeight()/2;
		icon.paintIcon(info.titlebar, g, x, y);
	}
	
	protected void paintButtons(Graphics g, TitlebarInfo info) {
		
	}
	
	public void layoutButtons(TitlebarInfo info) {

	}
	
	protected int getIconMargin(Icon icon) {
		return 3;
	}

	protected Color getFontColor(boolean inFocus) {
		String key = inFocus? ACTIVE_FONT_COLOR: INACTIVE_FONT_COLOR;
		return resource.getColor(key);
	}

	protected Color getBackgroundColor(boolean inFocus) {
		String key = inFocus? ACTIVE_BACKGROUND_COLOR: INACTIVE_BACKGROUND_COLOR;
		return resource.getColor(key);
	}
	
	protected Image getBackgroundImage(boolean inFocus) {
		String key = inFocus? ACTIVE_BACKGROUND_IMAGE: INACTIVE_BACKGROUND_IMAGE;
		return resource.getImage(key);
	}
	
	public UIResource getUIResource() {
		return resource; 
	}

	public void setUIResource(UIResource resource) {
		this.resource = resource;
	}

	public int getDefaultHeight() {
		if(defaultHeight==-1)
			setDefaultHeight(resource.getInt(DEFAULT_HEIGHT));
		return defaultHeight;
	}

	public void setDefaultHeight(int defaultHeight) {
		defaultHeight = Math.max(defaultHeight, MINIMUM_HEIGHT);
		this.defaultHeight = defaultHeight;
	}
}
