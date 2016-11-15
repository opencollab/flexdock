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
package org.flexdock.docking.floating.policy;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.event.DockingEvent;

/**
 * This interface describes a set of method used to determine how floating
 * events should be handled. Classes implementing this interface should be
 * plugged into to the {@code FloatPolicyManager} to affect floating behavior of
 * the framework at runtime.
 *
 * @author Christopher Butler
 */
public interface FloatPolicy {

    /**
     * Returns {@code true} if floating should be allowed for the specified
     * {@code Dockable}. This method will be invoked by the
     * {@code FloatPolicyManager} at the beginning of a drag operation to
     * determine whether or not floating support will be enabled for the
     * {@code Dockable} as a result of the drag. If this method returns
     * {@code false}, floating will not be allowed for the drag operation
     * against the specified {@code Dockable}.
     *
     * @param dockable
     *            the {@code Dockable} to be checked for floating support
     * @return {@code true} if floating should be allowed for the specified
     *         {@code Dockable}; {@code false} otherwise.
     */
    boolean isFloatingAllowed(Dockable dockable);

    /**
     * Indicates whether floating should be allowed for the specified
     * {@code DockingEvent} at the end of a drag operation. The
     * {@code FloatPolicyManager} will catch all attempts to float a
     * {@code Dockable} at the end of a drag operation and invoke this method on
     * all installed {@code FloatPolicies}. If any of them returns
     * {@code false}, the docking operation will be canceled.
     *
     * @param evt
     *            the {@code DockingEvent} to be checked for drop-to-float
     *            support
     * @return {@code true} if floating should be allowed for the specified
     *         {@code DockingEvent}; {@code false} otherwise.
     */
    boolean isFloatDropAllowed(DockingEvent evt);

    /**
     * Provides a default implementation of the FloatPolicy interface.
     *
     * @author Christopher Butler
     */
    class NullFloatPolicy implements FloatPolicy {
        /**
         * Returns {@code true}.
         *
         * @return {@code true}.
         * @see FloatPolicy#isFloatingAllowed(Dockable)
         */
        public boolean isFloatingAllowed(Dockable dockable) {
            return true;
        }

        /**
         * Returns {@code true}.
         *
         * @return {@code true}.
         * @see FloatPolicy#isFloatDropAllowed(DockingEvent)
         */
        public boolean isFloatDropAllowed(DockingEvent evt) {
            return true;
        }
    }
}
