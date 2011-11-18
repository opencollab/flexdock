/*
 * Created on May 17, 2005
 */
package org.flexdock.perspective.event;

/**
 * @author Christopher Butler
 */
public class RegistrationEvent extends org.flexdock.event.RegistrationEvent {

    public RegistrationEvent(Object src, Object owner, boolean registered) {
        super(src, owner, registered);
    }
}
