/*
 * Created on Mar 14, 2005
 */
package org.flexdock.docking.defaults;

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.flexdock.dockbar.DockbarManager;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.DockingStrategy;
import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.drag.DragToken;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.EventDispatcher;
import org.flexdock.util.DockingUtility;
import org.flexdock.util.RootWindow;
import org.flexdock.util.SwingUtility;

/**
 * @author Christopher Butler
 */
public class DefaultDockingStrategy implements DockingStrategy {
	public static final String PREFERRED_PROPORTION = "DefaultDockingStrategy.PREFERRED_PROPORTION";
	
	public static Dockable getSibling(Dockable dockable, String region) {
		if(dockable==null || !DockingManager.isValidDockingRegion(region) || DockingPort.CENTER_REGION.equals(region))
			return null;
		
		DockingPort port = dockable.getDockingPort();
		String startRegion = findRegion(dockable.getDockable());
		Dockable sibling = findDockable(port, dockable.getDockable(), region, startRegion);

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

		return DockingManager.getRegisteredDockable(sibling);
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
	
	public static String findRegion(Component comp) {
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
			return DockingPort.CENTER_REGION;
		
		JSplitPane split = (JSplitPane)docked;
		boolean horiz = split.getOrientation()==JSplitPane.HORIZONTAL_SPLIT;
		Component left = split.getLeftComponent();
		if(left==port) {
			return horiz? DockingPort.WEST_REGION: DockingPort.NORTH_REGION;
		}
		return horiz? DockingPort.EAST_REGION: DockingPort.SOUTH_REGION;
			
	}
	
	public boolean dock(Dockable dockable, DockingPort port, String region) {
		return dock(dockable, port, region, null);
	}

	public boolean dock(Dockable dockable, DockingPort port, String region, DragToken token) {
		if(!isDockingPossible(dockable, port, region, token))
			return false;
		
		// cache the old parent
		DockingPort oldPort = dockable.getDockingPort();

		// perform the drop operation.
		DockingResults results = dropComponent(dockable, port, region, token);

		// perform post-drag operations
		DockingPort newPort = results.dropTarget;
		int evtType = results.success? DockingEvent.DOCKING_COMPLETE: DockingEvent.DOCKING_CANCELED;
		DockingEvent evt = new DockingEvent(dockable, oldPort, newPort, evtType);
		// populate DockingEvent status info 
		evt.setRegion(region);
		evt.setOverWindow(token==null? true: token.isOverWindow());

		// notify the old docking port
		EventDispatcher.notifyDockingMonitor(oldPort, evt);
		// notify the new docking port
		EventDispatcher.notifyDockingMonitor(newPort, evt);
		// notify the dockable
		EventDispatcher.notifyDockingMonitor(dockable, evt);
		
		return results.success; 
	}
	
	protected boolean isDockingPossible(Dockable dockable, DockingPort port, String region, DragToken token) {
		if(dockable==null || dockable.getDockable()==null || port==null)
			return false;
		
		if(!DockingManager.isValidDockingRegion(region))
			return false;
		
		Dockable docked = DockingManager.getRegisteredDockable(port.getDockedComponent());
		if(docked==null)
			return true;

		// don't allow them to dock into this region if the territory there is blocked.
		if(docked.getDockingProperties().isTerritoryBlocked(region).booleanValue())
			return false;
		
		// check to see if we're already docked into this region.
		// get the parent dockingPort.
		Container container = docked.getDockable().getParent();
		// now get the grandparent dockingport
		DockingPort grandparent = DockingManager.getDockingPort(container);
		
		// if we don't share the grandparent dockingport, then we're definitely not split in the same dockingport
		// across different region.  in this case, it's ok to proceed with the dock
		if(grandparent==null)
			return true;
		
		Component currentlyInRegion = grandparent.getComponent(region);
		// block docking if we're already the component docked within the specified region
		if(currentlyInRegion==dockable.getDockable())
			return false;
		
		return true;
	}
	

	protected DockingResults dropComponent(Dockable dockable, DockingPort target, String region, DragToken token) {
		DockingResults results = new DockingResults(target, false);
		
		if (DockingPort.UNKNOWN_REGION.equals(region) || target==null) {
			return results;
		}
			
		Component docked = target.getDockedComponent();
		Component dockableCmp = dockable.getDockable();
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

		String tabText = dockable.getDockingProperties().getDockableDesc();
		results.success = target.dock(dockableCmp, tabText, region);
		SwingUtility.revalidateComponent((Component) target);
		return results;
	}
	
	public boolean undock(Dockable dockable) {
		if(dockable==null)
			return false;
		
		Component dragSrc = dockable.getDockable();
		Container parent = dragSrc.getParent();
		RootWindow rootWin = RootWindow.getRootContainer(parent);
		
		// if there's no parent container, then we really don't have anything from which to to 
		// undock this component, now do we?
		if(parent==null)
			return false;
		
		boolean success = false;
		DockingPort dockingPort = DockingUtility.getParentDockingPort(dragSrc);
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
			SwingUtility.revalidateComponent(rootWin.getContentPane());
	}
	
		if (success) {
			DockingEvent dockingEvent = new DockingEvent(dockable, dockingPort, dockingPort, DockingEvent.UNDOCKING_COMPLETE);
			// notify the docking port
			EventDispatcher.notifyDockingMonitor(dockingPort, dockingEvent);
			// notify the dockable
			EventDispatcher.notifyDockingMonitor(dockable, dockingEvent);
		}

		return success;
	}
	
	protected static class DockingResults {
		public DockingResults(DockingPort port, boolean status) {
			dropTarget = port;
			success = status;
		}
		public DockingPort dropTarget;
		public boolean success;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public DockingPort createDockingPort(DockingPort base) {
		DockingPort port = createDockingPortImpl(base);
		
		if(port instanceof DefaultDockingPort && base instanceof DefaultDockingPort) {
			DefaultDockingPort newPort = (DefaultDockingPort)port;
			DefaultDockingPort ddp = (DefaultDockingPort)base;
			newPort.setBorderManager(ddp.getBorderManager());
			newPort.setTabsAsDragSource(ddp.isTabsAsDragSource());
			newPort.setTransient(true);
		}
		return port;
	}
	
	protected DockingPort createDockingPortImpl(DockingPort base) {
		return new DefaultDockingPort();
	}
	
	
	public JSplitPane createSplitPane(DockingPort base, String region) {
		JSplitPane split = createSplitPaneImpl(base, region);
		
		// determine the orientation
		int orientation = JSplitPane.HORIZONTAL_SPLIT;
		if(DockingPort.NORTH_REGION.equals(region) || DockingPort.SOUTH_REGION.equals(region))
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
	

	
	public int getInitialDividerLocation(DockingPort port, JSplitPane splitPane, Component elder) {
		if(port==null || splitPane==null || elder==null)
			return 0;
		
		Component cmp = port.getDockedComponent();
		if(!(cmp instanceof JSplitPane))
			return 0;
		
		Container dockingPort = (Container)port;
		JSplitPane split = (JSplitPane)cmp;
		
		Insets in = dockingPort.getInsets();
		boolean vert = split.getOrientation()==JSplitPane.VERTICAL_SPLIT;
		
		// get the dimensions of the DockingPort, minus the insets and multiply by 
		// the divider proportion. 
		int dim = vert? (dockingPort.getHeight()-in.top-in.bottom): (dockingPort.getWidth()-in.left-in.right);
		double proportion = getDividerProportion(port, split, elder);
		if(proportion<0 || proportion>1)
			proportion = 0.5d;
		
		return (int)(dim*proportion);
	}
	
	public double getDividerProportion(DockingPort port, JSplitPane splitPane, Component elder) {
		if(port==null || splitPane==null || elder==null || !(splitPane instanceof DockingSplitPane))
			return RegionChecker.DEFAULT_SIBLING_SIZE;

		Float prefProp = getPreferredProportion(splitPane, elder);
		if(prefProp!=null)
			return prefProp.doubleValue();
		
		if(elder instanceof DockingSplitPane) {
			elder = ((DockingSplitPane)elder).getController();
		}
		
		Dockable dockable = DockingManager.getRegisteredDockable(elder);
		if(dockable!=null) {
			DockingSplitPane splitter = (DockingSplitPane)splitPane;
			RegionChecker rc = port.getDockingProperties().getRegionChecker();
			float prefSize = rc.getSiblingSize(dockable.getDockable(), splitter.getRegion());
			return splitter.isElderTopLeft()? 1f-prefSize: prefSize;
		}

		return RegionChecker.DEFAULT_SIBLING_SIZE;
	}
	
	protected Float getPreferredProportion(JSplitPane splitPane, Component controller) {
		// 'controller' is inside a dockingPort.  re-reference to the parent dockingPort.
		Container controllerPort = controller.getParent();
		return getPreferredProportion(controllerPort);
	}
	
	public static Float getPreferredProportion(Component c) {
		return c==null? null: (Float)SwingUtility.getClientProperty(c, PREFERRED_PROPORTION);
	}
	
	
	public void setMinimized(Dockable dockable, boolean minimizing, Component component, int edge) {
		DockbarManager mgr = DockbarManager.getInstance(component);
		if(mgr==null)
			return;

		// if minimizing, send to the dockbar
		if(minimizing) {
			if(edge==DockbarManager.UNSPECIFIED_EDGE)
				mgr.dock(dockable);
			else
				mgr.dock(dockable, edge);
		}
		// otherwise, remove from the dockbar
		else
			mgr.undock(dockable);
	}
}
