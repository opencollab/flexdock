/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.flexdock.util.ResourceManager;
import org.flexdock.windowing.Titlebar;
import org.flexdock.windowing.TitlebarUI;
import org.flexdock.windowing.View;
import org.flexdock.windowing.plaf.resolvers.PlafBasedViewResolver;
import org.flexdock.windowing.plaf.resources.ResourceHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewFactory {
	public static final String TARGET_VIEW_NAME = "org.flexdock.windowing.view.target";
	public static final String DEFAULT_PREFS_URI = "org/flexdock/windowing/view-prefs-default.xml";
	public static final String PREFS_URI = "view-prefs.xml";
	public static final String DEFAULT_VIEW_NAME = "default";
	
	public static final String DEFAULT_VIEW_CLASS = "org.flexdock.windowing.views.DefaultView";
	public static final String DEFAULT_TITLEBAR_CLASS = "org.flexdock.windowing.titlebar.DefaultTitlebar";
	public static final String DEFAULT_TITLEBAR_UI_CLASS = "org.flexdock.windowing.titlebar.DefaultTitlebarUI";
	
	public static final String PLAF_KEY = "plaf";
	public static final String VIEW_KEY = "view";
	public static final String TITLEBAR_KEY = "titlebar";
	public static final String ATTRIBUTE_KEY = "attribute";
	public static final String PROPERTY_KEY = "property";
	public static final String NAME_KEY = "name";
	public static final String VALUE_KEY = "value";
	public static final String EXTENDS_KEY = "extends";
	public static final String HANDLER_KEY = "handler";
	public static final String CLASSNAME_KEY = "classname";
	
	private static HashMap plafMappings;
	private static HashMap viewFactories;
	private static HashMap resourceHandlers;
	
	private UIResource viewResources;
	private UIResource titlebarResources;
	
	private ViewFactory() {
	}
	
	public static View createView() {
		return createView(null);
	}
	
	public static View createView(String title) {
		// first, check the system property
		String viewType = System.getProperty(TARGET_VIEW_NAME);
		// if no system property was supplied, then try to pick out a view
		// based on the currently installed look and feel
		if(viewType==null)
			viewType = getPlafView();
		return createView(title, viewType);
	}
	
	
	public static View createView(String title, String viewType) {
		if(viewType==null)
			viewType = DEFAULT_VIEW_NAME;
			
		ViewFactory factory = getViewFactory(viewType);
		return factory==null? null: factory.buildView(title);
	}
	
	private static Document loadUserPrefs() {
		return ResourceManager.getDocument(PREFS_URI);
	}
	
	private static Document loadDefaultPrefs() {
		return ResourceManager.getDocument(DEFAULT_PREFS_URI);
	}
	
	public static String getPlafView() {
		LookAndFeel currentPlaf = UIManager.getLookAndFeel();
		if(currentPlaf==null)
			return null;
		
		String key = currentPlaf.getClass().getName();
		return getPlafView(key);
	}
	
	public static String getPlafView(String key) {
		if(key==null)
			return null;
		
		Object value = getLookAndFeelMappings().get(key);
		if(value instanceof String)
			return (String)value;
		
		// if not a String, then we must have a PlafBasedViewResolver
		if(value instanceof PlafBasedViewResolver) {
			PlafBasedViewResolver resolver = (PlafBasedViewResolver)value;
			return resolver.getView(key);
		}
		return null;
	}
	
	private static HashMap getLookAndFeelMappings() {
		if(plafMappings!=null)
			return plafMappings;
		
		Document defaults = loadDefaultPrefs();
		Document user = loadUserPrefs();
		
		HashMap defaultMappings = loadPlafMappings(defaults);
		HashMap userMappings = loadPlafMappings(user);
		HashMap mappings = new HashMap(defaultMappings.size() + userMappings.size());

		mappings.putAll(defaultMappings);
		mappings.putAll(userMappings);
		
		plafMappings = mappings;
		return plafMappings;
	}
	
	private static HashMap loadPlafMappings(Document document) {
		if(document==null)
			return new HashMap(0);
		
		NodeList plafs = document.getElementsByTagName(PLAF_KEY);
		HashMap map = new HashMap(plafs.getLength());
		
		for(int i=0; i<plafs.getLength(); i++) {
			Element plaf = (Element)plafs.item(i);
			String key = plaf.getAttribute(NAME_KEY);
			String view = plaf.getAttribute(VIEW_KEY);
			String resolver = plaf.getAttribute(HANDLER_KEY);
			Object value = createPlafMapping(view, resolver);
			map.put(key, value);
		}
		return map;
	}
	
	private static Object createPlafMapping(String viewName, String resolverName) {
		if(isNull(resolverName))
			return viewName;
		
		PlafBasedViewResolver resolver = null;
		try {
			Class clazz = Class.forName(resolverName);
			// must be a type of PlafBasedViewResolver
			resolver = (PlafBasedViewResolver)clazz.newInstance();
		} catch(Exception e) {
			System.err.println("Error trying to create new instance of '" +resolverName + "'.");
			e.printStackTrace();
			return viewName;
		}

		// setup the default value on the resolver and return
		resolver.setDefaultView(viewName);
		return resolver;
	}
	
	private static ResourceHandler getResourceHandler(String handlerName) {
		if(resourceHandlers==null)
			resourceHandlers = loadResourceHandlers();
		return (ResourceHandler)resourceHandlers.get(handlerName);
	}
	
	private static HashMap loadResourceHandlers() {
		Document defaults = loadDefaultPrefs();
		Document user = loadUserPrefs();
		
		HashMap defaultHandlers = loadResourceHandlers(defaults);
		HashMap userHandlers = loadResourceHandlers(user);
		HashMap handlers = new HashMap(defaultHandlers.size() + userHandlers.size());

		handlers.putAll(defaultHandlers);
		handlers.putAll(userHandlers);
		
		return handlers;
	}
	
	private static HashMap loadResourceHandlers(Document document) {
		if(document==null)
			return new HashMap(0);
		
		NodeList handlers = document.getElementsByTagName(HANDLER_KEY);
		HashMap map = new HashMap(handlers.getLength());
		
		for(int i=0; i<handlers.getLength(); i++) {
			Element plaf = (Element)handlers.item(i);
			String key = plaf.getAttribute(NAME_KEY);
			String className = plaf.getAttribute(VALUE_KEY);
			ResourceHandler handler = createResourceHandler(className);
			if(handler!=null)
				map.put(key, handler);
		}
		return map;
	}
	
	private static ResourceHandler createResourceHandler(String className) {
		if(isNull(className))
			return null;

		try {
			Class clazz = Class.forName(className);
			return (ResourceHandler)clazz.newInstance();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
	
	
	
	private static ViewFactory getViewFactory(String targetView) {
		if(viewFactories==null)
			viewFactories = new HashMap();
		
		ViewFactory factory = (ViewFactory)viewFactories.get(targetView);
		if(factory==null) {
			factory = loadViewFactory(targetView);
			if(factory!=null)
				viewFactories.put(targetView, factory);
		}
		return factory==null? getViewFactory(DEFAULT_VIEW_NAME): factory;
	}
	
	
	
	
	
	
	
	
	private static ViewFactory loadViewFactory(String target) {
		HashMap viewCache = new HashMap();
		HashMap titlebarCache = new HashMap();
		
		 // first load the default prefs
		 loadViewPrefs(loadDefaultPrefs(), viewCache, titlebarCache);
		 // then load the user prefs, overwriting any collisions with the default prefs
		 loadViewPrefs(loadUserPrefs(), viewCache, titlebarCache);
		 
		 return loadViewFactory(target, viewCache, titlebarCache);
	}

	private static void loadViewPrefs(Document document, HashMap viewCache, HashMap titlebarCache) { 
		if(document!=null) {
			loadViewPrefs(document, viewCache, VIEW_KEY);
			loadViewPrefs(document, titlebarCache, TITLEBAR_KEY);			
		}
	}
	
	private static void loadViewPrefs(Document document, HashMap cache, String tagName) {
		NodeList list = document.getElementsByTagName(tagName);
		for(int i=0; i<list.getLength(); i++) {
			Element elem = (Element)list.item(i);
			String key = elem.getAttribute(NAME_KEY);
			cache.put(key, elem);
		}
	}
	
	private static HashMap getItemElements(String target, Map cache, String tagName) {
		Element targetElem = (Element)cache.get(target);
		if(targetElem==null)
			return null;
		
		// load the parent element (may return null)
		String parentKey = targetElem.getAttribute(EXTENDS_KEY);
		if(target.equals(parentKey))
			throw new IllegalArgumentException("Element '" + target + "' cannot extend itself.");
		
		HashMap elements = parentKey==null? null: getItemElements(parentKey, cache, tagName);
		if(elements==null)
			elements = new HashMap();
		
		NodeList properties = targetElem.getElementsByTagName(tagName);
		for(int i=0; i<properties.getLength(); i++) {
			Element propertyElem = (Element)properties.item(i);
			String key = propertyElem.getAttribute(NAME_KEY);
			elements.put(key, propertyElem);
		}
		return elements;
		
	}
	
	private static ViewFactory loadViewFactory(String target, HashMap views, HashMap titlebars) {
		Element viewElem = (Element)views.get(target);
		if(viewElem==null)
			return null;
		
		String titlebarKey = viewElem.getAttribute(TITLEBAR_KEY);
		Element titleElem = titlebarKey==null? null: (Element)titlebars.get(titlebarKey);
		if(titleElem==null)
			titleElem = (Element)titlebars.get(DEFAULT_VIEW_NAME);
		titlebarKey = titleElem.getAttribute(NAME_KEY);
		
		ViewFactory factory = new ViewFactory();
		factory.viewResources = createUIResource(target, views);
		factory.titlebarResources = createUIResource(titlebarKey, titlebars);
		return factory;
	}
	
	private static UIResource createUIResource(String target, HashMap propertiesCache) {
		HashMap propertyElements = getItemElements(target, propertiesCache, PROPERTY_KEY);
		return createUIResource(propertyElements);
	}
	
	private static UIResource createUIResource(HashMap propertyElements) {
		UIResource resource = new UIResource(propertyElements.size());
		
		for(Iterator it=propertyElements.keySet().iterator(); it.hasNext();) {
			String key = (String)it.next();
			Element elem = (Element)propertyElements.get(key);
			String value = elem.getAttribute(VALUE_KEY);
			String handlerKey = elem.getAttribute(HANDLER_KEY);
			Object resourceItem = createResource(value, handlerKey);
			if(resourceItem!=null)
				resource.setProperty(key, resourceItem);
		}
		
		return resource;
	}
	
	private static Object createResource(String value, String handlerName) {
		if(isNull(handlerName))
			return value;
		
		ResourceHandler handler = getResourceHandler(handlerName);
		if(handler==null)
			return value;
		
		return handler.getResource(value);
	}
	
	
	private static boolean isNull(String data) {
		data = data==null? null: data.trim();
		return data==null || data.length()==0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private View buildView(String title) {
		View view = createViewInstance();
		Titlebar titlebar = createTitlebarInstance();
		TitlebarUI titlebarUI = createTitlebarUIInstance();
		
		titlebarUI.setUIResource(titlebarResources);
		titlebar.setUI(titlebarUI);
		view.setTitlebar(titlebar);
		
		view.setTitle(title==null? "": title);
		
		return view;
	}
	
	private View createViewInstance() {
		String cName = getViewClass();
		Object obj = newObject(cName);
		return obj instanceof View? (View)obj: (View)newObject(DEFAULT_VIEW_CLASS);
	}
	
	private Titlebar createTitlebarInstance() {
		String cName = getTitlebarClass();
		Object obj = newObject(cName);
		return obj instanceof Titlebar? (Titlebar)obj: (Titlebar)newObject(DEFAULT_TITLEBAR_CLASS);
	}
	
	private TitlebarUI createTitlebarUIInstance() {
		String cName = getTitlebarUIClass();
		Object obj = newObject(cName);
		return obj instanceof TitlebarUI? (TitlebarUI)obj: (TitlebarUI)newObject(DEFAULT_TITLEBAR_UI_CLASS);
	}
	
	private String getViewClass() {
		String className = viewResources.getString(CLASSNAME_KEY);
		return className==null? DEFAULT_VIEW_CLASS: className;
	}
	
	private String getTitlebarClass() {
		String className = titlebarResources.getString(CLASSNAME_KEY);
		return className==null? DEFAULT_TITLEBAR_CLASS: className;
	}
	
	private String getTitlebarUIClass() {
		String className = titlebarResources.getString(TitlebarConstants.UI_DELEGATE);
		return className==null? DEFAULT_TITLEBAR_UI_CLASS: className;
	}
	
	private Object newObject(String className) {
		try {
			Class clazz = Class.forName(className);
			return clazz.newInstance();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
}
