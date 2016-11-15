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

import org.flexdock.docking.RegionChecker;

/**
 * @author Christopher Butler
 */
public interface DockingPortPropertySet {
    public static final String REGION_CHECKER = "DockingPort.REGION_CHECKER";
    public static final String SINGLE_TABS = "DockingPort.SINGLE_TABS";
    public static final String TAB_PLACEMENT = "DockingPort.TAB_PLACEMENT";

    public static final String REGION_SIZE_NORTH = "DockingPort.REGION_SIZE_NORTH";
    public static final String REGION_SIZE_SOUTH = "DockingPort.REGION_SIZE_SOUTH";
    public static final String REGION_SIZE_EAST = "DockingPort.REGION_SIZE_EAST";
    public static final String REGION_SIZE_WEST = "DockingPort.REGION_SIZE_WEST";

    public RegionChecker getRegionChecker();

    public Boolean isSingleTabsAllowed();

    public Integer getTabPlacement();

    public Float getRegionInset(String region);

    public void setRegionChecker(RegionChecker checker);

    public void setSingleTabsAllowed(boolean allowed);

    public void setTabPlacement(int placement);

    public void setRegionInset(String region, float inset);

}
