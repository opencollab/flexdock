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
package org.flexdock.docking.adapter;

import java.awt.Component;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

/**
 * @author Christopher Butler
 */
//TODO shouldn't this implement Dockable?
public class DockingAdapter {
    private static final Class[] EMPTY_PARAMS = {};
    private static final Object[] EMPTY_ARGS = {};

    private Component component;
    private AdapterMapping mapping;

    DockingAdapter(Component comp, AdapterMapping mapping) {
        component = comp;
        this.mapping = mapping;
    }

    public Component getComponent() {
        return component;
    }

    public List getDragSources() {
        // first, try to get a list of drag sources
        Object obj = get(component, mapping.getDragSourceList());
        if(obj instanceof List) {
            return (List)obj;
        }

        // if we couldn't find a list, then try to get an individual drag source
        // and create a List from it
        obj = get(component, mapping.getDragSource());
        if(obj instanceof Component) {
            ArrayList list = new ArrayList(1);
            list.add(obj);
            return list;
        }

        // if both attempts failed, then return null
        return null;
    }

    public Set getFrameDragSources() {
        // first, try to get a set of frame drag sources
        Object obj = get(component, mapping.getFrameDragSourceList());
        if(obj instanceof Set) {
            return (Set)obj;
        }

        // if we couldn't find a set, then try to get an individual
        // frame drag source and create a Set from it
        obj = get(component, mapping.getFrameDragSource());
        if(obj instanceof Component) {
            HashSet set = new HashSet(1);
            set.add(obj);
            return set;
        }

        // if both attempts failed, then return null
        return null;
    }

    public String getPersistentId() {
        Object obj = get(component, mapping.getPersistentId());
        return obj instanceof String? (String)obj: null;
    }

    public Icon getDockbarIcon() {
        Object obj = get(component, mapping.getDockbarIcon());
        return obj instanceof Icon? (Icon)obj: null;
    }

    public String getTabText() {
        Object obj = get(component, mapping.getTabText());
        return obj instanceof String? (String)obj: null;
    }

    private Object get(Object obj, String methodName) {
        if(obj==null || methodName==null) {
            return null;
        }

        try {
            Class c = obj.getClass();
            Method method = c.getMethod(methodName, EMPTY_PARAMS);
            return method.invoke(obj, EMPTY_ARGS);
        } catch(Throwable t) {
            // ignore.
            return null;
        }
    }
}
