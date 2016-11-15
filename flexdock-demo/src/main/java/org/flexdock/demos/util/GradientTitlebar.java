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
package org.flexdock.demos.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * @author Christopher Butler
 */
public class GradientTitlebar extends Titlebar {
    public static final Color DEFAULT_MID_COLOR = new Color(168, 203, 239);
    public static final Color DEFAULT_START_COLOR = new Color(10, 36, 106);

    private GradientPainter gradient;

    public GradientTitlebar() {
        super();
        init(null, null);
    }

    public GradientTitlebar(String text) {
        super(text);
        init(null, null);
    }

    public GradientTitlebar(String text, Color start, Color mid) {
        super(text);
        init(start, mid);
    }

    private void init(Color start, Color mid) {
        setOpaque(false);
        gradient = new GradientPainter(start, mid);
        setStartColor(start);
        setMidColor(mid);
        setForeground(Color.WHITE);
        setFont(getFont().deriveFont(Font.PLAIN));
    }

    public void setStartColor(Color color) {
        gradient.setStartColor(color==null? DEFAULT_START_COLOR: color);
    }

    public void setMidColor(Color color) {
        gradient.setMidColor(color==null? DEFAULT_MID_COLOR: color);
    }

    protected void paintComponent(Graphics g) {
        gradient.paintGradient(this, g);
        super.paintComponent(g);
    }
}
