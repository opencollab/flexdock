/*
 * Created on 22.03.2005
 */
package org.flexdock.view.plaf.theme.officexp;

import java.awt.Rectangle;

import org.flexdock.view.Titlebar;
import org.flexdock.view.plaf.theme.TitlebarUI;

/**
 * @author cro
 */
public class OfficeXPTitlebarUI extends TitlebarUI {
    protected Rectangle getPaintAreaRectangle(Titlebar titlebar) {
	    return new Rectangle(0, 0, titlebar.getWidth(), titlebar.getHeight()-3);
	}

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
