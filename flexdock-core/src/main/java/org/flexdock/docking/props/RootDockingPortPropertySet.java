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

import java.util.HashSet;

import javax.swing.JTabbedPane;

import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.defaults.DefaultRegionChecker;

/**
 * @author Christopher Butler
 */
@SuppressWarnings(value = { "serial" })
public class RootDockingPortPropertySet extends BasicDockingPortPropertySet {
    private static final RegionChecker DEFAULT_REGION_CHECKER = new DefaultRegionChecker();
    private static final Integer DEFAULT_TAB_PLACEMENT = new Integer(JTabbedPane.BOTTOM);
    private static final Float DEFAULT_REGION_INSET = new Float(RegionChecker.DEFAULT_REGION_SIZE);

    private HashSet constraints;

    public RootDockingPortPropertySet() {
        super(5);
        constraints = new HashSet(5);

        initConstraint(REGION_CHECKER, DEFAULT_REGION_CHECKER);
        initConstraint(SINGLE_TABS, Boolean.FALSE);
        initConstraint(TAB_PLACEMENT, DEFAULT_TAB_PLACEMENT);

        initConstraint(REGION_SIZE_NORTH, DEFAULT_REGION_INSET);
        initConstraint(REGION_SIZE_SOUTH, DEFAULT_REGION_INSET);
        initConstraint(REGION_SIZE_EAST, DEFAULT_REGION_INSET);
        initConstraint(REGION_SIZE_WEST, DEFAULT_REGION_INSET);
    }

    private void initConstraint(Object key, Object value) {
        put(key, value);
        constraints.add(key);
    }

    public synchronized Object remove(Object key) {
        return constraints.contains(key)? null: super.remove(key);
    }
}
