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

import javax.swing.Icon;

/**
 * @author Christopher Butler
 */
public interface DockablePropertySet {
    public static final String DESCRIPTION = "Dockable.DESCRIPTION";
    public static final String DOCKING_ENABLED = "Dockable.DOCKING_ENABLED";
    public static final String MOUSE_MOTION_DRAG_BLOCK = "Dockable.MOUSE_MOTION_DRAG_BLOCK";
    public static final String DRAG_THRESHOLD = "Dockable.DRAG_THRESHOLD";

    public static final String REGION_SIZE_NORTH = "Dockable.REGION_SIZE_NORTH";
    public static final String SIBLING_SIZE_NORTH = "Dockable.SIBLING_SIZE_NORTH";
    public static final String TERRITORY_BLOCKED_NORTH = "Dockable.TERRITORY_BLOCKED_NORTH";

    public static final String REGION_SIZE_SOUTH = "Dockable.REGION_SIZE_SOUTH";
    public static final String SIBLING_SIZE_SOUTH = "Dockable.SIBLING_SIZE_SOUTH";
    public static final String TERRITORY_BLOCKED_SOUTH = "Dockable.TERRITORY_BLOCKED_SOUTH";

    public static final String REGION_SIZE_EAST = "Dockable.REGION_SIZE_EAST";
    public static final String SIBLING_SIZE_EAST = "Dockable.SIBLING_SIZE_EAST";
    public static final String TERRITORY_BLOCKED_EAST = "Dockable.TERRITORY_BLOCKED_EAST";

    public static final String REGION_SIZE_WEST = "Dockable.REGION_SIZE_WEST";
    public static final String SIBLING_SIZE_WEST = "Dockable.SIBLING_SIZE_WEST";
    public static final String TERRITORY_BLOCKED_WEST = "Dockable.TERRITORY_BLOCKED_WEST";

    public static final String TERRITORY_BLOCKED_CENTER = "Dockable.TERRITORY_BLOCKED_CENTER";
    public static final String DOCKBAR_ICON = "Dockable.DOCKBAR_ICON";
    public static final String TAB_ICON = "Dockable.TAB_ICON";
    public static final String PREVIEW_SIZE = "Dockable.PREVIEW_SIZE";

    public static final String ACTIVE = "Dockable.ACTIVE";



    public String getDockableDesc();

    public Boolean isDockingEnabled();

    public Boolean isMouseMotionListenersBlockedWhileDragging();

    public Float getRegionInset(String region);

    public Float getSiblingSize(String region);

    public Boolean isTerritoryBlocked(String region);

    public Float getDragThreshold();

    public Icon getDockbarIcon();

    public Icon getTabIcon();

    public Float getPreviewSize();

    public String getDockingId();

    public Boolean isActive();





    public void setDockableDesc(String desc);

    public void setDockingEnabled(boolean enabled);

    public void setMouseMotionListenersBlockedWhileDragging(boolean blocked);

    public void setRegionInset(String region, float inset);

    public void setSiblingSize(String region, float size);

    public void setTerritoryBlocked(String region, boolean blocked);

    public void setDragTheshold(float threshold);

    public void setDockbarIcon(Icon icon);

    public void setTabIcon(Icon icon);

    public void setPreviewSize(float size);

    public void setActive(boolean active);

    public Object put(Object key, Object value);

    public Object remove(Object key);

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);
}
