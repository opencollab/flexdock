/*
 * Created on 2005-03-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.perspective;

/**
 * @author mateusz
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface PerspectiveMonitor {
	
	void addPerspectiveListener(PerspectiveListener perspectiveListener);

	void removePerspectiveListener(PerspectiveListener perspectiveListener);
	
	IPerspective[] getPerspectiveListners();
	
}
