/*
 * Created on Aug 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.flexdock.docking.drag.outline;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @author Christopher Butler
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DefaultRubberBand extends AbstractRubberBand {
	private static final DefaultRubberBand SINGLETON = new DefaultRubberBand();
	
	private DefaultRubberBand() {
		
	}
	public void paint(Graphics g, Rectangle r) {
		if(g==null || r==null || true)
			return;
		
		g.setXORMode(Color.BLACK);
		g.drawRect(r.x, r.y, r.width, r.height);
		g.setXORMode(null);
	}
	
	public void clear() {
	}
	
	public static AbstractRubberBand getInstance() {
		return SINGLETON;
	}
}
