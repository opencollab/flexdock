/*
 * Created on 2005-04-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.restore;

/**
 * 
 * @author Mateusz Szczap
 */
public interface ViewStateMonitor {

	void addViewStateListener(ViewStateListener viewStateListener);

	void removeViewStateListener(ViewStateListener viewStateListener);

	ViewStateListener[] getViewStateListeners();

}
