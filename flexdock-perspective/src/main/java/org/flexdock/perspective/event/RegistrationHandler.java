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

import org.flexdock.docking.Dockable;
import org.flexdock.event.Event;
import org.flexdock.event.EventHandler;
import org.flexdock.perspective.Perspective;

/**
 * @author Christopher Butler
 */
public class RegistrationHandler extends EventHandler {

    @Override
    public boolean acceptsEvent(Event evt) {
        return evt instanceof RegistrationEvent;
    }

    @Override
    public boolean acceptsListener(EventListener listener) {
        return listener instanceof RegistrationListener;
    }
    @Override
    public void handleEvent(Event evt, EventListener listener, int eventType) {

        RegistrationEvent event = (RegistrationEvent)evt;
        RegistrationListener consumer = (RegistrationListener)listener;

        switch(eventType) {
            case RegistrationEvent.REGISTERED:
                register(event, consumer);
                break;
            case RegistrationEvent.UNREGISTERED:
                unregister(event, consumer);
                break;
        }
    }

    private void register(RegistrationEvent evt, RegistrationListener listener) {
        if(evt.getSource() instanceof Perspective) {
            listener.perspectiveAdded(evt);
        } else if(evt.getSource() instanceof Dockable) {
            listener.dockableAdded(evt);
        }
    }

    private void unregister(RegistrationEvent evt, RegistrationListener listener) {
        if(evt.getSource() instanceof Perspective) {
            listener.perspectiveRemoved(evt);
        } else if(evt.getSource() instanceof Dockable) {
            listener.dockableRemoved(evt);
        }
    }
}
