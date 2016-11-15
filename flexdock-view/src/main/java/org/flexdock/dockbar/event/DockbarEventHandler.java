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

import org.flexdock.event.Event;
import org.flexdock.event.EventHandler;

/**
 * An event handler to match {@code DockbarEvent} types to the appropriate
 * {@code DockbarListener} method.
 *
 * @author Christopher Butler
 */
public class DockbarEventHandler extends EventHandler {

    /**
     * This class accepts {@code DockbarEvent}s.
     *
     * @param evt
     */
    @Override
    public boolean acceptsEvent(Event evt) {
        return evt instanceof DockbarEvent;
    }

    @Override
    public boolean acceptsListener(EventListener listener) {
        return listener instanceof DockbarListener;
    }

    @Override
    public void handleEvent(Event event, EventListener consumer, int eventType) {
        DockbarEvent evt = (DockbarEvent) event;
        DockbarListener listener = (DockbarListener) consumer;

        switch (eventType) {
            case DockbarEvent.EXPANDED:
                listener.dockableExpanded(evt);
                break;
            case DockbarEvent.LOCKED:
                listener.dockableLocked(evt);
                break;
            case DockbarEvent.COLLAPSED:
                listener.dockableCollapsed(evt);
                break;
            case DockbarEvent.MINIMIZE_STARTED:
                listener.minimizeStarted(evt);
                break;
            case DockbarEvent.MINIMIZE_COMPLETED:
                listener.minimizeCompleted(evt);
                break;
        }
    }
}
