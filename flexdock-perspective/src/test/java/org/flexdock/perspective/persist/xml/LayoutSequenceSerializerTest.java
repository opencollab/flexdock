package org.flexdock.perspective.persist.xml;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.state.DockingState;
import org.flexdock.perspective.LayoutSequence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LayoutSequenceSerializerTest {

    private final LayoutSequenceSerializer serializer = new LayoutSequenceSerializer();

    @BeforeAll
    static void registerSerializers() {
        XMLPersister.newDefaultInstance();
    }

    @Test
    void testOrderParentsRegionsAndRatiosSurvive() throws Exception {
        LayoutSequence sequence = new LayoutSequence();
        sequence.add("first");
        sequence.add("second", "first", DockingConstants.WEST_REGION, 0.3f);
        sequence.add("third", "second", DockingConstants.SOUTH_REGION, 0.6f);

        LayoutSequence result = (LayoutSequence) XmlRoundTrip.roundTrip(serializer, sequence);

        List<?> states = result.getDockingStates();
        assertEquals(3, states.size());
        DockingState first = (DockingState) states.get(0);
        DockingState second = (DockingState) states.get(1);
        DockingState third = (DockingState) states.get(2);

        assertEquals("first", first.getDockableId());
        assertNull(first.getRelativeParentId());
        assertEquals("second", second.getDockableId());
        assertEquals("first", second.getRelativeParentId());
        assertEquals(DockingConstants.WEST_REGION, second.getRegion());
        assertEquals(0.3f, second.getSplitRatio());
        assertEquals("third", third.getDockableId());
        assertEquals("second", third.getRelativeParentId());
        assertEquals(DockingConstants.SOUTH_REGION, third.getRegion());
        assertEquals(0.6f, third.getSplitRatio());
    }

    @Test
    void testEmptySequenceSurvives() throws Exception {
        LayoutSequence result = (LayoutSequence) XmlRoundTrip.roundTrip(serializer, new LayoutSequence());

        assertTrue(result.getDockingStates().isEmpty());
    }

}
