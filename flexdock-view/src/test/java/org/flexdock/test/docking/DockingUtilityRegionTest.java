package org.flexdock.test.docking;

import org.flexdock.docking.DockingConstants;
import org.flexdock.util.DockingUtility;
import org.junit.jupiter.api.Test;

import javax.swing.JSplitPane;

import static org.junit.jupiter.api.Assertions.*;

class DockingUtilityRegionTest {

    @Test
    void testFlipRegion() {
        assertEquals(DockingConstants.SOUTH_REGION, DockingUtility.flipRegion(DockingConstants.NORTH_REGION));
        assertEquals(DockingConstants.NORTH_REGION, DockingUtility.flipRegion(DockingConstants.SOUTH_REGION));
        assertEquals(DockingConstants.WEST_REGION, DockingUtility.flipRegion(DockingConstants.EAST_REGION));
        assertEquals(DockingConstants.EAST_REGION, DockingUtility.flipRegion(DockingConstants.WEST_REGION));
    }

    @Test
    void testFlipRegionWithoutOppositeIsCenter() {
        assertEquals(DockingConstants.CENTER_REGION, DockingUtility.flipRegion(DockingConstants.CENTER_REGION));
        assertEquals(DockingConstants.CENTER_REGION, DockingUtility.flipRegion("no-such-region"));
        assertEquals(DockingConstants.CENTER_REGION, DockingUtility.flipRegion(null));
    }

    @Test
    void testAxisEquivalence() {
        assertTrue(DockingUtility.isAxisEquivalent(DockingConstants.NORTH_REGION, DockingConstants.WEST_REGION));
        assertTrue(DockingUtility.isAxisEquivalent(DockingConstants.WEST_REGION, DockingConstants.NORTH_REGION));
        assertTrue(DockingUtility.isAxisEquivalent(DockingConstants.SOUTH_REGION, DockingConstants.EAST_REGION));
        assertTrue(DockingUtility.isAxisEquivalent(DockingConstants.EAST_REGION, DockingConstants.SOUTH_REGION));
        assertTrue(DockingUtility.isAxisEquivalent(DockingConstants.EAST_REGION, DockingConstants.EAST_REGION));
    }

    @Test
    void testAxisNonEquivalence() {
        assertFalse(DockingUtility.isAxisEquivalent(DockingConstants.NORTH_REGION, DockingConstants.SOUTH_REGION));
        assertFalse(DockingUtility.isAxisEquivalent(DockingConstants.EAST_REGION, DockingConstants.WEST_REGION));
        assertFalse(DockingUtility.isAxisEquivalent(DockingConstants.CENTER_REGION, DockingConstants.NORTH_REGION));
        assertFalse(DockingUtility.isAxisEquivalent(DockingConstants.NORTH_REGION, "no-such-region"));
        assertFalse(DockingUtility.isAxisEquivalent(null, DockingConstants.NORTH_REGION));
    }

    @Test
    void testTranslateRegionAxisHorizontalSplit() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        assertEquals(DockingConstants.WEST_REGION, DockingUtility.translateRegionAxis(split, DockingConstants.NORTH_REGION));
        assertEquals(DockingConstants.EAST_REGION, DockingUtility.translateRegionAxis(split, DockingConstants.SOUTH_REGION));
        assertEquals(DockingConstants.WEST_REGION, DockingUtility.translateRegionAxis(split, DockingConstants.WEST_REGION));
        assertEquals(DockingConstants.EAST_REGION, DockingUtility.translateRegionAxis(split, DockingConstants.EAST_REGION));
    }

    @Test
    void testTranslateRegionAxisVerticalSplit() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        assertEquals(DockingConstants.NORTH_REGION, DockingUtility.translateRegionAxis(split, DockingConstants.WEST_REGION));
        assertEquals(DockingConstants.SOUTH_REGION, DockingUtility.translateRegionAxis(split, DockingConstants.EAST_REGION));
        assertEquals(DockingConstants.NORTH_REGION, DockingUtility.translateRegionAxis(split, DockingConstants.NORTH_REGION));
        assertEquals(DockingConstants.SOUTH_REGION, DockingUtility.translateRegionAxis(split, DockingConstants.SOUTH_REGION));
    }

    @Test
    void testTranslateRegionAxisWithInvalidArguments() {
        assertNull(DockingUtility.translateRegionAxis(null, DockingConstants.NORTH_REGION));
        assertNull(DockingUtility.translateRegionAxis(new JSplitPane(), "no-such-region"));
    }

    @Test
    void testRegionTopLeft() {
        assertTrue(DockingUtility.isRegionTopLeft(DockingConstants.NORTH_REGION));
        assertTrue(DockingUtility.isRegionTopLeft(DockingConstants.WEST_REGION));
        assertFalse(DockingUtility.isRegionTopLeft(DockingConstants.SOUTH_REGION));
        assertFalse(DockingUtility.isRegionTopLeft(DockingConstants.EAST_REGION));
        assertFalse(DockingUtility.isRegionTopLeft(DockingConstants.CENTER_REGION));
    }

    @Test
    void testRegionFromOrientationConstant() {
        assertEquals(DockingConstants.WEST_REGION, DockingUtility.getRegion(DockingConstants.LEFT));
        assertEquals(DockingConstants.EAST_REGION, DockingUtility.getRegion(DockingConstants.RIGHT));
        assertEquals(DockingConstants.NORTH_REGION, DockingUtility.getRegion(DockingConstants.TOP));
        assertEquals(DockingConstants.SOUTH_REGION, DockingUtility.getRegion(DockingConstants.BOTTOM));
        assertEquals(DockingConstants.CENTER_REGION, DockingUtility.getRegion(DockingConstants.CENTER));
        assertEquals(DockingConstants.UNKNOWN_REGION, DockingUtility.getRegion(-1));
    }

}
