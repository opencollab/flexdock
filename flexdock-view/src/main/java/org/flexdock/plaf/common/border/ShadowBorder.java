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
package org.flexdock.plaf.common.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;


public class ShadowBorder implements Border {
    private Insets insets;

    public ShadowBorder() {
        insets = new Insets(1, 1, 3, 3);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        // we'll be filling in our own background.
        return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x,
                            int y, int w, int h) {
        // choose which colors we want to use
        Color bg = c.getBackground();
        if(c.getParent()!=null) {
            bg = c.getParent().getBackground();
        }
        Color mid = bg.darker();
        Color rect = mid.darker();
        Color edge = average(mid, bg);

        // fill in the corners with the parent-background
        // so it looks see-through
        g.setColor(bg);
        g.fillRect(0, h-3, 3, 3);
        g.fillRect(w-3, 0, 3, 3);
        g.fillRect(w-3, h-3, 3, 3);

        // draw the outline
        g.setColor(rect);
        g.drawRect(0, 0, w - 3, h - 3);

        // draw the drop-shadow
        g.setColor(mid);
        g.drawLine(1, h - 2, w - 2, h - 2);
        g.drawLine(w - 2, 1, w - 2, h - 2);

        g.setColor(edge);
        g.drawLine(2, h - 1, w - 2, h - 1);
        g.drawLine(w - 1, 2, w - 1, h - 2);
    }

    private static Color average(Color c1, Color c2) {
        int red = c1.getRed() + (c2.getRed() - c1.getRed()) / 2;
        int green = c1.getGreen() + (c2.getGreen() - c1.getGreen()) / 2;
        int blue = c1.getBlue() + (c2.getBlue() - c1.getBlue()) / 2;
        return new Color(red, green, blue);
    }
}