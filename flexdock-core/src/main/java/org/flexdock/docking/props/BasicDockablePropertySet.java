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

import javax.swing.Icon;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.util.TypedHashtable;
import org.flexdock.util.Utilities;

/**
 * @author Christopher Butler
 */
@SuppressWarnings(value = { "serial" })
public class BasicDockablePropertySet extends TypedHashtable implements DockablePropertySet, DockingConstants {
    private String dockingId;
    private PropertyChangeSupport changeSupport;

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
        super();
        init(dockable);
    }

    public BasicDockablePropertySet(int initialCapacity, Dockable dockable) {
        super(initialCapacity);
        init(dockable);
    }

    public BasicDockablePropertySet(int initialCapacity, float loadFactor, Dockable dockable) {
        super(initialCapacity, loadFactor);
        init(dockable);
    }

    public BasicDockablePropertySet(Map t, Dockable dockable) {
        super(t);
        init(dockable);
    }

    private void init(Dockable dockable) {
        this.dockingId = dockable==null? null: dockable.getPersistentId();
        Object changeSrc = dockable==null? this: dockable;
        changeSupport = new PropertyChangeSupport(changeSrc);
    }















    @Override
    public Icon getDockbarIcon() {
        return (Icon)get(DOCKBAR_ICON);
    }

    @Override
    public Icon getTabIcon() {
        return (Icon)get(TAB_ICON);
    }

    @Override
    public String getDockableDesc() {
        return (String)get(DESCRIPTION);
    }

    @Override
    public Boolean isDockingEnabled() {
        return getBoolean(DOCKING_ENABLED);
    }

    @Override
    public Boolean isActive() {
        return getBoolean(ACTIVE);
    }

    @Override
    public Boolean isMouseMotionListenersBlockedWhileDragging() {
        return getBoolean(MOUSE_MOTION_DRAG_BLOCK);
    }


    @Override
    public Float getRegionInset(String region) {
        String key = getRegionInsetKey(region);
        return key==null? null: (Float)get(key);
    }

    @Override
    public Float getSiblingSize(String region) {
        String key = getSiblingSizeKey(region);
        return key==null? null: (Float)get(key);
    }

    @Override
    public Boolean isTerritoryBlocked(String region) {
        String key = getTerritoryBlockedKey(region);
        return key==null? null: (Boolean)get(key);
    }

    @Override
    public Float getDragThreshold() {
        return getFloat(DRAG_THRESHOLD);
    }

    @Override
    public Float getPreviewSize() {
        return getFloat(PREVIEW_SIZE);
    }












    @Override
    public void setDockbarIcon(Icon icon) {
        Icon oldValue = getDockbarIcon();
        put(DOCKBAR_ICON, icon);
        firePropertyChange(DOCKBAR_ICON, oldValue, icon);
    }

    @Override
    public void setTabIcon(Icon icon) {
        Icon oldValue = getTabIcon();
        put(TAB_ICON, icon);
        firePropertyChange(TAB_ICON, oldValue, icon);
    }

    @Override
    public void setDockableDesc(String dockableDesc) {
        String oldValue = getDockableDesc();
        put(DESCRIPTION, dockableDesc);
        firePropertyChange(DESCRIPTION, oldValue, dockableDesc);
    }

    @Override
    public void setDockingEnabled(boolean enabled) {
        put(DOCKING_ENABLED, enabled);
    }

    @Override
    public void setActive(boolean active) {
        Boolean oldValue = isActive();
        if(oldValue==null) {
            oldValue = Boolean.FALSE;
        }

        put(ACTIVE, active);
        firePropertyChange(ACTIVE, oldValue.booleanValue(), active);
    }

    @Override
    public void setMouseMotionListenersBlockedWhileDragging(boolean blocked) {
        put(MOUSE_MOTION_DRAG_BLOCK, blocked);
    }

    @Override
    public void setRegionInset(String region, float inset) {
        String key = getRegionInsetKey(region);
        if(key!=null) {
            Float f = new Float(inset);
            put(key, f);
        }
    }

    @Override
    public void setSiblingSize(String region, float size) {
        String key = getSiblingSizeKey(region);
        if(key!=null) {
            Float f = new Float(size);
            put(key, f);
        }
    }

    @Override
    public void setTerritoryBlocked(String region, boolean blocked) {
        String key = getTerritoryBlockedKey(region);
        if(key!=null) {
            Boolean bool = blocked? Boolean.TRUE: Boolean.FALSE;
            put(key, bool);
        }
    }


    @Override
    public void setDragTheshold(float threshold) {
        threshold = Math.max(threshold, 0);
        put(DRAG_THRESHOLD, threshold);
    }

    @Override
    public void setPreviewSize(float previewSize) {
        previewSize = Math.max(previewSize, 0f);
        previewSize = Math.min(previewSize, 1f);
        put(PREVIEW_SIZE, previewSize);
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
