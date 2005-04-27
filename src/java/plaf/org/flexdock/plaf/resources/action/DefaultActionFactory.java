/*
 * Created on Apr 26, 2005
 */
package org.flexdock.plaf.resources.action;

import javax.swing.AbstractAction;

/**
 * @author Christopher Butler
 */
public class DefaultActionFactory extends AbstractActionFactory {
	public static final DefaultActionFactory SINGLETON = new DefaultActionFactory();
	
	public AbstractAction createAction(Object args) {
		return DefaultAction.SINGLETON;
	}
}
