/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.titlebar;

import java.util.HashMap;

import org.flexdock.windowing.plaf.Configurator;
import org.flexdock.windowing.plaf.PropertySet;
import org.flexdock.windowing.plaf.titlebar.buttons.ButtonUIFactory;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TitlebarUIFactory {
	public static final String TITLEBAR_KEY = "titlebar-ui";
	
	public static final String DEFAULT_HEIGHT = "default.height";
	public static final String FONT_COLOR = "font.color";
	public static final String FONT_COLOR_ACTIVE = "font.color.active";
	public static final String BACKGROUND_COLOR = "bgcolor";
	public static final String BACKGROUND_COLOR_ACTIVE = "bgcolor.active";

	
	private static final HashMap UI_CACHE = new HashMap();
	
	public static TitlebarUI getUI(String name) {
		if(Configurator.isNull(name))
			return null;
		
		TitlebarUI ui = (TitlebarUI)UI_CACHE.get(name);
		if(ui==null) {
			ui = loadUI(name);
			if(ui!=null) {
				synchronized(UI_CACHE) {
					UI_CACHE.put(name, ui);		
				}
			}
		}
		return ui;
	}
	
	private static TitlebarUI loadUI(String name) {
		PropertySet properties = Configurator.getProperties(name, TITLEBAR_KEY);
		
		TitlebarUI ui = new TitlebarUI();
		ui.setUiName(properties.getName());
		ui.setDefaultHeight(properties.getInt(DEFAULT_HEIGHT));
		ui.setActiveBackground(properties.getColor(BACKGROUND_COLOR_ACTIVE));
		ui.setActiveFont(properties.getColor(FONT_COLOR_ACTIVE));
		ui.setInactiveBackground(properties.getColor(BACKGROUND_COLOR));
		ui.setInactiveFont(properties.getColor(FONT_COLOR));
		ui.setPreferredButtonUI(properties.getString(ButtonUIFactory.BUTTON_KEY));
		
		return ui;
	}
}
