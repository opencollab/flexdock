package org.flexdock.view.perspective;

import org.flexdock.view.Viewport;

/**
 * @author Mateusz Szczap
 */
public interface IPerspectiveManager extends PerspectiveMonitor {

	void addPerspective(String perspectiveId, IPerspective perspective);

	void removePerspective(String perspectiveId);
	
	IPerspective getPerspective(String perspectiveId);

	IPerspective createPerspective(String perspectiveName, Viewport centralViewport);
	
	void setDefaultPerspective(String perspectiveId);

	IPerspective getDefaultPerspective();
	
	void applyPerspective(IPerspective perspective);

	void applyPerspective(String perspectiveId);
	
	void clearPerspective(IPerspective perspective);

}
