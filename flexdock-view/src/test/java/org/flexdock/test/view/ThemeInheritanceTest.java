package org.flexdock.test.view;

import org.flexdock.plaf.Configurator;
import org.flexdock.plaf.PropertySet;
import org.flexdock.plaf.theme.UIFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Uses src/test/resources/flexdock-themes.xml, which Configurator reads as the user preferences
class ThemeInheritanceTest {

    private static PropertySet getTitlebar(String name) {
        return Configurator.getProperties(name, UIFactory.TITLEBAR_KEY);
    }

    @Test
    void testPropertiesWithoutParent() {
        PropertySet base = getTitlebar("test.base");

        assertEquals("test.base", base.getName());
        assertEquals("base-a", base.getString("a"));
        assertEquals("base-b", base.getString("b"));
        assertEquals(2, base.size());
    }

    @Test
    void testChildInheritsAndOverridesParentProperties() {
        PropertySet child = getTitlebar("test.child");

        assertEquals("test.child", child.getName());
        assertEquals("base-a", child.getString("a"));
        assertEquals("child-b", child.getString("b"));
        assertEquals("child-c", child.getString("c"));
    }

    @Test
    void testInheritanceChainsThroughSeveralLevels() {
        PropertySet grandchild = getTitlebar("test.grandchild");

        assertEquals("base-a", grandchild.getString("a"));
        assertEquals("child-b", grandchild.getString("b"));
        assertEquals("grandchild-c", grandchild.getString("c"));
    }

    @Test
    void testParentIsNotChangedByChild() {
        getTitlebar("test.grandchild");

        assertEquals("base-b", getTitlebar("test.base").getString("b"));
        assertNull(getTitlebar("test.base").getString("c"));
    }

    @Test
    void testMissingParentIsIgnored() {
        PropertySet dangling = getTitlebar("test.dangling");

        assertEquals("dangling-d", dangling.getString("d"));
        assertEquals(1, dangling.size());
    }

    @Test
    void testUnknownNameGivesNoProperties() {
        assertNull(getTitlebar("test.unknown"));
        assertNull(Configurator.getProperties("test.base", null));
    }

    @Test
    void testShippedThemeExtendsDefaultTitlebar() {
        PropertySet win32 = getTitlebar("win32");
        PropertySet standard = getTitlebar("default");

        // Overridden by win32 ...
        assertNotNull(standard.getString("default.height"));
        assertNotNull(win32.getString("default.height"));
        assertNotEquals(standard.getString("default.height"), win32.getString("default.height"));
        // ... and taken over from the default titlebar
        assertEquals(standard.getString("button-ui"), win32.getString("button-ui"));
        assertNotNull(win32.getString("button-ui"));
    }

    @Test
    void testInheritsFlagKeepsPropertiesOfOverriddenElement() {
        PropertySet rounded = Configurator.getProperties("rounded", UIFactory.BUTTON_KEY);

        assertEquals("added", rounded.getString("test.added"));
        // Defined by the default file and kept because the user element inherits
        assertNotNull(rounded.getProperty("border.hover"));
        assertNotNull(rounded.getProperty("border.pressed"));
    }

    @Test
    void testAllPropertiesOfTagIncludeUserAndDefaultElements() {
        PropertySet[] all = Configurator.getProperties(UIFactory.TITLEBAR_KEY);
        boolean user = false;
        boolean shipped = false;
        for (PropertySet set : all) {
            user |= "test.child".equals(set.getName());
            shipped |= "win32".equals(set.getName());
        }

        assertTrue(user);
        assertTrue(shipped);
        assertEquals(0, Configurator.getProperties((String) null).length);
    }

}
