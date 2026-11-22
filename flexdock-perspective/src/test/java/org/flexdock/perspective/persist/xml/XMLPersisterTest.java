package org.flexdock.perspective.persist.xml;

import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.persist.PerspectiveModel;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class XMLPersisterTest {

    private static final String E_ACUTE = String.valueOf((char) 0xE9);

    // Fails without the fix only where the platform charset is not UTF-8, e.g. on
    // Windows with -DargLine=-Dfile.encoding=COMPAT (JDK 18+ defaults to UTF-8)
    @Test
    void testNonAsciiNameSurvives() throws Exception {
        String name = "Caf" + E_ACUTE;
        Perspective perspective = new Perspective("one", name);
        PerspectiveModel model = new PerspectiveModel("one", "one", new Perspective[] {perspective});
        XMLPersister persister = XMLPersister.newDefaultInstance();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        persister.store(os, model);
        PerspectiveModel result = persister.load(new ByteArrayInputStream(os.toByteArray()));

        assertEquals(name, result.getPerspectives()[0].getName());
    }

}
