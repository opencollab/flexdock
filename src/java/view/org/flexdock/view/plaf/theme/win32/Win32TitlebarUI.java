/*
 * Created on Mar 2, 2005
 */
package org.flexdock.view.plaf.theme.win32;

import java.awt.Component;

import org.flexdock.view.Button;
import org.flexdock.view.Titlebar;
import org.flexdock.view.plaf.theme.TitlebarUI;

/**
 * @author Christopher Butler
 */
public class Win32TitlebarUI extends TitlebarUI {

	public void layoutButtons(Titlebar titlebar) {
		int margin = getMargin()+2;
		int h = titlebar.getHeight()-2*margin;
		int x = titlebar.getWidth()-margin-h;
		
		Component[] c = titlebar.getComponents();
		for(int i=0; i<c.length; i++) {
			if(!(c[i] instanceof Button))
				continue;
			
			Button b = (Button)c[i];
			b.setBounds(x, margin, h, h);
			x -= h;
		}
	}

	private int getMargin() {
		return 2;
	}

	protected int getLeftIconMargin() {
		return 4;
	}
}
