/*
 * Created on May 17, 2005
 */
package org.flexdock.event;

import java.util.EventListener;


/**
 * @author Christopher Butler
 */
public class RegistrationHandler extends EventHandler {

    public boolean acceptsEvent(Event evt) {
        return evt instanceof RegistrationEvent;
    }

    public boolean acceptsListener(EventListener listener) {
        return listener instanceof RegistrationListener;
    }

    public void handleEvent(Event evt, EventListener listener, int eventType) {
        RegistrationEvent regEvt = (RegistrationEvent)evt;
        RegistrationListener regListener = (RegistrationListener)listener;

        switch(eventType) {
        case RegistrationEvent.REGISTERED:
            regListener.registered(regEvt);
            break;
        case RegistrationEvent.UNREGISTERED:
            regListener.unregistered(regEvt);
            break;
        }
    }
}
