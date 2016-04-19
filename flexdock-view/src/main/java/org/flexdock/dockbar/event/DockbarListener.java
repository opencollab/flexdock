/*
 * Created on Apr 18, 2005
 */
package org.flexdock.dockbar.event;

import java.util.EventListener;

/**
 * @author Christopher Butler
 */
public interface DockbarListener extends EventListener {

    //comment public is redundant in interfaces since all of the methods are by default public

    void dockableExpanded(DockbarEvent evt);
    void dockableLocked(DockbarEvent evt);
    void dockableCollapsed(DockbarEvent evt);

    void minimizeStarted(DockbarEvent evt);
    void minimizeCompleted(DockbarEvent evt);

    static class Stub implements DockbarListener {

        public void dockableExpanded(DockbarEvent evt) {}

        public void dockableLocked(DockbarEvent evt) {}

        public void dockableCollapsed(DockbarEvent evt) {}

        public void minimizeStarted(DockbarEvent evt) {}

        public void minimizeCompleted(DockbarEvent evt) {}

    }

}
