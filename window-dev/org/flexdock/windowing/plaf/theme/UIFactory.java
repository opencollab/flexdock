/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.theme;

import java.util.HashMap;

import org.flexdock.windowing.plaf.Configurator;
import org.flexdock.windowing.plaf.IFlexViewComponentUI;
import org.flexdock.windowing.plaf.PropertySet;
import org.flexdock.windowing.plaf.XMLConstants;
import org.w3c.dom.Element;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UIFactory implements XMLConstants {
	public static final String THEME_KEY = "theme";
	public static final String VIEW_KEY = "view-ui";
	public static final String TITLEBAR_KEY = "titlebar-ui";
	public static final String BUTTON_KEY = "button-ui";
	private static final HashMap VIEW_UI_CACHE = new HashMap();
	private static final HashMap TITLEBAR_UI_CACHE = new HashMap();
	private static final HashMap BUTTON_UI_CACHE = new HashMap();
	private static final HashMap THEME_UI_CACHE = new HashMap();
	
	public static ViewUI getViewUI(String name) {
		return (ViewUI)getUI(name, VIEW_UI_CACHE, VIEW_KEY, ViewUI.class);
	}
	
	public static TitlebarUI getTitlebarUI(String name) {
		return (TitlebarUI)getUI(name, TITLEBAR_UI_CACHE, TITLEBAR_KEY, TitlebarUI.class);
	}
	
	public static ButtonUI getButtonUI(String name) {
		return (ButtonUI)getUI(name, BUTTON_UI_CACHE, BUTTON_KEY, ButtonUI.class);
	}
	
	public static Theme getTheme(String name) {
		if(Configurator.isNull(name))
			return null;
		
		Theme theme = (Theme)THEME_UI_CACHE.get(name);
		if(theme==null) {
			theme = loadTheme(name);
			if(theme!=null) {
				synchronized(THEME_UI_CACHE) {
					THEME_UI_CACHE.put(name, theme);		
				}
			}
		}
		return theme;
	}
	
	private static IFlexViewComponentUI getUI(String name, HashMap cache, String tagName, Class rootClass) {
		if(Configurator.isNull(name))
			return null;
		
		IFlexViewComponentUI ui = (IFlexViewComponentUI)cache.get(name);
		if(ui==null) {
			ui = loadUI(name, tagName, rootClass);
			if(ui!=null) {
				synchronized(cache) {
					cache.put(name, ui);		
				}
			}
		}
		return ui;
	}

	private static IFlexViewComponentUI loadUI(String name, String tagName, Class rootClass) {
		PropertySet properties = Configurator.getProperties(name, tagName);
		String classname = properties.getString(CLASSNAME_KEY);
		Class implClass = loadUIClass(classname, rootClass);
		
		try {
			IFlexViewComponentUI ui = (IFlexViewComponentUI)implClass.newInstance();
			ui.setCreationParameters(properties);
			return ui;
		} catch(Exception e) {
			// we use public, no-argument constructors, so if this happens, we
			// have a configuration error.
			e.printStackTrace();
			return null;
		}
	}
	
	private static Class loadUIClass(String classname, Class rootClass) {
		if(Configurator.isNull(classname))
			return rootClass;
		
		Class implClass = null;
		try {
			implClass = Class.forName(classname);
			if(!rootClass.isAssignableFrom(implClass)) {
				System.err.println("Invalid UI class " + implClass + ".  Using '" + rootClass + "' instead.");
				implClass = null;
			}
		} catch(ClassNotFoundException e) {
			System.err.println("Unable to load " + classname + ".  Using '" + rootClass + "' instead.");
			e.printStackTrace();
			implClass = null;
		}
		return implClass==null? rootClass: implClass;
	}
	
	private static Theme loadTheme(String themeName) {
		HashMap map = Configurator.getNamedElementsByTagName(THEME_KEY);
		if(map==null)
			return null;
		return loadTheme(themeName, map);
	}
	
	private static Theme loadTheme(String themeName, HashMap cache) {
		Element themeElem = (Element)cache.get(themeName);
		if(themeElem==null)
			return null;
		
		// if we're an indirect reference to a different theme, then return that theme
		String redirect = themeElem.getAttribute(REFERENCE_KEY);
		if(!Configurator.isNull(redirect))
			return loadTheme(redirect, cache);
		
		// if we're a child of another theme, then load the parent and 
		// add our properties afterward
		String parentName = themeElem.getAttribute(EXTENDS_KEY);
		Theme theme = Configurator.isNull(parentName)? new Theme(): loadTheme(parentName, cache);
		if(theme==null)
			theme = new Theme();
		
		String name = themeElem.getAttribute(NAME_KEY);
		String desc = themeElem.getAttribute(DESC_KEY);
		String view = themeElem.getAttribute(VIEW_KEY);
		
		theme.setName(name);
		theme.setDescription(desc);

		ViewUI viewUI = Configurator.isNull(view)? null: getViewUI(view);
		TitlebarUI titlebarUI = viewUI==null? null: getTitlebarUI(viewUI.getPreferredTitlebarUI());
		ButtonUI buttonUI = titlebarUI==null? null: getButtonUI(titlebarUI.getPreferredButtonUI());
		
		if(viewUI!=null)
			theme.setViewUI(viewUI);
		if(titlebarUI!=null)
			theme.setTitlebarUI(titlebarUI);
		if(buttonUI!=null)
			theme.setButtonUI(buttonUI);
		
		return theme;
	}
}
