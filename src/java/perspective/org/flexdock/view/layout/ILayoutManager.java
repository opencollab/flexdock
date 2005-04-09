/*
 * Created on 2005-04-09
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.layout;

import org.flexdock.view.View;

/**
 * @author mateusz
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ILayoutManager {

	void registerTerritoralView(View view);
	
	void registerView(View view, ViewDockingInfo mainViewDockingInfo);
	
	void unregisterView(View view);
	
	boolean showView(View view);

	boolean hideView(View view); 
	
	void setPreservingStrategy(PreservingStrategy preservingStrategy);

	PreservingStrategy getPreservingStrategy();

}
