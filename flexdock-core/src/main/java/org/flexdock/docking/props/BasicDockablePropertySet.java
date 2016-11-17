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
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.HashMap;

import javax.swing.Icon;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.RegionChecker;
import org.flexdock.util.Utilities;

/**
 * @author Christopher Butler
 */
public class BasicDockablePropertySet implements DockablePropertySet, DockingConstants {
    private static final Float DEFAULT_REGION_INSETS = RegionChecker.DEFAULT_REGION_SIZE;
    private static final Float DEFAULT_SIBLING_INSETS = DockingManager.getDefaultSiblingSize();
    private String dockingId;
    private PropertyChangeSupport changeSupport;
    private Icon dockbarIcon;
    private Icon tabIcon;
    private String dockableDesc;
    private boolean dockingEnabled;
    private boolean active;
    private boolean mouseMotionListenersBlockedWhileDragging;
    private Map<String, Float> regionInsetMap = new HashMap<String, Float>();
    private Map<String, Float> siblingSizeMap = new HashMap<String, Float>();
    private Map<String, Boolean> territoryBlockedMap = new HashMap<String, Boolean>();
    private float dragThreshold;
    private float previewSize;

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

    public static String getSiblingSizeKey(String region) {
        if(NORTH_REGION.equals(region)) {
            return SIBLING_SIZE_NORTH;
        }
        if(SOUTH_REGION.equals(region)) {
            return SIBLING_SIZE_SOUTH;
        }
        if(EAST_REGION.equals(region)) {
            return SIBLING_SIZE_EAST;
        }
        if(WEST_REGION.equals(region)) {
            return SIBLING_SIZE_WEST;
        }
        return null;
    }

    public static String getTerritoryBlockedKey(String region) {
        if(NORTH_REGION.equals(region)) {
            return TERRITORY_BLOCKED_NORTH;
        }
        if(SOUTH_REGION.equals(region)) {
            return TERRITORY_BLOCKED_SOUTH;
        }
        if(EAST_REGION.equals(region)) {
            return TERRITORY_BLOCKED_EAST;
        }
        if(WEST_REGION.equals(region)) {
            return TERRITORY_BLOCKED_WEST;
        }
        if(CENTER_REGION.equals(region)) {
            return TERRITORY_BLOCKED_CENTER;
        }
        return null;
    }

    public BasicDockablePropertySet(Dockable dockable) {
        this.dockingId = dockable==null? null: dockable.getPersistentId();
        Object changeSrc = dockable==null? this: dockable;
        changeSupport = new PropertyChangeSupport(changeSrc);

        regionInsetMap.put(REGION_SIZE_NORTH, DEFAULT_REGION_INSETS);
        regionInsetMap.put(REGION_SIZE_SOUTH, DEFAULT_REGION_INSETS);
        regionInsetMap.put(REGION_SIZE_EAST, DEFAULT_REGION_INSETS);
        regionInsetMap.put(REGION_SIZE_WEST, DEFAULT_REGION_INSETS);

        siblingSizeMap.put(SIBLING_SIZE_NORTH, DEFAULT_SIBLING_INSETS);
        siblingSizeMap.put(SIBLING_SIZE_SOUTH, DEFAULT_SIBLING_INSETS);
        siblingSizeMap.put(SIBLING_SIZE_EAST, DEFAULT_SIBLING_INSETS);
        siblingSizeMap.put(SIBLING_SIZE_WEST, DEFAULT_SIBLING_INSETS);

        territoryBlockedMap.put(TERRITORY_BLOCKED_NORTH, Boolean.FALSE);
        territoryBlockedMap.put(TERRITORY_BLOCKED_SOUTH, Boolean.FALSE);
        territoryBlockedMap.put(TERRITORY_BLOCKED_EAST, Boolean.FALSE);
        territoryBlockedMap.put(TERRITORY_BLOCKED_WEST, Boolean.FALSE);
        territoryBlockedMap.put(TERRITORY_BLOCKED_CENTER, Boolean.FALSE);
    }

    public BasicDockablePropertySet(Dockable dockable,
            String dockableDesc,
            boolean dockingEnabled,
            boolean active,
            boolean mouseMotionListenersBlockedWhileDragging,
            float dragThreshold,
            float previewSize) {
        this(dockable);
        this.dockableDesc = dockableDesc;
        this.dockingEnabled = dockingEnabled;
        this.active = active;
        this.mouseMotionListenersBlockedWhileDragging = mouseMotionListenersBlockedWhileDragging;
        this.dragThreshold = dragThreshold;
        this.previewSize = previewSize;
    }

    /**
     * @return the dockbarIcon
     */
    @Override
    public Icon getDockbarIcon() {
        return dockbarIcon;
    }

    /**
     * @return the tabIcon
     */
    @Override
    public Icon getTabIcon() {
        return tabIcon;
    }

    /**
     * @return the description
     */
    @Override
    public String getDockableDesc() {
        return dockableDesc;
    }

    /**
     * @return the dockingEnabled
     */
    @Override
    public Boolean isDockingEnabled() {
        return dockingEnabled;
    }

    /**
     * @return the active
     */
    @Override
    public Boolean isActive() {
        return active;
    }

    /**
     * @return the mouseMotionListenersBlockedWhileDragging
     */
    @Override
    public Boolean isMouseMotionListenersBlockedWhileDragging() {
        return mouseMotionListenersBlockedWhileDragging;
    }

    /**
     * @return the regionInsetMap
     */
    @Override
    public Float getRegionInset(String region) {
        String key = getRegionInsetKey(region);
        return regionInsetMap.get(key);
    }

    /**
     * @return the siblingSizeMap
     */
    @Override
    public Float getSiblingSize(String region) {
        String key = getSiblingSizeKey(region);
        return siblingSizeMap.get(key);
    }

    /**
     * @return the territoryBlockedMap
     */
    @Override
    public Boolean isTerritoryBlocked(String region) {
        String key = getTerritoryBlockedKey(region);
        return territoryBlockedMap.get(key);
    }

    /**
     * @return the dragThreshold
     */
    @Override
    public Float getDragThreshold() {
        return dragThreshold;
    }

    /**
     * @return the previewSize
     */
    @Override
    public Float getPreviewSize() {
        return previewSize;
    }

    @Override
    public void setDockbarIcon(Icon icon) {
        Icon oldValue = getDockbarIcon();
        this.dockbarIcon = icon;
        firePropertyChange(DOCKBAR_ICON, oldValue, icon);
    }

    @Override
    public void setTabIcon(Icon icon) {
        Icon oldValue = getTabIcon();
        this.tabIcon = icon;
        firePropertyChange(TAB_ICON, oldValue, icon);
    }

    @Override
    public void setDockableDesc(String dockableDesc) {
        String oldValue = getDockableDesc();
        this.dockableDesc = dockableDesc;
        firePropertyChange(DESCRIPTION, oldValue, dockableDesc);
    }

    @Override
    public void setDockingEnabled(boolean enabled) {
        this.dockingEnabled = enabled;
    }

    @Override
    public void setActive(boolean active) {
        Boolean oldValue = isActive();
        if(oldValue==null) {
            oldValue = Boolean.FALSE;
        }
        this.active = active;
        firePropertyChange(ACTIVE, oldValue.booleanValue(), active);
    }

    @Override
    public void setMouseMotionListenersBlockedWhileDragging(boolean blocked) {
        this.mouseMotionListenersBlockedWhileDragging = blocked;
    }

    @Override
    public void setRegionInset(String region, float inset) {
        String key = getRegionInsetKey(region);
        if(key!=null) {
            Float f = inset;
            regionInsetMap.put(key, f);
        }
    }

    @Override
    public void setSiblingSize(String region, float size) {
        String key = getSiblingSizeKey(region);
        if(key!=null) {
            Float f = size;
            siblingSizeMap.put(key, f);
        }
    }

    @Override
    public void setTerritoryBlocked(String region, boolean blocked) {
        String key = getTerritoryBlockedKey(region);
        if(key!=null) {
            Boolean bool = blocked? Boolean.TRUE: Boolean.FALSE;
            territoryBlockedMap.put(key, bool);
        }
    }

    @Override
    public void setDragTheshold(float threshold) {
        threshold = Math.max(threshold, 0);
        this.dragThreshold = threshold;
    }

    @Override
    public void setPreviewSize(float previewSize) {
        previewSize = Math.max(previewSize, 0f);
        previewSize = Math.min(previewSize, 1f);
        this.previewSize = previewSize;
    }

    /**
     * @return Returns the dockingId.
     */
    @Override
    public String getDockingId() {
        return dockingId;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String property, Object oldValue, Object newValue) {
        if(Utilities.isChanged(oldValue, newValue)) {
            changeSupport.firePropertyChange(property, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String property, int oldValue, int newValue) {
        if(oldValue!=newValue) {
            changeSupport.firePropertyChange(property, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String property, boolean oldValue, boolean newValue) {
        if(oldValue!=newValue) {
            changeSupport.firePropertyChange(property, oldValue, newValue);
        }
    }
}
