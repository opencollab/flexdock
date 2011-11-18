/*
 * Created on 04.03.2005
 */
package org.flexdock.plaf.resources.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

import org.flexdock.plaf.resources.ResourceHandler;

/**
 * @author Claudio Romano
 */
public class RaisedBorderResource extends ResourceHandler {

    public Object getResource(String data) {
        return new RaisedBorder();
    }

    private static final class RaisedBorder extends AbstractBorder {
        private static final Color HIGHLIGHT_COLOR = UIManager.getColor("controlLtHighlight");
        private static final Color CONTROL_SHADOW = UIManager.getColor("controlShadow");

        private static final Insets INSETS = new Insets(1, 1, 1, 0);

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.setColor(HIGHLIGHT_COLOR);
            g.fillRect(0, 0, w, 1);
            g.fillRect(0, 1, 1, h - 1);
            g.setColor(CONTROL_SHADOW);
            g.fillRect(0, h - 1, w, 1);
        }
    }
}

