package org.flexdock.test.docking;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.defaults.DefaultDockingStrategy;
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
import javax.swing.JSplitPane;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Component;
import java.awt.Container;

import static org.junit.jupiter.api.Assertions.*;

class SplitPersistenceTest {

    private static final float DELTA = 0.01f;

    @BeforeAll
    static void registerSerializers() {
        XMLPersister.newDefaultInstance();
    }

    private static DefaultDockingPort createSplitPort(String region) {
        Dockable base = DockingManager.registerDockable(new JPanel(), "Base");
        Dockable added = DockingManager.registerDockable(new JPanel(), "Added");

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

    private static void layoutTree(Component component) {
        component.doLayout();
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                layoutTree(child);
            }
        }
    }

    // Proportion of the split after importing a stored layout into a sized port. There is no
    // peer, so validate() does nothing: size the port and run doLayout() on the whole tree instead.
    private static float importStoredProportion(boolean constantPercent, float percent) throws Exception {
        DefaultDockingPort source = createSplitPort(DockingConstants.WEST_REGION);
        LayoutNode layout = source.exportLayout();
        getSplitNode(layout).setPercentage(percent);
        LayoutNode restored = roundTripThroughXml(layout);

        // The flag is read when the split panes are created, so set it before the import
        DefaultDockingStrategy.keepConstantPercentage(constantPercent);
        try {
            DefaultDockingPort target = new DefaultDockingPort();
            target.setSize(1000, 600);
            target.importLayout(restored);
            layoutTree(target);

            // Measure the divider itself: with constant percent, export returns the stored percent
            JSplitPane split = assertInstanceOf(JSplitPane.class, target.getDockedComponent());

            return (float) split.getDividerLocation() / split.getWidth();
        } finally {
            DefaultDockingStrategy.keepConstantPercentage(false);
        }
    }

    @Test
    void testExportKeepsHorizontalOrientation() {
        DefaultDockingPort port = createSplitPort(DockingConstants.WEST_REGION);

        assertEquals(SplitNode.HORIZONTAL, getSplitNode(port.exportLayout()).getOrientation());
    }

    @Test
    void testExportKeepsVerticalOrientation() {
        DefaultDockingPort port = createSplitPort(DockingConstants.SOUTH_REGION);

        assertEquals(SplitNode.VERTICAL, getSplitNode(port.exportLayout()).getOrientation());
    }

    @Test
    void testProportionSurvivesXml() throws Exception {
        DefaultDockingPort port = createSplitPort(DockingConstants.WEST_REGION);
        LayoutNode layout = port.exportLayout();
        getSplitNode(layout).setPercentage(.3f);

        SplitNode restored = getSplitNode(roundTripThroughXml(layout));

        assertEquals(.3f, restored.getPercentage(), DELTA);
        assertEquals(SplitNode.HORIZONTAL, restored.getOrientation());
    }

    @Test
    void testOrientationSurvivesXmlAndImport() throws Exception {
        DefaultDockingPort source = createSplitPort(DockingConstants.SOUTH_REGION);
        LayoutNode restored = roundTripThroughXml(source.exportLayout());

        DefaultDockingPort target = new DefaultDockingPort();
        target.importLayout(restored);

        assertEquals(SplitNode.VERTICAL, getSplitNode(target.exportLayout()).getOrientation());
    }

    @Test
    void testProportionIsRestoredWithConstantPercent() throws Exception {
        assertEquals(.3f, importStoredProportion(true, .3f), DELTA);
    }

    // Below 1/3 the unsized split used to come back at 0.5, from 1/3 at Swing's preferred sizes
    @Test
    void testProportionIsRestoredWithoutConstantPercent() throws Exception {
        assertEquals(.3f, importStoredProportion(false, .3f), DELTA);
        assertEquals(.7f, importStoredProportion(false, .7f), DELTA);
    }

}
