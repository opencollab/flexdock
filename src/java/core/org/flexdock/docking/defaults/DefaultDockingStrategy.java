/*
 * Created on Mar 14, 2005
 */
package org.flexdock.docking.defaults;

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;

import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.DockingStrategy;
import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.drag.DragManager;
import org.flexdock.docking.drag.DragOperation;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.floating.frames.DockingFrame;
import org.flexdock.docking.floating.frames.FloatingDockingPort;
import org.flexdock.docking.state.FloatManager;
import org.flexdock.event.EventDispatcher;
import org.flexdock.util.DockingUtility;
import org.flexdock.util.RootWindow;
import org.flexdock.util.SwingUtility;

/**
 * @author Christopher Butler
 */
public class DefaultDockingStrategy implements DockingStrategy, DockingConstants {
	public static final String PREFERRED_PROPORTION = "DefaultDockingStrategy.PREFERRED_PROPORTION";
	
	/**
	 * Returns the specified <code>Dockable's</code> sibling <code>Dockable</code> within the 
	 * current docking layout.  This method checks the parent <code>DockingPort</code> of a given 
	 * <code>Dockable</code> to see if it is split equally with another <code>Dockable</code>.  If
	 * so, the immediate sibling <code>Dockable</code> is returned.  If there are more than 
	 * two <code>Dockables</code> within the split layout, then the closest sibling region is 
	 * determined and this method dispatches to 
	 * <code>getSibling(Dockable dockable, String region)</code>.
	 * <br/>
	 * If the specified <code>Dockable</code> is <code>null</code>, or there are no siblings
	 * available in the docking layout, then this methdo returns a <code>null</code> reference.
	 * If the specified <code>Dockable</code> is not currently docked within a <code>DockingPort</code>, 
	 * then this method returns a <code>null</code> reference.
	 * 
	 * @param dockable the <code>Dockable</code> whose sibling is to be returned
	 * @return the sibling of the specified <code>Dockable</code> within the current docking layout.
	 * @see Dockable#getDockingPort()
	 * @see #getSibling(Dockable, String)
	 */
	public static Dockable getSibling(Dockable dockable) {
		if(dockable==null)
			return null;
		
		DockingPort port = dockable.getDockingPort();
		String startRegion = findRegion(dockable.getComponent());
		String region = DockingUtility.flipRegion(startRegion);
		Dockable sibling = findDockable(port, dockable.getComponent(), region, startRegion);

		return sibling;
	}
	
	/**
	 * Returns the sibling <code>Dockable</code> relative to the specified <code>Dockable's</code> 
	 * supplied region in the current docking layout.  If <code>dockable</code> is <code>null</code> 
	 * or <code>region</code> is either invalid or equal to <code>CENTER_REGION</code>, then this
	 * method returns a <code>null</code> reference.
	 * <br/>
	 * If the specified <code>Dockable</code> is in a <code>DockingPort</code> that equally splits
	 * the layout between two <code>Dockables</code> in a fashion that matches up with the specified
	 * region, then the immediate sibling <code>Dockable</code> is returned.  Otherwise, a fuzzy 
	 * search is performed throughout the docking layout for a <code>Dockable</code> that "looks like"
	 * it is docked to the supplied region of the specified <code>Dockable</code> from a visual 
	 * standpoint.
	 * <br/>
	 * For instance, a docking layout may consist of four quadrants <i>Dockable1</i> (top-left), 
	 * <i>Dockable2</i> (top-right), <i>Dockable3</i> (bottom-left) and <i>Dockable4</i> (bottom-right).  
	 * The layout is built by docking <i>Dockable2>/i> to the <code>EAST_REGION</code> of
	 * <i>Dockable1</i>, <i>Dockable3</i> to the <code>SOUTH_REGION</code> of <i>Dockable1</i>, and 
	 * <i>Dockable4</i> to the <code>SOUTH_REGION</code> of <i>Dockable2</i>.  Within this layout, 
	 * <i>Dockable1</i> and <i>Dockable3</i> are immediate siblings, as are <i>Dockable2</i> and 
	 * <i>Dockable4</i>.  Thus, requesting sibling NORTH_REGION of <i>Dockable3</i> will easily 
	 * yield <i>Dockable1</i>.  However, <i>Dockable3</i> has no immediate <code>EAST_REGION</code>
	 * sibling.  In this case, a fuzzy search through the layout is performed to determine the visual
	 * sibling, and this method returns <i>Dockable4</i>.  Likewise, this method will return a 
	 * <code>null</code> reference for the <code>WEST_REGION</code> sibling of <i>Dockable3</code>, 
	 * since there are no <code>Dockables</code> in the visual layout to the west of this <code>Dockable</code>.
	 * 
	 * @param dockable the <code>Dockable</code> whose sibling is to be returned
	 * @param region the region of the specified <code>Dockable</code> whose visual sibling is to
	 * be returned
	 * @return the <code>Dockable</code> in the supplied region relative to the specified <code>Dockable</code> 
	 */
	public static Dockable getSibling(Dockable dockable, String region) {
		if(dockable==null || !DockingManager.isValidDockingRegion(region) || CENTER_REGION.equals(region))
			return null;
		
		DockingPort port = dockable.getDockingPort();
		String startRegion = findRegion(dockable.getComponent());
		Dockable sibling = findDockable(port, dockable.getComponent(), region, startRegion);

		return sibling;
	}
	
	private static Dockable findDockable(DockingPort port, Component self, String region, String startRegion) {
		if(port==null)
			return null;
		
		Component docked = port.getDockedComponent();
		// if we're not a split port, then there is no concept of 'outer regions'.
		// jump up a level to find the parent split port
		if(!(docked instanceof JSplitPane)) {
			DockingPort superPort = DockingManager.getDockingPort((Component)port);
			return findDockable(superPort, self, region, startRegion);
		}
		
		JSplitPane split = (JSplitPane)docked;
		Component sibling = port.getComponent(region);
		if(sibling==self) {
			if(!(self instanceof JSplitPane)) {
				DockingPort superPort = DockingManager.getDockingPort((Component)port);
				return findDockable(superPort, self, region, startRegion);				
			}
			return null;
		}
		
		if(sibling instanceof JSplitPane) {
			// go one level deeper
			DockingPort subPort = DockingManager.getDockingPort(sibling);
			Component other = port.getComponent(DockingUtility.flipRegion(region));
			String subRegion = findSubRegion((JSplitPane)sibling, other, region, startRegion);
			return findDockable(subPort, self, subRegion, startRegion);
		}
		
		// if we have no direct sibling in the specified region, the jump
		// up a level.
		if(sibling==null) {
			DockingPort superPort = DockingManager.getDockingPort((Component)port);
			self = port.getDockedComponent();
			return findDockable(superPort, self, region, startRegion);
		}

		return DockingManager.getDockable(sibling);
	}
	
	private static String findSubRegion(JSplitPane split, Component other, String targetRegion, String baseRegion) {
		String region = DockingUtility.translateRegion(split, targetRegion);
		if(!(other instanceof JSplitPane))
			return region;

		
		boolean translated = !targetRegion.equals(region);
		if(translated && !DockingUtility.isAxisEquivalent(region, baseRegion)) {
			region = DockingUtility.flipRegion(region);
		}

		return region;
	}
	
	/**
	 * Returns the docking region within the current split docking layout containing the
	 * specified <code>Component</code>.  If <code>comp</code> is <code>null</code>, then
	 * a <code>null</code> reference is returned.  If <code>comp</code> is not in a 
	 * split layout, then <code>CENTER_REGION</code> is returned.
	 * <br/>
	 * This method resolves the associated <code>Dockable</code> and <code>DockingPort</code>
	 * for the specified <code>Component</code> and backtracks through the docking layout to
	 * find a split layout.  If a split layout is found, then the region retured by this method
	 * is calculated relative to its sibling in the layout.
	 * 
	 * @param comp the <code>Component</code> whose region is to be returned
	 * @return the region of the current split layout containing the specified <code>Dockable</code>
	 */
	public static String findRegion(Component comp) {
		if(comp==null)
			return null;
		
		DockingPort port = DockingManager.getDockingPort(comp);
		Component docked = port.getDockedComponent();

		if(!(docked instanceof JSplitPane)) {
			// we didn't find a split pane, to check the grandparent dockingport
			DockingPort superPort = DockingManager.getDockingPort((Component)port);
			// if there was no grandparent DockingPort, then we're stuck with the docked
			// component we already found.  this can happen on the root dockingport.
			docked = superPort==null? docked: superPort.getDockedComponent();
		}
		
		if(!(docked instanceof JSplitPane))
			return CENTER_REGION;
		
		JSplitPane split = (JSplitPane)docked;
		boolean horiz = split.getOrientation()==JSplitPane.HORIZONTAL_SPLIT;
		Component left = split.getLeftComponent();
		if(left==port) {
			return horiz? WEST_REGION: NORTH_REGION;
		}
		return horiz? EAST_REGION: SOUTH_REGION;
			
	}
	
	/**
	 * Docks the specified <code>Dockable</code> into the supplied <code>region</code> of
	 * the specified <code>DockingPort</code>.  This method is meant for programmatic 
	 * docking, as opposed to realtime, event-based docking operations.  As such, it defers
	 * processing to 
	 * <code>dock(Dockable dockable, DockingPort port, String region, DragOperation token)</code>, 
	 * passing a <code>null</code> argument for the <code>DragOperation</code> parameter.  This 
	 * implies that there is no event-based drag operation currently in progress to control the
	 * semantics of the docking operation, only that an attempt should be made to dock
	 * the specified <code>Dockable</code> into the specified <code>DockingPort</code>.
	 * <br/>
	 * This method will return <code>false</code> if <code>dockable</code> or <code>port</code> are
	 * <code>null</code>, or if <code>region</code> is not a valid region according to the specified
	 * <code>DockingPort</code>.  If a <code>Dockable</code> is currently docked within the 
	 * specified <code>DockingPort</code>, then that <code>Dockable's</code> territorial properties
	 * are also checked and this method may return <code>false</code> if the territory is 
	 * blocked.  Finally, this method will return <code>false</code> if the specified 
	 * <code>Dockable</code> is already docked within the supplied region of the specified
	 * <code.DockingPort</code>.
	 * 
	 * @param dockable the <code>Dockable</code> we wish to dock
	 * @param port the <code>DockingPort</code> into which we wish to dock
	 * @param region the region of the specified <code>DockingPort</code> into which we wish to dock.
	 * @return <code>true</code> if the docking operation was successful, <code>false</code. otherwise.
	 * @see #dock(Dockable, DockingPort, String, DragOperation)
	 * @see DockingPort#isDockingAllowed(String, Component)
	 * @see Dockable#getDockingProperties()
	 * @see DockableProps#isTerritoryBlocked(String)
	 */
	public boolean dock(Dockable dockable, DockingPort port, String region) {
		return dock(dockable, port, region, null);
	}

	/**
	 * Docks the specified <code>Dockable</code> into the supplied <code>region</code> of
	 * the specified <code>DockingPort</code>.  This method is meant for realtime, event-based 
	 * docking based on an in-progress drag operation.  It is not recommended for developers to 
	 * call this method programmatically, except to pass in a <code>null</code> 
	 * <code>DragOperation</code> argument.
	 * 	 * <br/>
	 * The <code>DragOperation</code> parameter, 
	 * if present, will control the semantics of the docking operation based upon current mouse
	 * position, drag threshold, and a customizable drag context <code>Map</code>.  For instance, 
	 * the <code>DragOperation</code> may contain information regarding the <code>Dockable</code>
	 * over which the mouse is currently hovered, whether the user is attempting to drag a 
	 * <code>Dockable</code> outside the bounds of any existing windows (perhaps in an attempt to
	 * float the <code>Dockable</code>), or whether the current distance offset from the original 
	 * drag point sufficiently warrants a valid docking operation.
	 * <br/>
	 * If the <code>DragOperation</code> is <code>null</code>, then this method will attempt to 
	 * programmatically dock the specified <code>Dockable</code> into the supplied <code>region</code>
	 * of the specified <code>DockingPort</code> without regard to external event-based criteria. 
	 * This is in accordance with the behavior specified by 
	 * <code>dock(Dockable dockable, DockingPort port, String region)</code>.
	 *  
	 * This method will return <code>false</code> if <code>dockable</code> or <code>port</code> are
	 * <code>null</code>, or if <code>region</code> is not a valid region according to the specified
	 * <code>DockingPort</code>.  If a <code>Dockable</code> is currently docked within the 
	 * specified <code>DockingPort</code>, then that <code>Dockable's</code> territorial properties
	 * are also checked and this method may return <code>false</code> if the territory is 
	 * blocked.  If a <code>DragOperation</code> is present, then this method will 
	 * return <code>false</code> if the required drag threshold has not been exceeded.  Finally, this 
	 * method will return <code>false</code> if the specified <code>Dockable</code> is already docked 
	 * within the supplied region of the specified <code.DockingPort</code>.
	 * 
	 * @param dockable the <code>Dockable</code> we wish to dock
	 * @param port the <code>DockingPort</code> into which we wish to dock
	 * @param region the region of the specified <code>DockingPort</code> into which we wish to dock.
	 * @return <code>true</code> if the docking operation was successful, <code>false</code. otherwise.
	 * @see #dock(Dockable, DockingPort, String, DragOperation)
	 * @see DockingPort#isDockingAllowed(String, Component)
	 * @see Dockable#getDockingProperties()
	 * @see DockableProps#isTerritoryBlocked(String)
	 */
	public boolean dock(Dockable dockable, DockingPort port, String region, DragOperation operation) {
		if(!isDockingPossible(dockable, port, region, operation))
			return false;
		
		if(!dragThresholdElapsed(operation))
			return false;
		
		// cache the old parent
		DockingPort oldPort = dockable.getDockingPort();

		// perform the drop operation.
		DockingResults results = dropComponent(dockable, port, region, operation);

		// perform post-drag operations
		DockingPort newPort = results.dropTarget;
		int evtType = results.success? DockingEvent.DOCKING_COMPLETE: DockingEvent.DOCKING_CANCELED;
		Map dragContext = DragManager.getDragContext(dockable);
		DockingEvent evt = new DockingEvent(dockable, oldPort, newPort, evtType, dragContext);
		// populate DockingEvent status info 
		evt.setRegion(region);
		evt.setOverWindow(operation==null? true: operation.isOverWindow());

		// notify the old docking port, new dockingport,and dockable
		Object[] evtTargets = {oldPort, newPort, dockable};
		EventDispatcher.dispatch(evt, evtTargets);
		
		return results.success; 
	}
	
	protected boolean dragThresholdElapsed(DragOperation token) {
		if(token==null || token.isPseudoDrag() || token.getStartTime()==-1)
			return true;
		
		long elapsed = System.currentTimeMillis() - token.getStartTime();
		// make sure the elapsed time of the drag is at least over .2 seconds.
		// otherwise, we'll probably be responding to inadvertent clicks (maybe double-clicks)
		return elapsed > 200;
	}
	
	protected boolean isDockingPossible(Dockable dockable, DockingPort port, String region, DragOperation token) {
		// superclass blocks docking if the 'port' or 'region' are null.  If we've dragged outside
		// the bounds of the parent frame, then both of these will be null.  This is expected here and
		// we intend to float in this case.
		if(isFloatable(dockable, token))
			return true;
		
		
		// check to see if we're already floating and we're trying to drop into the 
		// same dialog.
		DockingPort oldPort = DockingManager.getDockingPort(dockable);
		if(oldPort instanceof FloatingDockingPort && oldPort==port) {
			// only allow this situation if we're not the *last* dockable
			// in the viewport.  if we're removing the last dockable, then
			// the dialog will disappear before we redock, and we don't want this
			// to happen.
			FloatingDockingPort floatingDockingPort = (FloatingDockingPort)oldPort;
			if(floatingDockingPort.getDockableCount()==1)
				return false;
		}
		
		if(dockable==null || dockable.getComponent()==null || port==null)
			return false;
		
		if(!DockingManager.isValidDockingRegion(region))
			return false;
		
		Dockable docked = DockingManager.getDockable(port.getDockedComponent());
		if(docked==null)
			return true;

		// don't allow them to dock into this region if the territory there is blocked.
		if(docked.getDockingProperties().isTerritoryBlocked(region).booleanValue())
			return false;
		
		// check to see if we're already docked into this region.
		// get the parent dockingPort.
		Container container = docked.getComponent().getParent();
		// now get the grandparent dockingport
		DockingPort grandparent = DockingManager.getDockingPort(container);
		
		// if we don't share the grandparent dockingport, then we're definitely not split in the same dockingport
		// across different region.  in this case, it's ok to proceed with the dock
		if(grandparent==null)
			return true;
		
		Component currentlyInRegion = grandparent.getComponent(region);
		// block docking if we're already the component docked within the specified region
		if(currentlyInRegion==dockable.getComponent())
			return false;
		
		return true;
	}
	
	
	
	
	
	
	protected boolean isFloatable(Dockable dockable, DragOperation token) {
		// can't float null objects
		if(dockable==null || dockable.getComponent()==null || token==null)
			return false;
		
		// can't float on a fake drag operation 
		if(token.isPseudoDrag())
			return false;
		
		// TODO: break this check out into a separate DropPolicy class.
		// should be any customizable criteria, not hardcoded to checking
		// for being outside the bounds of a window
		if(token.isOverWindow())
			return false;
		
		return true;
	}
	
	
	
	
	

	protected DockingResults dropComponent(Dockable dockable, DockingPort target, String region, DragOperation token) {
		if(isFloatable(dockable, token))
			return floatComponent(dockable, target, token);
		
		DockingResults results = new DockingResults(target, false);
		
		if (UNKNOWN_REGION.equals(region) || target==null) {
			return results;
		}
			
		Component docked = target.getDockedComponent();
		Component dockableCmp = dockable.getComponent();
		if (dockableCmp!=null && dockableCmp == docked) {
			// don't allow docking the same component back into the same port
			return results;
		}

		// obtain a reference to the content pane that holds the target DockingPort.
		// MUST happen before undock(), in case the undock() operation removes the 
		// target DockingPort from the container tree.
		Container contentPane = SwingUtility.getContentPane((Component)target);
		Point contentPaneLocation = token==null? null: token.getCurrentMouse(contentPane);
		
		// undock the current Dockable instance from it's current parent container
		undock(dockable);

		// when the original parent reevaluates its container tree after undocking, it checks to see how 
		// many immediate child components it has.  split layouts and tabbed interfaces may be managed by 
		// intermediate wrapper components.  When undock() is called, the docking port 
		// may decide that some of its intermedite wrapper components are no longer needed, and it may get 
		// rid of them. this isn't a hard rule, but it's possible for any given DockingPort implementation.  
		// In this case, the target we had resolved earlier may have been removed from the component tree 
		// and may no longer be valid.  to be safe, we'll resolve the target docking port again and see if 
		// it has changed.  if so, we'll adopt the resolved port as our new target.
		if(contentPaneLocation!=null && contentPane!=null) {
			results.dropTarget = DockingUtility.findDockingPort(contentPane, contentPaneLocation);
			target = results.dropTarget;
		}

		results.success = target.dock(dockableCmp, region);
		SwingUtility.revalidate((Component) target);
		return results;
	}
	
	/**
	 * Undocks the specified <code>Dockable</code> from it's parent <code>DockingPort</code>.
	 * If <code>dockable</code> is <code>null</code> or is not currently docked within a 
	 * <code>DockingPort</code>, then this method returns <code>false</code>.
	 *
	 *@param dockable the <code>Dockable</code> to be undocked.
	 *@return <code>true</code> if the undocking operation was successful, <code>false</code> otherwise.
	 *@see #dock(Dockable, DockingPort, String)
	 */
	public boolean undock(Dockable dockable) {
		if(dockable==null)
			return false;
		
		Component dragSrc = dockable.getComponent();
		Container parent = dragSrc.getParent();
		RootWindow rootWin = RootWindow.getRootContainer(parent);
		
		// if there's no parent container, then we really don't have anything from which to to 
		// undock this component, now do we?
		if(parent==null)
			return false;
		
		boolean success = false;
		DockingPort dockingPort = DockingUtility.getParentDockingPort(dragSrc);
		
		// notify that we are about to undock
		Map dragContext = DragManager.getDragContext(dockable);
		DockingEvent dockingEvent = new DockingEvent(dockable, dockingPort, dockingPort, DockingEvent.UNDOCKING_STARTED, dragContext);
		EventDispatcher.dispatch(dockingEvent);
//		if(dockingEvent.isConsumed())
//			return false;
		
		if(dockingPort!=null) {
			// if 'dragSrc' is currently docked, then undock it instead of using a 
			// simple remove().  this will allow the DockingPort to do any of its own 
			// cleanup operations associated with component removal.
			success = dockingPort.undock(dragSrc);
		} else {
			// otherwise, just remove the component
			parent.remove(dragSrc);
			success = true;
		}
		
		if(rootWin != null) {
			SwingUtility.revalidate(rootWin.getContentPane());
	}
	
		if (success) {
			dockingEvent = new DockingEvent(dockable, dockingPort, dockingPort, DockingEvent.UNDOCKING_COMPLETE, dragContext);
			// notify the docking port and dockable
			Object[] evtTargets = {dockingPort, dockable};
			EventDispatcher.dispatch(dockingEvent, evtTargets);
		}

		return success;
	}
	
	
	protected DockingResults floatComponent(Dockable dockable, DockingPort target, DragOperation token) {
		// otherwise,  setup a new DockingFrame and retarget to the CENTER region
		DockingResults results = new DockingResults(target, false);

		// determine the bounds of the new frame
		Point screenLoc = token.getCurrentMouse(true);
		SwingUtility.add(screenLoc, token.getMouseOffset());
		Rectangle screenBounds = dockable.getComponent().getBounds();
		screenBounds.setLocation(screenLoc);
		
		// create the frame
		FloatManager mgr = DockingManager.getFloatManager();
		DockingFrame frame = mgr.floatDockable(dockable, dockable.getComponent(), screenBounds);
		
		// grab a reference to the frame's dockingPort for posterity
		results.dropTarget = frame.getDockingPort();

		results.success = true;
		return results;
	}
	
	
	protected static class DockingResults {
		public DockingResults(DockingPort port, boolean status) {
			dropTarget = port;
			success = status;
		}
		public DockingPort dropTarget;
		public boolean success;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Returns a new <code>DefaultDockingPort</code> with characteristics similar to the 
	 * specified base <code>DockingPort</code>.  If the base <code>DockingPort</code> is a
	 * <code>DefaultDockingPort</code>, then the returned <code>DockingPort</code>
	 * will share the base <code>DockingPort's</code> border manager and tabbed drag-source flag.
	 * The returned <code>DockingPort's</code> <code>isRoot()</code> method will return 
	 * <code>false</code>.
	 * 
	 * @param base the <code>DockingPort</code> off of which to base the returned <code>DockingPort</code>
	 * @return a new <code>DefaultDockingPort</code> with characteristics similar to the 
	 * specified base <code>DockingPort</code>.
	 * @see DefaultDockingPort#getBorderManager()
	 * @see DefaultDockingPort#setBorderManager(BorderManager)
	 * @see DefaultDockingPort#isTabsAsDragSource()
	 * @see DefaultDockingPort#setTabsAsDragSource(boolean)
	 * @see DefaultDockingPort#setRoot(boolean)
	 */
	public DockingPort createDockingPort(DockingPort base) {
		DockingPort port = createDockingPortImpl(base);
		
		if(port instanceof DefaultDockingPort && base instanceof DefaultDockingPort) {
			DefaultDockingPort newPort = (DefaultDockingPort)port;
			DefaultDockingPort ddp = (DefaultDockingPort)base;
			newPort.setBorderManager(ddp.getBorderManager());
			newPort.setTabsAsDragSource(ddp.isTabsAsDragSource());
			newPort.setRoot(false);
		}
		return port;
	}
	
	protected DockingPort createDockingPortImpl(DockingPort base) {
		return new DefaultDockingPort();
	}
	
	/**
	 * Returns a new <code>DockingSplitPane</code> based on the specified <code>DockingPort</code.
	 * and region.  Creation of the <code>DockingSplitPane</code> is deferred to an internal 
	 * protected method to allow for overriding by subclasses. A client property is set on the 
	 * returned split pane with the key DockingConstants.REGION to indicate the creation
	 * region of the split pane for non-<code>DockingSplitPanes</code> returned by overriding 
	 * subclasses.
	 * <br/>  
	 * This method determines the "elder" component of the split pane by checking whether the 
	 * new creation region is in the TOP or LEFT (NORTH_REGION or WEST_REGION).  If the creation
	 * region, representing where the new <code>Dockable</code> will be docked, is <b>not</b> in the 
	 * top or left, then the elder <code>Component</code> in the split pane must be.  This information 
	 * is used to initialize the resize weight of the split pane, setting resize weight to <code>1</code>
	 * if the elder is in the top or left of the split pane and <code>0</code> if not.  This gives
	 * the elder <code>Component</code> in the resulting split pane priority in the layout with resizing
	 * the split pane.   
	 * <br/>
	 * If the creation region is <code>NORTH_REGION</code> or <code>SOUTH_REGION</code>, the 
	 * returned split pane is initialized with a <code>VERTICAL_SPLIT</code> orientation; otherwise
	 * a <code>HORIZONTAL_SPLIT</code> orientation is used.
	 * <br/>
	 * Before returning, the border is removed from the split pane, its divider size is set to 3, 
	 * and if possible the border is removed from the split pane divider.  This is to avoid an
	 * excessive compound border effect for embedded <code>Components</code> within the split pane
	 * that may have their own borders.
	 * 
	 * @param base the <code>DockingPort</code> off of which the returned <code>JSplitPane</code>
	 * will be based.
	 * @param region the region within the base <code>DockingPort</code> used to determine the
	 * orientation of the returned <code>JSplitPane</code>.
	 * @return a new <code>DockingSplitPane</code> based on the specified <code>DockingPort</code.
	 * and region.
	 * @see DockingSplitPane#DockingSplitPane(DockingPort, String)
	 * @see #createSplitPaneImpl(DockingPort, String)
	 * @see JSplitPane#setResizeWeight(double)
	 */
	public JSplitPane createSplitPane(DockingPort base, String region) {
		JSplitPane split = createSplitPaneImpl(base, region);
		// mark the creation region on the split pane
		SwingUtility.putClientProperty(split, DockingConstants.REGION, region);
		
		// the creation region represents the "new" region, not the elder region.
		// so if the creation region is NOT in the top left, then the elder region is.
		boolean elderInTopLeft = !DockingUtility.isRegionTopLeft(region);
		int resizeWeight = elderInTopLeft? 1: 0;
		// set the resize weight based on the location of the elder component
		split.setResizeWeight(resizeWeight);
		
		// determine the orientation
		int orientation = JSplitPane.HORIZONTAL_SPLIT;
		if(NORTH_REGION.equals(region) || SOUTH_REGION.equals(region))
			orientation = JSplitPane.VERTICAL_SPLIT;
		split.setOrientation(orientation);
		
		// remove the border from the split pane
		split.setBorder(null);
         
		// set the divider size for a more reasonable, less bulky look 
		split.setDividerSize(3);

		// check the UI.  If we can't work with the UI any further, then
		// exit here.
		if (!(split.getUI() instanceof BasicSplitPaneUI))
		   return split;

		//  grab the divider from the UI and remove the border from it
		BasicSplitPaneDivider divider =
					   ((BasicSplitPaneUI) split.getUI()).getDivider();
		if (divider != null)
		   divider.setBorder(null);

		return split;
	}
	
	protected JSplitPane createSplitPaneImpl(DockingPort base, String region) {
		return new DockingSplitPane(base, region);
	}
	

	/**
	 * Returns the initial divider location to be used by the specified <code>JSplitPane</code> when
	 * it is embedded within the specified <code>DockingPort</code>.  It is assumed that the <code>JSplitPane</code> 
	 * parameter is embedded within the specified <code>DockingPort</code>, is validated, visible, and its dimensions
	 * are non-zero.
	 * <br/>
	 * This method gets the "size" of the specified <code>DockingPort</code> based on the orientation of the
	 * split pane (<i>width</i> for horizontal split, <i>height</i> for vertical split) minus the <code>DockingPort's</code>
	 * insets.  It then dispatches to <code>getDividerProportion(DockingPort port, JSplitPane splitPane)</code>
	 * to determine the preferred proportion of the split pane divider.  The returned value for this method is the 
	 * product of the <code>DockingPort</code> size and the split proportion.
	 * <br/>
	 * If either <code>port</code> or <code>splitPane</code> parameters are <code>null</code>, then this method
	 * returns <code>0</code>.
	 * 
	 * @param port the <code>DockingPort</code> that contains the specified <code>JSplitPane</code>.
	 * @param splitPane the <code>JSplitPane</code> whose initial divider location is to be determined.
	 * @return the desired divider location of the supplied <code>JSplitPane</code>.
	 * @see DockingStrategy#getInitialDividerLocation(DockingPort, JSplitPane)
	 * @see #getDividerProportion(DockingPort, JSplitPane)
	 */
	public int getInitialDividerLocation(DockingPort port, JSplitPane splitPane) {
		if(port==null || splitPane==null)
			return 0;

		Container dockingPort = (Container)port;
		Insets in = dockingPort.getInsets();
		boolean vert = splitPane.getOrientation()==JSplitPane.VERTICAL_SPLIT;
		int inset = vert? in.top + in.bottom: in.left + in.right;
		
		// get the dimensions of the DockingPort, minus the insets 
		int portSize = vert? dockingPort.getHeight(): dockingPort.getWidth();
		portSize -= inset;
		
		// get the divider proportion for the split pane and multiply by the port size
		double proportion = getDividerProportion(port, splitPane);
		if(proportion<0 || proportion>1)
			proportion = 0.5d;
		
		return (int)(portSize*proportion);
	}
	
	/**
	 * Returns the desired divider proportion of the specified <code>JSplitPane</code> after
	 * rendering.  This method assumes that the <code>JSplitPane</code> parameter is, or will be 
	 * embedded within the specified <code>DockingPort</code>.  This method does <b>not</b> assume that 
	 * the <code>JSplitPane</code> has been validated and that it's current dimensions are non-zero.
	 * <br/>
	 * If either <code>port</code> or <code>splitPane</code> parameters are <code>null</code>, then this
	 * method returns the default value of <code>RegionChecker.DEFAULT_SIBLING_SIZE</code>.  
	 * Otherwise the "elder" component within the <code>JSplitPane</code> is determined to see if it
	 * is contained within a sub-<code>DockingPort</code>.  If the "elder" <code>Component</code> 
	 * cannot be determined, or it is not contained within a sub-<code>DockingPort</code>, then
	 * the default value of <code>RegionChecker.DEFAULT_SIBLING_SIZE</code> is returned.
	 * <br/>
	 * If the "elder" <code>Component</code> is successfully resolved inside a 
	 * sub-<code>DockingPort</code>, then a check is done on the sub-port for the client property 
	 * <code>DefaultDockingStrategy.PREFERRED_PROPORTION</code>.  If this value is found, then the
	 * primitive float version of it is returned.
	 * <br/>
	 * Failing these checks, the <code>Dockable</code> is resolved for the "elder" 
	 * <code>Component</code> in the specified <code>JSplitPane</code> via
	 * <code>DockingManager.getDockable(Component comp)</code>.  If no <code>Dockable</code> can
	 * be found, then <code>RegionChecker.DEFAULT_SIBLING_SIZE</code> is returned.
	 * <br/>
	 * Otherwise, the <code>DockingPortProps<code> is retrieved from the specified
	 * <code>DockingPort</code> and its <code>getRegionChecker()</code> method is called.
	 * <code>getSiblingSize(Component c, String region)</code> is invoked on the returned 
	 * <code>RegionChecker</code> passing the "elder" <code>Component</code> in the split pane 
	 * and the creation region resolved for the specified <code>JSplitPane</code>.  This resolves
	 * the preferred sibling size for the elder <code>Dockable</code> component.  If the elder 
	 * <code>Component</code> is in the top/left of the split pane, then <code>1F-prefSize</code>
	 * is returned.  Otherwise, the preferred sibling size is returned.
	 * 
	 * @param port the <code>DockingPort</code> that contains, or will contain the specified <code>JSplitPane</code>.
	 * @param splitPane the <code>JSplitPane</code> whose initial divider location is to be determined.
	 * @return the desired divider proportion of the supplied <code>JSplitPane</code>.
	 * @see RegionChecker#DEFAULT_SIBLING_SIZE
	 * @see #PREFERRED_PROPORTION
	 * @see DockingManager#getDockable(Component)
	 * @see RegionChecker#getSiblingSize(Component, String)
	 */
	public double getDividerProportion(DockingPort port, JSplitPane splitPane) {
		if(port==null || splitPane==null)
			return RegionChecker.DEFAULT_SIBLING_SIZE;

		Component elder = getElderComponent(splitPane);
		if(elder==null)
			return RegionChecker.DEFAULT_SIBLING_SIZE;
		
		Float prefProp = getPreferredProportion(splitPane, elder);
		if(prefProp!=null)
			return prefProp.doubleValue();
		
		if(elder instanceof DockingSplitPane) {
			elder = ((DockingSplitPane)elder).getElderComponent();
		}
		
		Dockable dockable = DockingManager.getDockable(elder);
		if(dockable!=null) {
//			DockingSplitPane splitter = (DockingSplitPane)splitPane;
			RegionChecker rc = port.getDockingProperties().getRegionChecker();
			float prefSize = rc.getSiblingSize(dockable.getComponent(), getCreationRegion(splitPane));
			return isElderTopLeft(splitPane)? 1f-prefSize: prefSize;
//			return prefSize;
		}

		return RegionChecker.DEFAULT_SIBLING_SIZE;
	}
	
	protected String getCreationRegion(JSplitPane splitPane) {
		if(splitPane instanceof DockingSplitPane)
			return ((DockingSplitPane)splitPane).getRegion();
		return (String)SwingUtility.getClientProperty(splitPane, DockingConstants.REGION);
	}
	
	protected boolean isElderTopLeft(JSplitPane splitPane) {
		if(splitPane instanceof DockingSplitPane)
			return ((DockingSplitPane)splitPane).isElderTopLeft();
		String region = getCreationRegion(splitPane);
		// creation region represents the "new" region, not the "elder" region.
		// so if the "new" region is NOT the topLeft, then the "elder" is.
		return !DockingUtility.isRegionTopLeft(region);
	}
	
	protected Float getPreferredProportion(JSplitPane splitPane, Component controller) {
		// 'controller' is inside a dockingPort.  re-reference to the parent dockingPort.
		Container controllerPort = controller.getParent();
		return getPreferredProportion(controllerPort);
	}
	
	protected Component getElderComponent(JSplitPane splitPane) {
		if(splitPane instanceof DockingSplitPane)
			return ((DockingSplitPane)splitPane).getElderComponent();
		
		boolean inTopLeft = isElderTopLeft(splitPane);
		Component comp = inTopLeft? splitPane.getLeftComponent(): splitPane.getRightComponent();
		if(comp instanceof DockingPort)
			comp = ((DockingPort)comp).getDockedComponent();
		return comp;
	}
	
	
	protected static Float getPreferredProportion(Component c) {
		return c==null? null: (Float)SwingUtility.getClientProperty(c, PREFERRED_PROPORTION);
	}
}
