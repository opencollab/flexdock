package org.flexdock.test.docking;

import org.flexdock.docking.DockingConstants;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;
import org.junit.jupiter.api.BeforeEach;

// Base for tests that need the layout of the manual SiblingTest demo:
//   +-------+--------+---------+
//   | left  | centre |  right  |
//   +-------+--------+---------+
//   | below |      bottom      |
//   +-------+------------------+
//   (left is docked west of centre, bottom south of centre, right east of
//   centre and below south of left; the two rows have independent dividers)
// Persistent ids are prefixed with the test class name, as the registries are shared by all tests.
abstract class SiblingLayoutBase {

    protected Viewport viewport;
    protected View centre;
    protected View left;
    protected View bottom;
    protected View below;
    protected View right;

    @BeforeEach
    void createLayout() {
        String prefix = getClass().getSimpleName() + ".";
        viewport = new Viewport();
        centre = new View(prefix + "centre", null, null);
        left = new View(prefix + "left", "Left");
        bottom = new View(prefix + "bottom", "Bottom");
        below = new View(prefix + "below", "Below");
        right = new View(prefix + "right", "Right");

        viewport.dock(centre);
        centre.dock(left, DockingConstants.WEST_REGION, .3f);
        centre.dock(bottom, DockingConstants.SOUTH_REGION, .3f);
        centre.dock(right, DockingConstants.EAST_REGION, .3f);
        left.dock(below, DockingConstants.SOUTH_REGION, .3f);
    }

}
