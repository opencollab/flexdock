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

import java.util.HashMap;
import java.util.Map;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.RegionChecker;

/**
 * @author Christopher Butler
 */
public class BasicDockingPortPropertySet implements DockingPortPropertySet, DockingConstants {
    private static final Float DEFAULT_REGION_INSET = RegionChecker.DEFAULT_REGION_SIZE;

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

    private RegionChecker regionChecker;
    private boolean singleTabsAllowed;
    private int tabPlacement;
    private Map<String, Float> regionInsetMap = new HashMap<String, Float>();

    public BasicDockingPortPropertySet() {
        regionInsetMap.put(REGION_SIZE_NORTH, DEFAULT_REGION_INSET);
        regionInsetMap.put(REGION_SIZE_SOUTH, DEFAULT_REGION_INSET);
        regionInsetMap.put(REGION_SIZE_EAST, DEFAULT_REGION_INSET);
        regionInsetMap.put(REGION_SIZE_WEST, DEFAULT_REGION_INSET);
    }

    public BasicDockingPortPropertySet(RegionChecker regionChecker,
            boolean singleTabsAllowed,
            int tabPlacement) {
        this.regionChecker = regionChecker;
        this.singleTabsAllowed = singleTabsAllowed;
        this.tabPlacement = tabPlacement;
    }

    @Override
    public RegionChecker getRegionChecker() {
        return regionChecker;
    }

    @Override
    public Boolean isSingleTabsAllowed() {
        return singleTabsAllowed;
    }

    @Override
    public Integer getTabPlacement() {
        return tabPlacement;
    }

    @Override
    public Float getRegionInset(String region) {
        String key = getRegionInsetKey(region);
        return regionInsetMap.get(key);
    }

    @Override
    public void setRegionChecker(RegionChecker checker) {
        this.regionChecker = checker;
    }

    @Override
    public void setSingleTabsAllowed(boolean allowed) {
        this.singleTabsAllowed = allowed;
    }

    @Override
    public void setTabPlacement(int placement) {
        this.tabPlacement = placement;
    }

    @Override
    public void setRegionInset(String region, float inset) {
        String key = getRegionInsetKey(region);
        if(key!=null) {
            regionInsetMap.put(key, inset);
        }
    }
}
