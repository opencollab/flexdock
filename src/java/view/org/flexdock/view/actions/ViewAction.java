/*
 * Created on Apr 26, 2005
 */
package org.flexdock.view.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.flexdock.docking.state.DockingState;
import org.flexdock.view.Button;
import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public abstract class ViewAction extends AbstractAction {
	public static final ViewAction EMPTY_ACTION = createDefault();
	
	protected View getView(ActionEvent evt) {
		Component c = (Component)evt.getSource();
		return (View)SwingUtilities.getAncestorOfClass(View.class, c);
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
	
	public void updateState(View view, DockingState info, Button button) {
	}
}
