/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.titlebar.buttons;

import java.util.HashMap;

import org.flexdock.windowing.plaf.Configurator;
import org.flexdock.windowing.plaf.PropertySet;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ButtonUIFactory {
	public static final String BUTTON_KEY = "button-ui";
	
	public static final String BORDER = "border";
	public static final String BORDER_HOVER = "border.hover";
	public static final String BORDER_ACTIVE = "border.active";
	public static final String BORDER_ACTIVE_HOVER = "border.active.hover";
	public static final String BORDER_PRESSED = "border.pressed";
	
	private static final HashMap UI_CACHE = new HashMap();
	
	public static ButtonUI getUI(String name) {
		if(Configurator.isNull(name))
			return null;
		
		ButtonUI ui = (ButtonUI)UI_CACHE.get(name);
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
	
	private static ButtonUI loadUI(String name) {
		PropertySet properties = Configurator.getProperties(name, BUTTON_KEY);
		
		ButtonUI ui = new ButtonUI();
		ui.setUiName(properties.getName());
		ui.setBorderDefault(properties.getBorder(BORDER));
		ui.setBorderDefaultHover(properties.getBorder(BORDER_HOVER));
		ui.setBorderActive(properties.getBorder(BORDER_ACTIVE));
		ui.setBorderActiveHover(properties.getBorder(BORDER_ACTIVE_HOVER));
		ui.setBorderPressed(properties.getBorder(BORDER_PRESSED));
		
		return ui;
	}
}
