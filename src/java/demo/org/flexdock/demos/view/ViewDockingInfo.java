package org.flexdock.demos.view;

import org.flexdock.docking.DockingPort;
import org.flexdock.view.View;

/**
 * 
 * @author Mateusz Szczap
 */
public class ViewDockingInfo {
	
	private View m_view = null;
	
	private String m_region = DockingPort.UNKNOWN_REGION;
	
	private float m_ratio = -1.0f;

	public ViewDockingInfo(View view, String region, float ratio) {
		m_view = view;
		m_region = region;
		m_ratio = ratio;
	}
	
	public View getView() {
		return m_view;
	}
	
	public String getRegion() {
		return m_region;
	}
	
	public float getRatio() {
		return m_ratio;
	}
	
}
