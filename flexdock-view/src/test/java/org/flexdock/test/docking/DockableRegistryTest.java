package org.flexdock.test.docking;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockableFactory;
import org.flexdock.docking.DockingManager;
import org.flexdock.view.View;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import java.awt.Component;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

// The registry is JVM-wide: use unique ids and put the dockable factory back
class DockableRegistryTest {

    private DockableFactory originalFactory;

    @BeforeEach
    void rememberFactory() {
        originalFactory = DockingManager.getDockableFactory();
    }

    @AfterEach
    void restoreFactory() {
        DockingManager.setDockableFactory(originalFactory);
    }

    @Test
    void testRegisteredComponentIsFoundByComponentAndId() {
        JPanel panel = new JPanel();

        Dockable dockable = DockingManager.registerDockable(panel, "Tab text");

        assertNotNull(dockable);
        assertSame(panel, dockable.getComponent());
        assertNotNull(dockable.getPersistentId());
        assertSame(dockable, DockingManager.getDockable(panel));
        assertSame(dockable, DockingManager.getDockable(dockable.getPersistentId()));
        assertTrue(DockingManager.getDockableIds().contains(dockable.getPersistentId()));
    }

    @Test
    void testRegisteringNothingGivesNull() {
        assertNull(DockingManager.registerDockable((Component) null));
        assertNull(DockingManager.registerDockable((Dockable) null));
        assertNull(DockingManager.registerDockable(null, "Tab text"));
    }

    @Test
    void testViewIsRegisteredByItsOwnId() {
        View view = new View("registry.view", "View");

        assertSame(view, DockingManager.getDockable("registry.view"));
        assertSame(view, DockingManager.getDockable((Component) view));
        assertSame(view, DockingManager.registerDockable((Dockable) view));
        assertTrue(DockingManager.getDockableIds().contains("registry.view"));
    }

    @Test
    void testUnknownLookupsGiveNull() {
        DockingManager.setDockableFactory(null);

        assertNull(DockingManager.getDockable("registry.unknown"));
        assertNull(DockingManager.getDockable((String) null));
        assertNull(DockingManager.getDockable((Component) null));
        assertNull(DockingManager.getDockable(new JPanel()));
    }

    @Test
    void testUnregisterByDockable() {
        View view = new View("registry.by.dockable", "View");

        DockingManager.unregisterDockable((Dockable) view);

        assertNull(lookupWithoutFactory("registry.by.dockable"));
        assertNull(DockingManager.getDockable((Component) view));
        assertFalse(DockingManager.getDockableIds().contains("registry.by.dockable"));
    }

    @Test
    void testUnregisterById() {
        View view = new View("registry.by.id", "View");

        DockingManager.unregisterDockable("registry.by.id");

        assertNull(lookupWithoutFactory("registry.by.id"));
        assertNull(DockingManager.getDockable((Component) view));
    }

    @Test
    void testUnregisterByComponent() {
        View view = new View("registry.by.component", "View");

        DockingManager.unregisterDockable((Component) view);

        assertNull(lookupWithoutFactory("registry.by.component"));
    }

    @Test
    void testUnregisteringUnknownDockableChangesNothing() {
        int count = DockingManager.getDockableIds().size();

        DockingManager.unregisterDockable("registry.never.registered");
        DockingManager.unregisterDockable((Dockable) null);

        assertEquals(count, DockingManager.getDockableIds().size());
    }

    @Test
    void testFactoryCreatesUnknownDockableOnce() {
        AtomicInteger calls = new AtomicInteger();
        DockingManager.setDockableFactory(new DockableFactory.Stub() {
            @Override
            public Dockable getDockable(String dockableId) {
                calls.incrementAndGet();
                return new View(dockableId, "Created");
            }
        });

        Dockable created = DockingManager.getDockable("registry.factory.created");

        assertNotNull(created);
        assertEquals("registry.factory.created", created.getPersistentId());
        assertSame(created, DockingManager.getDockable("registry.factory.created"));
        assertEquals(1, calls.get());
    }

    @Test
    void testFactoryComponentIsRegistered() {
        JPanel panel = new JPanel();
        DockingManager.setDockableFactory(new DockableFactory.Stub() {
            @Override
            public Component getDockableComponent(String dockableId) {
                return panel;
            }
        });

        Dockable created = DockingManager.getDockable("registry.factory.component");

        assertNotNull(created);
        assertSame(panel, created.getComponent());
        assertSame(created, DockingManager.getDockable(panel));
    }

    @Test
    void testFactoryThatKnowsNothingGivesNull() {
        DockingManager.setDockableFactory(new DockableFactory.Stub());

        assertNull(DockingManager.getDockable("registry.factory.nothing"));
    }

    private static Dockable lookupWithoutFactory(String id) {
        DockableFactory factory = DockingManager.getDockableFactory();
        DockingManager.setDockableFactory(null);
        try {
            return DockingManager.getDockable(id);
        } finally {
            DockingManager.setDockableFactory(factory);
        }
    }

}
