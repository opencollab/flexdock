package org.flexdock.perspective.persist.xml;

import org.flexdock.perspective.LayoutSequence;
import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.persist.PerspectiveModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PerspectiveModelSerializerTest {

    private final PerspectiveModelSerializer serializer = new PerspectiveModelSerializer();

    @BeforeAll
    static void registerSerializers() {
        XMLPersister.newDefaultInstance();
    }

    @Test
    void testIdsAndPerspectivesSurvive() throws Exception {
        LayoutSequence sequence = new LayoutSequence();
        sequence.add("first");
        Perspective withSequence = new Perspective("one", "First perspective");
        withSequence.setInitialSequence(sequence);
        Perspective withoutSequence = new Perspective("two", "Second perspective");
        PerspectiveModel model = new PerspectiveModel("one", "two", new Perspective[] {withSequence, withoutSequence});

        PerspectiveModel result = (PerspectiveModel) XmlRoundTrip.roundTrip(serializer, model);

        assertEquals("one", result.getDefaultPerspective());
        assertEquals("two", result.getCurrentPerspective());
        Perspective[] perspectives = result.getPerspectives();
        assertEquals(2, perspectives.length);
        assertEquals("one", perspectives[0].getPersistentId());
        assertEquals("First perspective", perspectives[0].getName());
        assertEquals(1, perspectives[0].getInitialSequence().getDockingStates().size());
        assertEquals("two", perspectives[1].getPersistentId());
        assertNull(perspectives[1].getInitialSequence());
    }

    @Test
    void testModelWithoutPerspectivesSurvives() throws Exception {
        PerspectiveModel model = new PerspectiveModel("default", "current", new Perspective[0]);

        PerspectiveModel result = (PerspectiveModel) XmlRoundTrip.roundTrip(serializer, model);

        assertEquals("default", result.getDefaultPerspective());
        assertEquals("current", result.getCurrentPerspective());
        assertEquals(0, result.getPerspectives().length);
    }

}
