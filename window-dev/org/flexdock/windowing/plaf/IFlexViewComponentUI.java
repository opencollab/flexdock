/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IFlexViewComponentUI {
	public static final String ICON_RESOURCE = "flexdock.button.icon.resource";
	
	public PropertySet getCreationParameters();
	public void setCreationParameters(PropertySet creationParameters);
	public void initializeCreationParameters();
}
