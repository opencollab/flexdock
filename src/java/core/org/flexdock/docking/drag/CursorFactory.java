/*
 * Created on Aug 28, 2004
 */
package org.flexdock.docking.drag;

import java.awt.Cursor;

import org.flexdock.docking.CursorProvider;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultCursorProvider;

/**
 * @author Christopher Butler
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
