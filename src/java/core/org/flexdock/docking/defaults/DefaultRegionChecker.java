/*
 * Created on Mar 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
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
import org.flexdock.docking.ScaledInsets;

/**
 * @author Christopher Butler
 *
 */
public class DefaultRegionChecker implements RegionChecker {
	public String getRegion(Component c, Point p) {
		if(c==null || p==null || !c.getBounds().contains(p))
			return DockingPort.UNKNOWN_REGION;

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
				return westPoly.contains(p)? DockingPort.WEST_REGION: DockingPort.NORTH_REGION;
			}
			// check NORTH_EAST
			Rectangle east = getEastRegion(c);
			if(east.contains(p)) {
				Polygon eastPoly = new Polygon();
				eastPoly.addPoint(rightX, 0);
				eastPoly.addPoint(rightX, north.height);
				eastPoly.addPoint(east.x, north.height);
				return eastPoly.contains(p)? DockingPort.EAST_REGION: DockingPort.NORTH_REGION;
			}
			return DockingPort.NORTH_REGION;
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
				return westPoly.contains(p)? DockingPort.WEST_REGION: DockingPort.SOUTH_REGION;
			}
			// check SOUTH_EAST
			Rectangle east = getEastRegion(c);
			if(east.contains(p)) {
				Polygon eastPoly = new Polygon();
				eastPoly.addPoint(east.y, south.y);
				eastPoly.addPoint(rightX, south.y);
				eastPoly.addPoint(rightX, bottomY);
				return eastPoly.contains(p)? DockingPort.EAST_REGION: DockingPort.SOUTH_REGION;
			}
			return DockingPort.SOUTH_REGION;
		}
		
		// Now check EAST and WEST.  We've already checked NORTH and SOUTH, so we don't have to
		// check for NE, SE, NW, and SW anymore.
		Rectangle east = getEastRegion(c);
		if(east.contains(p))
			return DockingPort.EAST_REGION;
		Rectangle west = getWestRegion(c);
		if(west.contains(p))
			return DockingPort.WEST_REGION;
		
		// not in any of the outer regions, so return CENTER.
		return DockingPort.CENTER_REGION;
	}
	
	public Rectangle getNorthRegion(Component c) {
		return getRegionBounds(c, DockingPort.NORTH_REGION);
	}
	
	public Rectangle getSouthRegion(Component c) {
		return getRegionBounds(c, DockingPort.SOUTH_REGION);
	}
	
	public Rectangle getEastRegion(Component c) {
		return getRegionBounds(c, DockingPort.EAST_REGION);
	}
	
	public Rectangle getWestRegion(Component c) {
		return getRegionBounds(c, DockingPort.WEST_REGION);
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

		if(DockingPort.NORTH_REGION.equals(region) || DockingPort.SOUTH_REGION.equals(region)) {
			int h = (int)((float)bounds.height * size);
			int y = DockingPort.NORTH_REGION.equals(region)? 0: bounds.height-h;
			return new Rectangle(0, y, bounds.width, h);
		}

		if(DockingPort.WEST_REGION.equals(region) || DockingPort.EAST_REGION.equals(region)) {
			int w = (int)((float)bounds.width * size);
			int x = DockingPort.WEST_REGION.equals(region)? 0: bounds.width-w;
			return new Rectangle(x, 0, w, bounds.height);
		}
		
		return null;
	}
	
	public float getRegionSize(Component c, String region) {
		Dockable d = DockingManager.getRegisteredDockable(c);
		return getRegionPreference(d, region);
	}
	
	public float getSiblingSize(Component c, String region) {
		Dockable d = DockingManager.getRegisteredDockable(c);
		return getSiblingPreference(d, region);

	}
	
	protected static float getDockingInset(ScaledInsets insets, String region, float defaultVal, float max, float min) {
		float f = insets==null? -1: insets.getRegion(region);
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
		ScaledInsets insets = d==null? null: d.getRegionInsets();
		return getDockingInset(insets, region, DEFAULT_REGION_SIZE, MAX_REGION_SIZE, MIN_REGION_SIZE);
	}
	
	public static float getSiblingPreference(Dockable d, String region) {
		ScaledInsets insets = d==null? null: d.getSiblingInsets();
		return getDockingInset(insets, region, DEFAULT_SIBLING_SIZE, MAX_SIBILNG_SIZE, MIN_SIBILNG_SIZE);
	}
	
}
