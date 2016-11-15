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
package org.flexdock.dockbar.event;

import org.flexdock.docking.Dockable;
import org.flexdock.event.Event;

/**
 * @author Christopher Butler
 */
public class DockbarEvent extends Event {
    public static final int EXPANDED = 0;
    public static final int LOCKED = 1;
    public static final int COLLAPSED = 2;

    public static final int MINIMIZE_STARTED = 10;
    public static final int MINIMIZE_COMPLETED = 11;

    private int edge;
    private boolean consumed;

    public DockbarEvent(Dockable dockable, int type, int edge) {
        super(dockable, type);
        this.edge = edge;
    }

    public int getEdge() {
        return edge;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void consume() {
        consumed = true;
    }
}
