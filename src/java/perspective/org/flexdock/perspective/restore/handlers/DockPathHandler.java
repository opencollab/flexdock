/*
 * Created on 2005-05-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.restore.handlers;

import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.state.DockingPath;
import org.flexdock.docking.state.DockingState;

/**
 * 
 * @author Mateusz Szczap
 */
public class DockPathHandler implements RestorationHandler {

	public boolean restore(Dockable dockable, DockingState info, Map context) {
        DockingPath dockingPath = info.getPath();
        if (dockingPath == null) {
            return false;
        }
        return dockingPath.restore(dockable);
    }

}
