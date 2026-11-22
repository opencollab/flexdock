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
package org.flexdock.docking.state;

import java.io.IOException;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;

/**
 * @author Christopher Butler
 */
public interface LayoutManager {

    LayoutManager DEFAULT_STUB = new Stub();

    DockingState getDockingState(String dockableId);

    DockingState getDockingState(Dockable dockable);

    FloatManager getFloatManager();

    LayoutNode createLayout(DockingPort port);

    boolean display(Dockable dockable);

    boolean store() throws IOException, PersistenceException;

    boolean store(String persistenceKey) throws IOException, PersistenceException;

    boolean load() throws IOException, PersistenceException;

    boolean load(String persistenceKey) throws IOException, PersistenceException;

    boolean restore(boolean loadFromStorage) throws IOException, PersistenceException;

    String getDefaultPersistenceKey();

    void setDefaultPersistenceKey(String key);

    // Used when the default layout manager (in flexdock-perspective) is not
    // on the classpath
    public static class Stub implements LayoutManager {

        @Override
        public DockingState getDockingState(String dockableId) {
            return null;
        }

        @Override
        public DockingState getDockingState(Dockable dockable) {
            return null;
        }

        @Override
        public FloatManager getFloatManager() {
            return FloatManager.DEFAULT_STUB;
        }

        @Override
        public LayoutNode createLayout(DockingPort port) {
            return null;
        }

        @Override
        public boolean display(Dockable dockable) {
            return false;
        }

        @Override
        public boolean store() {
            return false;
        }

        @Override
        public boolean store(String persistenceKey) {
            return false;
        }

        @Override
        public boolean load() {
            return false;
        }

        @Override
        public boolean load(String persistenceKey) {
            return false;
        }

        @Override
        public boolean restore(boolean loadFromStorage) {
            return false;
        }

        @Override
        public String getDefaultPersistenceKey() {
            return null;
        }

        @Override
        public void setDefaultPersistenceKey(String key) {
        }

    }

}
