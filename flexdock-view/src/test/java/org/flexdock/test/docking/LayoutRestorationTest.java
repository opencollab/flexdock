package org.flexdock.test.docking;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.defaults.DefaultDockingStrategy;
import org.flexdock.perspective.Layout;
import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.persist.FilePersistenceHandler;
import org.flexdock.perspective.persist.PerspectiveModel;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.JSplitPane;
import java.awt.Component;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

// Save and restore of a layout without a window (a frame cannot be created headless, which rules
// out PerspectiveManager.store and restore): export the live tree, write it as XML, read it back
// and apply it to a new viewport.
class LayoutRestorationTest extends SiblingLayoutBase {

    private static final String PERSPECTIVE_ID = "restoration.test";

    private Layout createLayoutOf(Viewport source) {
        Layout layout = new Layout();
        layout.setRestorationLayout(source.exportLayout());

        return layout;
    }

    private static Component topmostParent(Component component) {
        Component result = component;
        while (result.getParent() != null) {
            result = result.getParent();
        }

        return result;
    }

    private void assertStructure(Viewport port) {
        assertSame(port, topmostParent(centre));
        assertInstanceOf(JSplitPane.class, port.getDockedComponent());

        assertSame(left, DefaultDockingStrategy.getSibling(centre, DockingConstants.WEST_REGION));
        assertSame(right, DefaultDockingStrategy.getSibling(centre, DockingConstants.EAST_REGION));
        assertSame(bottom, DefaultDockingStrategy.getSibling(centre, DockingConstants.SOUTH_REGION));
        assertSame(below, DefaultDockingStrategy.getSibling(left, DockingConstants.SOUTH_REGION));
        assertSame(centre, DefaultDockingStrategy.getSibling(left, DockingConstants.EAST_REGION));
    }

    @Test
    void testApplyRestoresLayoutIntoNewViewport() {
        Layout layout = createLayoutOf(viewport);
        Viewport target = new Viewport();

        layout.apply(target);

        assertStructure(target);
    }

    @Test
    void testApplyReplacesExistingContent() {
        Layout layout = createLayoutOf(viewport);
        View other = new View("restoration.other", "Other");
        Viewport target = new Viewport();
        target.dock(other);

        layout.apply(target);

        assertStructure(target);
        assertNull(other.getParent());
    }

    @Test
    void testUninitialisedLayoutLeavesViewportUntouched() {
        Viewport target = new Viewport();
        View other = new View("restoration.untouched", "Other");
        target.dock(other);

        new Layout().apply(target);

        assertSame(other, target.getDockedComponent());
    }

    @Test
    void testLayoutSurvivesXmlFile(@TempDir File dir) throws Exception {
        Perspective perspective = new Perspective(PERSPECTIVE_ID, "Restoration");
        perspective.getLayout().setRestorationLayout(viewport.exportLayout());
        FilePersistenceHandler handler = new FilePersistenceHandler(new File(dir, "perspectives.xml"));

        handler.store(null, new PerspectiveModel(PERSPECTIVE_ID, PERSPECTIVE_ID, new Perspective[] {perspective}));
        PerspectiveModel model = handler.load(null);

        Layout loaded = model.getPerspectives()[0].getLayout();
        Viewport target = new Viewport();
        loaded.apply(target);

        assertStructure(target);
    }

}
