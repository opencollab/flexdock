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
package org.flexdock.dockbar;

import java.awt.Component;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.state.MinimizationManager;

/**
 * @author Christopher Butler
 */
public class MinimizationAdapter implements MinimizationManager {

    static {
        init();
    }

    private static void init() {
        // make sure DockbarManager is initialized
        Class c = DockbarManager.class;
    }

    @Override
    public boolean close(Dockable dockable) {
        DockbarManager mgr = DockbarManager.getCurrent(dockable);
        return mgr==null? false: mgr.remove(dockable);
    }

    @Override
    public void preview(Dockable dockable, boolean locked) {
        DockbarManager.activate(dockable, true);
    }

    @Override
    public void setMinimized(Dockable dockable, boolean minimizing, Component window, int edge) {
        DockbarManager mgr = DockbarManager.getInstance(window);
        if(mgr==null) {
            return;
        }

        if(minimizing) {
            // if minimizing, send to the dockbar
            if(edge==MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT) {
                mgr.minimize(dockable);
            } else {
                mgr.minimize(dockable, edge);
            }
        } else {
            // otherwise, restore from the dockbar
            mgr.restore(dockable);
        }
    }
}
