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
package org.flexdock.docking.drag.preview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.drag.effects.DefaultPreview;

public class XORPreview extends DefaultPreview {

    public void drawPreview(Graphics2D g, Polygon p, Dockable dockable, Map dragInfo) {
        float[] pattern = { 1.0f, 1.0f };
        Stroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, pattern, 0f);
        g.setStroke(stroke);

        g.setColor(Color.BLACK);
        g.setXORMode(Color.WHITE);
        drawPolygon(g, p, 3);
    }

    private void drawPolygon(Graphics2D g, Polygon p, int thickness) {
        Point center = getCenterOfGravity(p);
        for(int i=0; i<thickness; i++) {
            g.drawPolygon(p);
            gravitate(p, center, 1);
        }
    }

    private void gravitate(Polygon p, Point center, int step) {
        int len = p.npoints;

        for(int i=0; i<len; i++) {
            int deltaX = center.x > p.xpoints[i]? step: -step;
            int deltaY = center.y > p.ypoints[i]? step: -step;
            p.xpoints[i] += deltaX;
            p.ypoints[i] += deltaY;
        }
    }

    private Point getCenterOfGravity(Polygon p) {
        int x = 0;
        int y = 0;
        int len = p.npoints;
        for(int i=0; i<len; i++) {
            x += p.xpoints[i];
            y += p.ypoints[i];
        }
        return new Point(x/len, y/len);
    }
}
