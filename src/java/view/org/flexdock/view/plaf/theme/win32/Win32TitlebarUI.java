/*
 * Created on Mar 2, 2005
 */
package org.flexdock.view.plaf.theme.win32;

import java.awt.Rectangle;

import org.flexdock.view.Titlebar;
import org.flexdock.view.plaf.theme.TitlebarUI;

/**
 * @author Christopher Butler
 */
public class Win32TitlebarUI extends TitlebarUI {

	protected Rectangle getPaintAreaRectangle(Titlebar titlebar) {
	    return new Rectangle(0, 2, titlebar.getWidth(), titlebar.getHeight()-4);
	}

	protected int getButtonMargin() {
	    return 4;
	}

	protected int getLeftIconMargin() {
		return 4;
	}
}
