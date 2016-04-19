/*
 * Created on May 26, 2005
 */
package org.flexdock.docking.state;

import java.awt.Component;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;

/**
 * @author Christopher Butler
 */
public interface MinimizationManager {

    MinimizationManager DEFAULT_STUB = new Stub();

    int UNSPECIFIED_LAYOUT_CONSTRAINT = -1;

    int TOP = DockingConstants.TOP;

    int LEFT = DockingConstants.LEFT;

    int BOTTOM = DockingConstants.BOTTOM;

    int RIGHT = DockingConstants.RIGHT;

    int CENTER = DockingConstants.CENTER;

    boolean close(Dockable dockable);

    void preview(Dockable dockable, boolean locked);

    void setMinimized(Dockable dockable, boolean minimized, Component window, int constraint);

    class Stub implements MinimizationManager {
        public boolean close(Dockable dockable) {
            return false;
        }

        public void preview(Dockable dockable, boolean locked) {}

        public void setMinimized(Dockable dockable, boolean minimized, Component window, int edge) {}

    }

}
