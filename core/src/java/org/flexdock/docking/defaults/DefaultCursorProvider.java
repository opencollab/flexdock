/*
 * Created on Aug 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.docking.defaults;

import java.awt.Cursor;
import java.awt.Point;
import java.net.URL;

import org.flexdock.docking.CursorProvider;
import org.flexdock.util.ResourceManager;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DefaultCursorProvider implements CursorProvider {
	private static final String NORTH_IMG = "upArrow.gif";
	private static final String SOUTH_IMG = "downArrow.gif";
	private static final String EAST_IMG = "rightArrow.gif";
	private static final String WEST_IMG = "leftArrow.gif";
	private static final String CENTER_IMG = "stacked.gif";
	private static final String BLOCKED_IMG = "notAllowed.gif";
	
	private static final DefaultCursorProvider SINGLETON = new DefaultCursorProvider();
	private Cursor north;
	private Cursor south;
	private Cursor east;
	private Cursor west;
	private Cursor center;
	private Cursor blocked;
	
	public static DefaultCursorProvider getInstance() {
		return SINGLETON;
	}
	
	private DefaultCursorProvider() {
		north = createCursor(NORTH_IMG);
		south = createCursor(SOUTH_IMG);
		east = createCursor(EAST_IMG);
		west = createCursor(WEST_IMG);
		center = createCursor(CENTER_IMG);
		blocked = createCursor(BLOCKED_IMG);
	}
	
	private static Cursor createCursor(String imgName) {
		URL url = DefaultCursorProvider.class.getResource(imgName);
		return ResourceManager.createCursor(url, new Point(8, 8), null);
	}
	
	/* (non-Javadoc)
	 * @see org.flexdock.docking.CursorProvider#getCenterCursor()
	 */
	public Cursor getCenterCursor() {
		// TODO Auto-generated method stub
		return center;
	}
	/* (non-Javadoc)
	 * @see org.flexdock.docking.CursorProvider#getDisallowedCursor()
	 */
	public Cursor getDisallowedCursor() {
		// TODO Auto-generated method stub
		return blocked;
	}
	/* (non-Javadoc)
	 * @see org.flexdock.docking.CursorProvider#getEastCursor()
	 */
	public Cursor getEastCursor() {
		// TODO Auto-generated method stub
		return east;
	}
	/* (non-Javadoc)
	 * @see org.flexdock.docking.CursorProvider#getNorthCursor()
	 */
	public Cursor getNorthCursor() {
		// TODO Auto-generated method stub
		return north;
	}
	/* (non-Javadoc)
	 * @see org.flexdock.docking.CursorProvider#getSouthCursor()
	 */
	public Cursor getSouthCursor() {
		// TODO Auto-generated method stub
		return south;
	}
	/* (non-Javadoc)
	 * @see org.flexdock.docking.CursorProvider#getWestCursor()
	 */
	public Cursor getWestCursor() {
		// TODO Auto-generated method stub
		return west;
	}
}
