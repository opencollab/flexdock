package org.flexdock.view.perspective;

import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * @author Mateusz Szczap
 */
public interface IPerspective {

	String getPerspectiveName();

	String getPersistentId();
	
	String getCenterViewId();
	
	void setMainViewport(Viewport viewport);
	
	Viewport getMainViewport();
	
	void addView(View view);

	boolean removeView(String viewId);
	
	View getView(String viewId);
	
	View[] getViews();
	
	Perspective.ViewDockingInfo[] getDockingInfoChain();

	//can be used for reset perspective
	Perspective.ViewDockingInfo[] getDefaultDockingInfoChain();

	void dockToCenterViewport(String viewId, boolean isDefault);

	void dock(String view1Id, String view2Id, boolean isDefault);
	
	void dock(String sourceViewId, String targetViewId, String region, float ratio, boolean isDefault);
	
	void dock(View sourceView, View targetView, String region, float ratio, boolean isDefault);

	void dock(View sourceView, View targetView, boolean isDefault);

	void undock(View sourceView, View targetView);
	
}
