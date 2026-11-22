package org.flexdock.docking;

import org.flexdock.docking.state.LayoutManager;
import org.flexdock.docking.state.MinimizationManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Neither flexdock-view nor flexdock-perspective is on core's test classpath, so the
// default managers named in flexdock-core.properties cannot be created
class DockingManagerWithoutModulesTest {

    @Test
    void testStubsReplaceMissingDefaultManagers() {
        assertSame(MinimizationManager.DEFAULT_STUB, DockingManager.getMinimizeManager());
        assertSame(LayoutManager.DEFAULT_STUB, DockingManager.getLayoutManager());
    }

    @Test
    void testNullManagersFallBackToStubs() {
        DockingManager.setMinimizeManager((MinimizationManager) null);
        DockingManager.setLayoutManager((LayoutManager) null);

        assertSame(MinimizationManager.DEFAULT_STUB, DockingManager.getMinimizeManager());
        assertSame(LayoutManager.DEFAULT_STUB, DockingManager.getLayoutManager());
    }

}
