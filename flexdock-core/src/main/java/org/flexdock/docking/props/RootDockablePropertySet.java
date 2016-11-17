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

import org.flexdock.docking.Dockable;

/**
 * @author Christopher Butler
 */
public class RootDockablePropertySet extends BasicDockablePropertySet {
    public static final Float DEFAULT_DRAG_THRESHOLD = new Float(4);
    public static final Float DEFAULT_PREVIEW_SIZE = new Float(0.3);

    public RootDockablePropertySet(Dockable dockable) {
        super(dockable,
                "null", //dockableDesc
                true, //dockingEnabled
                false, //active
                true, //mouseMotionListenersBlockedWhileDragging
                DEFAULT_DRAG_THRESHOLD, //dragThreshold
                DEFAULT_PREVIEW_SIZE //previewSize
        );
    }
}
