/*
 * Created on 19.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.ext;

import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * @author cro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Painter {
    public void paint(Graphics g, boolean active, JComponent titlebar);
    
    public PainterResource getPainterResource();
    public void setPainterResource(PainterResource painterResource);
}
