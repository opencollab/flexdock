/*
 * Created on Apr 26, 2005
 */
package org.flexdock.plaf.resources.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * @author Christopher Butler
 */
public class DefaultAction extends AbstractAction {
    public static final DefaultAction SINGLETON = new DefaultAction();

    public void actionPerformed(ActionEvent e) {
        // noop
    }
}
