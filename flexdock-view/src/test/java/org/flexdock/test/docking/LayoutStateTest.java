package org.flexdock.test.docking;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.FloatingGroup;
import org.flexdock.docking.state.tree.DockingPortNode;
import org.flexdock.perspective.Layout;
import org.flexdock.perspective.LayoutSequence;
import org.flexdock.perspective.Perspective;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.*;

class LayoutStateTest {

    private Layout layout;
    private Dockable known;
    private Dockable other;

    @BeforeEach
    void createLayout() {
        layout = new Layout();
        known = new View("state.known", "Known");
        other = new View("state.other", "Other");
    }

    @Test
    void testAddCreatesStateOnce() {
        layout.add("state.known");
        DockingState state = layout.getDockingState("state.known");

        layout.add(known);

        assertTrue(layout.contains("state.known"));
        assertTrue(layout.contains(known));
        assertSame(state, layout.getDockingState(known));
    }

    @Test
    void testAddIgnoresNull() {
        layout.add((String) null);
        layout.add((Dockable) null);

        assertEquals(0, layout.getDockables().length);
        assertFalse(layout.contains((String) null));
        assertNull(layout.getDockingState((String) null));
    }

    @Test
    void testRemoveReturnsStateOnce() {
        layout.add(known);
        DockingState state = layout.getDockingState(known);

        assertSame(state, layout.remove("state.known"));
        assertFalse(layout.contains(known));
        assertNull(layout.remove("state.known"));
        assertNull(layout.remove(null));
    }

    @Test
    void testGetDockablesDropsUnregisteredIds() {
        layout.add(known);
        layout.add("state.not.registered");

        Dockable[] dockables = layout.getDockables();

        assertEquals(1, dockables.length);
        assertSame(known, dockables[0]);
        // The state itself is kept, only the dockable lookup fails
        assertTrue(layout.contains("state.not.registered"));
        assertNull(layout.getDockable("state.not.registered"));
        assertSame(known, layout.getDockable("state.known"));
    }

    @Test
    void testLoadingStateAddsRegisteredDockable() {
        assertNull(layout.getDockingState(known));

        assertNotNull(layout.getDockingState(known, true));
        assertTrue(layout.contains(known));
        assertNull(layout.getDockingState("state.not.registered", true));
    }

    @Test
    void testCloneIsIndependentOfOriginal() {
        layout.add(known);
        layout.getDockingState(known).setRegion(DockingConstants.WEST_REGION);
        layout.setRestorationLayout(new DockingPortNode());

        Layout clone = (Layout) layout.clone();
        clone.getDockingState(known).setRegion(DockingConstants.EAST_REGION);
        clone.add(other);
        clone.remove("state.known");

        assertEquals(DockingConstants.WEST_REGION, layout.getDockingState(known).getRegion());
        assertFalse(layout.contains(other));
        assertTrue(layout.contains(known));
        assertNotSame(layout.getRestorationLayout(), clone.getRestorationLayout());
        assertTrue(clone.isInitialized());
    }

    @Test
    void testCloneOfUninitialisedLayoutIsUninitialised() {
        assertFalse(layout.isInitialized());
        assertFalse(((Layout) layout.clone()).isInitialized());
    }

    @Test
    void testCloneCopiesFloatingGroups() {
        FloatingGroup group = new FloatingGroup("state.group");
        group.setBounds(new Rectangle(1, 2, 3, 4));
        layout.addFloatingGroup(group);

        Layout clone = (Layout) layout.clone();
        clone.getGroup("state.group").setBounds(new Rectangle(5, 6, 7, 8));

        assertEquals(new Rectangle(1, 2, 3, 4), layout.getGroup("state.group").getBounds());
        assertEquals(new Rectangle(5, 6, 7, 8), clone.getGroup("state.group").getBounds());
    }

    @Test
    void testDockableJoinsOneFloatingGroupAtATime() {
        layout.add(known);
        layout.addFloatingGroup(new FloatingGroup("state.first"));
        layout.addFloatingGroup(new FloatingGroup("state.second"));

        layout.addToGroup(known, "state.first");
        assertEquals("state.first", layout.getDockingState(known).getFloatingGroup());
        assertEquals(1, layout.getGroup("state.first").getDockableCount());

        layout.addToGroup(known, "state.second");

        assertEquals("state.second", layout.getDockingState(known).getFloatingGroup());
        assertEquals(1, layout.getGroup("state.second").getDockableCount());
        // The first group is empty now and has been disposed of
        assertNull(layout.getGroup("state.first"));
    }

    @Test
    void testRemovingLastDockableDisposesGroup() {
        layout.add(known);
        layout.add(other);
        layout.addFloatingGroup(new FloatingGroup("state.group"));
        layout.addToGroup(known, "state.group");
        layout.addToGroup(other, "state.group");

        layout.removeFromGroup(known);

        assertNull(layout.getDockingState(known).getFloatingGroup());
        assertNotNull(layout.getGroup("state.group"));

        layout.removeFromGroup(other);

        assertNull(layout.getGroup("state.group"));
        assertEquals(0, layout.getFloatingGroupIds().length);
    }

    @Test
    void testPerspectiveRequiresIdAndName() {
        assertThrows(IllegalArgumentException.class, () -> new Perspective(null, "name"));
        assertThrows(IllegalArgumentException.class, () -> new Perspective("id", null));
    }

    @Test
    void testPerspectiveCloneIsIndependent() {
        Perspective perspective = new Perspective("state.perspective", "Perspective");
        perspective.addDockable("state.known");
        perspective.getInitialSequence(true).add("state.known");

        Perspective clone = (Perspective) perspective.clone();
        clone.addDockable("state.other");
        clone.getInitialSequence().add("state.other", "state.known");

        assertEquals("state.perspective", clone.getPersistentId());
        assertEquals("Perspective", clone.getName());
        assertNull(perspective.getDockingState("state.other"));
        assertEquals(1, perspective.getInitialSequence().getDockingStates().size());
        assertEquals(2, clone.getInitialSequence().getDockingStates().size());
    }

    @Test
    void testPerspectiveWithoutSequenceClones() {
        Perspective clone = (Perspective) new Perspective("state.plain", "Plain").clone();

        assertNull(clone.getInitialSequence());
    }

    @Test
    void testResetAppliesInitialSequenceAndUpdatesLayout() {
        View centre = new View("state.centre", null, null);
        View side = new View("state.side", "Side");
        Viewport viewport = new Viewport();
        LayoutSequence sequence = new LayoutSequence();
        sequence.add("state.centre");
        sequence.add("state.side", "state.centre", DockingConstants.WEST_REGION, .3f);
        Perspective perspective = new Perspective("state.reset", "Reset");
        perspective.setInitialSequence(sequence);

        perspective.reset(viewport);

        assertTrue(DockingManager.isDocked((Dockable) centre));
        assertTrue(DockingManager.isDocked((Dockable) side));
        assertSame(side.getComponent(), viewport.getComponent(DockingConstants.WEST_REGION));
        assertSame(centre.getComponent(), viewport.getComponent(DockingConstants.EAST_REGION));
        assertEquals(DockingConstants.WEST_REGION, perspective.getDockingState("state.side").getRegion());
        assertEquals(.3f, perspective.getDockingState("state.side").getSplitRatio());
    }

    @Test
    void testResetWithoutSequenceLeavesViewportUntouched() {
        View view = new View("state.untouched", "Untouched");
        Viewport viewport = new Viewport();
        viewport.dock(view);

        new Perspective("state.noseq", "No sequence").reset(viewport);

        assertTrue(DockingManager.isDocked((Dockable) view));
    }

}
