package org.flexdock.view.restore;

import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * 
 * @author Mateusz Szczap
 */
public interface IViewManager extends ViewStateMonitor {

	void registerCenterViewport(Viewport viewport);
	
	//TODO should we get rid of it?
	void registerTerritoralView(View view);

	void registerView(View view);

	void unregisterView(String viewId);
	
	View getRegisteredView(String viewId);
	
	void registerViewDockingInfo(String viewId, ViewDockingInfo mainViewDockingInfo);

	void unregisterViewDockingInfo(String viewId);

	void addShowViewHandler(ShowViewHandler showViewHandler);
	
	void maximizeView(View view);

	void unmaximizeView(View view);
	
	void removeShowViewHandler(ShowViewHandler showViewHandler);
	
	void removeAllShowViewHandlers();
	
	void initializeDefaultShowViewHandlers();
	
	boolean showView(View view);

	boolean hideView(View view); 
	
}
