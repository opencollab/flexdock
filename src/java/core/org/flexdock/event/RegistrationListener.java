/*
 * Created on May 17, 2005
 */
package org.flexdock.event;


/**
 * @author Christopher Butler
 */
public interface RegistrationListener {
    public void registered(RegistrationEvent evt);
    public void unregistered(RegistrationEvent evt);
}
