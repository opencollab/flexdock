/*
 * Created on Mar 11, 2005
 */
package org.flexdock.docking.defaults;

import java.awt.Component;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.RegionChecker;
import org.flexdock.util.DockingConstants;

/**
 * @author Christopher Butler
 *
 */
public class DefaultRegionChecker implements RegionChecker, DockingConstants {
	
	public String getRegion(Component c, Point p) {
		if(c==null || p==null)
			return UNKNOWN_REGION;

		// make sure the point is actually inside of the target dockingport
		Rectangle targetArea = c.getBounds();
		// if our target component is the dockingport itself, then getBounds() would
		// have returned a target area relative to the dockingport's parent.  reset
		// relative to the dockingport.
		if(c instanceof DockingPort)
			targetArea.setLocation(0, 0);
		if(!targetArea.contains(p))
			return UNKNOWN_REGION;
		
		// if our target component is the dockingport, then the dockingport is 
		// currently empty and all points within it are in the CENTER
		if(c instanceof DockingPort)
			return CENTER_REGION;
		
		// start with the north region
		Rectangle north = getNorthRegion(c);
		int rightX = north.x + north.width;
		if(north.contains(p)) {
			// check NORTH_WEST
			Rectangle west = getWestRegion(c);
			if(west.contains(p)) {
				Polygon westPoly = new Polygon();
				westPoly.addPoint(0, 0);
				westPoly.addPoint(0, north.height);
				westPoly.addPoint(west.width, north.height);
				return westPoly.contains(p)? WEST_REGION: NORTH_REGION;
			}
			// check NORTH_EAST
			Rectangle east = getEastRegion(c);
			if(east.contains(p)) {
				Polygon eastPoly = new Polygon();
				eastPoly.addPoint(rightX, 0);
				eastPoly.addPoint(rightX, north.height);
				eastPoly.addPoint(east.x, north.height);
				return eastPoly.contains(p)? EAST_REGION: NORTH_REGION;
			}
			return NORTH_REGION;
		}

		// check with the south region
		Rectangle south = getSouthRegion(c);
		int bottomY = south.y + south.height;
		if(south.contains(p)) {
			// check SOUTH_WEST
			Rectangle west = getWestRegion(c);
			if(west.contains(p)) {
				Polygon westPoly = new Polygon();
				westPoly.addPoint(0, south.y);
				westPoly.addPoint(west.width, south.y);
				westPoly.addPoint(0, bottomY);
				return westPoly.contains(p)? WEST_REGION: SOUTH_REGION;
			}
			// check SOUTH_EAST
			Rectangle east = getEastRegion(c);
			if(east.contains(p)) {
				Polygon eastPoly = new Polygon();
				eastPoly.addPoint(east.y, south.y);
				eastPoly.addPoint(rightX, south.y);
				eastPoly.addPoint(rightX, bottomY);
				return eastPoly.contains(p)? EAST_REGION: SOUTH_REGION;
			}
			return SOUTH_REGION;
		}
		
		// Now check EAST and WEST.  We've already checked NORTH and SOUTH, so we don't have to
		// check for NE, SE, NW, and SW anymore.
		Rectangle east = getEastRegion(c);
		if(east.contains(p))
			return EAST_REGION;
		Rectangle west = getWestRegion(c);
		if(west.contains(p))
			return WEST_REGION;
		
		// not in any of the outer regions, so return CENTER.
		return CENTER_REGION;
	}
	
	public Rectangle getNorthRegion(Component c) {
		return getRegionBounds(c, NORTH_REGION);
	}
	
	public Rectangle getSouthRegion(Component c) {
		return getRegionBounds(c, SOUTH_REGION);
	}
	
	public Rectangle getEastRegion(Component c) {
		return getRegionBounds(c, EAST_REGION);
	}
	
	public Rectangle getWestRegion(Component c) {
		return getRegionBounds(c, WEST_REGION);
	}
	
	public Rectangle getRegionBounds(Component c, String region) {
		if(c!=null && region!=null) {
			float size = getRegionSize(c, region);
			return calculateRegionalBounds(c, region, size);
		}
		return null;
	}
	
	public Rectangle getSiblingBounds(Component c, String region) {
		if(c!=null && region!=null) {
			float size = getSiblingSize(c, region);
			return calculateRegionalBounds(c, region, size);
		}
		return null;
	}
	
	protected Rectangle calculateRegionalBounds(Component c, String region, float size) {
		if(c==null || region==null)
			return null;
		
		Rectangle bounds = c.getBounds();

		if(NORTH_REGION.equals(region) || SOUTH_REGION.equals(region)) {
			int h = (int)((float)bounds.height * size);
			int y = NORTH_REGION.equals(region)? 0: bounds.height-h;
			return new Rectangle(0, y, bounds.width, h);
		}

		if(WEST_REGION.equals(region) || EAST_REGION.equals(region)) {
			int w = (int)((float)bounds.width * size);
			int x = WEST_REGION.equals(region)? 0: bounds.width-w;
			return new Rectangle(x, 0, w, bounds.height);
		}
		
		return null;
	}
	
	public float getRegionSize(Component c, String region) {
		Dockable d = DockingManager.getDockable(c);
		return getRegionPreference(d, region);
	}
	
	public float getSiblingSize(Component c, String region) {
		Dockable d = DockingManager.getDockable(c);
		return getSiblingPreference(d, region);

	}
	
	protected static float getDockingInset(Float value, float defaultVal, float max, float min) {
		float f = value==null? -1: value.floatValue();
		if(f==-1)
			f = defaultVal;
		return checkBounds(f, max, min);
	}
	
	protected static float checkBounds(float val, float max, float min) {
		val = Math.min(val, max);
		return Math.max(val, min);
	}
	
	public static float validateRegionSize(float size) {
		return checkBounds(size, MAX_REGION_SIZE, MIN_REGION_SIZE);
	}
	
	public static float validateSiblingSize(float size) {
		return checkBounds(size, MAX_SIBILNG_SIZE, MIN_SIBILNG_SIZE);
	}
	
	public static float getRegionPreference(Dockable d, String region) {
		Float inset = d==null? null: d.getDockingProperties().getRegionInset(region);
		return getDockingInset(inset, DEFAULT_REGION_SIZE, MAX_REGION_SIZE, MIN_REGION_SIZE);
	}
	
	public static float getSiblingPreference(Dockable d, String region) {
		Float size = d==null? null: d.getDockingProperties().getSiblingSize(region);
		return getDockingInset(size, DEFAULT_SIBLING_SIZE, MAX_SIBILNG_SIZE, MIN_SIBILNG_SIZE);
	}
	
}
