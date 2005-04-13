/*
 * Created on 2005-04-09
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.restore;

import java.util.HashMap;
import java.util.Iterator;

import org.flexdock.docking.DockingPort;
import org.flexdock.docking.RegionChecker;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * @author Mateusz Szczap
 */
public class SimplePreservingStrategy implements PreservingStrategy {

	private HashMap m_mainDockingInfos = new HashMap();

	private HashMap m_accessoryDockingInfos = new HashMap();
	
	/**
	 * @see org.flexdock.demos.view.PreservingStrategy#preserve(org.flexdock.docking.DockingPort)
	 */
	public boolean preserve(View view, DockingPort dockingPort, String region) {
		Viewport viewPort = (Viewport) dockingPort;
		if (!viewPort.getViewset().isEmpty()) {
			for (Iterator it = viewPort.getViewset().iterator(); it.hasNext();) {
				View childView = (View) it.next();
				if (!childView.equals(view)) {
					Float ratioObject = view.getDockingProperties().getRegionInset(region);
					float ratio = -1.0f;
					if (ratioObject != null) {
						ratio = ratioObject.floatValue();
					} else {
						ratio = RegionChecker.DEFAULT_SIBLING_SIZE;
					}
					m_accessoryDockingInfos.put(view.getPersistentId(), new ViewDockingInfo(childView, region, ratio)); 
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @see org.flexdock.view.restore.PreservingStrategy#getMainDockingInfo(org.flexdock.view.View)
	 */
	public ViewDockingInfo getMainDockingInfo(View view) {
		return (ViewDockingInfo) m_mainDockingInfos.get(view.getPersistentId());
	}

	/**
	 * @see org.flexdock.view.restore.PreservingStrategy#setMainDockingInfo(org.flexdock.view.View, org.flexdock.view.layout.ViewDockingInfo)
	 */
	public void setMainDockingInfo(View view, ViewDockingInfo dockingInfo) {
		m_mainDockingInfos.put(view.getPersistentId(), dockingInfo);
	}
	
	/**
	 * @see org.flexdock.demos.view.PreservingStrategy#getAccessoryDockingInfos()
	 */
	public ViewDockingInfo[] getAccessoryDockingInfos(View view) {
		ViewDockingInfo dockingInfo = (ViewDockingInfo) m_accessoryDockingInfos.get(view.getPersistentId());
		if (dockingInfo == null) return new ViewDockingInfo[]{};
		return new ViewDockingInfo[] {dockingInfo};
	}

}
