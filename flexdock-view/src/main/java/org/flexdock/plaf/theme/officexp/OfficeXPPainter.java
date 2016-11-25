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
package org.flexdock.plaf.theme.officexp;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.flexdock.plaf.resources.paint.DefaultPainter;
import org.flexdock.util.SwingUtility;

/**
 * @author Claudio Romano
 */
public class OfficeXPPainter extends DefaultPainter {
    public static final String GRADIENT_COLOR = "gradient.color";
    public static final String GRADIENT_COLOR_ACTIVE = "gradient.color.active";


    @Override
    public void paint(Graphics g, int width, int height, boolean active, JComponent titlebar) {
        int center = (int)(height / 1.2);

        Color backgroundColor = getBackgroundColor(active);
        Color gradColor = getGradientColor(active);

        GradientPaint gradientPaint;
        if( active) {
            gradientPaint = new GradientPaint(0, 0, gradColor, 0, center, backgroundColor);
        } else {
            gradientPaint = new GradientPaint(0, 0, backgroundColor, 0, center, gradColor);
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(gradientPaint);
        g2.fillRect(0, 0, width, height);

    }

    protected Color getGradientColor(boolean active) {
        Color color = active ? painterResource.getColor( GRADIENT_COLOR_ACTIVE) : painterResource.getColor( GRADIENT_COLOR);
        return color == null ? SwingUtility.darker(getBackgroundColor( active), 0.75) : color;
    }

}
