/*
 * Created on 2005-03-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.perspective;

import java.util.EventListener;

/**
 * @author mateusz
 *
 * TODO To change the templaste for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface PerspectiveListener extends EventListener {

	void onPerspectiveAdded(IPerspective perspective);

	void onPerspectiveRemoved(IPerspective perspective);

	void onPerspectiveChanged(IPerspective oldPerspective, IPerspective newPerspective);

	void onPerspectiveCleared(IPerspective perspective);
	
}
