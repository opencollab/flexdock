/*
 * Created on Aug 29, 2004
 */
package org.flexdock.docking.drag.outline.x11;

import java.awt.Rectangle;

import org.flexdock.docking.drag.effects.RubberBand;

/**
 * @author marius
 */
public class X11RubberBand extends RubberBand {
	private static final Graphics X11_GRAPHICS = Graphics.getGraphics();
	private static X11RubberBand SINGLETON;
	private static final Object LOCK = new Object();
	
	private Graphics graphics;
	private Rectangle currentRect;

	
	public static X11RubberBand getInstance() {
		synchronized(LOCK) {
			return SINGLETON==null? new X11RubberBand(): SINGLETON;			
		}
	}
	
	public X11RubberBand() {
		synchronized(LOCK) {
			if(SINGLETON!=null)
				throw new IllegalStateException("There may only be one singleton X11RubberBand.  Use X11RubberBand.getInstance() instead.");
			
			this.graphics = X11_GRAPHICS;
			graphics.setForeground(graphics.getWhitePixel());
			graphics.setSubWindowMode(Graphics.SUBWIN_MODE_INCLUDE_INFERIORS);
			graphics.setXor();			
		}
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
