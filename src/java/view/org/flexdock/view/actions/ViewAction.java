/*
 * Created on Apr 26, 2005
 */
package org.flexdock.view.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public abstract class ViewAction extends AbstractAction {
	public static final ViewAction EMPTY_ACTION = createDefault();
	
	public abstract void actionPerformed(ActionEvent e);
	
	private static ViewAction createDefault() {
		return new ViewAction() {
			public void actionPerformed(ActionEvent ae) {
				
			}
		};
	}
	
	protected View getView(ActionEvent evt) {
		Component c = (Component)evt.getSource();
		return (View)SwingUtilities.getAncestorOfClass(View.class, c);
	}
}
