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

import java.awt.Insets;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 * @author Christopher Butler
 */
public class CompoundEmptyBorder extends CompoundBorder {
    protected static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
    protected boolean inner;

    public static CompoundEmptyBorder create(Border border, boolean inner) {
        return create(border, inner, null);
    }

    public static CompoundEmptyBorder create(Border border, boolean inner, Insets base) {
        if(base==null) {
            base = new Insets(0, 0, 0, 0);
        }

        MutableEmptyBorder empty = new MutableEmptyBorder(base.top, base.left, base.bottom, base.right);
        if(inner) {
            return new CompoundEmptyBorder(border, empty, inner);
        }
        return new CompoundEmptyBorder(empty, border, inner);
    }

    protected CompoundEmptyBorder(Border outer, Border inner, boolean emptyInner) {
        super(outer, inner);
        this.inner = emptyInner;
    }

    public boolean setEmptyInsets(Insets insets) {
        if(insets==null) {
            insets = EMPTY_INSETS;
        }
        return setEmptyInsets(insets.top, insets.left, insets.bottom, insets.right);
    }

    public boolean setEmptyInsets(int top, int left, int bottom, int right) {
        Border border = inner? getInsideBorder(): getOutsideBorder();
        return ((MutableEmptyBorder)border).updateInsets(top, left, bottom, right);
    }

    public Insets getEmptyInsets() {
        Border border = inner? getInsideBorder(): getOutsideBorder();
        MutableEmptyBorder empty = (MutableEmptyBorder)border;
        return empty.getInsetsCopy();
    }

    public Border getWrappedBorder() {
        return inner? getOutsideBorder(): getInsideBorder();
    }

    protected static class MutableEmptyBorder extends EmptyBorder {
        public MutableEmptyBorder(int top, int left, int bottom, int right) {
            super(top, left, bottom, right);
        }

        public MutableEmptyBorder(Insets borderInsets) {
            super(borderInsets);
        }

        private boolean updateInsets(int top, int left, int bottom, int right) {
            boolean changed = this.top!=top || this.left!=left || this.bottom!=bottom || this.right!=right;
            this.top = top;
            this.bottom = bottom;
            this.left = left;
            this.right = right;
            return changed;
        }

        private Insets getInsetsCopy() {
            return new Insets(top, left, bottom, right);
        }
    }
}
