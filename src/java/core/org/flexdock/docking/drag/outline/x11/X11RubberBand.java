/*
 * Created on Aug 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.docking.drag.outline.x11;

import java.awt.Rectangle;

import org.flexdock.docking.drag.outline.AbstractRubberBand;

/**
 * @author marius
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class X11RubberBand extends AbstractRubberBand {
	private static final X11RubberBand SINGLETON = create();
	private Graphics graphics;
	private Rectangle currentRect;
	
	public static AbstractRubberBand getInstance() {
		return SINGLETON;
	}
	
	private static X11RubberBand create() {
		Graphics g = Graphics.getGraphics();
		if(g==null)
			return null;
		return new X11RubberBand(g);
	}
	
	private X11RubberBand(Graphics g) {
		graphics = Graphics.getGraphics();
		graphics.setForeground(graphics.getWhitePixel());
		graphics.setSubWindowMode(Graphics.SUBWIN_MODE_INCLUDE_INFERIORS);
		graphics.setXor();
	}
	
	public void paint(java.awt.Graphics g, Rectangle rect) {
		clear();
		currentRect = rect;
		drawImpl();
	}
	
	private void drawImpl() {
		if(currentRect==null)
			return;
		
		graphics.drawRectangle(currentRect);
	}
	
	public void clear() {
		if(currentRect!=null)
			drawImpl();
		currentRect = null;
	}
}
