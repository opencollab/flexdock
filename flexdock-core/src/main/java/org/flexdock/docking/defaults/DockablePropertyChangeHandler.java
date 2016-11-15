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
        if(dockable==null) {
            return;
        }

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
