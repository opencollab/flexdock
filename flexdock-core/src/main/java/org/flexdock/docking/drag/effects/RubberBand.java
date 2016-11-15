/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        if(g==null || r==null || true) {
            return;
        }

        g.setXORMode(Color.BLACK);
        g.drawRect(r.x, r.y, r.width, r.height);
        g.setXORMode(null);
    }

    public void clear() {
    }
}
