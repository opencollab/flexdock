/*
 * Created on 2005-03-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.perspective;

import org.flexdock.view.View;

/**
 * @author mateusz
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IPerspective {

	String getPerspectiveName();
	
	void setTerritoralView(View view);
	
	View getTerritoralView();
	
	void addView(String viewId, View view);

	boolean removeView(String viewId);
	
	View getView(String viewId);
	
	String[] getViewIds();
	
	Perspective.ViewDockingInfo[] getDockingInfoChain();
	
	void dock(String view1Id, String view2Id, String region, float ratio);

	void dock(String view1Id, String view2Id);

	void undock(int index);

}
