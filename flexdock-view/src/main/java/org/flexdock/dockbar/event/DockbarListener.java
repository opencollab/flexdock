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

import java.util.EventListener;

/**
 * @author Christopher Butler
 */
public interface DockbarListener extends EventListener {

    //comment public is redundant in interfaces since all of the methods are by default public

    void dockableExpanded(DockbarEvent evt);
    void dockableLocked(DockbarEvent evt);
    void dockableCollapsed(DockbarEvent evt);

    void minimizeStarted(DockbarEvent evt);
    void minimizeCompleted(DockbarEvent evt);

    static class Stub implements DockbarListener {

        public void dockableExpanded(DockbarEvent evt) {}

        public void dockableLocked(DockbarEvent evt) {}

        public void dockableCollapsed(DockbarEvent evt) {}

        public void minimizeStarted(DockbarEvent evt) {}

        public void minimizeCompleted(DockbarEvent evt) {}

    }

}
