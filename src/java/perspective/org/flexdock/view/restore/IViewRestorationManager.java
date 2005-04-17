/*
 * Created on 2005-04-09
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.restore;

import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * 
 * @author Mateusz Szczap
 */
public interface IViewRestorationManager {

	void registerCenterViewport(Viewport viewport);
	
	//TODO should we get rid of it?
	void registerTerritoralView(View view);
	
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
	
	void setPreservingStrategy(PreservingStrategy preservingStrategy);

	PreservingStrategy getPreservingStrategy();

}
