/*
 * Created on 2005-03-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.perspective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.flexdock.docking.DockingPort;
import org.flexdock.view.View;

/**
 * @author Mateusz Szczap
 */
public class Perspective implements IPerspective {
	
	private String m_persistentId = null;
	private String m_perspectiveName = null;
	
	private HashMap m_views = new HashMap();
	private List m_dockingInfosList = new ArrayList();
	private List m_defaultDockingInfosList = new ArrayList();
	
	private String m_centerViewId = null;
	private DockingPort m_centerDockingPort = null;
	
	public Perspective(String persistentId, String perspectiveName) {
		if (persistentId == null) throw new NullPointerException("perspectiveName cannot be null");
		if (perspectiveName == null) throw new NullPointerException("perspectiveName cannot be null");
		m_persistentId = persistentId;
		m_perspectiveName = perspectiveName;
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#getPerspectiveName()
	 */
	public String getPerspectiveName() {
		return m_perspectiveName;
	}
	
	public String getPersistentId() {
		return m_persistentId;
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#setMainDockingPort(org.flexdock.docking.DockingPort)
	 */
	public void setMainDockingPort(DockingPort dockingPort) {
		m_centerDockingPort = dockingPort;
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#getMainDockingPort()
	 */
	public DockingPort getMainDockingPort() {
		return m_centerDockingPort;
	}

	/**
	 * @see org.flexdock.view.perspective.IPerspective#getCenterViewId()
	 */
	public String getCenterViewId() {
		return m_centerViewId;
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#addView(java.lang.String, org.flexdock.view.View)
	 */
	public void addView(View view) {
		if (view == null) throw new IllegalArgumentException("view cannot be null");
		m_views.put(view.getPersistentId(), view);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#removeView(java.lang.String)
	 */
	public boolean removeView(String viewId) {
		return (m_views.remove(viewId) != null);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#getView(java.lang.String)
	 */
	public View getView(String viewId) {
		return (View) m_views.get(viewId);
	}
	
	public List getDockingInfoList() {
		return m_dockingInfosList;
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#dockToCenterDockingPort(java.lang.String)
	 */
	public void dockToCenterDockingPort(String viewId, boolean isDefault) {
		if (viewId == null) throw new IllegalArgumentException("viewId cannot be null");
		m_centerViewId = viewId;
	}

	/**
	 * @see org.flexdock.view.perspective.IPerspective#dock(org.flexdock.view.View, org.flexdock.view.View, java.lang.String, float)
	 */
	public void dock(View sourceView, View targetView, String region, float ratio, boolean isDefault) {
		Perspective.ViewDockingInfo viewDockingInfo = new Perspective.ViewDockingInfo(sourceView, targetView, region, ratio);

		if (isDefault) {
			m_defaultDockingInfosList.add(viewDockingInfo);
		}

		m_dockingInfosList.add(viewDockingInfo);

	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#dock(java.lang.String, org.flexdock.view.View, java.lang.String, float)
	 */
	public void dock(String sourceViewId, String targetViewId, String region, float ratio, boolean isDefault) {
		View sourceView = getView(sourceViewId);
		View targetView = getView(targetViewId);

		if (sourceView == null) throw new RuntimeException("Unable to find sourceView: "+sourceViewId);
		if (targetViewId == null) throw new RuntimeException("Unable to find targetView: "+targetViewId);

		dock(sourceView, targetView, region, ratio, isDefault);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#dock(org.flexdock.view.View, org.flexdock.view.View)
	 */
	public void dock(View sourceView, View targetView, boolean isDefault) {
		dock(sourceView, targetView, DockingPort.CENTER_REGION, -1.0f, isDefault);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#dock(java.lang.String, java.lang.String)
	 */
	public void dock(String view1Id, String view2Id, boolean isDefault) {
		dock(view1Id, view2Id, DockingPort.CENTER_REGION, -1.0f, isDefault);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#undock(org.flexdock.view.View, org.flexdock.view.View)
	 */
	public void undock(View sourceView, View targetView) {
		for (Iterator it = m_dockingInfosList.iterator(); it.hasNext();) {
			ViewDockingInfo dockingInfo = (ViewDockingInfo) it.next();
			View childSourceView = dockingInfo.getSourceView();
			View childTargetView = dockingInfo.getTargetView();
			if (childTargetView == targetView && childSourceView == sourceView) {
				m_dockingInfosList.remove(dockingInfo);
			}
		}
		
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#getViewIds()
	 */
	public View[] getViews() {
		return (View[]) Collections.unmodifiableCollection(m_views.values()).toArray(new View[]{});
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#getDockingInfoChain()
	 */
	public ViewDockingInfo[] getDockingInfoChain() {
		return (ViewDockingInfo[]) m_dockingInfosList.toArray(new ViewDockingInfo[]{});
	}

	/**
	 * @see org.flexdock.view.perspective.IPerspective#getDefaultDockingInfoChain()
	 */
	public ViewDockingInfo[] getDefaultDockingInfoChain() {
		return (ViewDockingInfo[]) m_defaultDockingInfosList.toArray(new ViewDockingInfo[]{});
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#getDockingInfo(java.lang.String)
	 */
	public ViewDockingInfo getDockingInfo(int index) {
		return (ViewDockingInfo) m_dockingInfosList.get(index);
	}
	
	public static class ViewDockingInfo {
		
		private View m_sourceView = null;
		private View m_targetView = null;
		
		private String m_relativeRegion = DockingPort.UNKNOWN_REGION;
		
		private float m_ratio = -1.0f;
		
		private boolean m_isFloatingView = false;
		
		public ViewDockingInfo(View sourceView, View targetView, String region, float ratio) {
			m_sourceView = sourceView;
			m_targetView = targetView;
			m_relativeRegion = region;
			m_ratio = ratio;
		}
		
		public View getSourceView() {
			return m_sourceView;
		}
		
		public View getTargetView() {
			return m_targetView;
		}
		
		public String getRelativeRegion() {
			return m_relativeRegion;
		}
		
		public float getRatio() {
			return m_ratio;
		}
		
		public void setSourceView(View sourceView) {
			m_sourceView = sourceView;
		}

		public void setTargetView(View targetView) {
			m_targetView = targetView;
		}
		
		public void setRelativeRegion(String region) {
			m_relativeRegion = region;
		}
		
		public void setRatio(float ratio) {
			m_ratio = ratio;
		}
		
	}
	
}
