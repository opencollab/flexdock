package org.flexdock.demos.maximizing;

import java.awt.Component;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.AbstractDockable;

public class DockableSimpleInternalFrame extends AbstractDockable {
    private Component component;

    public DockableSimpleInternalFrame(SimpleInternalFrame sif) {
        this(sif, sif.getTitle());
    }

    public DockableSimpleInternalFrame(SimpleInternalFrame sif, String id) {
        super(id);
        this.component = sif;
        getDragSources().add(sif.getDragHandle());
        getFrameDragSources().add(sif.getDragHandle());
        setTabText(sif.getTitle());
    }

    public Component getComponent() {
        return component;
    }

    public void dispose() {
        DockingManager.unregisterDockable(this);
        component = null;
    }
}
