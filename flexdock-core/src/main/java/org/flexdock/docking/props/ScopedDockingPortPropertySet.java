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

import org.flexdock.docking.RegionChecker;

/**
 * @author Christopher Butler
 */
public class ScopedDockingPortPropertySet extends BasicDockingPortPropertySet implements ScopedMap {
    public static final RootDockingPortPropertySet ROOT_PROPS = new RootDockingPortPropertySet();
    public static final List DEFAULTS = new ArrayList(0);
    public static final List GLOBALS = new ArrayList(0);

    private ArrayList locals;

    public ScopedDockingPortPropertySet() {
        super();
        init();
    }

    public ScopedDockingPortPropertySet(int initialCapacity) {
        super(initialCapacity);
        init();
    }

    public ScopedDockingPortPropertySet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        init();
    }

    public ScopedDockingPortPropertySet(Map t) {
        super(t);
        init();
    }

    protected void init() {
        locals = new ArrayList(1);
        locals.add(this);
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
    public RegionChecker getRegionChecker() {
        return (RegionChecker)PropertyManager.getProperty(REGION_CHECKER, this);
    }

    @Override
    public Float getRegionInset(String region) {
        String key = getRegionInsetKey(region);
        return key==null? null: (Float)PropertyManager.getProperty(key, this);
    }

    @Override
    public Integer getTabPlacement() {
        return (Integer)PropertyManager.getProperty(TAB_PLACEMENT, this);
    }

    @Override
    public Boolean isSingleTabsAllowed() {
        return (Boolean)PropertyManager.getProperty(SINGLE_TABS, this);
    }
}
