package org.flexdock.perspective.persist;

import org.flexdock.perspective.Perspective;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FilePersistenceHandlerTest {

    private static PerspectiveModel createModel() {
        Perspective perspective = new Perspective("one", "First perspective");

        return new PerspectiveModel("one", "one", new Perspective[] {perspective});
    }

    @Test
    void testNullKeyUsesDefaultFile(@TempDir File dir) throws Exception {
        File defaultFile = new File(dir, "default.xml");
        FilePersistenceHandler handler = new FilePersistenceHandler(defaultFile);

        assertEquals(defaultFile, handler.getPerspectiveFile(null));

        handler.store(null, createModel());

        assertTrue(defaultFile.isFile());
        assertTrue(defaultFile.length() > 0);
    }

    @Test
    void testKeyWithSeparatorIsUsedAsPath(@TempDir File dir) throws Exception {
        FilePersistenceHandler handler = new FilePersistenceHandler(new File(dir, "default.xml"));
        File keyFile = new File(dir, "sub/key.xml");

        assertEquals(keyFile, handler.getPerspectiveFile(keyFile.getPath()));

        handler.store(keyFile.getPath(), createModel());

        // The missing directory is created and the default file is left alone
        assertTrue(keyFile.isFile());
        assertFalse(new File(dir, "default.xml").exists());
    }

    @Test
    void testKeyWithoutSeparatorIsFileInDefaultDirectory(@TempDir File dir) {
        FilePersistenceHandler handler = new FilePersistenceHandler(new File(dir, "default.xml"));

        File file = handler.getPerspectiveFile("name.xml");

        // Only the path is checked: storing would write to the user's home directory
        assertEquals("name.xml", file.getName());
        assertEquals(FilePersistenceHandler.DEFAULT_PERSPECTIVE_DIR.getAbsoluteFile(),
                file.getAbsoluteFile().getParentFile());
    }

    @Test
    void testLoadOfMissingFileGivesNull(@TempDir File dir) throws Exception {
        FilePersistenceHandler handler = new FilePersistenceHandler(new File(dir, "default.xml"));

        assertNull(handler.load(null));
        assertNull(handler.load(new File(dir, "missing.xml").getPath()));
        assertFalse(new File(dir, "default.xml").exists());
    }

    @Test
    void testStoredModelIsLoadedBack(@TempDir File dir) throws Exception {
        FilePersistenceHandler handler = new FilePersistenceHandler(new File(dir, "default.xml"));

        handler.store(null, createModel());
        PerspectiveModel result = handler.load(null);

        assertNotNull(result);
        assertEquals("one", result.getDefaultPerspective());
        assertEquals("one", result.getCurrentPerspective());
        assertEquals(1, result.getPerspectives().length);
        assertEquals("First perspective", result.getPerspectives()[0].getName());
    }

}
