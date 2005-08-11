/*
 * Created on Aug 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.docking.defaults;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DefaultDockablePropertyHandler implements PropertyChangeListener {

    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println(evt.getSource() + ": " + evt.getOldValue() + " -> " + evt.getNewValue());
    }
}
