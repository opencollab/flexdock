/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf;

import javax.swing.plaf.ComponentUI;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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
