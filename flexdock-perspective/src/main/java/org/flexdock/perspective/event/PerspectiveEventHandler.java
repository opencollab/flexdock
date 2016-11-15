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
package org.flexdock.perspective.event;

import java.util.EventListener;



import org.flexdock.event.Event;
import org.flexdock.event.EventHandler;

/**
 * @author Christopher Butler
 */
public class PerspectiveEventHandler extends EventHandler {

    private static final PerspectiveEventHandler SINGLETON = new PerspectiveEventHandler();

    public static PerspectiveEventHandler getInstance() {
        return SINGLETON;
    }

    private PerspectiveEventHandler() {

    }

    public boolean acceptsEvent(Event evt) {
        return evt instanceof PerspectiveEvent;
    }

    public boolean acceptsListener(EventListener listener) {
        return listener instanceof PerspectiveListener;
    }

    public void handleEvent(Event evt, EventListener listener, int eventType) {
        PerspectiveEvent event = (PerspectiveEvent)evt;
        PerspectiveListener consumer = (PerspectiveListener)listener;
        switch(eventType) {
            case PerspectiveEvent.CHANGED:
                consumer.perspectiveChanged(event);
                break;
            case PerspectiveEvent.RESET:
                consumer.perspectiveReset(event);
                break;
            default:
                break;
        }
    }

    public PerspectiveListener[] getListeners() {
        synchronized(globalListeners) {
            return (PerspectiveListener[])globalListeners.toArray(new PerspectiveListener[0]);
        }
    }
}
