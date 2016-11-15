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
package org.flexdock.docking.state.tree;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.state.LayoutNode;

/**
 * @author Christopher Butler
 */
public abstract class DockingNode extends DefaultMutableTreeNode implements LayoutNode, DockingConstants {

    public Object getUserObject() {
        Object obj = super.getUserObject();
        if(obj==null) {
            obj = getDockingObject();
            setUserObject(obj);
        }
        return obj;
    }

    public abstract Object getDockingObject();

    protected abstract DockingNode shallowClone();

    public Object clone() {
        return deepClone();
    }

    public DockingNode deepClone() {
        DockingNode clone = shallowClone();
        for(Enumeration en=children(); en.hasMoreElements();) {
            DockingNode child = (DockingNode)en.nextElement();
            clone.add(child.deepClone());
        }
        return clone;
    }

}
