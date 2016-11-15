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
package org.flexdock.plaf.resources.paint;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.UIManager;

/**
 * @author Claudio Romano
 */
public class DefaultPainter implements Painter {
    public static final Color DEFAULT_BG_COLOR = UIManager.getColor( "Panel.background");
    public static final Color DEFAULT_BG_COLOR_ACTIVE = UIManager.getColor( "InternalFrame.activeTitleBackground");

    protected PainterResource painterResource;

    public void paint(Graphics g, int width, int height, boolean active, JComponent titlebar) {
        Color c = getBackgroundColor(active);

        g.setColor(c);
        g.fillRect(0, 0, width, height);

    }

    protected Color getBackgroundColor(boolean active) {
        return active ? getBackgroundColorActive() :  getBackgroundColorInactive();
    }

    protected Color getBackgroundColorInactive() {
        return painterResource.getBgColor()==null ? DEFAULT_BG_COLOR : painterResource.getBgColor();
    }

    protected Color getBackgroundColorActive( ) {
        return painterResource.getBgColorActive()==null ? DEFAULT_BG_COLOR_ACTIVE : painterResource.getBgColorActive();
    }

    /**
     * @return Returns the painterResource.
     */
    public PainterResource getPainterResource() {
        return painterResource;
    }

    /**
     * @param painterResource The painterResource to set.
     */
    public void setPainterResource(PainterResource painterResource) {
        this.painterResource = painterResource;
    }

}