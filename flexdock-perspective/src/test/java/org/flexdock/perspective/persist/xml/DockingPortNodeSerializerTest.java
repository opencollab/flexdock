package org.flexdock.perspective.persist.xml;

import org.flexdock.docking.state.tree.DockableNode;
import org.flexdock.docking.state.tree.DockingPortNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;

class DockingPortNodeSerializerTest {

    private final DockingPortNodeSerializer serializer = new DockingPortNodeSerializer();

    @BeforeAll
    static void registerSerializers() {
        // registers all serializers that port node serializer relies on for its children
        XMLPersister.newDefaultInstance();
    }

    private DockingPortNode roundTrip(DockingPortNode node) throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = serializer.serialize(document, node);
        document.appendChild(element);

        return (DockingPortNode) serializer.deserialize(element);
    }

    private DockingPortNode createNode(String selectedDockableId) {
        DockingPortNode node = new DockingPortNode();
        for (String id : new String[] {"first", "second", "third"}) {
            DockableNode child = new DockableNode();
            child.setDockableId(id);
            node.add(child);
        }
        node.setSelectedDockableId(selectedDockableId);

        return node;
    }

    @Test
    void testRoundTripKeepsSelectedDockable() throws Exception {
        DockingPortNode result = roundTrip(createNode("second"));

        assertEquals("second", result.getSelectedDockableId());
        assertEquals(3, result.getChildCount());
    }

    @Test
    void testRoundTripWithoutSelectedDockable() throws Exception {
        DockingPortNode result = roundTrip(createNode(null));

        assertNull(result.getSelectedDockableId());
        assertEquals(3, result.getChildCount());
    }

    @Test
    void testCloneKeepsSelectedDockable() {
        DockingPortNode clone = (DockingPortNode) createNode("third").clone();

        assertEquals("third", clone.getSelectedDockableId());
    }

}
