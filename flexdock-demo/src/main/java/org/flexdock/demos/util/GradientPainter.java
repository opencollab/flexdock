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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

/**
 * @author Christopher Butler
 */
public class GradientPainter {
    private Color startColor;
    private Color midColor;


    public GradientPainter(Color start, Color mid) {
        startColor = start;
        midColor = mid;
    }

    public void paintGradient(JComponent comp, Graphics g) {
        int h = comp.getHeight();
        int w = comp.getWidth();
        int mid = w/2;

        Color bgColor = comp.getBackground();
        Color start = startColor==null? bgColor: startColor;
        Color middle = midColor==null? bgColor: midColor;

        GradientPaint firstHalf = new GradientPaint(0, 0, start, mid, 0, middle);
        GradientPaint secondHalf = new GradientPaint(mid, 0, middle, w, 0, bgColor);

        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(firstHalf);
        g2.fillRect(0, 0, mid, h);
        g2.setPaint(secondHalf);
        g2.fillRect(mid-1, 0, mid, h);
    }

    public Color getMidColor() {
        return midColor;
    }
    public void setMidColor(Color midColor) {
        this.midColor = midColor;
    }
    public Color getStartColor() {
        return startColor;
    }
    public void setStartColor(Color startColor) {
        this.startColor = startColor;
    }
}
