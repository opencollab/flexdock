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
package org.flexdock.event;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;

/**
 * @author Christopher Butler
 */
public abstract class EventHandler {
    protected ArrayList globalListeners = new ArrayList();

    /**
     * Tests a given event to determine if this handler can handle that event.
     *
     * @param evt
     *            the event to test.
     * @return {@code true} if this handler handles the event, {@code false}
     *         otherwise.
     */
    public abstract boolean acceptsEvent(Event evt);

    public abstract boolean acceptsListener(EventListener listener);

    public abstract void handleEvent(Event evt, EventListener listener,
                                     int eventType);

    public void addListener(EventListener listener) {
        synchronized (globalListeners) {
            if (listener != null) {
                globalListeners.add(listener);
            }
        }
    }

    public void removeListener(EventListener listener) {
        synchronized (globalListeners) {
            if (listener != null) {
                globalListeners.remove(listener);
            }
        }
    }

    /**
     * This method handles all of the events. First passing each event to
     * {@code handleEvent(Event, EventListener, int)} for every registered
     * listener in the {@link #globalListeners} list. Then, it passes the event
     * to each of the target listeners passed in via {@code targets}.
     *
     * @param evt
     *            the event to process.
     * @param targets
     *            the local listeners to pass the event to.
     */
    public void handleEvent(Event evt, Object[] targets) {
        if (evt == null) {
            return;
        }

        int evtType = evt.getEventType();

        // allow all globally registered listeners to handle the event first
        for (Iterator it = globalListeners.iterator(); it.hasNext();) {
            EventListener listener = (EventListener) it.next();
            handleEvent(evt, listener, evtType);
        }

        // if there were no specified targets for the event, then we can quit
        // now
        if (targets == null) {
            return;
        }

        // for each of the targets, get their local event listeners
        // and dispatch the event to them
        for (int i = 0; i < targets.length; i++) {
            // get the local event listeners
            EventListener[] targetListeners = targets[i] == null ? null
                                              : getListeners(targets[i]);
            if (targetListeners == null) {
                continue;
            }

            // for each local event listener, dispatch the event
            for (int j = 0; j < targetListeners.length; j++) {
                EventListener listener = targetListeners[j];
                if (listener != null && acceptsListener(listener)) {
                    handleEvent(evt, listener, evtType);
                }
            }
        }
    }

    public EventListener[] getListeners(Object eventTarget) {
        return null;
    }
}
