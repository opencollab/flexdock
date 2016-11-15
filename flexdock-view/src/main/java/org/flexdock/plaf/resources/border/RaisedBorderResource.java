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
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

import org.flexdock.plaf.resources.ResourceHandler;

/**
 * @author Claudio Romano
 */
public class RaisedBorderResource extends ResourceHandler {

    public Object getResource(String data) {
        return new RaisedBorder();
    }

    private static final class RaisedBorder extends AbstractBorder {
        private static final Color HIGHLIGHT_COLOR = UIManager.getColor("controlLtHighlight");
        private static final Color CONTROL_SHADOW = UIManager.getColor("controlShadow");

        private static final Insets INSETS = new Insets(1, 1, 1, 0);

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(HIGHLIGHT_COLOR);
            g.fillRect(0, 0, w, 1);
            g.fillRect(0, 1, 1, h - 1);
            g.setColor(CONTROL_SHADOW);
            g.fillRect(0, h - 1, w, 1);
        }
    }
}

