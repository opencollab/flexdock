/*
 * Created on Mar 1, 2005
 */
package org.flexdock.plaf;

/**
 * @author Christopher Butler
 */
public interface IFlexViewComponentUI extends XMLConstants {
    public static final String ICON_RESOURCE = "flexdock.button.icon.resource";

    public PropertySet getCreationParameters();
    public void setCreationParameters(PropertySet creationParameters);
    public void initializeCreationParameters();
}
