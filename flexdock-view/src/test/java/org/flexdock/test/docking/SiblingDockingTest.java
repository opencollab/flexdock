package org.flexdock.test.docking;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.defaults.DefaultDockingStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SiblingDockingTest extends SiblingLayoutBase {

    private static void assertSibling(Dockable expected, Dockable dockable, String region) {
        assertSame(expected, DefaultDockingStrategy.getSibling(dockable, region),
                dockable.getPersistentId() + " " + region);
    }

    @Test
    void testDirectSiblings() {
        assertSibling(left, centre, DockingConstants.WEST_REGION);
        assertSibling(right, centre, DockingConstants.EAST_REGION);
        assertSibling(bottom, centre, DockingConstants.SOUTH_REGION);
        assertSibling(below, left, DockingConstants.SOUTH_REGION);
        assertSibling(left, below, DockingConstants.NORTH_REGION);
        assertSibling(centre, bottom, DockingConstants.NORTH_REGION);
        assertSibling(centre, right, DockingConstants.WEST_REGION);
    }

    @Test
    void testSiblingAcrossNestedSplits() {
        // Fuzzy search: the nearest dockable on that side, found by looking up the tree
        assertSibling(centre, left, DockingConstants.EAST_REGION);
        assertSibling(below, bottom, DockingConstants.WEST_REGION);
        assertSibling(bottom, right, DockingConstants.SOUTH_REGION);
    }

    @Test
    void testNoSiblingOutsideLayout() {
        assertSibling(null, centre, DockingConstants.NORTH_REGION);
        assertSibling(null, left, DockingConstants.WEST_REGION);
        assertSibling(null, right, DockingConstants.EAST_REGION);
        assertSibling(null, bottom, DockingConstants.SOUTH_REGION);
    }

    @Test
    void testNoSiblingForCenterInvalidRegionOrNullDockable() {
        assertSibling(null, centre, DockingConstants.CENTER_REGION);
        assertSibling(null, centre, "no-such-region");
        assertNull(DefaultDockingStrategy.getSibling(null, DockingConstants.WEST_REGION));
        assertNull(DefaultDockingStrategy.getSibling(null));
    }

}
