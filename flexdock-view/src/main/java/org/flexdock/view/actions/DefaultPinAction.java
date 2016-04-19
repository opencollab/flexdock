/*
 * Created on Apr 26, 2005
 */
package org.flexdock.view.actions;

import java.awt.event.ActionEvent;

import javax.swing.ButtonModel;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.state.DockingState;
import org.flexdock.view.Button;
import org.flexdock.view.View;
import org.flexdock.view.model.ViewButtonModel;

/**
 * @author Christopher Butler
 * @author Bobby Rosenberger
 */
public class DefaultPinAction extends ViewAction {

    public DefaultPinAction() {

    }

    public void actionPerformed(View view, ActionEvent evt) {
        boolean minimize = view.isMinimized()? false: true;
        DockingManager.setMinimized(view, minimize);
    }

    public void updateState(View view, DockingState info, Button button) {
        button.getModel().setSelected(info.isMinimized());
    }

    public ButtonModel createButtonModel() {
        return new PinButtonModel();
    }

    private static class PinButtonModel extends ViewButtonModel {
        public boolean isSelected() {
            DockingState info = getDockingState();
            if(info==null)
                return super.isSelected();
            return info.isMinimized();
        }
    }

}
