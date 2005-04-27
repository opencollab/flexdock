/*
 * Created on Apr 26, 2005
 */
package org.flexdock.view.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public class ViewAction extends AbstractAction {
	protected View m_View;
	
	public ViewAction(View view) {
		m_View = view;
	}
	
	public void actionPerformed(ActionEvent e) {
		// noop
	}
}
