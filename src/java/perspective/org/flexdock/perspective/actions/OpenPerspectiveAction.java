/*
 * Created on Jun 8, 2005
 */
package org.flexdock.perspective.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.PerspectiveManager;

/**
 * @author Christopher Butler
 */
public class OpenPerspectiveAction extends AbstractAction {
    private String m_perspective;

    public OpenPerspectiveAction(String perspectiveId) {
        if (perspectiveId == null) throw new IllegalArgumentException("perspectiveId cannot be null");
        m_perspective = perspectiveId;

        Perspective perspective = getPerspective();
        if(perspective!=null)
            putValue(Action.NAME, perspective.getName());
    }

    public Perspective getPerspective() {
        return PerspectiveManager.getInstance().getPerspective(m_perspective);
    }

    public void actionPerformed(ActionEvent e) {
        if (m_perspective != null) {
            PerspectiveManager.getInstance().loadPerspective(m_perspective);
        }
    }
}
