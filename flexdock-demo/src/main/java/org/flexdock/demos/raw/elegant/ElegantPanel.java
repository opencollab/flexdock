package org.flexdock.demos.raw.elegant;

import javax.swing.JComponent;

import org.flexdock.demos.util.DockingStubTitlepane;
import org.flexdock.demos.util.GradientTitlebar;
import org.flexdock.demos.util.Titlebar;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;


public class ElegantPanel extends DockingStubTitlepane {
    private Dockable dockable;

    public ElegantPanel(String title) {
        super(title, title);
        DockingManager.registerDockable(this);
    }

    public void dock(ElegantPanel otherPanel) {
        DockingManager.dock(otherPanel, this);
    }

    public void dock(ElegantPanel otherPanel, String region) {
        DockingManager.dock(otherPanel, this, region);
    }

    public void dock(ElegantPanel otherPanel, String region, float ratio) {
        DockingManager.dock(otherPanel, this, region, ratio);
    }

    protected JComponent createContentPane() {
        return null;
    }

    protected Titlebar createTitlebar(String title) {
        return new GradientTitlebar(title);
    }
}
