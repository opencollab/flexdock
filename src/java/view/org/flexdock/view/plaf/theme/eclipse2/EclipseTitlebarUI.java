/*
 * Created on 22.03.2005
 */
package org.flexdock.view.plaf.theme.eclipse2;

import org.flexdock.view.plaf.theme.TitlebarUI;

/**
 * @author Claudio Romano
 */
public class EclipseTitlebarUI extends TitlebarUI {
	protected int getButtonMargin() {
	    return 2;
	}
	
	protected int getLeftIconMargin() {
	    return 4;
	}
	
	public void initializeCreationParameters() {
	    super.initializeCreationParameters();
	    setDefaultHeight( 25);
	}
	
}
