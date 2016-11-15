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

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.RegionChecker;

/**
 * @author Christopher Butler
 */
@SuppressWarnings(value = { "serial" })
public class RootDockablePropertySet extends BasicDockablePropertySet {
    private static final Float DEFAULT_REGION_INSETS = new Float(RegionChecker.DEFAULT_REGION_SIZE);
    private static final Float DEFAULT_SIBLING_INSETS = new Float(DockingManager.getDefaultSiblingSize());
    public static final Float DEFAULT_DRAG_THRESHOLD = new Float(4);
    public static final Float DEFAULT_PREVIEW_SIZE = new Float(0.3);

    private HashSet constraints;

    public RootDockablePropertySet(Dockable dockable) {
        super(5, dockable);
        constraints = new HashSet(5);

        constrain(DESCRIPTION, "null");
        constrain(DOCKING_ENABLED, Boolean.TRUE);
        constrain(MOUSE_MOTION_DRAG_BLOCK, Boolean.TRUE);
        constrain(ACTIVE, Boolean.FALSE);

        constrain(REGION_SIZE_NORTH, DEFAULT_REGION_INSETS);
        constrain(REGION_SIZE_SOUTH, DEFAULT_REGION_INSETS);
        constrain(REGION_SIZE_EAST, DEFAULT_REGION_INSETS);
        constrain(REGION_SIZE_WEST, DEFAULT_REGION_INSETS);

        constrain(SIBLING_SIZE_NORTH, DEFAULT_SIBLING_INSETS);
        constrain(SIBLING_SIZE_SOUTH, DEFAULT_SIBLING_INSETS);
        constrain(SIBLING_SIZE_EAST, DEFAULT_SIBLING_INSETS);
        constrain(SIBLING_SIZE_WEST, DEFAULT_SIBLING_INSETS);

        constrain(TERRITORY_BLOCKED_NORTH, Boolean.FALSE);
        constrain(TERRITORY_BLOCKED_SOUTH, Boolean.FALSE);
        constrain(TERRITORY_BLOCKED_EAST, Boolean.FALSE);
        constrain(TERRITORY_BLOCKED_WEST, Boolean.FALSE);
        constrain(TERRITORY_BLOCKED_CENTER, Boolean.FALSE);

        constrain(DRAG_THRESHOLD, DEFAULT_DRAG_THRESHOLD);
        constrain(PREVIEW_SIZE, DEFAULT_PREVIEW_SIZE);
    }

    public void constrain(Object key, Object value) {
        if(key!=null && value!=null) {
            put(key, value);
            constraints.add(key);
        }
    }


    public synchronized Object remove(Object key) {
        return constraints.contains(key)? null: super.remove(key);
    }
}
