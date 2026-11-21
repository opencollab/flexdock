package org.flexdock.test.view;

import org.flexdock.plaf.Configurator;
import org.flexdock.plaf.PlafManager;
import org.flexdock.plaf.theme.Theme;
import org.flexdock.plaf.theme.UIFactory;
import org.junit.jupiter.api.Test;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ThemeConfigurationTest {

    private static final String THEME_NAME = "test.theme";

    // All three UIs are given, so the theme does not depend on the installed look and feel
    private static Properties createProperties() {
        Properties properties = new Properties();
        properties.setProperty(UIFactory.VIEW_KEY, "win32");
        properties.setProperty(UIFactory.TITLEBAR_KEY, "officeXP");
        properties.setProperty(UIFactory.BUTTON_KEY, "rounded");

        return properties;
    }

    @Test
    void testDefaultPrefsDefineNamedUis() {
        assertTrue(Configurator.getNamedElementsByTagName(UIFactory.VIEW_KEY).containsKey("default"));
        assertTrue(Configurator.getNamedElementsByTagName(UIFactory.TITLEBAR_KEY).containsKey("win32"));
        assertTrue(Configurator.getNamedElementsByTagName(UIFactory.BUTTON_KEY).containsKey("rounded"));
    }

    @Test
    void testNullTagNameGivesNoElements() {
        assertNull(Configurator.getNamedElementsByTagName(null));
    }

    @Test
    void testCustomThemeUsesSelectedUis() {
        try {
            Theme theme = PlafManager.setCustomTheme(THEME_NAME, createProperties());

            assertNotNull(theme);
            assertEquals(THEME_NAME, theme.getName());
            assertEquals("win32", theme.getViewUI().getCreationParameters().getName());
            assertEquals("officeXP", theme.getTitlebarUI().getCreationParameters().getName());
            assertEquals("rounded", theme.getButtonUI().getCreationParameters().getName());
        } finally {
            PlafManager.removeCustomTheme(THEME_NAME);
        }
    }

    @Test
    void testCustomThemeWithoutNameOrPropertiesIsNull() {
        assertNull(PlafManager.setCustomTheme(null, new Properties()));
        assertNull(PlafManager.setCustomTheme(THEME_NAME, null));
    }

    @Test
    void testRemovedCustomThemeIsReturnedOnce() {
        PlafManager.addCustomTheme(THEME_NAME, createProperties());

        assertNotNull(PlafManager.removeCustomTheme(THEME_NAME));
        assertNull(PlafManager.removeCustomTheme(THEME_NAME));
    }

    @Test
    void testCustomThemeUnderUnmappedLookAndFeel() throws Exception {
        LookAndFeel original = UIManager.getLookAndFeel();
        // The anonymous subclass has a class name that no <plaf> mapping knows
        UIManager.setLookAndFeel(new MetalLookAndFeel() { });
        try {
            assertNotNull(PlafManager.addCustomTheme(THEME_NAME, new Properties()));
        } finally {
            UIManager.setLookAndFeel(original);
            PlafManager.removeCustomTheme(THEME_NAME);
        }
    }

}
