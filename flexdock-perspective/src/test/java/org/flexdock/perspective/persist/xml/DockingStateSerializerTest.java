package org.flexdock.perspective.persist.xml;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.state.DockingPath;
import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.MinimizationManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

class DockingStateSerializerTest {

    private final DockingStateSerializer serializer = new DockingStateSerializer();

    @BeforeAll
    static void registerSerializers() {
        XMLPersister.newDefaultInstance();
    }

    private DockingState roundTrip(DockingState state) throws Exception {
        return (DockingState) XmlRoundTrip.roundTrip(serializer, state);
    }

    @Test
    void testOpenedStateKeepsParentRegionAndRatio() throws Exception {
        DockingState state = new DockingState("view");
        state.setRelativeParentId("parent");
        state.setRegion(DockingConstants.WEST_REGION);
        state.setSplitRatio(0.25f);

        DockingState result = roundTrip(state);

        assertEquals("view", result.getDockableId());
        assertEquals("parent", result.getRelativeParentId());
        assertEquals(DockingConstants.WEST_REGION, result.getRegion());
        assertEquals(0.25f, result.getSplitRatio());
        assertFalse(result.isMinimized());
        assertFalse(result.isFloating());
    }

    @Test
    void testDefaultsAreNotWritten() throws Exception {
        DockingState state = new DockingState("view");
        state.setRegion(DockingConstants.CENTER_REGION);

        Element element = XmlRoundTrip.serialize(serializer, state);

        assertFalse(element.hasAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_RELATIVE_PARENT_ID));
        assertFalse(element.hasAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_SPLIT_RATIO));
        assertFalse(element.hasAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_FLOATING_GROUP_NAME));
        assertFalse(element.hasAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_MINIMIZE_CONSTRAINT));

        DockingState result = (DockingState) serializer.deserialize(element);

        assertNull(result.getRelativeParentId());
        assertEquals(DockingConstants.UNINITIALIZED_RATIO, result.getSplitRatio());
        assertFalse(result.hasCenterPoint());
        assertFalse(result.hasDockingPath());
    }

    @Test
    void testMinimizedConstraintsSurvive() throws Exception {
        int[] constraints = {
            MinimizationManager.LEFT,
            MinimizationManager.RIGHT,
            MinimizationManager.TOP,
            MinimizationManager.BOTTOM,
            MinimizationManager.CENTER
        };

        for (int constraint : constraints) {
            DockingState state = new DockingState("view");
            state.setRegion(DockingConstants.CENTER_REGION);
            state.setMinimizedConstraint(constraint);

            DockingState result = roundTrip(state);

            assertTrue(result.isMinimized(), "constraint " + constraint);
            assertEquals(constraint, result.getMinimizedConstraint());
        }
    }

    @Test
    void testFloatingGroupSurvives() throws Exception {
        DockingState state = new DockingState("view");
        state.setRegion(DockingConstants.CENTER_REGION);
        state.setFloatingGroup("group");

        DockingState result = roundTrip(state);

        assertTrue(result.isFloating());
        assertEquals("group", result.getFloatingGroup());
        assertFalse(result.isMinimized());
    }

    @Test
    void testCenterPointAndPathSurvive() throws Exception {
        DockingPath path = new DockingPath();
        path.setRootPortId("root");
        DockingState state = new DockingState("view");
        state.setRegion(DockingConstants.CENTER_REGION);
        state.setCenter(new Point(30, 70));
        state.setPath(path);

        DockingState result = roundTrip(state);

        assertTrue(result.hasCenterPoint());
        assertEquals(new Point(30, 70), result.getCenterPoint());
        assertTrue(result.hasDockingPath());
        assertEquals("root", result.getPath().getRootPortId());
    }

    @Test
    void testRegionIsWrittenInLowerCase() throws Exception {
        DockingState state = new DockingState("view");
        state.setRegion(DockingConstants.SOUTH_REGION);

        Element element = XmlRoundTrip.serialize(serializer, state);

        assertEquals("south", element.getAttribute(PersistenceConstants.DOCKING_STATE_ATTRIBUTE_REGION));
    }

}
