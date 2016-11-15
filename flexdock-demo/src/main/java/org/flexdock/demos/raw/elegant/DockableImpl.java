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
package org.flexdock.demos.raw.elegant;

import java.awt.Component;

import javax.swing.JComponent;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.AbstractDockable;

public class DockableImpl extends AbstractDockable {
    private ElegantPanel panel;
    private JComponent dragInitiator;

    public DockableImpl(ElegantPanel dockable, JComponent dragInit, String id) {
        super(id);
        if(dockable==null) {
            new IllegalArgumentException(
                    "Cannot create DockableImpl with a null DockablePanel.");
        }
        if(dragInit==null) {
            new IllegalArgumentException(
                    "Cannot create DockableImpl with a null drag initiator.");
        }

        panel = dockable;
        dragInitiator = dragInit;
        setTabText(panel.getTitle());
        getDragSources().add(dragInit);
        getFrameDragSources().add(dockable.getTitlebar());
        DockingManager.registerDockable(this);
    }

    public Component getComponent() {
        return panel;
    }
}
