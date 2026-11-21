package org.flexdock.test.docking;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.util.DockingUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import static org.junit.jupiter.api.Assertions.*;

class DockingPortTest {

    private DefaultDockingPort port;
    private Dockable first;
    private Dockable second;
    private Dockable third;

    @BeforeEach
    void createPort() {
        port = new DefaultDockingPort();
        first = DockingManager.registerDockable(new JPanel(), "First");
        second = DockingManager.registerDockable(new JPanel(), "Second");
        third = DockingManager.registerDockable(new JPanel(), "Third");
    }

    private void assertSplit(Dockable docked, String region, int orientation) {
        port.dock(docked, region);

        JSplitPane split = assertInstanceOf(JSplitPane.class, port.getDockedComponent());
        assertEquals(orientation, split.getOrientation());
        assertSame(docked.getComponent(), port.getComponent(region));
        assertSame(first.getComponent(), port.getComponent(DockingUtility.flipRegion(region)));
    }

    @Test
    void testFirstDockableGoesToCenterWhateverTheRegion() {
        assertTrue(port.dock(first, DockingConstants.EAST_REGION));

        assertSame(first.getComponent(), port.getDockedComponent());
        assertTrue(DockingManager.isDocked(port, first));
    }

    @Test
    void testCenterRegionCreatesTabsAndSelectsNewest() {
        port.dock(first, DockingConstants.CENTER_REGION);
        port.dock(second, DockingConstants.CENTER_REGION);
        port.dock(third, DockingConstants.CENTER_REGION);

        JTabbedPane tabs = assertInstanceOf(JTabbedPane.class, port.getDockedComponent());
        assertEquals(3, tabs.getTabCount());
        assertSame(third.getComponent(), tabs.getSelectedComponent());
    }

    @Test
    void testHorizontalRegionsCreateHorizontalSplit() {
        port.dock(first, DockingConstants.CENTER_REGION);

        assertSplit(second, DockingConstants.WEST_REGION, JSplitPane.HORIZONTAL_SPLIT);
    }

    @Test
    void testVerticalRegionsCreateVerticalSplit() {
        port.dock(first, DockingConstants.CENTER_REGION);

        assertSplit(second, DockingConstants.NORTH_REGION, JSplitPane.VERTICAL_SPLIT);
    }

    @Test
    void testUndockFromTabsLeavesSingleComponent() {
        port.dock(first, DockingConstants.CENTER_REGION);
        port.dock(second, DockingConstants.CENTER_REGION);

        assertTrue(DockingManager.undock(second));

        assertSame(first.getComponent(), port.getDockedComponent());
        assertFalse(DockingManager.isDocked(second));
    }

    @Test
    void testUndockFromSplitCollapsesIt() {
        port.dock(first, DockingConstants.CENTER_REGION);
        port.dock(second, DockingConstants.SOUTH_REGION);

        assertTrue(DockingManager.undock(second));

        assertSame(first.getComponent(), port.getDockedComponent());
    }

    @Test
    void testUndockOfLastDockableEmptiesPort() {
        port.dock(first, DockingConstants.CENTER_REGION);

        assertTrue(DockingManager.undock(first));

        assertNull(port.getDockedComponent());
    }

}
