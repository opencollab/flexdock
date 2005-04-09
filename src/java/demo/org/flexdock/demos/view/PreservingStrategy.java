/*
 * Created on 2005-04-09
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.demos.view;

import org.flexdock.docking.DockingPort;
import org.flexdock.view.View;

/**
 * @author mateusz
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface PreservingStrategy {

	boolean preserve(View view, DockingPort dockingPort, String region);
	
	ViewDockingInfo[] getAccessoryDockingInfos();

}
