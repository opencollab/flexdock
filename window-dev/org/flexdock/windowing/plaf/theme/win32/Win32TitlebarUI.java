/*
 * Created on Mar 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.theme.win32;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import org.flexdock.windowing.Button;
import org.flexdock.windowing.Titlebar;
import org.flexdock.windowing.plaf.theme.TitlebarUI;

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
		int margin = getMargin();
		
		int x = margin;
		int y = margin;
		int w = titlebar.getWidth()-2*margin;
		int h = titlebar.getHeight()-2*margin;
		boolean active = titlebar.isActive();

		Color c = getBackgroundColor(active);
		g.setColor(c);
		g.fillRect(x, y, w, h);
		
		if(active || outlineColor==null)
			return;

		w = titlebar.getWidth();
		h = titlebar.getHeight();
		
		g.setColor(outlineColor);
		g.drawLine(margin, margin, w-margin-1, margin);
		g.drawLine(margin, h-margin, w-margin-1, h-margin);
		g.drawLine(0, margin+1, 0, h-margin-1);
		g.drawLine(w-margin, margin+1, w-margin, h-margin-1);
	}
	
	private int getMargin() {
		return 1;
	}

	public void initializeCreationParameters() {
		super.initializeCreationParameters();
		outlineColor = creationParameters.getColor(OUTLINE_COLOR);
	}

	protected int getLeftIconMargin() {
		return 4;
	}
}
