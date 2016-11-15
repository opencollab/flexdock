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
import java.awt.Point;

import javax.swing.border.Border;

import org.flexdock.docking.DockingConstants;

/**
 * @author Christopher Butler
 */
public class SlideoutBorder implements Border, DockingConstants {
    private int orientation;
    public static final Color WIN32_GRAY = new Color(212, 208, 200);

    public SlideoutBorder() {
    }

    @Override
    public Insets getBorderInsets(Component c) {
        Insets insets = new Insets(0, 0, 0, 0);
        switch(orientation) {
            case LEFT:
                insets.right = 2;
                break;
            case RIGHT:
                insets.left = 2;
                break;
            case TOP:
                insets.bottom = 2;
                break;
            case BOTTOM:
                insets.top = 2;
                break;
        }
        return insets;
    }


    @Override
    public boolean isBorderOpaque() {
        return true;
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color outer = Color.BLACK;
        Color inner = Color.GRAY;
        outer = WIN32_GRAY;
        inner = Color.WHITE;

        Color base = g.getColor();

        int w = c.getWidth();
        int h = c.getHeight();

        Point outer1 = new Point(0, 0);
        Point outer2 = new Point(0, 0);
        Point inner1 = new Point(0, 0);
        Point inner2 = new Point(0, 0);

        switch(orientation) {
            case LEFT:
                outer1.setLocation(w-1, 0);
                outer2.setLocation(w-1, h-1);
                inner1.setLocation(w-2, 0);
                inner2.setLocation(w-2, h-1);
                break;
            case RIGHT:
                outer1.setLocation(0, 0);
                outer2.setLocation(0, h-1);
                inner1.setLocation(1, 0);
                inner2.setLocation(1, h-1);
                break;
            case TOP:
                outer1.setLocation(0, h-1);
                outer2.setLocation(w-1, h-1);
                inner1.setLocation(0, h-2);
                inner2.setLocation(w-1, h-2);
                break;
            case BOTTOM:
                outer1.setLocation(0, 0);
                outer2.setLocation(w-1, 0);
                inner1.setLocation(0, 1);
                inner2.setLocation(w-1, 1);
                break;
        }

        g.setColor(outer);
        g.drawLine(outer1.x, outer1.y, outer2.x, outer2.y);
        g.setColor(inner);
        g.drawLine(inner1.x, inner1.y, inner2.x, inner2.y);

        g.setColor(base);
    }

    public int getOrientation() {
        return orientation;
    }
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
