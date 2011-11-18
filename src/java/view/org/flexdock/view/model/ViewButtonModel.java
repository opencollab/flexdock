/*
 * Created on Jun 2, 2005
 */
package org.flexdock.view.model;

import javax.swing.JToggleButton;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.state.DockingState;
import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public class ViewButtonModel extends JToggleButton.ToggleButtonModel {
    private String viewId;

    public String getViewId() {
        return viewId;
    }
    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    protected View getView() {
        return View.getInstance(getViewId());
    }

    protected synchronized DockingState getDockingState() {
        return DockingManager.getDockingState(getViewId());
    }
}
