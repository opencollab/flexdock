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
package org.flexdock.dockbar.layout;

import java.awt.Insets;

import javax.swing.border.Border;

import org.flexdock.docking.state.MinimizationManager;
import org.flexdock.plaf.common.border.CompoundEmptyBorder;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InsetBorder extends CompoundEmptyBorder {
    private boolean leftEdge;
    private boolean rightEdge;
    private boolean bottomEdge;

    protected InsetBorder(Border outer, Border inner, boolean emptyInner) {
        super(outer, inner, emptyInner);
    }

    public static InsetBorder createBorder(Border border, boolean inner) {
        return createBorder(border, inner, null);
    }

    public static InsetBorder createBorder(Border border, boolean inner, Insets base) {
        if(base==null) {
            base = new Insets(0, 0, 0, 0);
        }

        MutableEmptyBorder empty = new MutableEmptyBorder(base.top, base.left, base.bottom, base.right);
        if(inner) {
            return new InsetBorder(border, empty, inner);
        }
        return new InsetBorder(empty, border, inner);
    }

    public void toggleEdge(int edge, boolean on) {
        switch(edge) {
            case MinimizationManager.LEFT:
                leftEdge = on;
                break;
            case MinimizationManager.RIGHT:
                rightEdge = on;
                break;
            case MinimizationManager.BOTTOM:
                bottomEdge = on;
                break;
        }
    }
    /**
     * @return Returns the bottomEdge.
     */
    public boolean isBottomEdge() {
        return bottomEdge;
    }
    /**
     * @param bottomEdge The bottomEdge to set.
     */
    public void setBottomEdge(boolean bottomEdge) {
        this.bottomEdge = bottomEdge;
    }
    /**
     * @return Returns the leftEdge.
     */
    public boolean isLeftEdge() {
        return leftEdge;
    }
    /**
     * @param leftEdge The leftEdge to set.
     */
    public void setLeftEdge(boolean leftEdge) {
        this.leftEdge = leftEdge;
    }
    /**
     * @return Returns the rightEdge.
     */
    public boolean isRightEdge() {
        return rightEdge;
    }
    /**
     * @param rightEdge The rightEdge to set.
     */
    public void setRightEdge(boolean rightEdge) {
        this.rightEdge = rightEdge;
    }

    void clearEdges() {
        setLeftEdge(false);
        setRightEdge(false);
        setBottomEdge(false);
    }

    @Override
    public boolean setEmptyInsets(int top, int left, int bottom, int right) {
        left = isLeftEdge()? left: 0;
        right = isRightEdge()? right: 0;
        bottom = isBottomEdge()? bottom: 0;
        return super.setEmptyInsets(top, left, bottom, right);
    }
}
