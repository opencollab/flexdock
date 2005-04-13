/*
 * Created on 2005-04-09
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.layout;

import java.util.HashMap;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.View;

/**
 * @author Mateusz Szczap
 */
public class LayoutManager implements ILayoutManager {

	private static LayoutManager SINGLETON = null;
	
	private HashMap m_registeredListeners = new HashMap();
	
	private PreservingStrategy m_preservingStrategy = new SimplePreservingStrategy();

	private View m_territoralView = null;
	
	public static LayoutManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new LayoutManager();
		}
		return SINGLETON;
	}

	/**
	 * @see org.flexdock.view.layout.ILayoutManager#registerTerritoralView(org.flexdock.view.View)
	 */
	public void registerTerritoralView(View territoralView) {
		if (territoralView == null) throw new IllegalArgumentException("territoralView cannot be null");
			
		m_territoralView = territoralView;
	}
	
	/**
	 * @see org.flexdock.view.layout.ILayoutManager#showView(org.flexdock.view.View)
	 */
	public boolean showView(View view) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");
		
		if (DockingManager.isDocked((Dockable) view)) {
			view.setActive(true);
			SwingUtility.focus(view);
			return true;
		}
		
		ViewDockingInfo dockingInfo = (ViewDockingInfo) m_preservingStrategy.getMainDockingInfo(view);
		ViewDockingInfo[] accessoryDockingInfos = m_preservingStrategy.getAccessoryDockingInfos(view);

		boolean docked = false;
		if (accessoryDockingInfos != null && accessoryDockingInfos.length > 0) {
			for (int i=0; i<accessoryDockingInfos.length; i++) {
				View sourceView = accessoryDockingInfos[i].getView();
				String region = accessoryDockingInfos[i].getRegion();
				float ratio = accessoryDockingInfos[i].getRatio();
				docked = sourceView.dock(view, region, ratio);
				if (docked) break;
			}
		}
		
		if (!docked) {
			View sourceView = dockingInfo.getView();
			String region = dockingInfo.getRegion();
			float ratio = dockingInfo.getRatio();
			View siblingView = (View) sourceView.getSibling(region);
			if (siblingView != null) {
				docked = siblingView.dock(view);
			} else {
				docked = sourceView.dock(view, region, ratio);
			}
			
		}

		if (!docked && m_territoralView != null) {
			docked = m_territoralView.dock(view, DockingPort.EAST_REGION, RegionChecker.DEFAULT_SIBLING_SIZE);
		}
		
		return docked;
	}
	
	/**
	 * @see org.flexdock.view.layout.ILayoutManager#hideView(org.flexdock.view.View)
	 */
	public boolean hideView(View view) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");
		//TODO in future we might do some other stuff here
		return DockingManager.undock(view);
	}

	/**
	 * @see org.flexdock.view.layout.ILayoutManager#registerView(org.flexdock.view.View)
	 */
	public void registerView(String viewId, ViewDockingInfo viewDockingInfo) {
		if (viewId == null) throw new IllegalArgumentException("viewId cannot be null");
		if (viewDockingInfo == null) throw new IllegalArgumentException("viewDockingInfo cannot be null");

		View view = (View) DockingManager.getRegisteredDockable(viewId);

		DockingHandler dockingHandler = new DockingHandler();
		//m_registeredViews.put(view.getPersistentId(), view);
		m_registeredListeners.put(viewId, dockingHandler);
		view.addDockingListener(dockingHandler);
		m_preservingStrategy.setMainDockingInfo(view, viewDockingInfo);
	}

	/**
	 * @see org.flexdock.view.layout.ILayoutManager#unregisterView(java.lang.String)
	 */
	public void unregisterView(String viewId) {
		if (viewId == null) throw new IllegalArgumentException("viewId cannot be null");

		View view = (View) DockingManager.getRegisteredDockable(viewId);
		DockingListener dockingListener = (DockingListener) m_registeredListeners.get(viewId);
		view.removeDockingListener(dockingListener);
	}
	
	/**
	 * @see org.flexdock.view.layout.ILayoutManager#setPreservingStrategy(org.flexdock.view.layout.PreservingStrategy)
	 */
	public void setPreservingStrategy(PreservingStrategy preservingStrategy) {
		m_preservingStrategy = preservingStrategy;
	}

	/**
	 * @see org.flexdock.view.layout.ILayoutManager#getPreservingStrategy()
	 */
	public PreservingStrategy getPreservingStrategy() {
		return m_preservingStrategy;
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
