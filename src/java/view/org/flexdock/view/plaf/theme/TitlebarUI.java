/*
 * Created on Feb 27, 2005
 */
package org.flexdock.view.plaf.theme;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;

import org.flexdock.view.Button;
import org.flexdock.view.Titlebar;
import org.flexdock.view.ext.Painter;
import org.flexdock.view.plaf.FlexViewComponentUI;
import org.flexdock.view.plaf.icons.IconMap;
import org.flexdock.view.plaf.icons.IconResource;
import org.flexdock.view.plaf.icons.IconResourceFactory;

/**
 * @author Christopher Butler
 */
public class TitlebarUI extends FlexViewComponentUI {
	public static final String DEFAULT_HEIGHT = "default.height";
	public static final String FONT = "font";
	public static final String FONT_COLOR = "font.color";
	public static final String FONT_COLOR_ACTIVE = "font.color.active";
	public static final String BACKGROUND_COLOR = "bgcolor";
	public static final String BACKGROUND_COLOR_ACTIVE = "bgcolor.active";
    public static final String PAINTER = "painter";
	public static final int MINIMUM_HEIGHT = 12;
	
	
	
	protected Font font;
	protected Color activeFont;
	protected Color inactiveFont;
	protected Color activeBackground;
	protected Color inactiveBackground;
	protected int defaultHeight = MINIMUM_HEIGHT;
	protected IconMap defaultIcons;
	protected Painter painter;
	
	public void installUI(JComponent c) {
		super.installUI(c);
		Dimension d = c.getPreferredSize();
		d.height = getDefaultHeight();
		c.setPreferredSize(d);
		
		if(font!=null)
			c.setFont(font);
	}

	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
	}

	
	public void paint(Graphics g, JComponent jc) {
		Titlebar titlebar = (Titlebar)jc;
		paintBackground(g, titlebar);
		paintIcon(g, titlebar);
		paintTitle(g, titlebar);
	}
	
	protected void paintBackground(Graphics g, Titlebar titlebar) {
        painter.paint( g, titlebar.isActive(), titlebar);
	}
	
	protected void paintTitle(Graphics g, Titlebar titlebar) {
		if(titlebar.getText()==null)
			return;
		
		Font font = titlebar.getFont();
		Rectangle iconRect = getIconRect(titlebar);
		
		int x = getTextLocation(iconRect);
		int y = titlebar.getHeight()/2 + font.getSize()/2 - 1;
		
		Color c = getFontColor(titlebar.isActive());
		g.setColor(c);
		g.drawString(titlebar.getText(), x, y);
	}
	
	protected int getTextLocation(Rectangle iconRect) {
		if(iconRect.width>0)
			return iconRect.x + iconRect.width + getRightIconMargin();
		return 5;
	}
	
	protected void paintIcon(Graphics g, Titlebar titlebar) {
		if(titlebar.getIcon()==null)
			return;
		
		Icon icon = titlebar.getIcon();
		Rectangle r = getIconRect(titlebar);
		icon.paintIcon(titlebar, g, r.x, r.y);
	}
	
	protected Rectangle getIconRect(Titlebar titlebar) {
		Icon icon = titlebar.getIcon();
		Rectangle r = new Rectangle(0,0, 0, 0);
		if(icon==null)
			return r;
		
		r.x = getLeftIconMargin();
		r.width = icon.getIconWidth();
		r.height = icon.getIconHeight();
		r.y = titlebar.getHeight()/2 - r.width/2;
		return r;
	}
	
	protected int getLeftIconMargin() {
		return 2;
	}
	
	protected int getRightIconMargin() {
		return 2;
	}
	
	public void layoutButtons(Titlebar titlebar) {
		int h = titlebar.getHeight();
		int x = titlebar.getWidth()-h;
		
		Component[] c = titlebar.getComponents();
		for(int i=0; i<c.length; i++) {
			if(!(c[i] instanceof Button))
				continue;
			
			Button b = (Button)c[i];
			b.setBounds(x, 0, h, h);
			x -= h;
		}
	}
	
	public void configureAction(Action action) {
		if(action==null)
			return;
		
		IconResource icons = getIcons(action);
		if(icons!=null)
			action.putValue(ICON_RESOURCE, icons);
	}

	protected Color getFontColor(boolean active) {
		Color c = active? activeFont: inactiveFont;
		return c==null? inactiveFont: c;
	}

	protected Color getBackgroundColor(boolean active) {
		Color color = active? activeBackground: inactiveBackground;
		return color==null? inactiveBackground: color;
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

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public IconMap getDefaultIcons() {
		return defaultIcons;
	}

	public void setDefaultIcons(IconMap defaultIcons) {
		this.defaultIcons = defaultIcons;
	}
	
	public void setDefaultIcons(String iconMapName) {
		IconMap map = IconResourceFactory.getIconMap(iconMapName);
		setDefaultIcons(map);
	}

	public IconResource getIcons(Action action) {
		String key = action==null? null: (String)action.getValue(Action.NAME);
		return getIcons(key);
	}
	
	public IconResource getIcons(String key) {
		return defaultIcons==null? null: defaultIcons.getIcons(key);
	}
	

    /**
     * @return Returns the painterResource.
     */
    public Painter getPainter() {
        return painter;
    }
    /**
     * @param painterResource The painterResource to set.
     */
    public void setPainter(Painter painter) {
        this.painter = painter;
    }

	public void initializeCreationParameters() {
		setActiveBackground(creationParameters.getColor(BACKGROUND_COLOR_ACTIVE));
		setActiveFont(creationParameters.getColor(FONT_COLOR_ACTIVE));
		setInactiveBackground(creationParameters.getColor(BACKGROUND_COLOR));
		setInactiveFont(creationParameters.getColor(FONT_COLOR));
		setDefaultHeight(creationParameters.getInt(DEFAULT_HEIGHT));
		setFont(creationParameters.getFont(FONT));
		setDefaultIcons(creationParameters.getString(IconResourceFactory.ICON_MAP_KEY));
		setPainter((Painter)creationParameters.getProperty(PAINTER));
	}
	
	public String getPreferredButtonUI() {
		return creationParameters.getString(UIFactory.BUTTON_KEY);
	}
	

}
