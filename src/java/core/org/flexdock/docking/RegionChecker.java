/*
 * Created on Mar 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.docking;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface RegionChecker {
	public static final float MAX_REGION_SIZE = .5F;
	public static final float MIN_REGION_SIZE = .0F;
	public static final float MAX_SIBILNG_SIZE = 1F;
	public static final float MIN_SIBILNG_SIZE = .0F;
	public static final float DEFAULT_REGION_SIZE = .25F;
	public static final float DEFAULT_SIBLING_SIZE = .5F;
	
	public String getRegion(Component c, Point p);
	public Rectangle getNorthRegion(Component c);
	public Rectangle getSouthRegion(Component c);
	public Rectangle getEastRegion(Component c);
	public Rectangle getWestRegion(Component c);

	public Rectangle getRegionBounds(Component c, String region);
	public Rectangle getSiblingBounds(Component c, String region);
	
	public float getRegionSize(Component c, String region);
	public float getSiblingSize(Component c, String region);
}
