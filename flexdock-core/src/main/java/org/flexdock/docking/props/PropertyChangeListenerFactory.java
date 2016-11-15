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
        if(factory!=null) {
            FACTORIES.add(factory);
        }
    }

    public static void removeFactory(PropertyChangeListenerFactory factory) {
        if(factory!=null) {
            FACTORIES.remove(factory);
        }
    }

    public static PropertyChangeListener[] getListeners() {
        ArrayList list = new ArrayList(FACTORIES.size());
        for(Iterator it=FACTORIES.iterator(); it.hasNext();) {
            PropertyChangeListenerFactory factory = (PropertyChangeListenerFactory)it.next();
            PropertyChangeListener listener = factory.getListener();
            if(listener!=null) {
                list.add(listener);
            }
        }
        return (PropertyChangeListener[])list.toArray(new PropertyChangeListener[list.size()]);
    }

    public abstract PropertyChangeListener getListener();
}
