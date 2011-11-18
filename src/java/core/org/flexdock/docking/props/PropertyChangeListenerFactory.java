/*
 * Created on Aug 12, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.docking.props;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author Christopher Butler
 */
public abstract class PropertyChangeListenerFactory {
    private static final Vector FACTORIES = new Vector();

    public static void addFactory(PropertyChangeListenerFactory factory) {
        if(factory!=null)
            FACTORIES.add(factory);
    }

    public static void removeFactory(PropertyChangeListenerFactory factory) {
        if(factory!=null)
            FACTORIES.remove(factory);
    }

    public static PropertyChangeListener[] getListeners() {
        ArrayList list = new ArrayList(FACTORIES.size());
        for(Iterator it=FACTORIES.iterator(); it.hasNext();) {
            PropertyChangeListenerFactory factory = (PropertyChangeListenerFactory)it.next();
            PropertyChangeListener listener = factory.getListener();
            if(listener!=null)
                list.add(listener);
        }
        return (PropertyChangeListener[])list.toArray(new PropertyChangeListener[list.size()]);
    }

    public abstract PropertyChangeListener getListener();
}
