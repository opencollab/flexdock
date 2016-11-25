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
package org.flexdock.dockbar.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;

import org.flexdock.dockbar.Dockbar;
import org.flexdock.dockbar.DockbarLabel;
import org.flexdock.dockbar.DockbarManager;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.props.DockablePropertySet;
import org.flexdock.docking.props.PropertyChangeListenerFactory;
import org.flexdock.util.DockingUtility;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DockablePropertyChangeHandler implements PropertyChangeListener {
    public static final DockablePropertyChangeHandler DEFAULT_INSTANCE = new DockablePropertyChangeHandler();

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(!(evt.getSource() instanceof Dockable)) {
            return;
        }

        Dockable dockable = (Dockable) evt.getSource();
        if(!DockingUtility.isMinimized(dockable)) {
            return;
        }

        String pName = evt.getPropertyName();
        DockbarLabel label = getDockbarLabel(dockable);
        if(label==null) {
            return;
        }

        if (DockablePropertySet.TAB_ICON.equals(pName) || DockablePropertySet.DOCKBAR_ICON.equals(pName)) {
            Icon icon = dockable.getDockingProperties().getDockbarIcon();
            if(icon==null) {
                icon = dockable.getDockingProperties().getTabIcon();
            }
            label.setIcon(icon);
        } else if(DockablePropertySet.DESCRIPTION.equals(pName)) {
            label.setText(dockable.getDockingProperties().getDockableDesc());
        }
    }

    private DockbarLabel getDockbarLabel(Dockable dockable) {
        DockbarManager mgr = DockbarManager.getCurrent();
        Dockbar dockbar = mgr==null? null: mgr.getDockbar(dockable);
        return dockbar==null? null: dockbar.getLabel(dockable);
    }

    public static class Factory extends PropertyChangeListenerFactory {
        @Override
        public PropertyChangeListener getListener() {
            return DEFAULT_INSTANCE;
        }
    }
}
