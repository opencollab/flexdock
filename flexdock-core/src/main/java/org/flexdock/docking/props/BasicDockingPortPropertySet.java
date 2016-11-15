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

import java.util.Map;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.RegionChecker;
import org.flexdock.util.TypedHashtable;

/**
 * @author Christopher Butler
 */
@SuppressWarnings(value = { "serial" })
public class BasicDockingPortPropertySet extends TypedHashtable implements DockingPortPropertySet, DockingConstants {

    public static String getRegionInsetKey(String region) {
        if(NORTH_REGION.equals(region)) {
            return REGION_SIZE_NORTH;
        }
        if(SOUTH_REGION.equals(region)) {
            return REGION_SIZE_SOUTH;
        }
        if(EAST_REGION.equals(region)) {
            return REGION_SIZE_EAST;
        }
        if(WEST_REGION.equals(region)) {
            return REGION_SIZE_WEST;
        }
        return null;
    }

    public BasicDockingPortPropertySet() {
        super();
    }

    public BasicDockingPortPropertySet(int initialCapacity) {
        super(initialCapacity);
    }

    public BasicDockingPortPropertySet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public BasicDockingPortPropertySet(Map t) {
        super(t);
    }







    @Override
    public RegionChecker getRegionChecker() {
        return (RegionChecker)get(REGION_CHECKER);
    }

    @Override
    public Boolean isSingleTabsAllowed() {
        return getBoolean(SINGLE_TABS);
    }

    @Override
    public Integer getTabPlacement() {
        return getInt(TAB_PLACEMENT);
    }

    @Override
    public Float getRegionInset(String region) {
        String key = getRegionInsetKey(region);
        return key==null? null: (Float)get(key);
    }


    @Override
    public void setRegionChecker(RegionChecker checker) {
        put(REGION_CHECKER, checker);
    }

    @Override
    public void setSingleTabsAllowed(boolean allowed) {
        put(SINGLE_TABS, allowed);
    }

    @Override
    public void setTabPlacement(int placement) {
        put(TAB_PLACEMENT, placement);
    }

    @Override
    public void setRegionInset(String region, float inset) {
        String key = getRegionInsetKey(region);
        if(key!=null) {
            put(key, new Float(inset));
        }
    }
}
