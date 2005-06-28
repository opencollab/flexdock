/*
 * Created on Mar 14, 2005
 */
package org.flexdock.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultRegionChecker;
import org.flexdock.docking.props.DockablePropertySet;
import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.MinimizationManager;

/**
 * @author Christopher Butler
 */
public class DockingUtility implements DockingConstants {
	
	/**
	 * Returns the <code>DockingPort</code> that contains the specified <code>Dockable</code>.
	 * If the <code>Dockable</code> is <code>null</code>, then a <code>null</code> reference
	 * is returned.
	 * <br/>
	 * This method will only return the immediate parent <code>DockingPort</code> of the 
	 * specified <code>Dockable</code>  This means that a check is performed for the 
	 * <code>Component</code> returned by the <code>Dockable's</code> <code>getComponent()</code>
	 * method.  The <code>DockingPort</code> returned 
	 * by this method will not only be an ancestor <code>Container</code> of this 
	 * <code>Component</code>, but invoking the <code>DockingPort's</code> 
	 * <code>isParentDockingPort(Component comp)</code>
	 * with the this <code>Component</code> will also return <code>true</code>.  If both 
	 * of these conditions cannot be satisfied, then this method returns a <code>null</code> 
	 * reference.
	 * 
	 * @param dockable the <code>Dockable</code> whose parent <code>DockingPort</code> is to 
	 * be returned.
	 * @return the imediate parent <code>DockingPort</code> that contains the specified 
	 * <code>Dockable</code>.
	 * @see #getParentDockingPort(Component)
	 */
	public static DockingPort getParentDockingPort(Dockable d) {
		return d==null? null: getParentDockingPort(d.getComponent());
	}

	/**
	 * Returns the <code>DockingPort</code> that contains the specified <code>Component</code>.
	 * If the <code>Component</code> is <code>null</code>, then a <code>null</code> reference
	 * is returned.
	 * <br/>
	 * This method will only return the immediate parent <code>DockingPort</code> of the 
	 * specified <code>Component</code>  This means that the <code>DockingPort</code> returned 
	 * by this method will not only be an ancestor <code>Container</code> of the specified 
	 * <code>Component</code>, but invoking its <code>isParentDockingPort(Component comp)</code>
	 * with the specified <code>Component</code> will also return <code>true</code>.  If both 
	 * of these conditions cannot be satisfied, then this method returns a <code>null</code> 
	 * reference.
	 * 
	 * @param dockable the <code>Component</code> whose parent <code>DockingPort</code> is to 
	 * be returned.
	 * @return the imediate parent <code>DockingPort</code> that contains the specified 
	 * <code>Component</code>.
	 */
	public static DockingPort getParentDockingPort(Component comp) {
		DockingPort port = comp==null? null: (DockingPort)SwingUtilities.getAncestorOfClass(DockingPort.class, comp);
		if(port==null)
			return null;
			
		return port.isParentDockingPort(comp)? port: null;
	}
	
	/**
	 * Returns <code>true</code> if the specified <code>DockingPort</code> has an ancestor <code>DockingPort</code>; 
	 * <code>false</code> otherwise.  If the specified <code>DockingPort</code> is <code>null</code>, then this
	 * method returns <code>false</code>.
	 * 
	 * @param the <code>DockingPort</code> to check for an ancestor port
	 * @return <code>true</code> if the specified <code>DockingPort</code> has an ancestor <code>DockingPort</code>; 
	 * <code>false</code> otherwise.
	 * @see SwingUtilities#getAncestorOfClass(java.lang.Class, java.awt.Component)
	 */
	public static boolean isSubport(DockingPort dockingPort) {
		return dockingPort==null? false: SwingUtilities.getAncestorOfClass(DockingPort.class, (Component)dockingPort)!=null;
	}
	
	/**
	 * Returns the deepest <code>DockingPort</code> within the specified <code>Container</code> at the 
	 * specified <code>location</code>.  If either <code>container</code> or <code>location</code> are
	 * <code>null</code>, then this method returns <code>null</code>.
	 * <br/>
	 * This method will find the deepest <code>Component</code> within the specified container that the 
	 * specified <code>Point</code> via 
	 * <code>SwingUtilities.getDeepestComponentAt(Component parent, int x, int y)</code>.  If no 
	 * <code>Component</code> is resovled, then this method returns <code>null</code>.  If the resolved 
	 * <code>Component</code> is a <code>DockingPort</code>, then it is returned.  Otherwise, the 
	 * <code>Component's</code> <code>DockingPort</code> ancestor is resovled and returned from 
	 * <code>SwingUtilities.getAncestorOfClass(Class c, Component comp)</code>, passing 
	 * <code>DockingPort.class</code> for the ancestor class parameter.
	 *
	 *@param container the <code>Container</code> within which to find a <code>DockingPort</code>.
	 *@param location the point within the specified <code>Container</code> at which to search for a 
	 *<code>DockingPort</code>.
	 *@returnthe deepest <code>DockingPort</code> within the specified <code>Container</code> at the 
	 * specified <code>location</code>.
	 * @see SwingUtilities#getDeepestComponentAt(java.awt.Component, int, int)
	 * @see SwingUtilities#getAncestorOfClass(java.lang.Class, java.awt.Component)
	 */
	public static DockingPort findDockingPort(Container container, Point location) {
		if(container==null || location==null)
			return null;
		
		Component deepestComponent = SwingUtilities.getDeepestComponentAt(container, location.x, location.y);
		if (deepestComponent == null)
			return null;
	
		// we're assured here that the deepest component is both a Component and DockingPort in
		// this case, so we're okay to return here.
		if (deepestComponent instanceof DockingPort)
			return (DockingPort) deepestComponent;
	
		// getAncestorOfClass() will either return a null or a Container that is also an instance of
		// DockingPort.  Since Container is a subclass of Component, we're fine in returning both
		// cases.
		return (DockingPort) SwingUtilities.getAncestorOfClass(DockingPort.class, deepestComponent);
	}
	
	/**
	 * Returns the specified <code>region's</code> cross-axis equivalent region in accordance with the 
	 * orientation used by the specified <code>JSplitPane</code>.  If the <code>JSplitPane</code>
	 * is <code>null</code>, or the specified <code>region</code> is invalid according to 
	 * <code>DockingManager.isValidDockingRegion(String region)</code>, then this method 
	 * returns <code>null</code>.  
	 * 
	 * <code>NORTH_REGION</code> and <code>SOUTH_REGION</code> are considered "vertical" regions, while 
	 * <code>WEST_REGION</code> and <code>EAST_REGION</code> are considered horizontal regions.  If the 
	 * <code>JSplitPane</code> orientation matches the specified <code>region</code> orientation, then the 
	 * original <code>region</code> value is returned.  For instance, if the specified <code>region</code>
	 * is <code>EAST_REGION</code>, and the <code>JSplitPane</code> is of a horizontal orientation, then
	 * there is no need to translate the <code>region</code> parameter across axes since its current
	 * axis is already horizontal.  In this case, <code>EAST_REGION</code> would be returned by this method.
	 * <br/>
	 * If the axis of the specified <code>region</code> does not match the orientation of the 
	 * <code>JSplitPane</code>, then the region is translated to its cross-axis equivalent and returns.
	 * In this case, <code>NORTH_REGION</code> will be translated to <code>WEST_REGION</code>, 
	 * <code>SOUTH_REGION</code> to <code>EAST_REGION</code>, <code>WEST_REGION</code> to <code>NORTH_REGION</code>, 
	 * and <code>EAST_REGION</code> to <code>SOUTH_REGION</code>.  <code>CENTER_REGION</code> is never 
	 * altered.
	 * 
	 * @param splitPane the <code>JSplitPane</code> whose orientation is to be used as a target axis
	 * @param region the docking region to translate to the target axis
	 * @return the specified <code>region's</code> cross-axis equivalent region in accordance with the 
	 * orientation used by the specified <code>JSplitPane</code>.
	 * @see DockingManager#isValidDockingRegion(String)
	 * @see JSplitPane#getOrientation()
	 * @see #isAxisEquivalent(String, String)
	 */
	public static String translateRegionAxis(JSplitPane splitPane, String region) {
		if(splitPane==null || !DockingManager.isValidDockingRegion(region))
			return null;
		
		boolean horizontal = splitPane.getOrientation()==JSplitPane.HORIZONTAL_SPLIT;
		if(horizontal) {
			if(NORTH_REGION.equals(region))
				region = WEST_REGION;
			else if(SOUTH_REGION.equals(region))
				region = EAST_REGION;
		}
		else {
			if(WEST_REGION.equals(region))
				region = NORTH_REGION;
			else if(EAST_REGION.equals(region))
				region = SOUTH_REGION;
		}
		return region;
	}
	
	/**
	 * Returns the opposite docking region of the specified <code>region</code>.  For <code>NORTH_REGION</code>,
	 * this method returns <code>SOUTH_REGION</code>.  For <code>SOUTH_REGION</code>,
	 * this method returns <code>NORTH_REGION</code>.  For <code>EAST_REGION</code>,
	 * this method returns <code>WEST_REGION</code>.  For <code>WEST_REGION</code>,
	 * this method returns <code>EAST_REGION</code>.  For <code>CENTER_REGION</code> or an invalid
	 * region, as specified by <code>DockingManager.isValidDockingRegion(String region)</code>, this method
	 * return <code>CENTER_REGION</code>.
	 * 
	 * @param region the region whose opposite is to be returned.
	 * @return the opposite docking region of the specified <code>region</code>.
	 * @see DockingManager#isValidDockingRegion(String)
	 */
	public static String flipRegion(String region) {
		if(!DockingManager.isValidDockingRegion(region) || CENTER_REGION.equals(region))
			return CENTER_REGION;
		
		if(NORTH_REGION.equals(region))
			return SOUTH_REGION;
		
		if(SOUTH_REGION.equals(region))
			return NORTH_REGION;
		
		if(EAST_REGION.equals(region))
			return WEST_REGION;
		
		return EAST_REGION;
	}

	/**
	 * Tests for region equivalency between the specified region parameters across horizontal and vertical axes.
	 * If either <code>region</code> or <code>otherRegion</code> are <code>null</code> or invalid according to 
	 * <code>DockingManager.isValidDockingRegion(String region)</code>, then this method returns
	 * <code>false</code>.
	 * <br/>
	 * Equivalency within the same axis means that the two specified regions are the same value, as each region
	 * is unique within its axis.  Thus, this method returns <code>true</code> if 
	 * <code>region.equals(otherRegion)</code> returns <code>true</code>.  This includes <code>CENTER_REGION</code>, 
	 * which is axis independent.
	 * <br/>
	 * <code>CENTER_REGION</code> is not an axis equivalent to any region other than itself since it is the only
	 * docking region that does not correspond to a horizontal or vertical axis.  If either the 
	 * specified <code>region</code> or <code>otherRegion</code> is <code>CENTER_REGION</code> and the other
	 * is not, then this method returns <code>false</code>.
	 * <br/>
	 * Equivalancy across axes follows a top-to-left and bottom-to-right mapping.  In this fashion, 
	 * <code>NORTH_REGION</code> and <code>WEST_REGION</code> are equivalent and <code>SOUTH_REGION</code>
	 * and <code>EAST_REGION</code> are equivalent.  These combination will return <code>true</code> for this 
	 * method.  All other region combinatinos will cause this method to return <code>false</code>.
	 * 
	 * @param region the first region to check for equivalency
	 * @param otherRegion the second region to check for equivalency
	 * @return <code>true</code> if the two specified regions are equal or cross-axis equivalents, 
	 * <code>false</code> otherwise.
	 * @see DockingManager#isValidDockingRegion(String)
	 */
	public static boolean isAxisEquivalent(String region, String otherRegion) {
		if(!DockingManager.isValidDockingRegion(region) || !DockingManager.isValidDockingRegion(otherRegion))
			return false;
		
		if(region.equals(otherRegion))
			return true;
		
		if(CENTER_REGION.equals(region))
			return false;
		
		if(NORTH_REGION.equals(region))
			return WEST_REGION.equals(otherRegion);
		if(SOUTH_REGION.equals(region))
			return EAST_REGION.equals(otherRegion);
		if(EAST_REGION.equals(region))
			return SOUTH_REGION.equals(otherRegion);
		if(WEST_REGION.equals(region))
			return NORTH_REGION.equals(otherRegion);
		
		return false;
	}
	
	/**
	 * Returns <code>true</code> if the specified <code>region</code> is equal to either <code>NORTH_REGION</code>
	 * or <code>WEST_REGION</code>.  Returns <code>false</code> otherwise.
	 * 
	 * @param the <code>region</code> to check for top or left equivalency
	 * @return <code>true</code> if the specified <code>region</code> is equal to either <code>NORTH_REGION</code>
	 * or <code>WEST_REGION</code>; <code>false</code> otherwise.
	 * @see DockingConstants#NORTH_REGION
	 * @see DockingConstants#WEST_REGION
	 */
	public static boolean isRegionTopLeft(String region) {
		return NORTH_REGION.equals(region) || WEST_REGION.equals(region);
	}
	
	/**
	 * Returns the <code>String</code> docking region for the specified orientation constant.
	 * <code>LEFT</code> maps to <code>WEST_REGION</code>, <code>RIGHT</code> maps to <code>EAST_REGION</code>, 
	 * <code>TOP</code> maps to <code>NORTH_REGION</code>, <code>BOTTOM</code> maps to <code>SOUTH_REGION</code>, 
	 * and <code>CENTER</code> maps to <code>CENTER_REGION</code>.  All other integer values will cause this 
	 * method to return <code>UNKNOWN_REGION</code>.
	 * <br/>
	 * All constants, both integer an <code>String</code> values, can be found on the <code>DockingConstants</code>
	 * interface.
	 * 
	 * @param regionType the orientation constant to translate into a docking region
	 * @return the <code>String</code> docking region for the specified orientation constant.
	 * @see DockingConstants#LEFT
	 * @see DockingConstants#RIGHT
	 * @see DockingConstants#TOP
	 * @see DockingConstants#BOTTOM
	 * @see DockingConstants#CENTER
	 * @see DockingConstants#WEST_REGION
	 * @see DockingConstants#EAST_REGION
	 * @see DockingConstants#NORTH_REGION
	 * @see DockingConstants#SOUTH_REGION
	 * @see DockingConstants#CENTER_REGION
	 * @see DockingConstants#UNKNOWN_REGION
	 */
	public static String getRegion(int regionType) {
		switch(regionType) {
			case LEFT:
				return WEST_REGION;
			case RIGHT:
				return EAST_REGION;
			case TOP:
				return NORTH_REGION;
			case BOTTOM:
				return SOUTH_REGION;
			case CENTER:
				return CENTER_REGION;
			default:
				return UNKNOWN_REGION;
		}
	}
	
	/**
	 * Returns <code>true</code> if the specified <code>Dockable</code> is currently minimized; <code>false</code>
	 * otherwise.  If the <code>Dockable</code> is <code>null</code>, then this method returns <code>false</code>.
	 * <br/>
	 * This method retrieves the current <code>DockingState</code> instance associated with the 
	 * <code>Dockable</code> and calls it's <code>isMinimized()</code> method to return.  
	 * <code>DockingState</code> for the specified <code>Dockable</code> is queried by calling
	 * <code>getDockingState(Dockable dockable)</code> on the <code>DockingManager's</code> currently 
	 * installed <code>LayoutManager</code>.
	 * 
	 * @param dockable the <code>Dockable</code> whose minimized state is to be returned
	 * @return <code>true</code> if the specified <code>Dockable</code> is currently minimized; <code>false</code>
	 * otherwise.
	 * @see DockingState#isMinimized()
	 * @see DockingManager#getLayoutManager()
	 * @see org.flexdock.docking.state.LayoutManager#getDockingState(Dockable)
	 */
	public static boolean isMinimized(Dockable dockable) {
		if(dockable==null)
			return false;
		
		DockingState info = getDockingState(dockable);
		return info==null? false: info.isMinimized();
	}
	
	/**
	 * Returns an <code>int</code> value representing the current minimization constraint for the
	 * specified <code>Dockable</code>.  If the <code>Dockable</code> is <code>null</code>, then this 
	 * method returns <code>MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT</code>.
	 * <br/>
	 * This method retrieves the current <code>DockingState</code> instance associated with the 
	 * <code>Dockable</code> and calls it's <code>getMinimizedConstraint()</code> method to return.  
	 * <code>DockingState</code> for the specified <code>Dockable</code> is queried by calling
	 * <code>getDockingState(Dockable dockable)</code> on the <code>DockingManager's</code> currently 
	 * installed <code>LayoutManager</code>.
	 * 
	 * @param dockable the <code>Dockable</code> whose minimized constraint is to be returned
	 * @return an <code>int</code> value representing the current minimization constraint for the
	 * specified <code>Dockable</code>
	 * @see MinimizationManager#UNSPECIFIED_LAYOUT_CONSTRAINT
	 * @see DockingState#getMinimizedConstraint()()
	 * @see DockingManager#getLayoutManager()
	 * @see org.flexdock.docking.state.LayoutManager#getDockingState(Dockable)
	 */
	public static int getMinimizedConstraint(Dockable dockable) {
		int defaultConstraint = MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT;
		DockingState info = dockable==null? null: getDockingState(dockable);
		return info==null? defaultConstraint: info.getMinimizedConstraint();
	}
	
	private static DockingState getDockingState(Dockable dockable) {
		return DockingManager.getLayoutManager().getDockingState(dockable);
	}

	/**
	 * Docks the specified <code>Dockable</code> relative to another already-docked 
	 * <code>Dockable</code> in the specified region.  The "parent" <code>Dockable</code> 
	 * must currently be docked.  If not, this method will return <code>false</code>.  
	 * Otherwise, its parent <code>DockingPort</code>
	 * will be resolved and the new <code>Dockable</code> will be docked into the 
	 * <code>DockingPort</code> relative to the "parent" <code>Dockable</code>.
	 * This method defers processing to 
	 * <code>dockRelative(Dockable dockable, Dockable parent, String relativeRegion, float ratio)</code> 
	 * passing <code>UNSPECIFIED_SIBLING_PREF</code> for the <code>ratio</code> parameter.
	 * <br/>
	 * This method returns <code>false</code> if any of the input parameters are <code>null</code> or if the 
	 * specified <code>region</code> is invalid according to 
	 * <code>DockingManager.isValidDockingRegion(String region)</code>.
	 * If the specified region is other than CENTER, then a split layout should result.
	 * 
	 * @param dockable the <code>Dockable</code> to be docked
	 * @param parent the <code>Dockable</code> used as a reference point for docking
	 * @param relativeRegion the docking region into which <code>dockable</code> will be docked
	 * @return <code>true</code> if the docking operation was successful; <code>false</code> otherwise.
	 * @see #dockRelative(Dockable, Dockable, String, float)
	 * @see DockingManager#isValidDockingRegion(String)
	 * @see Dockable#getDockingPort()
	 * @see DockingManager#dock(Dockable, DockingPort, String)
	 */
	public static boolean dockRelative(Dockable dockable, Dockable parent, String relativeRegion) {
		return dockRelative(parent, dockable, relativeRegion, UNSPECIFIED_SIBLING_PREF);
	}
	
	/**
	 * Docks the specified <code>Dockable</code> relative to another already-docked 
	 * <code>Dockable</code> in the specified region with the specified split proportion.
	 * The "parent" <code>Dockable</code> must currently be docked.  If not, this method will 
	 * return <code>false</code>.  Otherwise, its parent <code>DockingPort</code>
	 * will be resolved and the new <code>Dockable</code> will be docked into the 
	 * <code>DockingPort</code> relative to the "parent" <code>Dockable</code>.
	 * If the specified region is CENTER, then the <code>proportion</code> parameter is 
	 * ignored.  Otherwise, a split layout should result with the proportional space specified
	 * in the <code>proportion</code> parameter allotted to the <code>dockable</code> argument.
	 * <br/>
	 * This method returns <code>false</code> if any of the input parameters are <code>null</code> or if the 
	 * specified <code>region</code> is invalid according to 
	 * <code>DockingManager.isValidDockingRegion(String region)</code>.
	 * 
	 * @param dockable the <code>Dockable</code> to be docked
	 * @param parent the <code>Dockable</code> used as a reference point for docking
	 * @param relativeRegion the docking region into which <code>dockable</code> will be docked
	 * @param ratio the proportional space to allot the <code>dockable</code> argument 
	 * if the docking operation results in a split layout.
	 * @return <code>true</code> if the docking operation was successful; <code>false</code> otherwise.
	 * @see DockingManager#isValidDockingRegion(String)
	 * @see Dockable#getDockingPort()
	 * @see DockingManager#dock(Dockable, DockingPort, String)
	 */
	public static boolean dockRelative(Dockable dockable, Dockable parent, String relativeRegion, float ratio) {
		if(parent==null || dockable==null || !DockingManager.isValidDockingRegion(relativeRegion))
			return false;

		// set the sibling preference
		setSiblingPreference(parent, relativeRegion, ratio);
		
		DockingPort port = parent.getDockingPort();
		if(port!=null)
			return DockingManager.dock(dockable, port, relativeRegion);

		return false;
	}
	
	private static void setSiblingPreference(Dockable src, String region, float size) {
		if(size==UNSPECIFIED_SIBLING_PREF || CENTER_REGION.equals(region) || !DockingManager.isValidDockingRegion(region))
			return;
		
		size = DefaultRegionChecker.validateSiblingSize(size);
		src.getDockingProperties().setSiblingSize(region, size);
	}
	
	/**
	 * Returns <code>true</code> if the specified <code>Dockable</code> is currently docked within
	 * a floating dialog.  This method returns <code>false</code> if the <code>Dockable</code>
	 * is presently, minimized, hidden, docked within the main application layout, or if the 
	 * <code>Dockable</code> parameter is <code>null</code>.
	 * <br/>
	 * This method retrieves the current <code>DockingState</code> instance associated with the 
	 * <code>Dockable</code> and calls it's <code>isFloating()</code> method to return.  
	 * <code>DockingState</code> for the specified <code>Dockable</code> is queried by calling
	 * <code>getDockingState(Dockable dockable)</code> on the <code>DockingManager's</code> currently 
	 * installed <code>LayoutManager</code>.
	 * 
	 * @param dockable the <code>Dockable</code> whose floating state is to be returned
	 * @return <code>true</code> if the specified <code>Dockable</code> is currently floating; 
	 * <code>false</code> otherwise.
	 * @see DockingState#isFloating()
	 * @see DockingManager#getLayoutManager()
	 * @see org.flexdock.docking.state.LayoutManager#getDockingState(Dockable)
	 */
	public static boolean isFloating(Dockable dockable) {
		DockingState info = getDockingState(dockable);
		return info==null? false: info.isFloating();
	}
	
	/**
	 * Returns <code>true</code> if the specified <code>Dockable</code> is currently docked within
	 * a <code>DockingPort</code>.  This method returns <code>false</code> if the <code>Dockable</code>
	 * is presently floating, minimized, hidden, or if the <code>Dockable</code> parameter is <code>null</code>.
	 * 
	 * @param dockable the <code>Dockable</code> whose embedded state is to be returned
	 * @return <code>true</code> if the specified <code>Dockable</code> is currently docked within
	 * a <code>DockingPort</code>; <code>false</code> otherwise.
	 * @see DockingManager#isDocked(Dockable)
	 * @see #isFloating(Dockable)
	 */
	public static boolean isEmbedded(Dockable dockable) {
		return dockable==null? false: DockingManager.isDocked(dockable) && !isFloating(dockable);
	}
	


	/**
	 * Sets the divider location of the split layout embedded within the specified 
	 * <code>DockingPort</code>.  This method differs from both 
	 * <code>setSplitProportion(Dockable dockable, float proportion)</code> in that this method
	 * resolves the split layout embedded <b>within</b> the specified <code>DockingPort</code>, 
	 * whereas the other method modifies the split layout <b>containing</b> its respective 
	 * <code>Dockable</code> parameter.
	 * <br/>
	 * The resulting divider location will be a percentage of the split layout size based upon the 
	 * <code>proportion</code> parameter.  Valid values for <code>proportion</code> range from <code>0.0F<code>
	 * to <code>1.0F</code>.  For example, a <code>proportion</code> of <code>0.3F</code> will move the 
	 * divider to 30% of the "size" (<i>width</i> for horizontal split, <i>height</i> for vertical split) of the 
	 * split container embedded within the specified <code>DockingPort</code>.  If a <code>proportion</code> of less 
	 * than <code>0.0F</code> is supplied, the value </code>0.0F</code> is used.  If a <code>proportion</code> 
	 * greater than <code>1.0F</code> is supplied, the value </code>1.0F</code> is used.
	 * <br/>
	 * This method should be effective regardless of whether the split layout in question has been fully realized
	 * and is currently visible on the screen.  This should alleviate common problems associated with setting
	 * percentages of unrealized <code>Component</code> dimensions, which are initially <code>0x0</code> before
	 * the <code>Component</code> has been rendered to the screen.
	 * <br/>
	 * If the specified <code>DockingPort</code> is <code>null</code>, then no <code>Exception</code> is 
	 * thrown and no action is taken.  Identical behavior occurs if the <code>DockingPort</code> does not 
	 * contain split layout.  
	 * 
	 * @param port the <code>DockingPort</code> containing the split layout is to be resized.
	 * @param proportion the percentage of split layout size to which the split divider should be set.
	 * @see SwingUtility#setSplitDivider(JSplitPane, float)
	 */
	public static void setSplitProportion(DockingPort port, float proportion) {
		if(port==null)
			return;
		
		Component comp = port.getDockedComponent();
		if(comp instanceof JSplitPane)
			SwingUtility.setSplitDivider((JSplitPane)comp, proportion);
	}
	
	/**
	 * Sets the divider location of the split layout containing the specified dockable <code>Component</code>.
	 * <br/>
	 * The resulting divider location will be a percentage of the split layout size based upon the 
	 * <code>proportion</code> parameter.  Valid values for <code>proportion</code> range from <code>0.0F<code>
	 * to <code>1.0F</code>.  For example, a <code>proportion</code> of <code>0.3F</code> will move the 
	 * divider to 30% of the "size" (<i>width</i> for horizontal split, <i>height</i> for vertical split) of the 
	 * split container that contains the specified <code>Dockable</code>.  If a <code>proportion</code> of less 
	 * than <code>0.0F</code> is supplied, the value </code>0.0F</code> is used.  If a <code>proportion</code> 
	 * greater than <code>1.0F</code> is supplied, the value </code>1.0F</code> is used.
	 * <br/>
	 * It is important to note that the split divider location is only a percentage of the container size 
	 * from left to right or top to bottom.  A <code>proportion</code> of <code>0.3F</code> does not imply 
	 * that <code>dockable</code> itself will be allotted 30% of the available space.  The split divider will 
	 * be moved to the 30% position of the split container regardless of the region in which the specified 
	 * <code>Dockable</code> resides (which may possibly result in <code>dockable</code> being allotted 70% of 
	 * the available space). 
	 * <br/>
	 * This method should be effective regardless of whether the split layout in question has been fully realized
	 * and is currently visible on the screen.  This should alleviate common problems associated with setting
	 * percentages of unrealized <code>Component</code> dimensions, which are initially <code>0x0</code> before
	 * the <code>Component</code> has been rendered to the screen.
	 * <br/>
	 * If the specified <code>Dockable</code> is <code>null</code>, then no <code>Exception</code> is thrown
	 * and no action is taken.  Identical behavior occurs if the <code>Dockable</code> does not reside within a 
	 * split layout.  
	 * <br/>
	 * If the <code>Dockable</code> resides within a tabbed layout, a check is done to see if the 
	 * tabbed layout resides within a parent split layout.  If so, the resolved split layout is resized.  
	 * Otherwise no action is taken.
	 * 
	 * @param dockable the <code>Dockable</code> whose containing split layout is to be resized.
	 * @param proportion the percentage of containing split layout size to which the split divider should be set.
	 * @see SwingUtility#setSplitDivider(JSplitPane, float)
	 */
	public static void setSplitProportion(Dockable dockable, float proportion) {
		if(dockable==null)
			return;
		
		Component comp = dockable.getComponent();
		Container parent = comp.getParent();
		if(parent instanceof JTabbedPane) {
			parent = parent.getParent();
		}
		if(!(parent instanceof DockingPort))
			return;
		
		Container grandParent = parent.getParent();
		if(grandParent instanceof JSplitPane)
			SwingUtility.setSplitDivider((JSplitPane)grandParent, proportion);
	}
	
	/**
	 * Returns the text to be used by a <code>Dockable</code> as a tab label within a tabbed layout.
	 * This method retrieves the associated <code>DockablePropertySet</code> by calling 
	 * <code>getDockingProperties()</code> on the specified <code>Dockable</code>.  It then returns the
	 * value retrieved from calling <code>getDockableDesc()</code> on the <code>DockablePropertySet</code>
	 * instance.  If the specified <code>Dockable</code> is <code>null</code>, then this method returns
	 * <code>null</code>.
	 * 
	 * @param dockable the <code>Dockable</code> whose tab-text is to be returned
	 * @return the text to be used by a <code>Dockable</code> as a tab label within a tabbed layout.
	 * @see Dockable#getDockingProperties()
	 * @see DockablePropertySet#getDockableDesc()
	 */
	public static String getTabText(Dockable dockable) {
		DockablePropertySet props = dockable==null? null: dockable.getDockingProperties();
		return props==null? null: props.getDockableDesc();
	}
	
	/**
	 * Returns <code>true</code> if the specific <code>Object</code> is a <code>Dockable</code>.  If
	 * <code>obj instanceof Dockable</code> is <code>true</code>, then this method returns <code>true</code>.
	 * A <code>null</code> parameter will cause this method to return <code>false</code>.
	 * <br/>
	 * Registered <code>Dockable</code> components, if they are <code>JComponents</code>, will also have a
	 * <code>Boolean</code> client property present with the key <code>Dockable.DOCKABLE_INDICATOR</code>, used
	 * by dockable <code>JComponents</code> that don't implement the <code>Dockable</code> interface directly, 
	 * but acquire docking capabilities through a separate wrapper <code>Dockable</code> implementation.  For 
	 * these components, the <code>instanceof</code> check is insufficient since the valid <code>Dockable</code>
	 * is implemented by a separate class.  Therefore, if the <code>instanceof</code> check fails, and the 
	 * supplied <code>Object</code> parameter is a <code>JComponent</code>, a client property with the key 
	 * <code>Dockable.DOCKABLE_INDICATOR</code> is checked for a value of <code>Boolean.TRUE</code>.  If the
	 * client property is present, then this method returns <code>true</code>.
	 * 
	 * @param obj the <code>Object</code> to be checked to see if it represents a valid <code>Dockable</code>
	 * @return <code>true</code> if the specific <code>Object</code> is a <code>Dockable</code>
	 * @see Dockable#DOCKABLE_INDICATOR
	 * @see Boolean#TRUE
	 * @see javax.swing.JComponent#getClientProperty(java.lang.Object)
	 */
	public static boolean isDockable(Object obj) {
		if(obj==null)
			return false;
		
		// if the object directly implements Dockable, then we can return from here.
		if(obj instanceof Dockable)
			return true;
		
		// if the object is a JComponent, but not a Dockable implementation, then check its
		// client property indicator
		if(obj instanceof JComponent) {
			Component comp = (Component)obj;
			return SwingUtility.getClientProperty(comp, Dockable.DOCKABLE_INDICATOR)==Boolean.TRUE;
		}
		
		// they may have a heavyweight Component that does not directly implement Dockable.
		// in this case, Component does not have client properties we can check.  we'll have to
		// check directly with the DockingManager.
		if(obj instanceof Component) {
			Component comp = (Component)obj;
			return DockingManager.getDockable(comp)!=null;
		}
		
		return false;
	}
}
