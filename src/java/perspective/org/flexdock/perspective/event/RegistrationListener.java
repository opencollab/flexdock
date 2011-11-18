/*
 * Created on May 17, 2005
 */
package org.flexdock.perspective.event;

/**
 * @author Christopher Butler
 */
public interface RegistrationListener extends org.flexdock.event.RegistrationListener {
    public void perspectiveAdded(RegistrationEvent evt);
    public void dockableAdded(RegistrationEvent evt);

    public void perspectiveRemoved(RegistrationEvent evt);
    public void dockableRemoved(RegistrationEvent evt);
}
