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
	
	private HashMap m_registeredViews = new HashMap();
	private HashMap m_mainDockingInfos = new HashMap();
	
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
	public void registerTerritoralView(View view) {
		m_territoralView = view;
	}
	
	/**
	 * @see org.flexdock.view.layout.ILayoutManager#showView(org.flexdock.view.View)
	 */
	public boolean showView(View view) {
		if (DockingManager.isDocked((Dockable) view)) {
			view.setActive(true);
			SwingUtility.focus(view);
			return true;
		}
		
		ViewDockingInfo dockingInfo = (ViewDockingInfo) m_mainDockingInfos.get(view.getPersistentId());
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
			docked = sourceView.dock(view, region, ratio);
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
		//TODO in future we might do some other stuff here
		return DockingManager.undock(view);
	}

	/**
	 * @see org.flexdock.view.layout.ILayoutManager#registerView(org.flexdock.view.View)
	 */
	public void registerView(View view, ViewDockingInfo viewDockingInfo) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");
		if (viewDockingInfo == null) throw new IllegalArgumentException("viewDockingInfo cannot be null");

		m_registeredViews.put(view.getPersistentId(), view);
		m_mainDockingInfos.put(view.getPersistentId(), viewDockingInfo);
	}

	/**
	 * @see org.flexdock.view.layout.ILayoutManager#unregisterView(org.flexdock.view.View)
	 */
	public void unregisterView(View view) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");
		
		m_registeredViews.remove(view.getPersistentId());
		m_mainDockingInfos.remove(view.getPersistentId());
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
