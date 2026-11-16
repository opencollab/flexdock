package org.flexdock.test.docking;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.state.LayoutNode;
import org.flexdock.docking.state.tree.DockableNode;
import org.flexdock.docking.state.tree.DockingPortNode;
import org.flexdock.perspective.persist.xml.DockingPortNodeSerializer;
import org.flexdock.perspective.persist.xml.XMLPersister;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;

// Tests with live docking ports and dockables, which need DockingManager (and hence this module)
class TabSelectionPersistenceTest {

    @BeforeAll
    static void registerSerializers() {
        XMLPersister.newDefaultInstance();
    }

    private static Dockable[] createDockables(int count) {
        Dockable[] dockables = new Dockable[count];
        for (int i = 0; i < count; i++) {
            dockables[i] = DockingManager.registerDockable(new JPanel(), "tab" + i);
        }
        return dockables;
    }

    private static DockingPortNode createNode(String selectedId, Dockable... dockables) {
        DockingPortNode node = new DockingPortNode();
        for (Dockable dockable : dockables) {
            DockableNode child = new DockableNode();
            child.setDockableId(dockable.getPersistentId());
            node.add(child);
        }
        node.setSelectedDockableId(selectedId);

        return node;
    }

    private static JTabbedPane importLayout(LayoutNode node) {
        DefaultDockingPort port = new DefaultDockingPort();
        port.importLayout(node);

        return (JTabbedPane) port.getDockedComponent();
    }

    @Test
    void testStoredDockableIsSelected() {
        Dockable[] d = createDockables(3);
        JTabbedPane tabs = importLayout(createNode(d[1].getPersistentId(), d));

        assertEquals(3, tabs.getTabCount());
        assertSame(d[1].getComponent(), tabs.getSelectedComponent());
    }

    @Test
    void testFirstDockableIsSelectedWithoutStoredId() {
        Dockable[] d = createDockables(3);
        JTabbedPane tabs = importLayout(createNode(null, d));

        assertSame(d[0].getComponent(), tabs.getSelectedComponent());
    }

    @Test
    void testFirstDockableIsSelectedWithUnknownStoredId() {
        Dockable[] d = createDockables(3);
        JTabbedPane tabs = importLayout(createNode("no-such-dockable", d));

        assertSame(d[0].getComponent(), tabs.getSelectedComponent());
    }

    @Test
    void testStoredIdOfDockableOutsideGroupSelectsFirst() {
        Dockable[] d = createDockables(3);
        JTabbedPane tabs = importLayout(createNode(d[2].getPersistentId(), d[0], d[1]));

        assertSame(d[0].getComponent(), tabs.getSelectedComponent());
    }

    @Test
    void testSingleDockableWithStoredId() {
        Dockable[] d = createDockables(1);
        DefaultDockingPort port = new DefaultDockingPort();
        port.importLayout(createNode(d[0].getPersistentId(), d));

        assertNotNull(port.getDockedComponent());
        assertTrue(port.getDockables().contains(d[0]));
    }

    @Test
    void testSelectionSurvivesExportXmlAndImport() throws Exception {
        Dockable[] d = createDockables(3);
        DefaultDockingPort source = new DefaultDockingPort();
        source.importLayout(createNode(null, d));
        JTabbedPane sourceTabs = (JTabbedPane) source.getDockedComponent();
        sourceTabs.setSelectedComponent(d[1].getComponent());

        DockingPortNodeSerializer serializer = new DockingPortNodeSerializer();
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = serializer.serialize(document, source.exportLayout());
        document.appendChild(element);

        DockingPortNode restored = (DockingPortNode) serializer.deserialize(element);
        assertEquals(d[1].getPersistentId(), restored.getSelectedDockableId());

        // Docking the same dockables into a new port moves their components to its tabbed pane
        JTabbedPane tabs = importLayout(restored);
        assertEquals(3, tabs.getTabCount());
        assertSame(d[1].getComponent(), tabs.getSelectedComponent());
    }

}
