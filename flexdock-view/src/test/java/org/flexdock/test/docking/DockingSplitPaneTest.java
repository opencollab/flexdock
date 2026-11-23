package org.flexdock.test.docking;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.defaults.DockingSplitPane;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DockingSplitPaneTest {

    private static DockingSplitPane createUnsizedSplit() {
        return new DockingSplitPane(new DefaultDockingPort(), DockingConstants.WEST_REGION);
    }

    @Test
    void testPercentSetBeforeSizeIsAppliedOnceSized() {
        DockingSplitPane split = createUnsizedSplit();

        split.setDividerLocation(.3);
        split.setBounds(0, 0, 1000, 600);

        int expected = (int) ((1000 - split.getDividerSize()) * .3);
        assertEquals(expected, split.getDividerLocation());
    }

    @Test
    void testIntLocationReplacesPendingPercent() {
        DockingSplitPane split = createUnsizedSplit();

        split.setDividerLocation(.3);
        split.setDividerLocation(100);
        split.setBounds(0, 0, 1000, 600);

        assertEquals(100, split.getDividerLocation());
    }

}
