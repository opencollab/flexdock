/*
 * Created on 2005-04-09
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.restore;

import org.flexdock.docking.DockingPort;
import org.flexdock.view.View;

/**
 * @author Mateusz Szczap
 */
public interface PreservingStrategy {

	boolean preserve(View view, DockingPort dockingPort, String region);

	void setMainDockingInfo(View view, ViewDockingInfo dockingInfo);
	
	ViewDockingInfo getMainDockingInfo(View view);
	
	ViewDockingInfo[] getAccessoryDockingInfos(View view);

}
