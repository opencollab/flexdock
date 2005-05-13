package org.flexdock.view.restore;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.flexdock.dockbar.DockbarManager;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.defaults.layout.DockingPath;
import org.flexdock.docking.defaults.layout.SplitNode;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.view.View;

/**
 * @author Mateusz Szczap
 */
public class ViewManager implements IViewManager {
	
	private static ViewManager SINGLETON = null;
	
	private Map m_registeredViews = new HashMap();
	
	private HashMap m_registeredListeners = new HashMap();
	
	private HashSet m_viewStateListeners = new HashSet();
	
	private DockingPort m_centerDockingPort = null;
	
	private View m_territoralView = null;
	
	private ArrayList m_showViewHandlers = new ArrayList();
	
	private HashMap m_mainDockingInfos = new HashMap();
	
	private HashMap m_accessoryDockingInfos = new HashMap();
	
	private ViewManager() {
		initializeDefaultShowViewHandlers();
	}
	
	public static ViewManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new ViewManager();
		}
		return SINGLETON;
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#registerTerritoralView(org.flexdock.view.View)
	 */
	public void registerTerritoralView(View territoralView) {
		if (territoralView == null) throw new IllegalArgumentException("territoralView cannot be null");
		
		m_territoralView = territoralView;
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#registerView(org.flexdock.view.View)
	 */
	public void registerView(View view) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");
		
		DockingHandler dockingHandler = (DockingHandler) m_registeredListeners.get(view.getPersistentId());
		if (dockingHandler == null) {
			dockingHandler = new DockingHandler();
			m_registeredListeners.put(view.getPersistentId(), dockingHandler);
		}
		view.addDockingListener(dockingHandler);
		
		//it is ok since ViewManager is higher level so that it can know of the lower lever.
		//DockingManager is lower level
		DockingManager.registerDockable(view);
		m_registeredViews.put(view.getPersistentId(), view);
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#unregisterView(org.flexdock.view.View)
	 */
	public void unregisterView(String viewid) {
		if (viewid == null) throw new IllegalArgumentException("viewid cannot be null");
		
		if (m_registeredViews.containsKey(viewid)) {
			View view = (View) m_registeredViews.get(viewid);
			DockingHandler dockingHandler = (DockingHandler) m_registeredListeners.get(view.getPersistentId());
			if (dockingHandler != null) {
				view.removeDockingListener(dockingHandler);
			}
			m_registeredViews.remove(viewid);
		}
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#showView(org.flexdock.view.View)
	 */
	public boolean showView(View view) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");
		
		if (view == m_territoralView) {
			return m_centerDockingPort.dock(view, DockingPort.CENTER_REGION);
		}
		
		ViewDockingInfo mainDockingInfo = (ViewDockingInfo) m_mainDockingInfos.get(view.getPersistentId());
		ViewDockingInfo accessoryDockingInfo = (ViewDockingInfo) m_accessoryDockingInfos.get(view.getPersistentId());
		
		HashMap context = new HashMap();
		context.put("territoral.view", m_territoralView);
		context.put("main.docking.info", mainDockingInfo);
		context.put("accessory.docking.info", accessoryDockingInfo);
		
		boolean docked = false;
		for (int i=0; i<m_showViewHandlers.size(); i++) {
			ShowViewHandler showViewHandler = (ShowViewHandler) m_showViewHandlers.get(i);
			boolean isShown = showViewHandler.showView(view, context);
			if (isShown) {
				docked = isShown;
				break;
			}
		}
		
		if (docked) {
			fireViewStateChanged(view, ViewStateEvent.VIEW_SHOWN);
		}
		
		return docked;
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#getRegisteredView(java.lang.String)
	 */
	public View getRegisteredView(String viewId) {
		if (viewId == null) throw new IllegalArgumentException("viewId cannot be null");
		
		View view = (View) m_registeredViews.get(viewId);
		
		if (view != null) {
			return view;
		}
		
		return null;
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#hideView(org.flexdock.view.View)
	 */
	public boolean hideView(View view) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");

		DockingPath dockingPath = DockingPath.updateRestorePath(view);
		
		if (view.isMinimized()) {
			DockbarManager mgr = DockbarManager.getCurrent(view);
			if(mgr != null) {
			    int edge = DockbarManager.getInstance(view).getEdge(view);
			    ViewDockingInfo viewDockingInfo = ViewDockingInfo.createMinimizedDockingInfo(view, edge);
			    m_accessoryDockingInfos.put(view.getPersistentId(), viewDockingInfo);
			    mgr.remove(view);
				fireViewStateChanged(view, ViewStateEvent.VIEW_HIDDEN);
				return true;
			}
		} else if (view.isFloating()) {
		    Point locationOnScreen = (Point) view.getLocationOnScreen();
		    Dimension dimension = view.getSize();
			ViewDockingInfo viewDockingInfo = ViewDockingInfo.createFloatingDockingInfo(view, locationOnScreen, dimension);
			m_accessoryDockingInfos.put(view.getPersistentId(), viewDockingInfo); 

			boolean isHidden = DockingManager.undock(view);
			
			if (isHidden) {
				fireViewStateChanged(view, ViewStateEvent.VIEW_HIDDEN);
				return isHidden;
			}

			return true;
		} else { 
			boolean isHidden = DockingManager.undock(view);
			
			if (isHidden) {
				fireViewStateChanged(view, ViewStateEvent.VIEW_HIDDEN);
				return isHidden;
			}
		}
		
		return true;
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#registerCenterDockingPort(org.flexdock.docking.DockingPort)
	 */
	public void registerCenterDockingPort(DockingPort dockingPort) {
		if (dockingPort == null) throw new IllegalArgumentException("dockingPort cannot be null");
		
		m_centerDockingPort = dockingPort;
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#maximizeView(org.flexdock.view.View)
	 */
	public void maximizeView(View view) {
		//		if (view == null) throw new IllegalArgumentException("view cannot be null");
		//		
		//		if (view == m_territoralView) {
		//			//close all views except territoral view
		//		}
		//
		//		DockingManager.undock(m_territoralView);
		//		m_centerViewport.dock(view);
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#unmaximizeView(org.flexdock.view.View)
	 */
	public void unmaximizeView(View view) {
		//m_centerViewport.dock(m_territoralView);
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#registerViewDockingInfo(java.lang.String, org.flexdock.view.restore.ViewDockingInfo)
	 */
	public void registerViewDockingInfo(String viewId, ViewDockingInfo mainViewDockingInfo) {
		if (viewId == null) throw new IllegalArgumentException("viewId cannot be null");
		if (mainViewDockingInfo == null) throw new IllegalArgumentException("viewDockingInfo cannot be null");
		
		View view = (View) DockingManager.getRegisteredDockable(viewId);
		m_mainDockingInfos.put(view.getPersistentId(), mainViewDockingInfo);
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#unregisterViewDockingInfo(java.lang.String)
	 */
	public void unregisterViewDockingInfo(String viewId) {
		if (viewId == null) throw new IllegalArgumentException("viewId cannot be null");
		
		View view = (View) DockingManager.getRegisteredDockable(viewId);
		m_mainDockingInfos.remove(view.getPersistentId());
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#removeShowViewHandler(org.flexdock.view.restore.ShowViewHandler)
	 */
	public void removeShowViewHandler(ShowViewHandler showViewHandler) {
		if (showViewHandler == null) throw new NullPointerException("showViewHandler cannot be null");
		
		m_showViewHandlers.remove(showViewHandler);
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#initializeDefaultShowViewHandlers()
	 */
	public void initializeDefaultShowViewHandlers() {
		addShowViewHandler(new ViewShownHandler());
		addShowViewHandler(new AccessoryShowViewHandler());
		addShowViewHandler(new DockPathHandler());
		addShowViewHandler(new MainShowViewHandler());
		addShowViewHandler(new LastResortShowViewHandler());
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#addShowViewHandler(org.flexdock.view.perspective.ShowViewHandler)
	 */
	public void addShowViewHandler(ShowViewHandler showViewHandler) {
		if (showViewHandler == null) throw new NullPointerException("showViewHandler cannot be null");
		
		m_showViewHandlers.add(showViewHandler);
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#removeAllShowViewHandlers()
	 */
	public void removeAllShowViewHandlers() {
		m_showViewHandlers.clear();
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#addViewStateListener(org.flexdock.view.restore.ViewStateListener)
	 */
	public void addViewStateListener(ViewStateListener viewStateListener) {
		if (viewStateListener == null) throw new IllegalArgumentException("viewStateListener cannot be null");
		
		m_viewStateListeners.add(viewStateListener);
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#removeViewStateListener(org.flexdock.view.restore.ViewStateListener)
	 */
	public void removeViewStateListener(ViewStateListener viewStateListener) {
		if (viewStateListener == null) throw new IllegalArgumentException("viewStateListener cannot be null");
		
		m_viewStateListeners.remove(viewStateListener);
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#getViewStateListeners()
	 */
	public ViewStateListener[] getViewStateListeners() {
		return (ViewStateListener[]) m_viewStateListeners.toArray(new ViewStateListener[]{});
	}
	
	protected void fireViewStateChanged(View view, int viewStateEventType) {
		ViewStateEvent viewStateEvent = new ViewStateEvent(this, view, viewStateEventType);
		
		for (Iterator it = m_viewStateListeners.iterator(); it.hasNext();) {
			ViewStateListener viewStateListener = (ViewStateListener) it.next();
			viewStateListener.viewStateChanged(viewStateEvent);
		}
	}
	
	private class DockingHandler extends DockingListener.Stub {
		
		/**
		 * @see org.flexdock.docking.event.DockingListener#dockingComplete(org.flexdock.docking.event.DockingEvent)
		 */
		public void dockingComplete(DockingEvent dockingEvent) {
			final View sourceView = (View) dockingEvent.getSource();
			final DockingPort dockingPort = dockingEvent.getNewDockingPort();
			final String region = dockingEvent.getRegion();
			final boolean isOverWindow = dockingEvent.isOverWindow();
			
			// invoke preserve() later, as we'll be attempting to calculate a splitPane
			// ratio.  the new splitPane hasn't yet painted, so it's dimensions are 
			// currently 0x0.  wait until after rendering before we preserve().
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					preserve(sourceView, dockingPort, region, isOverWindow);
				}
			});
			// TODO: fix DockingEvent.  dockingComplete() has purposely been setup to execute
			// "now" instead of having events deferred.  we should really get rid of 
			// EventQueue.invokeLater() and precalculate the splitPane ratio, passing it
			// in within the DockingEvent.
		}
		
		private boolean preserve(View sourceView, DockingPort dockingPort, String region, boolean isOverWindow) {
		    Set dockableSet = dockingPort.getDockables();
			if (!dockableSet.isEmpty()) {
				for (Iterator it = dockableSet.iterator(); it.hasNext();) {
					Dockable childDockable = (Dockable) it.next();
					// for the time being, we only want to handle Views.
					// TODO Change this to handle raw Dockables in the future.
					if(!(childDockable instanceof View))
						continue;
					
					View childView = (View) childDockable;
					if (!childView.equals(sourceView)) {
						float ratio = getSplitPaneRatio(sourceView, region);
						ViewDockingInfo viewDockingInfo = ViewDockingInfo.createRelativeDockingInfo(childView, region, ratio);
						m_accessoryDockingInfos.put(sourceView.getPersistentId(), viewDockingInfo);
						return true;
					}
				}
			}
			return false;
		}
		
		private float getSplitPaneRatio(View sourceView, String region) {
			DockingPath dockingPath = DockingPath.create(sourceView);
			
			// check to see if the dockable was in a split layout.  if so, get the deepest split
			// node we can find so we can grab the split proportion percentage.
			SplitNode lastSplitNode = dockingPath==null? null: dockingPath.getLastNode();
			if (lastSplitNode != null) {
				if (lastSplitNode.getOrientation() == SplitNode.HORIZONTAL) {
					// I'm not sure the purpose of flipping around the percentage here for
					// horizontal splitPanes.  maybe there is something i'm missing? 
					// - marius 
					return 1.0f-lastSplitNode.getPercentage();
					//System.out.println(splitNode.getPercentage());
				}
			    return lastSplitNode.getPercentage();
			}
				
			// if we couldn't determine the splitPane ratio using the DockingPath above, then
			// try the regionInsets
			Float ratioObject = sourceView.getDockingProperties().getRegionInset(region);
			if (ratioObject != null) {
				return ratioObject.floatValue();
			} 
			// if we still can't find a specified splitPane percentage, then use
			// the default value
			return RegionChecker.DEFAULT_SIBLING_SIZE;
		}
		
	}
	
}
