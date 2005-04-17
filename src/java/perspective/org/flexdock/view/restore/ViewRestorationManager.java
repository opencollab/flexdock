package org.flexdock.view.restore;

import java.util.ArrayList;
import java.util.HashMap;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * @author Mateusz Szczap
 */
public class ViewRestorationManager implements IViewRestorationManager {

	private static ViewRestorationManager SINGLETON = null;
	
	private HashMap m_registeredListeners = new HashMap();
	
	private PreservingStrategy m_preservingStrategy = new SimplePreservingStrategy();

	private Viewport m_centerViewport = null;
	
	private View m_territoralView = null;

	private ArrayList m_showViewHandlers = new ArrayList();
	
	private ViewRestorationManager() {
		initializeDefaultShowViewHandlers();
	}
	
	public static ViewRestorationManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new ViewRestorationManager();
		}
		return SINGLETON;
	}

	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#registerTerritoralView(org.flexdock.view.View)
	 */
	public void registerTerritoralView(View territoralView) {
		if (territoralView == null) throw new IllegalArgumentException("territoralView cannot be null");
			
		m_territoralView = territoralView;
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#showView(org.flexdock.view.View)
	 */
	public boolean showView(View view) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");

		ViewDockingInfo dockingInfo = (ViewDockingInfo) m_preservingStrategy.getMainDockingInfo(view);
		ViewDockingInfo[] accessoryDockingInfos = m_preservingStrategy.getAccessoryDockingInfos(view);

		HashMap context = new HashMap();
		context.put("territoral.view", m_territoralView);
		context.put("main.docking.info", dockingInfo);
		context.put("accessory.docking.infos", accessoryDockingInfos);
		
		boolean docked = false;
		for (int i=0; i<m_showViewHandlers.size(); i++) {
			ShowViewHandler showViewHandler = (ShowViewHandler) m_showViewHandlers.get(i);
			boolean isShown = showViewHandler.showView(view, context);
			if (isShown) {
				docked = isShown;
				break;
			}
		}
		
		return docked;
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#hideView(org.flexdock.view.View)
	 */
	public boolean hideView(View view) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");
		//TODO in future we might do some other stuff here
		return DockingManager.undock(view);
	}

	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#registerCenterViewport(org.flexdock.view.Viewport)
	 */
	public void registerCenterViewport(Viewport viewport) {
		if (viewport == null) throw new IllegalArgumentException("viewPort cannot be null");
		
		m_centerViewport = viewport;
	}

	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#maximizeView(org.flexdock.view.View)
	 */
	public void maximizeView(View view) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");
		
		if (view == m_territoralView) {
			//close all views except territoral view
		}

		DockingManager.undock(m_territoralView);
		m_centerViewport.dock(view);
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#unmaximizeView(org.flexdock.view.View)
	 */
	public void unmaximizeView(View view) {
		m_centerViewport.dock(m_territoralView);
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#registerViewDockingInfo(java.lang.String, org.flexdock.view.restore.ViewDockingInfo)
	 */
	public void registerViewDockingInfo(String viewId, ViewDockingInfo mainViewDockingInfo) {
		if (viewId == null) throw new IllegalArgumentException("viewId cannot be null");
		if (mainViewDockingInfo == null) throw new IllegalArgumentException("viewDockingInfo cannot be null");

		View view = (View) DockingManager.getRegisteredDockable(viewId);

		DockingHandler dockingHandler = new DockingHandler();
		m_registeredListeners.put(viewId, dockingHandler);
		view.addDockingListener(dockingHandler);
		m_preservingStrategy.setMainDockingInfo(view, mainViewDockingInfo);
	}

	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#unregisterViewDockingInfo(java.lang.String)
	 */
	public void unregisterViewDockingInfo(String viewId) {
		if (viewId == null) throw new IllegalArgumentException("viewId cannot be null");

		View view = (View) DockingManager.getRegisteredDockable(viewId);
		DockingListener dockingListener = (DockingListener) m_registeredListeners.get(viewId);
		view.removeDockingListener(dockingListener);
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#removeShowViewHandler(org.flexdock.view.restore.ShowViewHandler)
	 */
	public void removeShowViewHandler(ShowViewHandler showViewHandler) {
		if (showViewHandler == null) throw new NullPointerException("showViewHandler cannot be null");
		
		m_showViewHandlers.remove(showViewHandler);
	}

	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#setPreservingStrategy(org.flexdock.view.layout.PreservingStrategy)
	 */
	public void setPreservingStrategy(PreservingStrategy preservingStrategy) {
		m_preservingStrategy = preservingStrategy;
	}

	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#getPreservingStrategy()
	 */
	public PreservingStrategy getPreservingStrategy() {
		return m_preservingStrategy;
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#initializeDefaultShowViewHandlers()
	 */
	public void initializeDefaultShowViewHandlers() {
		addShowViewHandler(new ViewShownHandler());
		addShowViewHandler(new AccessoryShowViewHandler());
		addShowViewHandler(new MainShowViewHandler());
		addShowViewHandler(new LastResortShowViewHandler());
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#addShowViewHandler(org.flexdock.view.perspective.ShowViewHandler)
	 */
	public void addShowViewHandler(ShowViewHandler showViewHandler) {
		if (showViewHandler == null) throw new NullPointerException("showViewHandler cannot be null");
		
		m_showViewHandlers.add(showViewHandler);
	}
	
	/**
	 * @see org.flexdock.view.restore.IViewRestorationManager#removeAllShowViewHandlers()
	 */
	public void removeAllShowViewHandlers() {
		m_showViewHandlers.clear();
	}
	
	private class DockingHandler extends DockingListener.DockingAdapter {

		/**
		 * @see org.flexdock.docking.event.DockingListener#dockingComplete(org.flexdock.docking.event.DockingEvent)
		 */
		public void dockingComplete(DockingEvent dockingEvent) {
			View sourceView = (View) dockingEvent.getSource();
			DockingPort dockingPort = dockingEvent.getNewDockingPort();
			String region = dockingEvent.getRegion();

			m_preservingStrategy.preserve(sourceView, dockingPort, region);
		}

	}
	
}
