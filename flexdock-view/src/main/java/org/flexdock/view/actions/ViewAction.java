/*
 * Created on Apr 26, 2005
 */
package org.flexdock.view.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonModel;
import javax.swing.SwingUtilities;

import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public abstract class ViewAction extends AbstractAction {
    /**
     * @deprecated unused
     */
    public static final ViewAction EMPTY_ACTION = createDefault();

    protected String viewId;

    protected View getView(ActionEvent evt) {
        View view = viewId == null ? null : View.getInstance(viewId);
        if (view == null) {
            Component c = (Component) evt.getSource();
            view = (View) SwingUtilities.getAncestorOfClass(View.class, c);
        }
        return view;
    }

    public void actionPerformed(ActionEvent e) {
        View view = getView(e);
        actionPerformed(view, e);
    }

    public abstract void actionPerformed(View view, ActionEvent evt);

    private static ViewAction createDefault() {
        return new ViewAction() {
            public void actionPerformed(View view, ActionEvent evt) {

            }
        };
    }

    public ButtonModel createButtonModel() {
        return null;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

}
