/*
 * Created on May 26, 2005
 */
package org.flexdock.dockbar;

import java.awt.Component;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.state.MinimizationManager;

/**
 * @author Christopher Butler
 */
public class MinimizationAdapter implements MinimizationManager {

    static {
        init();
    }

    private static void init() {
        // make sure DockbarManager is initialized
        Class c = DockbarManager.class;
    }

    public boolean close(Dockable dockable) {
        DockbarManager mgr = DockbarManager.getCurrent(dockable);
        return mgr==null? false: mgr.remove(dockable);
    }

    public void preview(Dockable dockable, boolean locked) {
        DockbarManager.activate(dockable, true);
    }

    public void setMinimized(Dockable dockable, boolean minimizing, Component window, int edge) {
        DockbarManager mgr = DockbarManager.getInstance(window);
        if(mgr==null)
            return;

        // if minimizing, send to the dockbar
        if(minimizing) {
            if(edge==MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT)
                mgr.minimize(dockable);
            else
                mgr.minimize(dockable, edge);
        }
        // otherwise, restore from the dockbar
        else
            mgr.restore(dockable);
    }
}
