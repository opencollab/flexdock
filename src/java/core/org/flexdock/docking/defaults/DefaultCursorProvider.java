/*
 * Created on Aug 28, 2004
 */
package org.flexdock.docking.defaults;

import java.awt.Cursor;
import java.awt.Point;
import java.net.URL;

import org.flexdock.docking.CursorProvider;
import org.flexdock.util.ResourceManager;

/**
 * @author Christopher Butler
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
	
	public Cursor getCenterCursor() {
		return center;
	}

	public Cursor getDisallowedCursor() {
		return blocked;
	}

	public Cursor getEastCursor() {
		return east;
	}

	public Cursor getNorthCursor() {
		return north;
	}

	public Cursor getSouthCursor() {
		return south;
	}

	public Cursor getWestCursor() {
		return west;
	}
}
