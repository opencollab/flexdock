/*
 * Created on 20.03.2005
 */
package org.flexdock.view.plaf.theme.win32;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import org.flexdock.view.ext.DefaultPainter;

/**
 * @author Claudio Romano
 */
public class Win32Painter extends DefaultPainter {

    public void paint(Graphics g, boolean active, JComponent titlebar) {
        int y = 2;
        int h = titlebar.getHeight() - 4;
        int w = titlebar.getWidth();

        Color c = getBackgroundColor(active);
        g.setColor(c);

        // fill up the whole width if we're active
        if (active) {
            g.fillRect(0, y, w, h);
            return;
        }

        // otherwise, fill up the center part and draw an outline
        g.fillRect(1, y + 1, w - 2, h - 2);
    }
}