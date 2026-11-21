package org.flexdock.perspective.persist.xml;

import org.flexdock.docking.state.FloatingGroup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FloatingGroupSerializerTest {

    private final FloatingGroupSerializer serializer = new FloatingGroupSerializer();

    @BeforeAll
    static void registerSerializers() {
        XMLPersister.newDefaultInstance();
    }

    private static Set<String> getDockableIds(FloatingGroup group) {
        Set<String> ids = new HashSet<>();
        for (Iterator<?> it = group.getDockableIterator(); it.hasNext();) {
            ids.add((String) it.next());
        }

        return ids;
    }

    @Test
    void testNameBoundsAndDockablesSurvive() throws Exception {
        FloatingGroup group = new FloatingGroup("group");
        group.setBounds(new Rectangle(10, 20, 300, 200));
        group.addDockable("first");
        group.addDockable("second");

        FloatingGroup result = (FloatingGroup) XmlRoundTrip.roundTrip(serializer, group);

        assertEquals("group", result.getName());
        assertEquals(new Rectangle(10, 20, 300, 200), result.getBounds());
        assertEquals(2, result.getDockableCount());
        assertEquals(getDockableIds(group), getDockableIds(result));
    }

    @Test
    void testGroupWithoutDockablesSurvives() throws Exception {
        FloatingGroup group = new FloatingGroup("empty");
        group.setBounds(new Rectangle(0, 0, 100, 50));

        FloatingGroup result = (FloatingGroup) XmlRoundTrip.roundTrip(serializer, group);

        assertEquals("empty", result.getName());
        assertEquals(new Rectangle(0, 0, 100, 50), result.getBounds());
        assertEquals(0, result.getDockableCount());
    }

    @Test
    void testGroupWithoutBoundsSurvives() throws Exception {
        FloatingGroup group = new FloatingGroup("unbounded");
        group.addDockable("first");

        FloatingGroup result = (FloatingGroup) XmlRoundTrip.roundTrip(serializer, group);

        assertEquals("unbounded", result.getName());
        assertNull(result.getBounds());
        assertEquals(getDockableIds(group), getDockableIds(result));
    }

}
