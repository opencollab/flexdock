/*
 * Created on May 17, 2005
 */
package org.flexdock.event;


/**
 * @author Christopher Butler
 */
public class RegistrationEvent extends Event {
    public static final int REGISTERED = 0;
    public static final int UNREGISTERED = 1;

    private Object owner;

    public RegistrationEvent(Object src, Object owner, int evtType) {
        super(src, evtType);
        this.owner = owner;
    }

    public RegistrationEvent(Object src, Object owner, boolean registered) {
        this(src, owner, registered? REGISTERED: UNREGISTERED);
    }

    public Object getOwner() {
        return owner;
    }
}
