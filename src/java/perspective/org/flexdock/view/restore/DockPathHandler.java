/*
 * Created on 2005-05-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.restore;

import java.util.Map;

import org.flexdock.dockbar.restore.DockingPath;
import org.flexdock.view.View;

/**
 * 
 * @author Mateusz Szczap
 */
public class DockPathHandler implements ShowViewHandler {

    /**
     * @see org.flexdock.view.restore.ShowViewHandler#showView(org.flexdock.view.View, java.util.Map)
     */
    public boolean showView(View view, Map context) {
        DockingPath dockingPath = DockingPath.getRestorePath(view);
        if (dockingPath == null) {
            return false;
        }
        return dockingPath.restore();
    }

}
