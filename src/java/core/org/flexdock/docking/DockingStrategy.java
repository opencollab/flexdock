/*
 * Created on Mar 14, 2005
 */
package org.flexdock.docking;

import javax.swing.JSplitPane;

import org.flexdock.docking.drag.DragOperation;

/**
 * This interface defines an API used by <code>DockingManager</code> and <code>DockingPort</code>
 * to support customizable behaviors during docking operations.  <code>DockingManager</code> will 
 * associate a <code>DockingStrategy</code> with a particular <code>DockingPort</code> or 
 * <code>Dockable</code> class type.  Calls to <code>DockingManager.dock()</code> and 
 * <code>DockingManager.undock()</code> will be deferred to the <code>DockingStrategy</code>
 * associated with the parameters supplied in the respective method calls.  
 * <code>DockingStrategies</code> are also responsible for creating sub-DockingPorts and 
 * split panes for nested <code>DockingPorts</code>
 * 
 * Implementations of <code>DockingStrategy</code> are responsible for managing component
 * relationships between <code>Dockables</code> and parent containers.  This includes making 
 * determinations as to whether a particular docking operation will be allowed for the specified
 * parameters and the specifics of how a particular <code>Dockable</code> may be removed from one
 * parent <code>Container<code> or <code>DockingPort</code> and added to another.
 * <code>DockingStrategy</code> may determine whether a call to <code>dock()</code> implies an 
 * attempt to float a <code>Dockable</code> in a separate window.
 * 
 * Because of the potentially large scope of responsibilities associated with a 
 * <code>DockingStrategy</code>, implementations may range from being very simple to highly
 * complex.  Although custom implementations of <code>DockingStrategy</code> are not discouraged, 
 * the recommeded path is to subclass <code>DefaultDockingStrategy</code> for consistency of 
 * behavior.
 * 
 * <code>DockingStrategies</code> are associated with a particular type of <code>Dockable</code>
 * or <code>DockingPort</code> by calling 
 * <code>DockingManager.setDockingStrategy(Class c, DockingStrategy strategy)</code>.
 * <code>DefaultDockingStrategy</code> is the default implementation used for all classes
 * that do not have a custom <code>DockingStrategy</code> registered.
 * 
 * @author Christopher Butler
 */
public interface DockingStrategy {
	
	/**
	 * Attempts to dock the specified <code>Dockable</code> into the supplied <code>DockingPort</code>
	 * in the specified region.  If docking is not possible for the specified parameters, then
	 * the method returns <code>false</code> and no action is taken.  Since there is no
	 * <code>DragOperation</code> parameter present, this method implies programmatic docking
	 * as opposed to docking as a result of drag-events.
	 *
	 * @param dockable the <code>Dockable</code> we wish to dock
	 * @param port the <code>DockingPort</code> into which we wish to dock
	 * @param region the region of the specified <code>DockingPort</code> into which we wish to dock.
	 * @return whether or not the docking operation was successful.
	 */
	boolean dock(Dockable dockable, DockingPort dockingPort, String dockingRegion);

	/**
	 * Attempts to dock the specified <code>Dockable</code> into the supplied <code>DockingPort</code>
	 * in the specified region based upon the semantics of the specified <code>DragOperation</code.  
	 * If docking is not possible for the specified parameters, then the method returns <code>false</code> and no action is taken.  
	 *
	 * @param dockable the <code>Dockable</code> we wish to dock
	 * @param port the <code>DockingPort</code> into which we wish to dock
	 * @param region the region of the specified <code>DockingPort</code> into which we wish to dock.
	 * @param operation the <code>DragOperation</code> describing the state of the application/mouse 
	 * at the point in time in which we're attempting to dock.
	 * @return whether or not the docking operation was successful.
	 */
	boolean dock(Dockable dockable, DockingPort dockingPort, String dockingRegion, DragOperation operation);
	
	/**
	 * Undocks the specified <code>Dockable</code> instance from its containing <code>DockingPort</code>.
	 *
	 * @param dockable the <code>Dockable</code> we wish to undock
	 * @return <code>true</code> if the <code>Dockable</code> was successfully undocked.  Otherwise, 
	 * returns <code>false</code>.
	 */
	boolean undock(Dockable dockable);
	
	/**
	 * Creates and returns a new <code>DockingPort</code> instance based upon the supplied 
	 * <code>DockingPort</code> parameter.  For layouts that support nested <code>DockingPorts</code>,  
	 * this method is useful for creating child <code>DockingPorts</code> suitable for embedding
	 * within the base <code>DockingPort</code>
	 * 
	 * @param base the <code>DockingPort</code> off of which the returned instance will be based.
	 * @return a new <code>DockingPort</code> instance based upon the supplied parameter.
	 */	
	DockingPort createDockingPort(DockingPort base);
	
	/**
	 * Creates and returns a new <code>JSplitPane</code> instance based upon the supplied parameters.
	 * The returned <code>JSplitPane</code> should be suitable for embedding within the base
	 * <code>DockingPort</code> and its orientation should reflect the supplied <code>region</code>
	 * parameter.
	 * 
	 * @param base the <code>DockingPort</code> off of which the returned <code>JSplitPane</code>
	 * will be based.
	 * @param region the region within the base <code>DockingPort</code> used to determine the
	 * orientation of the returned <code>JSplitPane</code>.
	 * @return a new <code>JSplitPane</code> suitable for embedding within the base <code>DockingPort</code>
	 * parameter.
	 */
	JSplitPane createSplitPane(DockingPort base, String region);
	
	/**
	 * Returns the initial divider location to be used by the specified <code>JSplitPane</code>.
	 * This method assumes that the <code>JSplitPane</code> parameter is embedded 
	 * within the specified <code>DockingPort</code> and that is has been validated and its
	 * current dimensions are non-zero.
	 * 
	 * @param port the <code>DockingPort</code> that contains, or will contain the specified <code>JSplitPane</code>.
	 * @param splitPane the <code>JSplitPane</code> whose initial divider location is to be determined.
	 * @return the desired divider location of the supplied <code>JSplitPane</code>.
	 */
	int getInitialDividerLocation(DockingPort dockingPort, JSplitPane splitPane);
	
	/**
	 * Returns the desired divider proportion of the specified <code>JSplitPane</code> after
	 * rendering.  This method assumes that the <code>JSplitPane</code> parameter is, or will be 
	 * embedded within the specified <code>DockingPort</code>.  This method does <b>not</b> assume that 
	 * the <code>JSplitPane</code> has been validated and that it's current dimensions are non-zero.
	 * 
	 * @param port the <code>DockingPort</code> that contains, or will contain the specified <code>JSplitPane</code>.
	 * @param splitPane the <code>JSplitPane</code> whose initial divider location is to be determined.
	 * @return the desired divider proportion of the supplied <code>JSplitPane</code>.
	 */
	double getDividerProportion(DockingPort dockingPort, JSplitPane splitPane);
    
}
