/*
 * Created on Aug 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.flexdock.docking.drag.outline;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @author Christopher Butler
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractRubberBand {
	public void paint(Graphics g, int x, int y, int width, int height) {
		paint(g, new Rectangle(x, y, width, height));
	}
	
	public void paint(int x, int y, int width, int height) {
		paint(new Rectangle(x, y, width, height));
	}
	
	public void paint(Rectangle r) {
		paint(null, r);
	}
	
	public abstract void paint(Graphics g, Rectangle r);
	public abstract void clear();
}
