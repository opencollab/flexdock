/*
 * Created on 2005-03-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.perspective;

import java.awt.Container;


/**
 * @author mateusz
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IPerspectiveManager extends PerspectiveMonitor {

	void addPerspective(String perspectiveId, IPerspective perspective);

	void removePerspective(String perspectiveId);
	
	IPerspective getPerspective(String perspectiveId);
	
	void setDefaultPerspective(String perspectiveId);

	IPerspective getDefaultPerspective();
	
	void applyPerspective(Container container, IPerspective perspective);

	void applyPerspective(Container container, String perspectiveId);

}
