package org.flexdock.docking.state;

import java.awt.Point;
import java.io.Serializable;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.util.DockingConstants;

/**
 * 
 * @author Mateusz Szczap
 */
public class DockingState implements Cloneable, Serializable, DockingConstants {
	
	private String m_dockableId;
	
	private String m_relativeParentId;
	
	private String m_region = UNKNOWN_REGION;
	
	private float m_splitRatio = UNINITIALIZED_RATIO;
	
	private String m_floatingGroup;

	//if the view is minimized we store the dockbar edge to which it is minimized
	private int m_dockbarEdge = MinimizationManager.UNSPECIFIED_LAYOUT_EDGE;
	
	private DockingPath m_dockingPath;
	
	private int m_layoutWeight;
	
	private boolean m_displayed;
	
	private int centerX = DockingConstants.UNINITIALIZED;
	
	private int centerY = DockingConstants.UNINITIALIZED;

	public DockingState(String dockableId) {
		this.m_dockableId = dockableId;
	}
	
	public DockingState(String dockableId, int weight) {
		this.m_dockableId = dockableId;
		this.m_layoutWeight = weight;
	}

	public Dockable getDockable() {
		return DockingManager.getDockable(m_dockableId);
	}

	public String getDockableId() {
	    return m_dockableId;
	}
	
	public float getSplitRatio() {
		return m_splitRatio;
	}
	
	public void setSplitRatio(float ratio) {
		m_splitRatio = ratio;
	}
	
	public String getRegion() {
		return m_region;
	}
	
	public void setRegion(String m_region) {
		this.m_region = m_region;
	}
	
	public int getDockbarEdge() {
		return m_dockbarEdge;
	}
	
	public String getFloatingGroup() {
		return m_floatingGroup;
	}
	
	public boolean isFloating() {
		return m_floatingGroup!=null;
	}
	
	public boolean isMinimized() {
		return m_dockbarEdge!=MinimizationManager.UNSPECIFIED_LAYOUT_EDGE;
	}
	
	public boolean hasDockingPath() {
		return m_dockingPath!=null;
	}
	
	public DockingPath getPath() {
		return m_dockingPath;
	}
	
	public void setPath(DockingPath path) {
		m_dockingPath = path;
	}
	 
	
	public void setDockbarEdge(int edge) {
		m_dockbarEdge = edge;
		if(edge!=MinimizationManager.UNSPECIFIED_LAYOUT_EDGE) {
			m_floatingGroup = null;
		}
	}

	public void setFloatingGroup(String group) {
		m_floatingGroup = group;
		if(group!=null) {
			m_dockbarEdge = MinimizationManager.UNSPECIFIED_LAYOUT_EDGE;
		}
	}
	
	public Dockable getRelativeParent() {
		return DockingManager.getDockable(m_relativeParentId);
	}

	public String getRelativeParentId() {
	    return m_relativeParentId;
	}

	public void setRelativeParent(Dockable parent) {
		String parentId = parent==null? null: parent.getPersistentId();
		setRelativeParent(parentId);
	}

	public void setRelativeParent(String relativeParentId) {
		m_relativeParentId = relativeParentId;
	}

	public int getLayoutWeight() {
		return m_layoutWeight;
	}
	
	public void setLayoutWeight(int weight) {
		m_layoutWeight = weight;
	}
	
	public boolean isDisplayed() {
		return m_displayed;
	}
	
	public void setDisplayed(boolean display) {
		m_displayed = display;
	}
	
	public String toString() {
		return "DockingState[id=" + m_dockableId + 
				"; center=[" + centerX + "%," + centerY + "%]" +   
				"; parent=" + m_relativeParentId + 
				"; region=" + m_region + "; ratio=" + m_splitRatio + 
				"; float=" + m_floatingGroup + "; edge=" + m_dockbarEdge + 
				"; weight=" + m_layoutWeight + "; display=" + m_displayed + "; ]";
	}

	public int getCenterX() {
		return centerX;
	}
	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}
	public int getCenterY() {
		return centerY;
	}
	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}
	
	public void setCenter(Point p) {
		centerX = p==null? 0: p.x;
		centerY = p==null? 0: p.y;
	}
	
	public boolean hasCenterPoint() {
		return centerX!=DockingConstants.UNINITIALIZED && centerY!=DockingConstants.UNINITIALIZED;
	}
	
	public Object clone() {
		DockingState dockingStateClone = new DockingState(m_dockableId, m_layoutWeight);

		dockingStateClone.m_relativeParentId = m_relativeParentId;
		dockingStateClone.m_region = m_region;
		dockingStateClone.m_splitRatio = m_splitRatio;
		dockingStateClone.m_floatingGroup = m_floatingGroup;
		dockingStateClone.m_dockbarEdge = m_dockbarEdge;
		dockingStateClone.m_dockingPath = m_dockingPath==null? null: (DockingPath)m_dockingPath.clone();
		dockingStateClone.m_displayed = m_displayed;
		dockingStateClone.centerX = centerX;
		dockingStateClone.centerY = centerY;
		
		return dockingStateClone;
	}
	
}
