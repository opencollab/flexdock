/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.resolvers;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlafBasedViewResolver {
	private String defaultView;
	
	public void setDefaultView(String view) {
		defaultView = view;
	}
	
	public String getDefaultView() {
		return defaultView;
	}
	
	public String getView(String plaf) {
		return getDefaultView();
	}
}
