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
package org.flexdock.view;

import java.util.Map;

import org.flexdock.docking.props.PropertyManager;
import org.flexdock.docking.props.RootDockablePropertySet;
import org.flexdock.docking.props.ScopedDockablePropertySet;

/**
 * @author Christopher Butler
 */
public class ViewProps extends ScopedDockablePropertySet {
    public static final String ACTIVE_STATE_LOCKED = "View.ACTIVE_STATE_LOCKED";

    public ViewProps(View view) {
        super(view);
        init();
    }

    public ViewProps(int initialCapacity, View view) {
        super(initialCapacity, view);
        init();
    }

    protected void init() {
        constrainRoot(ACTIVE_STATE_LOCKED, Boolean.FALSE);
    }

    protected void constrainRoot(Object key, Object value) {
        Map map = getRoot();
        if(map instanceof RootDockablePropertySet) {
            ((RootDockablePropertySet)map).constrain(key, value);
        }
    }

    private View getView() {
        return View.getInstance(getDockingId());
    }

    public Boolean isActiveStateLocked() {
        return (Boolean)PropertyManager.getProperty(ACTIVE_STATE_LOCKED, this);
    }

    public void setActiveStateLocked(boolean locked) {
        put(ACTIVE_STATE_LOCKED, locked);
    }

    public void setActive(boolean active) {
        View view = getView();
        if(view==null) {
            super.setActive(active);
            return;
        }

        if(!view.isActiveStateLocked() && active!=isActive().booleanValue()) {
            super.setActive(active);
            if (view.getTitlebar() != null) {
                view.getTitlebar().repaint();
            }
        }
    }
}
