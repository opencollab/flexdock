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
package org.flexdock.docking.event;

import java.util.EventListener;

/**
 * @author Kevin Duffey
 * @author Christopher Butler
 */
public interface DockingListener extends EventListener {

    /**
     * Fired when docking of a <code>Dockable</code> has completed.
     *
     * @param evt
     *            the <code>DockingEvent</code> event which provides the
     *            source Dockable, the old DockingPort and the new DockingPort
     */
    void dockingComplete(DockingEvent evt);

    /**
     * Fired when docking of a <code>Dockable</code> is canceled during the operation.
     *
     * @param evt
     *            the <code>DockingEvent</code> event which provides the
     *            source Dockable, the old DockingPort and the new DockingPort
     */
    void dockingCanceled(DockingEvent evt);


    /**
     * Fired when the dragging of a <code>Dockable</code> has begun.
     *
     * @param evt
     *            the <code>DockingEvent</code> event which provides the
     *            source Dockable, the old DockingPort and the new DockingPort
     */
    void dragStarted(DockingEvent evt);


    /**
     * Fired when the dropping of a <code>Dockable</code> has begun at the release
     * of a drag-operation.
     *
     * @param evt
     *            the <code>DockingEvent</code> event which provides the
     *            source Dockable, the old DockingPort and the new DockingPort
     */
    void dropStarted(DockingEvent evt);

    void undockingComplete(DockingEvent evt);

    void undockingStarted(DockingEvent evt);

    class Stub implements DockingListener {

        @Override
        public void dockingCanceled(DockingEvent evt) {}

        @Override
        public void dockingComplete(DockingEvent evt) {}

        @Override
        public void dragStarted(DockingEvent evt) {}

        @Override
        public void dropStarted(DockingEvent evt) {}

        @Override
        public void undockingComplete(DockingEvent evt) {}

        @Override
        public void undockingStarted(DockingEvent evt) {}

    }

}