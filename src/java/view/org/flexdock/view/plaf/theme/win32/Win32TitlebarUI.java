/*
 * Created on Mar 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.plaf.theme.win32;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import org.flexdock.view.Button;
import org.flexdock.view.Titlebar;
import org.flexdock.view.plaf.theme.TitlebarUI;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Win32TitlebarUI extends TitlebarUI {
	public static final String OUTLINE_COLOR = "outline.color";
	
	protected Color outlineColor;
	
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

	protected void paintBackground(Graphics g, Titlebar titlebar) {
		int y = 2;
		int h = titlebar.getHeight()-4;
		int w = titlebar.getWidth();
		boolean active = titlebar.isActive();
		
		Color c = getBackgroundColor(active);
		g.setColor(c);
		
		// fill up the whole width if we're active
		if(active) {
			g.fillRect(0, y, w, h);
			return;
		}
		
		// otherwise, fill up the center part and draw an outline 
		g.fillRect(1, y+1, w-2, h-2);
		
		// don't draw the outline if we can't
		if(outlineColor==null)
			return;

		g.setColor(outlineColor);
		int y2 = y + h - 1;
		
		// draw horizontal lines
		g.drawLine(1, y, w-2, y);
		g.drawLine(1, y2, w-2, y2);		
		
		// draw vertical lines
		g.drawLine(0, y+1, 0, y2-1);
		g.drawLine(w-1, y+1, w-1, y2-1);
	}
	
	private int getMargin() {
		return 2;
	}

	public void initializeCreationParameters() {
		super.initializeCreationParameters();
		outlineColor = creationParameters.getColor(OUTLINE_COLOR);
	}

	protected int getLeftIconMargin() {
		return 4;
	}
}
