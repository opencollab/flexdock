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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.flexdock.docking.Dockable;


/**
 * @author Christopher Butler
 */
@SuppressWarnings(value = { "serial" })
public class ScopedDockablePropertySet extends BasicDockablePropertySet implements ScopedMap {

    public static final RootDockablePropertySet ROOT_PROPS = new RootDockablePropertySet(null);
    public static final List DEFAULTS = new ArrayList(0);
    public static final List GLOBALS = new ArrayList(0);
    private ArrayList locals;

    public ScopedDockablePropertySet(Dockable dockable) {
        this(6, dockable);
        init();
    }

    public ScopedDockablePropertySet(int initialCapacity, Dockable dockable) {
        super(initialCapacity, dockable);
        init();
    }

    public ScopedDockablePropertySet(int initialCapacity, float loadFactor, Dockable dockable) {
        super(initialCapacity, loadFactor, dockable);
        init();
    }

    public ScopedDockablePropertySet(Map t, Dockable dockable) {
        super(t, dockable);
        init();
    }

    @Override
    public List getLocals() {
        return locals;
    }

    @Override
    public List getDefaults() {
        return DEFAULTS;
    }

    @Override
    public List getGlobals() {
        return GLOBALS;
    }

    @Override
    public Map getRoot() {
        return ROOT_PROPS;
    }

    @Override
    public String getDockableDesc() {
        return (String)PropertyManager.getProperty(DESCRIPTION, this);
    }

    @Override
    public Boolean isDockingEnabled() {
        return (Boolean)PropertyManager.getProperty(DOCKING_ENABLED, this);
    }

    @Override
    public Boolean isActive() {
        return (Boolean)PropertyManager.getProperty(ACTIVE, this);
    }

    @Override
    public Boolean isMouseMotionListenersBlockedWhileDragging() {
        return (Boolean)PropertyManager.getProperty(MOUSE_MOTION_DRAG_BLOCK, this);
    }

    @Override
    public Float getRegionInset(String region) {
        String key = getRegionInsetKey(region);
        return key==null? null: (Float)PropertyManager.getProperty(key, this);
    }

    @Override
    public Float getSiblingSize(String region) {
        String key = getSiblingSizeKey(region);
        return key==null? null: (Float)PropertyManager.getProperty(key, this);
    }

    @Override
    public Boolean isTerritoryBlocked(String region) {
        String key = getTerritoryBlockedKey(region);
        return key==null? null: (Boolean)PropertyManager.getProperty(key, this);
    }

    @Override
    public Float getDragThreshold() {
        return (Float)PropertyManager.getProperty(DRAG_THRESHOLD, this);
    }

    @Override
    public Float getPreviewSize() {
        return (Float)PropertyManager.getProperty(PREVIEW_SIZE, this);
    }

    private void init() {
        locals = new ArrayList(1);
        locals.add(this);
    }

}
