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
package org.flexdock.docking.state;

import java.awt.Component;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;

/**
 * @author Christopher Butler
 */
public interface MinimizationManager {

    MinimizationManager DEFAULT_STUB = new Stub();

    int UNSPECIFIED_LAYOUT_CONSTRAINT = -1;

    int TOP = DockingConstants.TOP;

    int LEFT = DockingConstants.LEFT;

    int BOTTOM = DockingConstants.BOTTOM;

    int RIGHT = DockingConstants.RIGHT;

    int CENTER = DockingConstants.CENTER;

    boolean close(Dockable dockable);

    void preview(Dockable dockable, boolean locked);

    void setMinimized(Dockable dockable, boolean minimized, Component window, int constraint);

    class Stub implements MinimizationManager {
        public boolean close(Dockable dockable) {
            return false;
        }

        public void preview(Dockable dockable, boolean locked) {}

        public void setMinimized(Dockable dockable, boolean minimized, Component window, int edge) {}

    }

}
