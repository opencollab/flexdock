/*
 * Created on Aug 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.dockbar.layout;

import java.awt.Container;
import java.awt.Rectangle;

import javax.swing.JLayeredPane;

import org.flexdock.dockbar.DockbarManager;
import org.flexdock.docking.Dockable;
import org.flexdock.util.RootWindow;

/**
 * @author Christopher Butler
 */
public class DockbarLayoutManager {
    private static final Object LOCK = new Object();
    private static final DockbarLayoutManager DEFAULT_INSTANCE = new DockbarLayoutManager();
	private static DockbarLayoutManager viewAreaManager = DEFAULT_INSTANCE;
	
	public static DockbarLayoutManager getManager() {
	    synchronized(LOCK) {
	        return viewAreaManager;
	    }
	}
	
	public static void setManager(DockbarLayoutManager mgr) {
	    synchronized(LOCK) {
	        viewAreaManager = mgr==null? DEFAULT_INSTANCE: mgr;
	    }
	}
	
	public Rectangle getViewArea(DockbarManager mgr, Dockable dockable) {
	    if(mgr==null)
	        return new Rectangle(0, 0, 0, 0);
	    
		Rectangle leftBar = mgr.getLeftBar().getBounds();
		Rectangle bottomBar = mgr.getBottomBar().getBounds();
		
		return new Rectangle(leftBar.x + leftBar.width, leftBar.y, bottomBar.width, leftBar.height);
	}
	
	public Rectangle getLayoutArea(DockbarManager mgr) {
		RootWindow window = mgr==null? null: mgr.getWindow();
		if(window==null)
			return new Rectangle(0, 0, 0, 0);
		
		Container contentPane = window.getContentPane();
		JLayeredPane layeredPane = window.getLayeredPane();

		// no rectangle translation required because layeredPane is already the direct
		// parent of contentPane.

		Rectangle rect = contentPane.getBounds();
		return rect;
	}
}
