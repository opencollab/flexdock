/*
 * Created on 20.03.2005
 */
package org.flexdock.view.plaf.theme.win32;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import org.flexdock.view.ext.DefaultPainter;

/**
 * @author Christopher Butler
 * @author Claudio Romano
 */
public class Win32Painter extends DefaultPainter {

    public void paint(Graphics g, int width, int height, boolean active, JComponent titlebar) {
        Color c = getBackgroundColor(active);
        g.setColor(c);

        // fill up the whole width if we're active
        if (active) {
            g.fillRect(0, 0, width, height);
            return;
        }

        // otherwise, fill up the center part and draw an outline
        g.fillRect(1, 1, width - 2, height - 2);
    }
}