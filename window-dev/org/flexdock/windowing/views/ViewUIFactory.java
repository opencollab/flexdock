/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.views;

import java.util.HashMap;

import org.flexdock.windowing.plaf.Configurator;
import org.flexdock.windowing.plaf.PropertySet;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewUIFactory {
	public static final String VIEW_KEY = "view";
	public static final String TITLEBAR_UI = "titlebar.ui";
	private static final HashMap UI_CACHE = new HashMap();
	
	public static ViewUI getUI(String name) {
		if(Configurator.isNull(name))
			return null;
		
		ViewUI ui = (ViewUI)UI_CACHE.get(name);
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
	
	private static ViewUI loadUI(String name) {
		PropertySet properties = Configurator.getProperties(name, VIEW_KEY);
		
		ViewUI ui = new ViewUI();
		ui.setPreferredTitlebarUI(properties.getString(TITLEBAR_UI));
		return ui;
	}
}
