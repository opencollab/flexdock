/*
 * Created on Aug 11, 2005
 */
package org.flexdock.docking.defaults;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.activation.ActiveDockableTracker;
import org.flexdock.docking.props.DockablePropertySet;
import org.flexdock.docking.props.PropertyChangeListenerFactory;

/**
 * @author Christopher Butler
 */
public class DockablePropertyChangeHandler implements PropertyChangeListener {
    public static final DockablePropertyChangeHandler DEFAULT_INSTANCE = new DockablePropertyChangeHandler();

    public void propertyChange(PropertyChangeEvent evt) {
        Dockable dockable = evt.getSource() instanceof Dockable? (Dockable)evt.getSource(): null;
        if(dockable==null)
            return;

        if (evt.getPropertyName().equals(DockablePropertySet.TAB_ICON)
                || evt.getPropertyName().equals(DockablePropertySet.DESCRIPTION)) {
            if (evt.getSource() instanceof Dockable) {
                DockingPort dockingPort = dockable.getDockingPort();
                if (dockingPort instanceof DefaultDockingPort) {
                    ((DefaultDockingPort) dockingPort).updateTab(dockable);
                }
            }
        } else if (DockablePropertySet.ACTIVE.equals(evt.getPropertyName())) {
            handleActivationChange(evt, dockable);
        }
    }

    private void handleActivationChange(PropertyChangeEvent evt, Dockable dockable) {
        if(Boolean.TRUE==evt.getNewValue() && ActiveDockableTracker.getActiveDockable()!=dockable) {
            ActiveDockableTracker.requestDockableActivation(dockable.getComponent(), true);
        }
    }

    public static class Factory extends PropertyChangeListenerFactory {
        public PropertyChangeListener getListener() {
            return DEFAULT_INSTANCE;
        }
    }
}
