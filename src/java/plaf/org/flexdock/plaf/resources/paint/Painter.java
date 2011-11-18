/*
 * Created on 19.03.2005
 */
package org.flexdock.plaf.resources.paint;

import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * @author Claudio Romano
 */
public interface Painter {
    public void paint(Graphics g, int width, int height, boolean active, JComponent titlebar);

    public PainterResource getPainterResource();
    public void setPainterResource(PainterResource painterResource);
}
