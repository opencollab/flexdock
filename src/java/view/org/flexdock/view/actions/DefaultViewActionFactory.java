/*
 * Created on Apr 26, 2005
 */
package org.flexdock.view.actions;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.flexdock.plaf.resources.action.AbstractActionFactory;

/**
 * @author Christopher Butler
 */
public class DefaultViewActionFactory extends AbstractActionFactory {
	public static final String PIN_ACTION = "pin";
	public static final String CLOSE_ACTION = "close";

	public AbstractAction createAction(Object arg) {
		String actionType = arg == null ? null : arg.toString();
		AbstractAction action = null;

		if (PIN_ACTION.equals(actionType))
			action = new DefaultPinAction();
		else if (CLOSE_ACTION.equals(actionType))
			action = new DefaultCloseAction();
		else
			action = ViewAction.EMPTY_ACTION;

		if(actionType!=null)
			action.putValue(Action.NAME, actionType);

		return action;
	}
}