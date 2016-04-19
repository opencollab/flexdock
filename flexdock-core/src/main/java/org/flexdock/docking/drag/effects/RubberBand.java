/*
 * Created on Aug 31, 2004
 */
package org.flexdock.docking.drag.effects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @author Christopher Butler
 *
 */
public class RubberBand {
    public static final String DEBUG_OUTPUT = "rubberband.debug";

    public void paint(Graphics g, int x, int y, int width, int height) {
        paint(g, new Rectangle(x, y, width, height));
    }

    public void paint(int x, int y, int width, int height) {
        paint(new Rectangle(x, y, width, height));
    }

    public void paint(Rectangle r) {
        paint(null, r);
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
}
