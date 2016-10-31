/*
 * Created on Jun 8, 2005
 */
package org.flexdock.view.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.flexdock.docking.DockingManager;
import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public class DefaultDisplayAction extends ViewAction {

    public DefaultDisplayAction() {

    }

    public DefaultDisplayAction(String viewId) {
        setViewId(viewId);
        View view = View.getInstance(viewId);
        if(view!=null)
            putValue(Action.NAME, view.getTitle());
    }

    public void actionPerformed(View view, ActionEvent evt) {
        DockingManager.display(view);
    }

}
