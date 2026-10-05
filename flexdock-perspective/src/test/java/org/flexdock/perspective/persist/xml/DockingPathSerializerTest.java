package org.flexdock.perspective.persist.xml;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.state.DockingPath;
import org.flexdock.docking.state.tree.SplitNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DockingPathSerializerTest {

    private final DockingPathSerializer serializer = new DockingPathSerializer();

    @BeforeAll
    static void registerSerializers() {
        // registers all serializers that path serializer relies on for its split nodes
        XMLPersister.newDefaultInstance();
    }

    @Test
    void testRoundTripKeepsAllSplitNodesOfPath() throws Exception {
        DockingPath path = new DockingPath();
        path.setRootPortId("rootPort");
        path.setTabbed(true);
        path.setSiblingId("sibling");
        List nodes = path.getNodes();
        nodes.add(new SplitNode(DockingConstants.HORIZONTAL, DockingConstants.LEFT, 0.87f, null));
        nodes.add(new SplitNode(DockingConstants.VERTICAL, DockingConstants.BOTTOM, 0.8f, "id123"));
        nodes.add(new SplitNode(DockingConstants.HORIZONTAL, DockingConstants.RIGHT, 0.3f, null));

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element element = serializer.serialize(document, path);
        document.appendChild(element);

        DockingPath result = (DockingPath) serializer.deserialize(element);

        assertEquals("rootPort", result.getRootPortId());
        assertTrue(result.isTabbed());
        assertEquals("sibling", result.getSiblingId());
        List resultNodes = result.getNodes();
        assertEquals(3, resultNodes.size());
        checkSplitNode((SplitNode) resultNodes.get(0), DockingConstants.HORIZONTAL, DockingConstants.LEFT, 0.87f, null);
        checkSplitNode((SplitNode) resultNodes.get(1), DockingConstants.VERTICAL, DockingConstants.BOTTOM, 0.8f, "id123");
        checkSplitNode((SplitNode) resultNodes.get(2), DockingConstants.HORIZONTAL, DockingConstants.RIGHT, 0.3f, null);
    }

    @Test
    void testRoundTripKeepsPathWithoutSplitNodes() throws Exception {
        DockingPath path = new DockingPath();
        path.setRootPortId("rootPort");

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element element = serializer.serialize(document, path);
        document.appendChild(element);

        DockingPath result = (DockingPath) serializer.deserialize(element);

        assertEquals("rootPort", result.getRootPortId());
        assertFalse(result.isTabbed());
        assertTrue(result.getNodes().isEmpty());
    }

    @Test
    void testDeserializeReadsAllSplitNodesOfStoredPath() throws Exception {
        String xmlSample = "<DockingPath isTabbed=\"true\" rootPortId=\"defaultDockingPort\">"
                + "<SplitNode orientation=\"horizontal\" percentage=\"0.44\" region=\"left\"/>"
                + "<SplitNode orientation=\"vertical\" percentage=\"0.77\" region=\"bottom\"/>"
                + "</DockingPath>";

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        try (ByteArrayInputStream is = new ByteArrayInputStream(xmlSample.getBytes(StandardCharsets.UTF_8))) {
            Element element = documentBuilder.parse(is).getDocumentElement();

            DockingPath path = (DockingPath) serializer.deserialize(element);

            assertEquals("defaultDockingPort", path.getRootPortId());
            assertTrue(path.isTabbed());

            List nodes = path.getNodes();
            assertEquals(2, nodes.size());
            checkSplitNode((SplitNode) nodes.get(0), DockingConstants.HORIZONTAL, DockingConstants.LEFT, 0.44f, null);
            checkSplitNode((SplitNode) nodes.get(1), DockingConstants.VERTICAL, DockingConstants.BOTTOM, 0.77f, null);
        }
    }

    private static void checkSplitNode(SplitNode node,
            int expOrientation, int expRegion, float expPercentage, String expSiblingId) {

        assertEquals(expOrientation, node.getOrientation());
        assertEquals(expRegion, node.getRegion());
        assertEquals(expPercentage, node.getPercentage(), 0.0001f);
        assertEquals(expSiblingId, node.getSiblingId());
    }

}
