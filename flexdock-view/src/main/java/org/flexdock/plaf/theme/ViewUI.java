/*
 * Created on Feb 28, 2005
 */
package org.flexdock.plaf.theme;

import java.awt.Graphics;

import javax.swing.JComponent;

import org.flexdock.plaf.FlexViewComponentUI;

/**
 * @author Christopher Butler
 */
public class ViewUI extends FlexViewComponentUI {

    public void installUI(JComponent c) {
        super.installUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
    }

    public void initializeCreationParameters() {

    }

    public String getPreferredTitlebarUI() {
        return creationParameters.getString(UIFactory.TITLEBAR_KEY);
    }
}
