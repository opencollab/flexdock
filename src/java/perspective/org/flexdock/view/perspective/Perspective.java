/*
 * Created on 2005-03-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.perspective;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.flexdock.docking.DockingPort;
import org.flexdock.view.View;

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
	public void dock(String view1Id, String view2Id, String region, float ratio) {
		ViewDockingInfo viewDockingInfo = new ViewDockingInfo();
		viewDockingInfo.m_view1Id = view1Id;
		viewDockingInfo.m_view2Id = view2Id;
		viewDockingInfo.m_relativeRegion = region;
		viewDockingInfo.m_ratio = ratio;
		
		m_dockingInfosList.add(viewDockingInfo);
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
	public String[] getViewIds() {
		String[] viewIds = new String[m_views.size()];
		return (String[]) m_views.keySet().toArray(viewIds);
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
		
		private String m_view1Id = null;
		private String m_view2Id = null;
		
		private String m_relativeRegion = DockingPort.UNKNOWN_REGION;
		
		private float m_ratio;
		
		public String getView1Id() {
			return m_view1Id;
		}
		
		public String getView2Id() {
			return m_view2Id;
		}
		
		public String getRelativeRegion() {
			return m_relativeRegion;
		}
		
		public float getRatio() {
			return m_ratio;
		}
		
	}
	
}
