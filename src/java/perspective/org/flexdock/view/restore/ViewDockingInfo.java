package org.flexdock.view.restore;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import org.flexdock.dockbar.DockbarManager;
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
	
	private boolean m_isMinimized = false;
	
	//if the view is minimized we store the dockbar edge to which it is minimized
	private int m_dockbarEdge = DockbarManager.UNSPECIFIED_EDGE;

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
		//the view that is floating can't be minimized at the same time.
		if (isFloating) {
			setMinimized(false);
		}
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
	
	public Rectangle getFloatingBounds() {
		Rectangle rect = new Rectangle();
		if(m_floatingLocation!=null)
			rect.setLocation(m_floatingLocation);
		if(m_floatingWindowDimension!=null)
			rect.setSize(m_floatingWindowDimension);
		return rect;
	}
	
	public void setMinimized(boolean isMinimized) {
		m_isMinimized = isMinimized;
		if (isMinimized) {
			//the view that is minimized can be floating at the same time
			setFloating(false);
		}
	}
	
	public boolean isMinimized() {
		return m_isMinimized;
	}
	
	public void setDockbarEdge(int dockBarEdge) {
		m_dockbarEdge = dockBarEdge;
	}
	
	public int getDockbarEdge() {
		return m_dockbarEdge;
	}
	
	public static ViewDockingInfo createRelativeDockingInfo(View sourceView, String region, float ratio) {
		ViewDockingInfo viewDockingInfo = new ViewDockingInfo();
		viewDockingInfo.m_view = sourceView;
		viewDockingInfo.m_region = region;
		viewDockingInfo.m_ratio = ratio;
		return viewDockingInfo;
	}

	public static ViewDockingInfo createMinimizedDockingInfo(View sourceView, String region, float ratio, int dockbarEdge) {
		ViewDockingInfo viewDockingInfo = new ViewDockingInfo();
		viewDockingInfo.m_view = sourceView;
		viewDockingInfo.m_region = region;
		viewDockingInfo.m_ratio = ratio;
		viewDockingInfo.m_isMinimized = true;
		viewDockingInfo.m_dockbarEdge = dockbarEdge;

		return viewDockingInfo;
	}

	public static ViewDockingInfo createMinimizedDockingInfo(View sourceView, int dockbarEdge) {
	    ViewDockingInfo viewDockingInfo = new ViewDockingInfo();
		viewDockingInfo.m_view = sourceView;
		viewDockingInfo.m_isMinimized = true;
		viewDockingInfo.m_dockbarEdge = dockbarEdge;

		return viewDockingInfo;
	}

	public static ViewDockingInfo createFloatingDockingInfo(View sourceView, Point location, Dimension dimension) {
		ViewDockingInfo viewDockingInfo = new ViewDockingInfo();
		viewDockingInfo.m_view = sourceView;
		viewDockingInfo.m_floating = true;
		viewDockingInfo.m_floatingLocation = location;
		viewDockingInfo.m_floatingWindowDimension = dimension;
		
		return viewDockingInfo;
	}

}
