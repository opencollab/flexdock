/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.theme;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;

import org.flexdock.windowing.Titlebar;
import org.flexdock.windowing.plaf.FlexViewComponentUI;
import org.flexdock.windowing.plaf.icons.IconMap;
import org.flexdock.windowing.plaf.icons.IconResource;
import org.flexdock.windowing.plaf.icons.IconResourceFactory;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TitlebarUI extends FlexViewComponentUI {
	public static final String DEFAULT_HEIGHT = "default.height";
	public static final String FONT_COLOR = "font.color";
	public static final String FONT_COLOR_ACTIVE = "font.color.active";
	public static final String BACKGROUND_COLOR = "bgcolor";
	public static final String BACKGROUND_COLOR_ACTIVE = "bgcolor.active";
	public static final int MINIMUM_HEIGHT = 12;
	
	protected Color activeFont;
	protected Color inactiveFont;
	protected Color activeBackground;
	protected Color inactiveBackground;
	protected int defaultHeight = MINIMUM_HEIGHT;
	protected IconMap defaultIcons;
	
	public void installUI(JComponent c) {
		// TODO Auto-generated method stub
		super.installUI(c);
	}

	public void uninstallUI(JComponent c) {
		// TODO Auto-generated method stub
		super.uninstallUI(c);
	}

	
	public void paint(Graphics g, Component c) {
		Titlebar titlebar = (Titlebar)c;
		paintBackground(g, titlebar);
		paintIcon(g, titlebar);
		paintTitle(g, titlebar);
	}
	
	protected void paintBackground(Graphics g, Titlebar titlebar) {
		int w = titlebar.getWidth();
		int h = titlebar.getHeight();

		Color c = getBackgroundColor(titlebar.isActive());
		g.setColor(c);
		g.fillRect(0, 0, w, h);
	}
	
	protected void paintTitle(Graphics g, Titlebar titlebar) {
		if(titlebar.getText()==null)
			return;
		
		Font font = titlebar.getFont();
		Icon icon = titlebar.getIcon();
		
		int xMargin = getIconMargin(icon);
		int x = icon==null? xMargin: icon.getIconWidth() + (2*xMargin);
		int y = titlebar.getHeight()/2 + font.getSize()/2 - 1;
		
		Color c = getFontColor(titlebar.isActive());
		g.setColor(c);
		g.drawString(titlebar.getText(), x, y);
	}
	
	protected void paintIcon(Graphics g, Titlebar titlebar) {
		if(titlebar.getIcon()==null)
			return;
		
		Icon icon = titlebar.getIcon();
		int x = 2;
		int y = titlebar.getHeight()/2 - icon.getIconHeight()/2;
		icon.paintIcon(titlebar, g, x, y);
	}
	
	public void layoutButtons(Titlebar titlebar) {

	}
	
	public void configureAction(Action action) {
		if(action==null)
			return;
		
		String name = (String)action.getValue(Action.NAME);
		IconMap iconMap = IconResourceFactory.getIconMap(name);
		if(iconMap!=null)
			action.putValue(ICON_RESOURCE, iconMap);
	}
	
	protected int getIconMargin(Icon icon) {
		return 3;
	}

	protected Color getFontColor(boolean active) {
		return active? activeFont: inactiveFont;
	}

	protected Color getBackgroundColor(boolean active) {
		return active? activeBackground: inactiveBackground;
	}

	public int getDefaultHeight() {
		return defaultHeight;
	}

	public void setDefaultHeight(int defaultHeight) {
		defaultHeight = Math.max(defaultHeight, MINIMUM_HEIGHT);
		this.defaultHeight = defaultHeight;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(10, getDefaultHeight());
	}

	/**
	 * @return Returns the activeBackground.
	 */
	public Color getActiveBackground() {
		return activeBackground;
	}
	/**
	 * @param activeBackground The activeBackground to set.
	 */
	public void setActiveBackground(Color activeBackground) {
		this.activeBackground = activeBackground;
	}
	/**
	 * @return Returns the activeFont.
	 */
	public Color getActiveFont() {
		return activeFont;
	}
	/**
	 * @param activeFont The activeFont to set.
	 */
	public void setActiveFont(Color activeFont) {
		this.activeFont = activeFont;
	}
	/**
	 * @return Returns the inactiveBackground.
	 */
	public Color getInactiveBackground() {
		return inactiveBackground;
	}
	/**
	 * @param inactiveBackground The inactiveBackground to set.
	 */
	public void setInactiveBackground(Color inactiveBackground) {
		this.inactiveBackground = inactiveBackground;
	}
	/**
	 * @return Returns the inactiveFont.
	 */
	public Color getInactiveFont() {
		return inactiveFont;
	}
	/**
	 * @param inactiveFont The inactiveFont to set.
	 */
	public void setInactiveFont(Color inactiveFont) {
		this.inactiveFont = inactiveFont;
	}



	public IconMap getDefaultIcons() {
		return defaultIcons;
	}

	public void setDefaultIcons(IconMap defaultIcons) {
		this.defaultIcons = defaultIcons;
	}

	public IconResource getIcons(Action action) {
		String key = action==null? null: (String)action.getValue(Action.NAME);
		return getIcons(key);
	}
	
	public IconResource getIcons(String key) {
		return defaultIcons==null? null: defaultIcons.getIcons(key);
	}

	public void initializeCreationParameters() {
		setDefaultHeight(creationParameters.getInt(DEFAULT_HEIGHT));
		setActiveBackground(creationParameters.getColor(BACKGROUND_COLOR_ACTIVE));
		setActiveFont(creationParameters.getColor(FONT_COLOR_ACTIVE));
		setInactiveBackground(creationParameters.getColor(BACKGROUND_COLOR));
		setInactiveFont(creationParameters.getColor(FONT_COLOR));
	}
	
	public String getPreferredButtonUI() {
		return creationParameters.getString(UIFactory.BUTTON_KEY);
	}
}
