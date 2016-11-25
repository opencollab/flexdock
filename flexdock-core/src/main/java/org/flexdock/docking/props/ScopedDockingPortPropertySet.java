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

/**
 * @author Christopher Butler
 */
public class ScopedDockingPortPropertySet extends BasicDockingPortPropertySet implements ScopedMap<RootDockingPortPropertySet> {
    public static final RootDockingPortPropertySet ROOT_PROPS = new RootDockingPortPropertySet();
    public static final List DEFAULTS = new ArrayList(0);
    public static final List GLOBALS = new ArrayList(0);

    private ArrayList locals;

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
    public RootDockingPortPropertySet getRoot() {
        return ROOT_PROPS;
    }
}
