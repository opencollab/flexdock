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

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TitlebarUIFactory implements TitlebarConstants {
	public static final String TITLEBAR_KEY = "titlebar";
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
		ui.setDefaultHeight(properties.getInt(DEFAULT_HEIGHT));
		ui.setActiveBackground(properties.getColor(ACTIVE_BACKGROUND_COLOR));
		ui.setActiveFont(properties.getColor(ACTIVE_FONT_COLOR));
		ui.setInactiveBackground(properties.getColor(INACTIVE_BACKGROUND_COLOR));
		ui.setInactiveFont(properties.getColor(INACTIVE_FONT_COLOR));
		ui.setPreferredButtonUI(properties.getString(BUTTON_UI));
		
		return ui;
	}
}
