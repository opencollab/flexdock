/*
 * Created on Feb 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;

import org.flexdock.windowing.plaf.PlafManager;
import org.flexdock.windowing.plaf.theme.TitlebarUI;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Titlebar extends JComponent {
	private String preferredUi;
	private Icon titleIcon;
	private String titleText;
	private List actionList;
	private HashMap actionButtons;
	private Button[] buttonList;
	private boolean active;
	
	public Titlebar() {
		this(null, null);
	}
	
	public Titlebar(String title) {
		this(title, null);
	}
	
	public Titlebar(Action[] actions) {
		this(null, actions);
	}
	
	public Titlebar(String title, Action[] actions) {
		setText(title);
		setActions(actions);
	}
	
	public void setPreferredUI(String uiName) {
		boolean change = preferredUi==null? preferredUi!=uiName: !preferredUi.equals(uiName);
		preferredUi = uiName;
		if(change)
			updateUI();
	}
	
	public void setText(String text) {
		titleText = text==null? "": text;
	}
	
	protected void setActions(Action[] actions) {
		if(actions==null) {
			actions = new Action[0];
			actionList = new ArrayList(3);
			actionButtons = new HashMap(3);
		}
		
		removeAllActions();
		for(int i=0; i<actions.length; i++)
			addAction(actions[i]);
	}

	public synchronized void addAction(Action action) {
		if(action==null)
			return;
		
		String key = getKey(action);
		Icon icon = getIcon(action);
		if(key==null || icon==null) {
			throw new IllegalArgumentException("Cannot add an Action that has no Name or Icon associated with it.");
		}
	
		// don't add the same action more than once
		if(hasAction(key))
			return;

		// create the button
		Button button = createActionButton(action);
		button.setIcon(icon);
		// cache the button
		actionButtons.put(key, button);
		// add the button to the container
		add(button);

		// add the action to our list
		actionList.add(action);
		regenerateButtonList();
	}
	
	private void regenerateButtonList() {
		Button[] list = new Button[actionList.size()];
		for(int i=0; i<list.length; i++) {
			Action action = (Action)actionList.get(i);
			String key = getKey(action);
			list[i] = getButton(key);
		}
		
		synchronized(this) {
			buttonList = list;
		}
	}
	
	public Action getAction(String key) {
		if(key==null)
			return null;
		
		for(Iterator it=actionList.iterator(); it.hasNext();) {
			Action action = (Action)it.next();
			String actionName = (String)action.getValue(Action.NAME); 
			if(key.equals(actionName))
				return action;
		}
		return null;
	}
	
	public Action[] getActions() {
		return (Action[])actionList.toArray(new Action[0]);
	}
	
	protected Button getButton(String key) {
		return (Button)actionButtons.get(key);
	}
	
	protected boolean hasAction(String key) {
		return actionButtons.containsKey(key);
	}
	
	public Icon getIcon() {
		return titleIcon;
	}
	
	public String getText() {
		return titleText;
	}
	
	public void removeAction(Action action) {
		if(action==null)
			return;
		
		String key = getKey(action);
		removeAction(key);
	}
	
	public synchronized void removeAction(String key) {
		if(!hasAction(key))
			return;
		
		// Remove button associated with this action.
		Button button = getButton(key);
		remove(button);
		actionButtons.remove(key);
		// remove the action
		Action action = getAction(key);
		actionList.remove(action);
		regenerateButtonList();
	}
	
	protected synchronized void removeAllActions() {
		if(actionList==null)
			return;
		
		while(actionList.size()>0) {
			Action action = (Action)actionList.get(0);
			String key = getKey(action);
			// Remove button associated with this action.
			Button button = getButton(key);
			remove(button);
			actionButtons.remove(key);
			// remove the action
			actionList.remove(0);
		}
		regenerateButtonList();
	}
	
	protected String getKey(Action action) {
		Object obj = action==null? null: action.getValue(Action.NAME);
		return obj instanceof String? (String)obj: null;
	}
	
	protected Icon getIcon(Action action) {
		Object obj = action==null? null: action.getValue(Action.SMALL_ICON);
		return obj instanceof Icon? (Icon)obj: null;
	}
	
	public void setIcon(Icon icon) {
		titleIcon = icon;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean b) {
		if(b!=active) {
			active = b;
			repaint();
		}
	}
	
	public Button createActionButton(Action action) {
		Button button = new Button();
		button.setAction(action);
		if(ui instanceof TitlebarUI)
			((TitlebarUI)ui).configureAction(action);
		return button;
	}
	
	public void doLayout() {
		if(ui instanceof TitlebarUI)
			((TitlebarUI)ui).layoutButtons(this);
	}
	
    public void updateUI() {
    	setUI(PlafManager.getUI(this));
    }
}
