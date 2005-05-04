package org.flexdock.view.perspective;

import org.flexdock.docking.DockingPort;

/**
 * @author Mateusz Szczap
 */
public interface IPerspectiveManager extends PerspectiveMonitor {

	void addPerspective(IPerspective perspective);

	void addPerspective(IPerspective perspective, boolean isDefaultPerspective);

	void removePerspective(String perspectiveId);
	
	IPerspective getPerspective(String perspectiveId);

	void setDefaultPerspective(String perspectiveId);

	IPerspective getCurrentPerspective();
	
	IPerspective getDefaultPerspective();
	
	void applyPerspective(IPerspective perspective);

	void resetPerspective(IPerspective perspective);
	
	void applyPerspective(String perspectiveId);
	
	void applyDefaultPerspective();

	void managePerspective(IPerspective perspective);
	
	void unmanagePerspective(IPerspective perspective);
	
	void clearPerspective(IPerspective perspective);

	IPerspective createPerspective(String perspectiveName, DockingPort centralDockingPort);

}
