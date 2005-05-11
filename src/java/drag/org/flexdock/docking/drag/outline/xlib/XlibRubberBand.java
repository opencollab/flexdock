/*
 * Created on May 10, 2005
 */
package org.flexdock.docking.drag.outline.xlib;

import java.awt.Graphics;
import java.awt.Rectangle;

import org.flexdock.docking.drag.effects.RubberBand;
import org.flexdock.util.ResourceManager;

/**
 * @author Christopher Butler
 *
 */
public class XlibRubberBand extends RubberBand {
	private static final String NATIVE_RESOURCE = "org/flexdock/docking/drag/outline/xlib/libRubberBand.so";
	private static final String NATIVE_LIB = "RubberBand";
	
	private static final XlibRubberBand SINGLETON = new XlibRubberBand();
	private Rectangle currentRect;
	
	private native void drawRectangle(int x, int y, int width, int height);
	private native void clearRectangle(int x, int y, int width, int height);
	private native void cleanup();
	
	static {
		ResourceManager.loadLibrary(NATIVE_LIB, NATIVE_RESOURCE);
	}
	
	public XlibRubberBand() {
		
	}
	
	public void paint(Graphics g, Rectangle r) {
		clear();
		currentRect = r;
		draw(currentRect, true);
	}
	
	public void clear() {
		draw(currentRect, false);
		currentRect = null;
	}
	
	private void draw(Rectangle r, boolean xor) {
		if(r==null)
			return;
		
		if(xor)
			drawRectangle(r.x, r.y, r.width, r.height);
		else
			clearRectangle(r.x, r.y, r.width, r.height);
	}
}
