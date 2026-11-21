package org.flexdock.test.docking;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.state.LayoutNode;
import org.flexdock.docking.state.tree.DockingPortNode;
import org.flexdock.docking.state.tree.SplitNode;
import org.flexdock.perspective.persist.xml.DockingPortNodeSerializer;
import org.flexdock.perspective.persist.xml.XMLPersister;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;

class SplitPersistenceTest {

    private static final float DELTA = 0.01f;

    @BeforeAll
    static void registerSerializers() {
        XMLPersister.newDefaultInstance();
    }

    private static DefaultDockingPort createSplitPort(String prefix, String region) {
        Dockable base = DockingManager.registerDockable(new JPanel(), prefix + ".base");
        Dockable added = DockingManager.registerDockable(new JPanel(), prefix + ".added");

        DefaultDockingPort port = new DefaultDockingPort();
        port.dock(base, DockingConstants.CENTER_REGION);
        DockingManager.dock(added, base, region);

        return port;
    }

    private static SplitNode getSplitNode(LayoutNode layout) {
        DockingPortNode root = assertInstanceOf(DockingPortNode.class, layout);
        assertTrue(root.isSplit());

        return assertInstanceOf(SplitNode.class, root.getChildAt(0));
    }

    private static LayoutNode roundTripThroughXml(LayoutNode layout) throws Exception {
        DockingPortNodeSerializer serializer = new DockingPortNodeSerializer();
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = serializer.serialize(document, layout);
        document.appendChild(element);

        return (LayoutNode) serializer.deserialize(element);
    }

    @Test
    void testExportKeepsHorizontalOrientation() {
        DefaultDockingPort port = createSplitPort("ratio.west", DockingConstants.WEST_REGION);

        assertEquals(SplitNode.HORIZONTAL, getSplitNode(port.exportLayout()).getOrientation());
    }

    @Test
    void testExportKeepsVerticalOrientation() {
        DefaultDockingPort port = createSplitPort("ratio.south", DockingConstants.SOUTH_REGION);

        assertEquals(SplitNode.VERTICAL, getSplitNode(port.exportLayout()).getOrientation());
    }

    @Test
    void testProportionSurvivesXml() throws Exception {
        DefaultDockingPort port = createSplitPort("ratio.xml", DockingConstants.WEST_REGION);
        LayoutNode layout = port.exportLayout();
        getSplitNode(layout).setPercentage(.3f);

        SplitNode restored = getSplitNode(roundTripThroughXml(layout));

        assertEquals(.3f, restored.getPercentage(), DELTA);
        assertEquals(SplitNode.HORIZONTAL, restored.getOrientation());
    }

    @Test
    void testOrientationSurvivesXmlAndImport() throws Exception {
        DefaultDockingPort source = createSplitPort("ratio.import", DockingConstants.SOUTH_REGION);
        LayoutNode restored = roundTripThroughXml(source.exportLayout());

        DefaultDockingPort target = new DefaultDockingPort();
        target.importLayout(restored);

        assertEquals(SplitNode.VERTICAL, getSplitNode(target.exportLayout()).getOrientation());
    }

}
