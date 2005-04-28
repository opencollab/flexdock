/*
 * Created on Mar 1, 2005
 */
package org.flexdock.view;

import javax.swing.Action;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.flexdock.plaf.PlafManager;

/**
 * @author Christopher Butler
 */
public class Button extends JToggleButton {
	
	public Button(Action action) {
		setAction(action);
	}
	
	public void updateUI() {
		setUI(PlafManager.getUI(this));
	}
	
	public View getView() {
		return (View)SwingUtilities.getAncestorOfClass(View.class, this);
	}
	
	public String getActionName() {
		Action action = getAction();
		if(action==null)
			return null;
		return (String)action.getValue(Action.NAME);
	}

}
