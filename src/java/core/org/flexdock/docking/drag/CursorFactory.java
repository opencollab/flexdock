/*
 * Created on Aug 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.docking.drag;

import java.awt.Cursor;

import org.flexdock.docking.CursorProvider;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultCursorProvider;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CursorFactory {
	private static CursorProvider DEFAULT_PROVIDER = DefaultCursorProvider.getInstance();
	
	public static Cursor getCursor(String region) {
		if(DockingPort.NORTH_REGION.equals(region))
			return DEFAULT_PROVIDER.getNorthCursor();
		if(DockingPort.SOUTH_REGION.equals(region))
			return DEFAULT_PROVIDER.getSouthCursor();
		if(DockingPort.EAST_REGION.equals(region))
			return DEFAULT_PROVIDER.getEastCursor();
		if(DockingPort.WEST_REGION.equals(region))
			return DEFAULT_PROVIDER.getWestCursor();
		if(DockingPort.CENTER_REGION.equals(region))
			return DEFAULT_PROVIDER.getCenterCursor();
		return DEFAULT_PROVIDER.getDisallowedCursor();
	}
}
