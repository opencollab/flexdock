/*
 * Created on Mar 1, 2005
 */
package org.flexdock.plaf;

import javax.swing.plaf.ComponentUI;

/**
 * @author Christopher Butler
 */
public abstract class FlexViewComponentUI extends ComponentUI implements IFlexViewComponentUI {
    protected PropertySet creationParameters;

    public PropertySet getCreationParameters() {
        return creationParameters;
    }

    public void setCreationParameters(PropertySet creationParameters) {
        this.creationParameters = creationParameters;
        initializeCreationParameters();
    }

    public abstract void initializeCreationParameters();
}
