package org.flexdock.view.restore;

import java.awt.Dimension;
import java.awt.Point;

import org.flexdock.docking.DockingPort;
import org.flexdock.view.View;

/**
 * 
 * @author Mateusz Szczap
 */
public class ViewDockingInfo {
	
	private View m_view = null;
	
	private String m_region = DockingPort.UNKNOWN_REGION;
	
	private float m_ratio = -1.0F;
	
	private boolean m_floating = false;
	
	private Point m_floatingLocation = null;
	
	private Dimension m_floatingWindowDimension = null;

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
	
	public boolean isFloating() {
		return m_floating;
	}

	public void setFloating(boolean isFloating) {
		m_floating = isFloating;
	}
	
	public void setFloatingLocation(Point location) {
		m_floatingLocation = location;
	}
	
	public void setFloatingWindowDimension(Dimension windowDimension) {
		m_floatingWindowDimension = windowDimension;
	}

	public Point getFloatingLocation() {
		return m_floatingLocation;
	}

	public Dimension getFloatingWindowDimension() {
		return m_floatingWindowDimension;
	}
	
}
