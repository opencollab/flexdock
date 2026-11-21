package org.flexdock.test.docking;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.state.DockingState;
import org.flexdock.perspective.PerspectiveManager;
import org.flexdock.perspective.restore.handlers.AlreadyRestoredHandler;
import org.flexdock.perspective.restore.handlers.RelativeHandler;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

// DockingManager.display needs a window, so the handlers it chains (see RestorationManager) are
// tested one by one instead
class RestorationHandlerTest {

    private static DockingState createState(String dockableId, String parentId, String region, float ratio) {
        DockingState state = new DockingState(dockableId);
        state.setRelativeParentId(parentId);
        state.setRegion(region);
        state.setSplitRatio(ratio);

        return state;
    }

    private static boolean isDocked(Dockable dockable) {
        return DockingManager.isDocked(dockable);
    }

    @Test
    void testRelativeHandlerRedocksClosedDockableNextToParent() {
        Viewport viewport = new Viewport();
        View parent = new View("restore.parent", null, null);
        View child = new View("restore.child", "Child");
        viewport.dock(parent);
        parent.dock(child, DockingConstants.WEST_REGION, .3f);
        PerspectiveManager.getInstance().getDockingState(parent, true);
        DockingManager.close(child);
        assertFalse(isDocked(child));

        boolean restored = new RelativeHandler().restore(child,
                createState("restore.child", "restore.parent", DockingConstants.WEST_REGION, .3f), new HashMap());

        assertTrue(restored);
        assertTrue(isDocked(child));
        assertSame(child.getComponent(), viewport.getComponent(DockingConstants.WEST_REGION));
        assertSame(parent.getComponent(), viewport.getComponent(DockingConstants.EAST_REGION));
    }

    @Test
    void testRelativeHandlerDeclinesWithoutParent() {
        View child = new View("restore.orphan", "Orphan");

        assertFalse(new RelativeHandler().restore(child,
                createState("restore.orphan", null, DockingConstants.WEST_REGION, .3f), new HashMap()));

        assertFalse(new RelativeHandler().restore(child, null, new HashMap()));
        assertFalse(isDocked(child));
    }

    @Test
    void testRelativeHandlerDeclinesWhenParentIsNotDocked() {
        // Registered, so the state resolves its parent, but never docked
        new View("restore.closed.parent", "Parent");
        View child = new View("restore.closed.child", "Child");

        assertFalse(new RelativeHandler().restore(child,
                createState("restore.closed.child", "restore.closed.parent", DockingConstants.WEST_REGION, .3f),
                new HashMap()));

        assertFalse(isDocked(child));
    }

    @Test
    void testRelativeHandlerDeclinesWhenParentIsFloating() {
        Viewport viewport = new Viewport();
        View parent = new View("restore.floating.parent", null, null);
        View child = new View("restore.floating.child", "Child");
        viewport.dock(parent);
        PerspectiveManager.getInstance().getDockingState(parent, true).setFloatingGroup("restore.group");

        assertFalse(new RelativeHandler().restore(child,
                createState("restore.floating.child", "restore.floating.parent", DockingConstants.WEST_REGION, .3f),
                new HashMap()));

        assertFalse(isDocked(child));
    }

    @Test
    void testAlreadyRestoredHandlerOnlyAcceptsDockedDockables() {
        Viewport viewport = new Viewport();
        View docked = new View("restore.docked", "Docked");
        View closed = new View("restore.never.docked", "Closed");
        viewport.dock(docked);

        assertTrue(new AlreadyRestoredHandler().restore(docked, new DockingState("restore.docked"), new HashMap()));
        assertFalse(new AlreadyRestoredHandler().restore(closed, new DockingState("restore.never.docked"),
                new HashMap()));
    }

}
