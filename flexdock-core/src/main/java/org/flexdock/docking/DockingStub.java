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
package org.flexdock.docking;

import java.awt.Component;

/**
 * @author Christopher Butler
 */
public interface DockingStub {

    /**
     * Returns the {@code Component} that is the event source for drag
     * operations. The component may or may not be the same as the Component
     * returned by {@code getFrameDragSource()}.
     *
     * @return as described
     * @see #getFrameDragSource()
     */
    Component getDragSource();

    /**
     * Returns the {@code Component} that is used as a frame drag source. When
     * this {@code DockingStub} is floated into an external frame, that frame
     * may or may not have a titlebar for repositioning. The Component returned
     * by this method will be setup with appropriate event listeners such that
     * dragging them will serve to reposition the containing frame as if they
     * were the frame titlebar. If the Component returned by this method and the
     * one returned by {@code getDragSource()} is the same, then then "frame
     * reposition" behavior will supercede any "drag-to-dock" behavior while
     * this stub is in a floating state.
     *
     * @return as described
     * @see #getDragSource()
     */
    Component getFrameDragSource();

    /**
     * Returns a {@code String} identifier that is unique within a JVM instance,
     * but persistent across JVM instances. This is used for configuration
     * mangement, allowing the JVM to recognize a {@code Dockable} instance
     * within an application instance, persist the ID, and recall it in later
     * application instances. The ID should be unique within an appliation
     * instance so that there are no collisions with other {@code Dockable}
     * instances, but it should also be consistent from JVM to JVM so that the
     * association between a {@code Dockable} instance and its ID can be
     * remembered from session to session.
     * <p>
     * The framework performs indexing on the persistent ID. Consequently, this
     * method may <b>not</b> return a {@code null} reference.
     *
     * @return as described
     */
    String getPersistentId();

    /**
     * Gets the tab text for this class.
     *
     * @return the text placed in a {@code JTabbedPane} tab.
     * @see javax.swing.JTabbedPane
     */
    String getTabText();

}
