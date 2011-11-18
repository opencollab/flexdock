/*
 * Created on May 17, 2005
 */
package org.flexdock.perspective.event;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.event.Event;
import org.flexdock.perspective.Layout;

/**
 * @author Christopher Butler
 */
public class LayoutEvent extends Event {
    public static final int LAYOUT_APPLIED = 0;
    public static final int LAYOUT_EMPTIED = 1;
    public static final int DOCKABLE_HIDDEN = 2;
    public static final int DOCKABLE_RESTORED = 3;

    private Layout oldLayout;
    private Dockable dockable;

    public LayoutEvent(Layout layout, Layout oldLayout, String dockableId, int evtType) {
        super(layout, evtType);
        this.oldLayout = oldLayout;
        dockable = DockingManager.getDockable(dockableId);
    }

    public Layout getOldLayout() {
        return oldLayout;
    }

    public Dockable getDockable() {
        return dockable;
    }
}
