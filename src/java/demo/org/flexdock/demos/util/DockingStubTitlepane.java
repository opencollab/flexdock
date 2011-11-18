/*
 * Created on Jul 7, 2005
 */
package org.flexdock.demos.util;

import java.awt.Component;

import org.flexdock.docking.DockingStub;

/**
 * @author Christopher Butler
 */
public class DockingStubTitlepane extends Titlepane implements DockingStub {
    private String dockingId;

    public DockingStubTitlepane(String id, String title) {
        super(title);
        dockingId = id;
    }

    public Component getDragSource() {
        return getTitlebar();
    }

    public Component getFrameDragSource() {
        return getTitlebar();
    }

    public String getPersistentId() {
        return dockingId;
    }

    public String getTabText() {
        return getTitle();
    }
}
