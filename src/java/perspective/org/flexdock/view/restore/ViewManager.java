package org.flexdock.view.restore;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.flexdock.dockbar.restore.DockingPath;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * @author Mateusz Szczap
 */
public class ViewManager implements IViewManager {

	private static ViewManager SINGLETON = null;
	
	private HashMap m_registeredListeners = new HashMap();

	private HashSet m_viewStateListeners = new HashSet();
	
	private Viewport m_centerViewport = null;
	
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
	 * @see org.flexdock.view.restore.IViewManager#showView(org.flexdock.view.View)
	 */
	public boolean showView(View view) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");

		if (view == m_territoralView) {
			return m_centerViewport.dock(view);
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
		
		Dockable dockable = DockingManager.getRegisteredDockable(viewId);
		
		if (dockable != null && dockable instanceof View) {
			return (View) dockable;
		}

		return null;
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewManager#hideView(org.flexdock.view.View)
	 */
	public boolean hideView(View view) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");
		DockingPath.setRestorePath(view);

		boolean isHidden = DockingManager.undock(view);

		if (isHidden) {
			fireViewStateChanged(view, ViewStateEvent.VIEW_HIDDEN);
		}

		return isHidden;
	}

	/**
	 * @see org.flexdock.view.restore.IViewManager#registerCenterViewport(org.flexdock.view.Viewport)
	 */
	public void registerCenterViewport(Viewport viewport) {
		if (viewport == null) throw new IllegalArgumentException("viewPort cannot be null");
		
		m_centerViewport = viewport;
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

		DockingHandler dockingHandler = new DockingHandler();
		m_registeredListeners.put(viewId, dockingHandler);
		view.addDockingListener(dockingHandler);
		
		m_mainDockingInfos.put(view.getPersistentId(), mainViewDockingInfo);
	}

	/**
	 * @see org.flexdock.view.restore.IViewManager#unregisterViewDockingInfo(java.lang.String)
	 */
	public void unregisterViewDockingInfo(String viewId) {
		if (viewId == null) throw new IllegalArgumentException("viewId cannot be null");

		View view = (View) DockingManager.getRegisteredDockable(viewId);
		DockingListener dockingListener = (DockingListener) m_registeredListeners.get(viewId);
		view.removeDockingListener(dockingListener);
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
	
	private class DockingHandler extends DockingListener.DockingAdapter implements HierarchyBoundsListener {

		/**
		 * @see org.flexdock.docking.event.DockingListener#dockingComplete(org.flexdock.docking.event.DockingEvent)
		 */
		public void dockingComplete(DockingEvent dockingEvent) {
			View sourceView = (View) dockingEvent.getSource();
			DockingPort dockingPort = dockingEvent.getNewDockingPort();
			String region = dockingEvent.getRegion();
			boolean isOverWindow = dockingEvent.isOverWindow();
			
			preserve(sourceView, dockingPort, region, isOverWindow);
		}

		private boolean preserve(View sourceView, DockingPort dockingPort, String region, boolean isOverWindow) {
			Viewport viewPort = (Viewport) dockingPort;
			if (!viewPort.getViewset().isEmpty()) {
				for (Iterator it = viewPort.getViewset().iterator(); it.hasNext();) {
					View childView = (View) it.next();

					if (!isOverWindow) {
						//if it is a floating window register as component listener
						childView.addHierarchyBoundsListener(this);
						Float ratioObject = sourceView.getDockingProperties().getRegionInset(region);
						float ratio = -1.0f;
						if (ratioObject != null) {
							ratio = ratioObject.floatValue();
						} else {
							ratio = RegionChecker.DEFAULT_SIBLING_SIZE;
						}

						ViewDockingInfo viewDockingInfo = new ViewDockingInfo(childView, region, ratio);
						viewDockingInfo.setFloating(true);
						Point locationOnScreen = sourceView.getLocationOnScreen();
						Dimension dim = sourceView.getSize();
						viewDockingInfo.setFloatingLocation(locationOnScreen);
						viewDockingInfo.setFloatingWindowDimension(dim);
						m_accessoryDockingInfos.put(sourceView.getPersistentId(), viewDockingInfo); 
						return true;
					} else {
						sourceView.removeHierarchyBoundsListener(this);
						ViewDockingInfo dockingInfo = (ViewDockingInfo) m_accessoryDockingInfos.get(sourceView.getPersistentId());
						if (dockingInfo != null) {
							dockingInfo.setFloating(false);
						}
					}
					
					if (!childView.equals(sourceView)) {
						Float ratioObject = sourceView.getDockingProperties().getRegionInset(region);
						float ratio = -1.0f;
						if (ratioObject != null) {
							ratio = ratioObject.floatValue();
						} else {
							ratio = RegionChecker.DEFAULT_SIBLING_SIZE;
						}
						ViewDockingInfo viewDockingInfo = new ViewDockingInfo(childView, region, ratio);
						m_accessoryDockingInfos.put(sourceView.getPersistentId(), viewDockingInfo); 
						return true;
					}
				}
			}
			return false;
		}

		public void ancestorMoved(HierarchyEvent e) {
			View sourceView = (View) e.getSource();
			if (sourceView.isShowing()) {
				handle(sourceView);
			}
		}
		
		public void ancestorResized(HierarchyEvent e) {
			View sourceView = (View) e.getSource();
			if (sourceView.isShowing()) {
				handle(sourceView);
			}
		}

		private void handle(View sourceView) {
			Dimension dimension = sourceView.getSize();
			Point location = sourceView.getLocationOnScreen();
			ViewDockingInfo viewDockingInfo = (ViewDockingInfo) m_accessoryDockingInfos.get(sourceView.getPersistentId());
			viewDockingInfo.setFloatingLocation(location);
			viewDockingInfo.setFloatingWindowDimension(dimension);
		}
		
	}
	
}
