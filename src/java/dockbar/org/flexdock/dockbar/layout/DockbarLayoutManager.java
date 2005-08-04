/*
 * Created on Aug 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.dockbar.layout;

import java.awt.Rectangle;

import org.flexdock.dockbar.DockbarManager;
import org.flexdock.docking.Dockable;

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
	
	public Rectangle getDockbarArea(DockbarManager mgr, Dockable dockable) {
	    if(mgr==null)
	        return null;
	    
		Rectangle leftBar = mgr.getLeftBar().getBounds();
		Rectangle bottomBar = mgr.getBottomBar().getBounds();
		
		return new Rectangle(leftBar.x + leftBar.width, leftBar.y, bottomBar.width, leftBar.height);
	}
}
