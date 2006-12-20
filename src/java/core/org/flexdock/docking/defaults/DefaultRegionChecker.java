/*
 * Created on Mar 11, 2005
 */
package org.flexdock.docking.defaults;

import java.awt.Component;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.RegionChecker;

/**
 * @author Christopher Butler
 *
 */
public class DefaultRegionChecker implements RegionChecker, DockingConstants {
	
	/**
	 * Returns the docking region of the supplied <code>Component</code> that contains
	 * the coordinates of the specified <code>Point</code>.  If either <code>comp</code> or 
	 * <code>point</code> is <code>null</code>, then <code>UNKNOWN_REGION</code> is returned.
	 * If the specified <code>Component</code> bounds do not contain the supplied <code>Point</code>,
	 * then <code>UNKNOWN_REGION</code> is returned.
	 * <br/>
	 * This implementation assumes that <code>comp</code> is a <code>Component</code> embedded
	 * within a <code>DockingPort</code>.  If <code>comp</code> is itself a <code>DockingPort</code>, 
	 * then <code>CENTER_REGION</code> is returned.  Otherwise, the returned region is based upon
	 * a section of the bounds of the specified <code>Component</code> relative to the containing
	 * <code>DockingPort</code>.
	 * <br/>
	 * This method divides the specified <code>Component's</code> bounds into four <code>Rectangles</code>
	 * determined by <code>getNorthRegion(Component c)</code>, 
	 * <code>getSouthRegion(Component c)</code>, <code>getEastRegion(Component c)</code>,
	 * and <code>getWestRegion(Component c)</code>, respectively.  Each <code>Rectangle</code> is then
	 * checked to see if it contains the specified <code>Point</code>.  The order of precedence is
	 * NORTH, SOUTH, EAST, and then WEST.  If the specified <code>Point</code> is contained by the 
	 * <code>Component</code> bounds but none of the sub-<code>Rectangles</code>, then 
	 * <code>CENTER_REGION</code> is returned.  
	 * <br/>
	 * For NORTH and SOUTH <code>Rectangles</code>, the 
	 * distance is checked between the top/bottom and left or right edge of the regional bounds.  If
	 * the horizontal distance to the regional edge is smaller than the vertical distance, then
	 * EAST or WEST takes precendence of NORTH or SOUTH.  This allows for proper determination between
	 * "northeast", "northwest", "southeast", and "southwest" cases.
	 * 
	 * @param comp the <code>Component</code> whose region is to be examined.
	 * @param point the coordinates whose region is to be determined.
	 * @return the docking region containing the specified <code>Point</code>.
	 * @see RegionChecker#getRegion(Component, Point)
	 * @see #getNorthRegion(Component)
	 * @see #getSouthRegion(Component)
	 * @see #getEastRegion(Component)
	 * @see #getWestRegion(Component)
	 */
	public String getRegion(Component comp, Point point) {
		if(comp==null || point==null)
			return UNKNOWN_REGION;

		// make sure the point is actually inside of the target dockingport
		Rectangle targetArea = comp.getBounds();
		// if our target component is the dockingport itself, then getBounds() would
		// have returned a target area relative to the dockingport's parent.  reset
		// relative to the dockingport.
		if(comp instanceof DockingPort)
			targetArea.setLocation(0, 0);
		if(!targetArea.contains(point))
			return UNKNOWN_REGION;
		
		// if our target component is the dockingport, then the dockingport is 
		// currently empty and all points within it are in the CENTER
		if(comp instanceof DockingPort)
			return CENTER_REGION;
		
		// start with the north region
		Rectangle north = getNorthRegion(comp);
		int rightX = north.x + north.width;
		if(north.contains(point)) {
			// check NORTH_WEST
			Rectangle west = getWestRegion(comp);
			if(west.contains(point)) {
				Polygon westPoly = new Polygon();
				westPoly.addPoint(0, 0);
				westPoly.addPoint(0, north.height);
				westPoly.addPoint(west.width, north.height);
				return westPoly.contains(point)? WEST_REGION: NORTH_REGION;
			}
			// check NORTH_EAST
			Rectangle east = getEastRegion(comp);
			if(east.contains(point)) {
				Polygon eastPoly = new Polygon();
				eastPoly.addPoint(rightX, 0);
				eastPoly.addPoint(rightX, north.height);
				eastPoly.addPoint(east.x, north.height);
				return eastPoly.contains(point)? EAST_REGION: NORTH_REGION;
			}
			return NORTH_REGION;
		}

		// check with the south region
		Rectangle south = getSouthRegion(comp);
		int bottomY = south.y + south.height;
		if(south.contains(point)) {
			// check SOUTH_WEST
			Rectangle west = getWestRegion(comp);
			if(west.contains(point)) {
				Polygon westPoly = new Polygon();
				westPoly.addPoint(0, south.y);
				westPoly.addPoint(west.width, south.y);
				westPoly.addPoint(0, bottomY);
				return westPoly.contains(point)? WEST_REGION: SOUTH_REGION;
			}
			// check SOUTH_EAST
			Rectangle east = getEastRegion(comp);
			if(east.contains(point)) {
				Polygon eastPoly = new Polygon();
				eastPoly.addPoint(east.y, south.y);
				eastPoly.addPoint(rightX, south.y);
				eastPoly.addPoint(rightX, bottomY);
				return eastPoly.contains(point)? EAST_REGION: SOUTH_REGION;
			}
			return SOUTH_REGION;
		}
		
		// Now check EAST and WEST.  We've already checked NORTH and SOUTH, so we don't have to
		// check for NE, SE, NW, and SW anymore.
		Rectangle east = getEastRegion(comp);
		if(east.contains(point))
			return EAST_REGION;
		Rectangle west = getWestRegion(comp);
		if(west.contains(point))
			return WEST_REGION;
		
		// not in any of the outer regions, so return CENTER.
		return CENTER_REGION;
	}
	
	/**
	 * Returns the rectangular bounds within the specified component that represent it's
	 * <code>DockingConstants.NORTH_REGION</code>.  This method dispatches to 
	 * <code>getRegionBounds(Component c, String region)</code>, passing an argument of 
	 * <code>DockingConstants.NORTH_REGION</code> for the region parameter.  If the specified
	 * <code>Component</code> is <code>null</code>, then a <code>null</code> reference is
	 * returned.
	 * 
	 * @param c the <code>Component</code> whose north region is to be returned.
	 * @return the bounds containing the north region of the specified <code>Component</code>.
	 * @see RegionChecker#getNorthRegion(Component)
	 * @see #getRegionBounds(Component, String) 
	 */
	public Rectangle getNorthRegion(Component c) {
		return getRegionBounds(c, NORTH_REGION);
	}
	
	/**
	 * Returns the rectangular bounds within the specified component that represent it's
	 * <code>DockingConstants.SOUTH_REGION</code>.  This method dispatches to 
	 * <code>getRegionBounds(Component c, String region)</code>, passing an argument of 
	 * <code>DockingConstants.SOUTH_REGION</code> for the region parameter.  If the specified
	 * <code>Component</code> is <code>null</code>, then a <code>null</code> reference is
	 * returned.
	 * 
	 * @param c the <code>Component</code> whose south region is to be returned.
	 * @return the bounds containing the north region of the specified <code>Component</code>.
	 * @see RegionChecker#getSouthRegion(Component)
	 * @see #getRegionBounds(Component, String) 
	 */
	public Rectangle getSouthRegion(Component c) {
		return getRegionBounds(c, SOUTH_REGION);
	}
	
	/**
	 * Returns the rectangular bounds within the specified component that represent it's
	 * <code>DockingConstants.EAST_REGION</code>.  This method dispatches to 
	 * <code>getRegionBounds(Component c, String region)</code>, passing an argument of 
	 * <code>DockingConstants.EAST_REGION</code> for the region parameter.  If the specified
	 * <code>Component</code> is <code>null</code>, then a <code>null</code> reference is
	 * returned.
	 * 
	 * @param c the <code>Component</code> whose east region is to be returned.
	 * @return the bounds containing the north region of the specified <code>Component</code>.
	 * @see RegionChecker#getEastRegion(Component)
	 * @see #getRegionBounds(Component, String) 
	 */
	public Rectangle getEastRegion(Component c) {
		return getRegionBounds(c, EAST_REGION);
	}
	
	/**
	 * Returns the rectangular bounds within the specified component that represent it's
	 * <code>DockingConstants.WEST_REGION</code>.  This method dispatches to 
	 * <code>getRegionBounds(Component c, String region)</code>, passing an argument of 
	 * <code>DockingConstants.WEST_REGION</code> for the region parameter.  If the specified
	 * <code>Component</code> is <code>null</code>, then a <code>null</code> reference is
	 * returned.
	 * 
	 * @param c the <code>Component</code> whose west region is to be returned.
	 * @return the bounds containing the north region of the specified <code>Component</code>.
	 * @see RegionChecker#getWestRegion(Component)
	 * @see #getRegionBounds(Component, String) 
	 */
	public Rectangle getWestRegion(Component c) {
		return getRegionBounds(c, WEST_REGION);
	}
	
	/**
	 * Returns the bounding <code>Rectangle</code> within the specified component that represents 
	 * the specified region.  If <code>c</code> or <code>region</code> are null, then this 
	 * method returns a <code>null</code> reference.
	 * <br/>
	 * This method dispatches to <code>getRegionSize(Component c, String region)</code> to 
	 * determine the proportional size of the specified <code>Component</code> dedicated 
	 * to the specified region.  It then multiplies this value by the relevant 
	 * <code>Component</code> dimension (<i><code>width</code> for east/west, 
	 * <code>height</code> for north/south</i>) and returns a <code>Rectangle</code> with the
	 * resulting dimension, spanning the <code>Component</code> edge for the specified region.
	 * 
	 * @param c the <code>Component</code> whose region bounds are to be returned.
	 * @param region the specified region that is to be examined.
	 * @return the bounds containing the supplied region of the specified <code>Component</code>.
	 * @see RegionChecker#getRegionBounds(Component, String)
	 * @see #getRegionSize(Component, String) 
	 */
	public Rectangle getRegionBounds(Component c, String region) {
		if(c!=null && region!=null) {
			float size = getRegionSize(c, region);
			return calculateRegionalBounds(c, region, size);
		}
		return null;
	}
	
	/**
	 * Returns the bounding <code>Rectangle</code> within the specified component that represents 
	 * the desired area to be allotted for sibling <code>Components</code> in the specified region.  
	 * If <code>c</code> or <code>region</code> are null, then this method returns a <code>null</code> 
	 * reference.
	 * <br/>
	 * This method dispatches to <code>getSiblingSize(Component c, String region)</code> to 
	 * determine the proportional size of the specified <code>Component</code> dedicated 
	 * to siblings in the specified region.  It then multiplies this value by the relevant 
	 * <code>Component</code> dimension (<i><code>width</code> for east/west, 
	 * <code>height</code> for north/south</i>) and returns a <code>Rectangle</code> with the
	 * resulting dimension, spanning the <code>Component</code> edge for the specified region.
	 * 
	 * @param c the <code>Component</code> whose sibling bounds are to be returned.
	 * @param region the specified region that is to be examined.
	 * @return the bounds representing the allotted sibling area for the supplied region of the 
	 * specified <code>Component</code>.
	 * @see RegionChecker#getSiblingBounds(Component, String)
	 * @see #getSiblingSize(Component, String) 
	 */
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
	
	/**
	 * Returns a percentage (0.0F through 1.0F) representing the amount of space allotted for the 
	 * specified region within the specified <code>Component</code>.
	 * <br/>
	 * This method resolves the <code>Dockable</code> associated with the specified 
	 * <code>Component</code> and dispatches to <code>getRegionPreference(Dockable d, String region)</code>.
	 * <code>getRegionPreference(Dockable d, String region)</code> attempts to invoke
	 * <code>getDockingProperties()</code> on the <code>Dockable</code> to resolve a 
	 * <code>DockablePropertySet</code> instance and return from its <code>getRegionInset(String region)</code>
	 * method.
	 * <br/>
	 * If the specified <code>Component</code> is <code>null</code>, no <code>Dockable</code> can be 
	 * resolved, or no value is specified in the <code>Dockable's</code> associated 
	 * <code>DockingProps</code> instance, then the default value of 
	 * <code>RegionChecker.DEFAULT_REGION_SIZE</code> is returned. 
	 * 
	 * @param c the <code>Component</code> whose region is to be examined.
	 * @param region the specified region that is to be examined.
	 * @return the percentage of the specified <code>Component</code> allotted for the specified 
	 * region.
	 * @see RegionChecker#getRegionSize(Component, String)
	 * @see DockingManager#getDockable(Component)
	 * @see #getRegionPreference(Dockable, String)
	 * @see Dockable#getDockingProperties()
	 */
	public float getRegionSize(Component c, String region) {
		Dockable d = DockingManager.getDockable(c);
		return getRegionPreference(d, region);
	}
	
	/**
	 * Returns a percentage (0.0F through 1.0F) representing the amount of space allotted for
	 * sibling <code>Component</code> docked to the specified region within the 
	 * specified <code>Component</code>.
	 * <br/>
	 * This method resolves the <code>Dockable</code> associated with the specified 
	 * <code>Component</code> and dispatches to 
	 * <code>getSiblingPreference(Dockable d, String region)</code>.
	 * <code>getSiblingPreference(Dockable d, String region)</code> attempts to invoke
	 * <code>getDockingProperties()</code> on the <code>Dockable</code> to resolve a 
	 * <code>DockablePropertySet</code> instance and return from its <code>getSiblingSize(String region)</code>
	 * method.
	 * <br/>
	 * If the specified <code>Component</code> is <code>null</code>, no <code>Dockable</code> can be 
	 * resolved, or no value is specified in the <code>Dockable's</code> associated 
	 * <code>DockingProps</code> instance, then the default value of 
	 * <code>RegionChecker.DEFAULT_SIBLING_SIZE</code> is returned. 
	 * 
	 * @param c the <code>Component</code> whose sibling size is to be examined.
	 * @param region the specified region that is to be examined.
	 * @return the percentage of the specified <code>Component</code> allotted for the siblings 
	 * within the specified region.
	 * @see DockingManager#getDockable(Component)
	 * @see #getSiblingPreference(Dockable, String)
	 * @see Dockable#getDockingProperties()
	 */
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
	
	/**
	 * Returns <code>size</code> if it is between the values <code>RegionChecker.MIN_REGION_SIZE</code>
	 * and <code>RegionChecker.MAX_REGION_SIZE</code>.  If <code>size</code> is less than
	 * <code>RegionChecker.MIN_REGION_SIZE</code>, then <code>RegionChecker.MIN_REGION_SIZE</code> is
	 * returned.  If <code>size</code> is greater than <code>RegionChecker.MAX_REGION_SIZE</code>, 
	 * then <code>RegionChecker.MAX_REGION_SIZE</code> is returned.
	 * 
	 * @return a valid <code>size</code> value between <code>RegionChecker.MIN_REGION_SIZE</code>
	 * and <code>RegionChecker.MAX_REGION_SIZE</code>, inclusive.
	 */
	public static float validateRegionSize(float size) {
		return checkBounds(size, MAX_REGION_SIZE, MIN_REGION_SIZE);
	}
	
	/**
	 * Returns <code>size</code> if it is between the values <code>RegionChecker.MIN_SIBILNG_SIZE</code>
	 * and <code>RegionChecker.MAX_SIBILNG_SIZE</code>.  If <code>size</code> is less than
	 * <code>RegionChecker.MIN_SIBILNG_SIZE</code>, then <code>RegionChecker.MIN_SIBILNG_SIZE</code> is
	 * returned.  If <code>size</code> is greater than <code>RegionChecker.MAX_SIBILNG_SIZE</code>, 
	 * then <code>RegionChecker.MAX_SIBILNG_SIZE</code> is returned.
	 * 
	 * @return a valid <code>size</code> value between <code>RegionChecker.MIN_SIBILNG_SIZE</code>
	 * and <code>RegionChecker.MAX_SIBILNG_SIZE</code>, inclusive.
	 */
	public static float validateSiblingSize(float size) {
		return checkBounds(size, MAX_SIBILNG_SIZE, MIN_SIBILNG_SIZE);
	}
	
	/**
	 * Returns a percentage (0.0F through 1.0F) representing the amount of space allotted for the 
	 * specified region within the specified <code>Dockable</code>.
	 * <br/>
	 * This method calls <code>getDockingProperties()</code> on the <code>Dockable</code> to resolve a 
	 * <code>DockablePropertySet</code> instance.  It then invokes <code>getRegionInset(String region)</code>
	 * on the <code>DockablePropertySet</code> to retrieve the preferred region size.  If the 
	 * <code>Dockable</code> is <code>null</code> or no region preference can be found, then
	 * the default value of <code>RegionChecker.DEFAULT_REGION_SIZE</code> is returned.  Otherwise, 
	 * the retrieved region preference is passed through <code>validateRegionSize(float size)</code>
	 * and returned.
	 * 
	 * @param d the <code>Dockable</code> whose region is to be checked
	 * @param region the region of the specified <code>Dockable</code> to be checked
	 * @return a percentage (0.0F through 1.0F) representing the amount of space allotted for the 
	 * specified region within the specified <code>Dockable</code>.
	 * @see Dockable#getDockingProperties()
	 * @see RegionChecker#DEFAULT_REGION_SIZE
	 * @see #validateRegionSize(float)
	 */
	public static float getRegionPreference(Dockable d, String region) {
		Float inset = d==null? null: d.getDockingProperties().getRegionInset(region);
		return getDockingInset(inset, DEFAULT_REGION_SIZE, MAX_REGION_SIZE, MIN_REGION_SIZE);
	}
	
	/**
	 * Returns a percentage (0.0F through 1.0F) representing the amount of space allotted for
	 * sibling <code>Components</code> docked to the specified region within the 
	 * specified <code>Dockable</code>.
	 * <br/>
	 * This method calls <code>getDockingProperties()</code> on the <code>Dockable</code> to resolve a 
	 * <code>DockablePropertySet</code> instance.  It then invokes <code>getSiblingSize(String region)</code>
	 * on the <code>DockablePropertySet</code> to retrieve the preferred sibling size.  If the 
	 * <code>Dockable</code> is <code>null</code> or no sibling preference can be found, then
	 * the default value of <code>RegionChecker.DEFAULT_SIBLING_SIZE</code> is returned.  Otherwise, 
	 * the retrieved region preference is passed through <code>validateSiblingSize(float size)</code>
	 * and returned.
	 * 
	 * @param d the <code>Dockable</code> whose sibling size is to be checked
	 * @param region the region of the specified <code>Dockable</code> to be checked
	 * @return a percentage (0.0F through 1.0F) representing the amount of space allotted for
	 * sibling <code>Components</code> docked to the specified region within the 
	 * specified <code>Dockable</code>.
	 * @see Dockable#getDockingProperties()
	 * @see RegionChecker#DEFAULT_SIBLING_SIZE
	 * @see #validateSiblingSize(float)
	 */
	public static float getSiblingPreference(Dockable d, String region) {
		Float size = d==null? null: d.getDockingProperties().getSiblingSize(region);
		return getDockingInset(size, DockingManager.getDefaultSiblingSize(), MAX_SIBILNG_SIZE, MIN_SIBILNG_SIZE);
	}
	
}
