/*
 * Created on Aug 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.flexdock.util;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;

/**
 * @author cb8167
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SwingUtility {
	public static void revalidateComponent(Component comp) {
		if (comp instanceof JComponent)
			 ((JComponent) comp).revalidate();
	}
	
	public static void drawRect(Graphics g, Rectangle r) {
		if(g==null || r==null)
			return;
			
		g.drawRect(r.x, r.y, r.width, r.height);
	}
	
	public static DockingPort[] getChildPorts(DockingPort port) {
		if(!(port instanceof DefaultDockingPort))
			return new DockingPort[0];
	
		DefaultDockingPort parent = (DefaultDockingPort)port;
		Component docked = parent.getDockedComponent();
		if(!(docked instanceof JSplitPane))
			return new DockingPort[0];
			
		JSplitPane split = (JSplitPane)docked;
		DockingPort left = null;
		DockingPort right = null;
		if(split.getLeftComponent() instanceof DockingPort)
			left = (DockingPort)split.getLeftComponent();
		if(split.getRightComponent() instanceof DockingPort)
			right = (DockingPort)split.getRightComponent();
		
		if(left==null && right==null)
			return new DockingPort[0];
		if(left==null)
			return new DockingPort[] {right};
		if(right==null)
			return new DockingPort[] {left};
		return new DockingPort[] {left, right};
			
	}
	
	public static Point[] getPoints(Rectangle rect) {
		return getPoints(rect, null);
	}
	
	public static Point[] getPoints(Rectangle rect, Component convertFromScreen) {
		if(rect==null)
			return null;
		
		Rectangle r = (Rectangle)rect.clone();
		Point p = r.getLocation();
		if(convertFromScreen!=null)
			SwingUtilities.convertPointFromScreen(p, convertFromScreen);
		
		r.setLocation(p);
		
		return new Point[] {
			p, 
			new Point(p.x + r.width, p.y),
			new Point(p.x + r.width, p.y+r.height),
			new Point(p.x, p.y+r.height)
		};
    }

    public static final void centerOnScreen(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();

        if (windowSize.height > screenSize.height)
            windowSize.height = screenSize.height;

        if (windowSize.width > screenSize.width)
            windowSize.width = screenSize.width;

        window.setLocation((screenSize.width - windowSize.width) / 2,
                (screenSize.height - windowSize.height) / 2);
    }

    public static void center(Window window, Component parent) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Rectangle bounds = new Rectangle(parent.getLocationOnScreen(), parent.getSize());

        int w = window.getWidth();
        int h = window.getHeight();

        // center according to parent

        int x = ((int) bounds.getCenterX()) - w / 2;
        int y = ((int) bounds.getCenterY()) - h / 2;

        // does it fit on screen?

        if (x < 0)
            x = 0;
        else if (x + w > screenSize.getWidth())
            x = ((int) screenSize.getWidth()) - w;

        if (y < 0)
            y = 0;
        else if (y + h > screenSize.getHeight())
            y = ((int) screenSize.getHeight()) - h;

        // done

        window.setBounds(x, y, w, h);
    }
}
