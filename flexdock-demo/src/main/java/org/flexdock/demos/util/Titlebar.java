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
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/**
 * @author Christopher Butler
 */
public class Titlebar extends JLabel {

    public Titlebar() {
        super();
        init();
    }

    public Titlebar(String text) {
        super(text);
        init();
    }

    public Titlebar(String text, Color bgColor) {
        super(text);
        init();
        setBackground(bgColor);
    }

    private void init() {
        setOpaque(true);
        setBorder(new EmptyBorder(2, 4, 2, 2));
    }

    @Override
    protected void paintBorder(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        g.setColor(getBackground().brighter());
        g.drawLine(0, 0, w, 0);
        g.drawLine(0, 0, 0, h);

        g.setColor(getBackground().darker());
        g.drawLine(0, h, w, h);
    }

    public void setTitle(String title) {
        if(title==null) {
            title = "";
        }
        title = title.trim();
        setText(title);
    }
}
