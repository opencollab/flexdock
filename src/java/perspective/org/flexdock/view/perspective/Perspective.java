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
import java.util.List;

import org.flexdock.docking.DockingPort;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * @author mateusz
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Perspective implements IPerspective {
	
	private String m_perspectiveName = null;
	
	private HashMap m_views = new HashMap();
	private List m_dockingInfosList = new ArrayList();
	
	private View m_centerView = null;
	private Viewport m_centerViewport = null;
	
	public Perspective(String perspectiveName) {
		if (perspectiveName == null) throw new NullPointerException("perspectiveName cannot be null");
		m_perspectiveName = perspectiveName;
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#getPerspectiveName()
	 */
	public String getPerspectiveName() {
		return m_perspectiveName;
	}
	
	public void setTerritoralView(View centerView) {
		m_centerView = centerView;
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#setMainViewport(org.flexdock.view.Viewport)
	 */
	public void setMainViewport(Viewport viewport) {
		m_centerViewport = viewport;
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#getMainViewport()
	 */
	public Viewport getMainViewport() {
		return m_centerViewport;
	}

	public View getTerritoralView() {
		return m_centerView;
	}

	/**
	 * @see org.flexdock.view.perspective.IPerspective#addView(java.lang.String, org.flexdock.view.View)
	 */
	public void addView(String viewId, View view) {
		if (viewId == null) throw new IllegalArgumentException("viewId cannot be null");
		if (view == null) throw new IllegalArgumentException("view cannot be null");
		m_views.put(viewId, view);
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
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#dock(org.flexdock.view.View, org.flexdock.view.View, java.lang.String, float)
	 */
	public void dock(View sourceView, View targetView, String region, float ratio) {
		ViewDockingInfo viewDockingInfo = new ViewDockingInfo();
		viewDockingInfo.m_sourceView = sourceView;
		viewDockingInfo.m_targetView = targetView;
		viewDockingInfo.m_relativeRegion = region;
		viewDockingInfo.m_ratio = ratio;
		
		m_dockingInfosList.add(viewDockingInfo);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#dock(java.lang.String, org.flexdock.view.View, java.lang.String, float)
	 */
	public void dock(String sourceViewId, String targetViewId, String region, float ratio) {
		View sourceView = getView(sourceViewId);
		View targetView = getView(targetViewId);

		if (sourceView == null) throw new RuntimeException("Unable to find sourceView: "+sourceViewId);
		if (targetViewId == null) throw new RuntimeException("Unable to find targetView: "+targetViewId);

		dock(sourceView, targetView, region, ratio);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#dock(org.flexdock.view.View, org.flexdock.view.View)
	 */
	public void dock(View sourceView, View targetView) {
		dock(sourceView, targetView, DockingPort.CENTER_REGION, -1.0f);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#undock(org.flexdock.view.View, org.flexdock.view.View)
	 */
	public void undock(View sourceView, View targetView) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#dock(java.lang.String, java.lang.String)
	 */
	public void dock(String view1Id, String view2Id) {
		dock(view1Id, view2Id, DockingPort.CENTER_REGION, -1.0f);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspective#undock(java.lang.String, java.lang.String)
	 */
	public void undock(int index) {
		//m_dockingInfosList.remove(index);
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
	 * @see org.flexdock.view.perspective.IPerspective#getDockingInfo(java.lang.String)
	 */
	public ViewDockingInfo getDockingInfo(int index) {
		return (ViewDockingInfo) m_dockingInfosList.get(index);
	}
	
	public static class ViewDockingInfo {
		
		private View m_sourceView = null;
		private View m_targetView = null;
		
		private String m_relativeRegion = DockingPort.UNKNOWN_REGION;
		
		private float m_ratio;
		
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
		
	}
	
}
