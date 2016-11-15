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
import org.flexdock.perspective.Layout;

/**
 * @author Christopher Butler
 */
public class LayoutEventHandler extends EventHandler {

    @Override
    public boolean acceptsEvent(Event evt) {
        return evt instanceof LayoutEvent;
    }
    @Override
    public boolean acceptsListener(EventListener listener) {
        return listener instanceof LayoutListener;
    }

    @Override
    public void handleEvent(Event evt, EventListener listener, int eventType) {
        LayoutEvent event = (LayoutEvent)evt;
        LayoutListener consumer = (LayoutListener)listener;

        switch(eventType) {
            case LayoutEvent.DOCKABLE_HIDDEN:
                consumer.dockableHidden(event);
                break;
            case LayoutEvent.DOCKABLE_RESTORED:
                consumer.dockableDisplayed(event);
                break;
            case LayoutEvent.LAYOUT_APPLIED:
                consumer.layoutApplied(event);
                break;
            case LayoutEvent.LAYOUT_EMPTIED:
                consumer.layoutEmptied(event);
                break;
        }
    }

    @Override
    public EventListener[] getListeners(Object eventTarget) {
        return eventTarget instanceof Layout?
               ((Layout)eventTarget).getListeners(): null;
    }
}
