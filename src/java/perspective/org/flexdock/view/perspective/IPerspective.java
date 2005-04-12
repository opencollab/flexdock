package org.flexdock.view.perspective;

import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * @author Mateusz Szczap
 */
public interface IPerspective {

	String getPerspectiveName();
	
	void setTerritoralView(View view);
	
	View getTerritoralView();
	
	void setMainViewport(Viewport viewport);
	
	Viewport getMainViewport();
	
	void addView(View view);

	boolean removeView(String viewId);
	
	View getView(String viewId);
	
	View[] getViews();
	
	Perspective.ViewDockingInfo[] getDockingInfoChain();

	void dock(String view1Id, String view2Id);
	
	void dock(String sourceViewId, String targetViewId, String region, float ratio);
	
	void dock(View sourceView, View targetView, String region, float ratio);

	void dock(View sourceView, View targetView);

	void undock(View sourceView, View targetView);

}
