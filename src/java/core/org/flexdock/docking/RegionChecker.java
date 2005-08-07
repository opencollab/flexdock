/*
 * Created on Mar 11, 2005
 */
package org.flexdock.docking;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * This interface provides an API for determining the desired regional bounds for a 
 * <code>Dockable</code> component.  Implementing classes are responsible for determining
 * the bounds and proportional sizes for both docking regions and sibling components.
 * 
 * As a <code>Dockable</code> is dragged across a <code>DockingPort</code> containing
 * another embedded <code>Dockable</code>, a determination must be made as to where the
 * dragged <code>Dockable</code> will be docked within the target <code>DockingPort<code>
 * based upon the current mouse position relative to the embedded <code>Dockable</code>
 * underneath the mouse.  Classes that implement this interface are responsible for 
 * making such determinations.
 * 
 * For example, if a <code>Dockable</code> is dragged over another <code>Dockable</code> 
 * embedded within a <code>DockingPort</code>, and the current mouse position is near the top
 * edge of the embedded <code>Dockable</code>, the current <code>RegionChecker</code> is
 * responsible for determining whether the user is attempting to dock in the <code>north</code>, 
 * <code>east</code>, <code>west</code>, or <code>center</code> of the embedded <code>Dockable</code>.
 * The visual <code>DragPreview</code> displayed to the end user should reflect this determination.
 * 
 * Once the docking operation is complete and the layout has been split between both
 * <code>Dockables</code>, the actual percentage of space allotted to the new <code>Dockable</code> 
 * in the layout, referred to as the "sibling", is also determined by the current 
 * <code>RegionChecker</code> implementation. 
 * 
 * @author Christopher Butler
 * @author Mateusz Szczap
 */
public interface RegionChecker {
    
    float MAX_REGION_SIZE = .5F;
    float MIN_REGION_SIZE = .0F;
    float MAX_SIBILNG_SIZE = 1F;
    float MIN_SIBILNG_SIZE = .0F;
    float DEFAULT_REGION_SIZE = .25F;
    float DEFAULT_SIBLING_SIZE = .5F;
    
    String DEFAULT_SIBLING_SIZE_KEY = "default.sibling.size";
    
    /**
     * Returns the docking region of the supplied <code>Component</code> that contains
     * the coordinates of the specified <code>Point</code>.  Valid return values are 
     * those regions defined in <code>DockingConstants</code> and include 
     * <code>CENTER_REGION</code>, <code>NORTH_REGION</code>, <code>SOUTH_REGION</code>, 
     * <code>EAST_REGION</code>, <code>WEST_REGION</code>, or <code>UNKNOWN_REGION</code>.
     * 
     * @param component the <code>Component</code> whose region is to be examined.
     * @param point the coordinates whose region is to be determined.
     * @return the docking region containing the specified <code>Point</code>.
     */
    String getRegion(Component component, Point point);
    
    /**
     * Returns the rectangular bounds within the specified component that represent it's
     * <code>DockingConstants.NORTH_REGION</code>.
     * 
     * @param component the <code>Component</code> whose north region is to be returned.
     * @return the bounds containing the north region of the specified <code>Component</code>. 
     */
    Rectangle getNorthRegion(Component component);
    
    /**
     * Returns the rectangular bounds within the specified component that represent it's
     * <code>DockingConstants.SOUTH_REGION</code>.
     * 
     * @param component the <code>Component</code> whose south region is to be returned.
     * @return the bounds containing the south region of the specified <code>Component</code>. 
     */
    Rectangle getSouthRegion(Component component);
    
    /**
     * Returns the rectangular bounds within the specified component that represent it's
     * <code>DockingConstants.EAST_REGION</code>.
     * 
     * @param component the <code>Component</code> whose east region is to be returned.
     * @return the bounds containing the east region of the specified <code>Component</code>. 
     */
    Rectangle getEastRegion(Component component);
    
    /**
     * Returns the rectangular bounds within the specified component that represent it's
     * <code>DockingConstants.WEST_REGION</code>.
     * 
     * @param component the <code>Component</code> whose west region is to be returned.
     * @return the bounds containing the west region of the specified <code>Component</code>. 
     */
    Rectangle getWestRegion(Component component);
    
    /**
     * Returns the rectangular bounds within the specified component that represent 
     * the specified region.  Valid values for the <code>region</code> parameter are 
     * those regions defined in <code>DockingConstants</code> and include 
     * <code>NORTH_REGION</code>, <code>SOUTH_REGION</code>, <code>EAST_REGION</code>, 
     * and <code>WEST_REGION</code>.  All other region values should result in this
     * method returning a <code>null</code> reference.
     * 
     * @param component the <code>Component</code> whose region bounds are to be returned.
     * @param region the specified region that is to be examined.
     * @return the bounds containing the supplied region of the specified <code>Component</code>. 
     */
    Rectangle getRegionBounds(Component component, String region);
    
    /**
     * Returns a percentage representing the amount of space allotted for the specified
     * region within the specified <code>Component</code>.  For example, a return value of
     * 0.25F for NORTH_REGION implies that the top 25% of the supplied 
     * <code>Component's</code> bounds rectangle is to be interpreted as the <code>Component's</code>
     * northern region.   
     * Valid values for the <code>region</code> parameter are 
     * those regions defined in <code>DockingConstants</code> and include 
     * <code>NORTH_REGION</code>, <code>SOUTH_REGION</code>, <code>EAST_REGION</code>, 
     * and <code>WEST_REGION</code>.  All other region values should result in this
     * method returning the constant <code>DEFAULT_SIBLING_SIZE</code>.
     * 
     * @param component the <code>Component</code> whose region is to be examined.
     * @param region the specified region that is to be examined.
     * @return the percentage of the specified <code>Component</code> allotted for the specified region. 
     */
    float getRegionSize(Component component, String region);
    
    /**
     * A <code>Rectangle</code> representing the actual amount of space to allot for sibling
     * <code>Components</code> should they be docked into the specified region.  This method differs
     * from <code>getRegionBounds(Component c, String region)</code> in that <code>getRegionBounds()</code>
     * determines the amount to space used to check whether a <code>Component's</code> docking
     * will intersect with a particular region, whereas this method returns the actual amount of 
     * space said <code>Component</code> will take up after docking has been completed.
     * Valid values for the <code>region</code> parameter are 
     * those regions defined in <code>DockingConstants</code> and include 
     * <code>NORTH_REGION</code>, <code>SOUTH_REGION</code>, <code>EAST_REGION</code>, 
     * and <code>WEST_REGION</code>.  All other region values should result in this
     * method returning the constant <code>DEFAULT_SIBLING_SIZE</code>. 
     * 
     * @param component the <code>Component</code> whose sibling bounds are to be returned.
     * @param region the specified region that is to be examined.
     * @return the bounds containing the sibling bounds desired for <code>Components</code>
     * docked into the specified region of the of the specified <code>Component</code>.
     */
    Rectangle getSiblingBounds(Component component, String region);
    
    /**
     * Returns a percentage representing the amount of space allotted for sibling
     * <code>Components</code> to be docked within the specified region of the supplied 
     * <code>Component</code>.   This method differs
     * from <code>getRegionSize(Component c, String region)</code> in that <code>getRegionSize()</code>
     * determines the proportional space used to check whether a <code>Component's</code> docking
     * will intersect with a particular region, whereas this method returns the proportional
     * space said <code>Component</code> will take up after docking has been completed.
     * Valid values for the <code>region</code> parameter are 
     * those regions defined in <code>DockingConstants</code> and include 
     * <code>NORTH_REGION</code>, <code>SOUTH_REGION</code>, <code>EAST_REGION</code>, 
     * and <code>WEST_REGION</code>.  All other region values should result in this
     * method returning the constant <code>DEFAULT_SIBLING_SIZE</code>. 
     * 
     * @param component the <code>Component</code> whose sibling proportions are to be returned.
     * @param region the specified region that is to be examined.
     * @return the percentage of the specified <code>Component</code> allotted for 
     * sibling <code>Components</code> that are to be docked into the specified region.
     */
    float getSiblingSize(Component component, String region);
    
}
