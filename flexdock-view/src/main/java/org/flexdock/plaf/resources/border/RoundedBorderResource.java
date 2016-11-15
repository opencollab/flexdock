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
package org.flexdock.plaf.resources.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ColorUIResource;

import org.flexdock.plaf.resources.ColorResourceHandler;
import org.flexdock.plaf.resources.ResourceHandler;

/**
 * @author Claudio Romano
 */
public class RoundedBorderResource extends ResourceHandler {
    private static final ColorUIResource DEFAULT_COLOR = new ColorUIResource(Color.BLACK);

    public Object getResource(String data) {
        //pattern should be "color"
        String[] args = getArgs(data);
        ColorUIResource lightColor = args.length==1? getColor(args[0]): DEFAULT_COLOR;

        return new RoundedBorder(lightColor);
    }

    private ColorUIResource getColor(String data) {
        ColorUIResource color = ColorResourceHandler.parseHexColor(data);
        return data==null? DEFAULT_COLOR: color;
    }

    public static class RoundedBorder extends AbstractBorder {
        private Color color;

        public RoundedBorder(Color color) {
            this.color = color;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(color);
            int y2 = y + height - 1;

            // draw horizontal lines
            g.drawLine(1, y, width - 2, y);
            g.drawLine(1, y2, width - 2, y2);

            // draw vertical lines
            g.drawLine(0, y + 1, 0, y2 - 1);
            g.drawLine(width - 1, y + 1, width - 1, y2 - 1);
        }
    }
}
