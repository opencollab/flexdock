/*
 * Created on 20.03.2005
 *
 */
package org.flexdock.view.ext;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;


/**
 * @author Claudio Romano
 *
 */
public class DefaultPainter implements Painter {
    
    private PainterResource painterResource;


    public void paint(Graphics g, boolean active, JComponent titlebar) {
		int w = titlebar.getWidth();
		int h = titlebar.getHeight();

		Color c = getBackgroundColor(active);
	
		g.setColor(c);
		g.fillRect(0, 0, w, h);

    }
    
    private Color getBackgroundColor(boolean active) {
        return active ? painterResource.getBgColorActiv() : painterResource.getBgColor(); 
    }

    /**
     * @return Returns the painterResource.
     */
    public PainterResource getPainterResource() {
        return painterResource;
    }
    /**
     * @param painterResource The painterResource to set.
     */
    public void setPainterResource(PainterResource painterResource) {
        this.painterResource = painterResource;
    }

}
