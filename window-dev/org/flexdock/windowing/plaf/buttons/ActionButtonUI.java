/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.buttons;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.FocusEvent;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;

import org.flexdock.windowing.Titlebar;
import org.flexdock.windowing.plaf.ActionButtonConstants;

/**
 * @author Christopher Butler
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ActionButtonUI extends BasicButtonUI implements ActionButtonConstants {
	protected BorderResource borderResource;
	
	public void setBorderResource(BorderResource resource) {
		borderResource = resource;
	}
	
	public BorderResource getBorderResource() {
		return borderResource;
	}	
	
	public void paint(Graphics g, JComponent c) {
		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();
		
		boolean active = isParentActive(b);
		boolean pressed = isPressed(b);
		boolean hover = pressed || model.isRollover();

		paintBackground(g, b, active, hover, pressed);
		paintIcon(g, b);
		paintBorder(g, b);
	}
	
	protected void paintBackground(Graphics g, AbstractButton b, boolean active, boolean hover, boolean pressed) {
		// do nothing
	}

	protected void paintBorder(Graphics g, AbstractButton b) {
		Border border = getBorder(b);
		if(border!=null)
			border.paintBorder(b, g, 0, 0, b.getWidth(), b.getHeight());
	}
	
	protected Border getBorder(AbstractButton button) {
		if(isPressed(button))
			return getPressedBorder(button);
		
		boolean active = isParentActive(button);
		if(button.getModel().isRollover())
			return getHoverBorder(button, active);
		
		return getDefaultBorder(button, active);
	}

	protected Border getPressedBorder(AbstractButton button) {
		Border border = getActionBorder(true, true, true);
		if(border==null)
			border = getHoverBorder(button, true);
		return border;
	}
	
	protected Border getHoverBorder(AbstractButton button, boolean active) {
		Border border = getActionBorder(false, active, true);
		if(border==null)
			border = getDefaultBorder(button, active);
		return border;
	}
	
	protected Border getDefaultBorder(AbstractButton button, boolean active) {
		return getActionBorder(false, active, false);
	}

	


	protected void paintIcon(Graphics g, AbstractButton b) {
		Icon icon = getIcon(b);
		if(icon==null)
			return;
		
		int h = icon.getIconHeight();
		int w = icon.getIconWidth();
		int x = b.getWidth()/2 - w/2;
		int y = b.getHeight()/2 - h/2;
		
		icon.paintIcon(b, g, x, y);
	}
	
	protected Icon getIcon(AbstractButton button) {
		if(isPressed(button))
			return getPressedIcon(button);
		
		boolean active = isParentActive(button);
		if(button.getModel().isRollover())
			return getHoverIcon(button, active);
		
		return getDefaultIcon(button, active);
	}
	
	protected Icon getPressedIcon(AbstractButton button) {
		Icon icon = button.getPressedIcon();
		if(icon==null)
			icon = getActionIcon(button, ICON_PRESSED);
		if(icon==null)
			icon = getHoverIcon(button, true);
		return icon;
	}
	
	protected Icon getHoverIcon(AbstractButton button, boolean active) {
		Icon icon = button.getRolloverIcon();
		if(icon==null && active)
			icon = getActionIcon(button, ICON_HOVER_ACTIVE);
		if(icon==null)
			icon = getActionIcon(button, ICON_HOVER_INACTIVE);
		if(icon==null)
			icon = getDefaultIcon(button, active);
		return icon;
	}
	
	protected Icon getDefaultIcon(AbstractButton button, boolean active) {
		Icon icon = button.getIcon();
		if(icon==null && active)
			icon = getActionIcon(button, ICON_DEFAULT_ACTIVE);
		if(icon==null)
			icon = getActionIcon(button, ICON_DEFAULT_INACTIVE);
		return icon;
	}
	
	protected Icon getActionIcon(AbstractButton button, String key) {
		Action action = button.getAction();
		return action==null? null: (Icon)action.getValue(key); 
	}
	
	protected Border getActionBorder(boolean pressed, boolean active, boolean hover) {
		if(pressed)
			return borderResource.getPressedBorder();
		
		if(active) {
			if(hover)
				return borderResource.getActiveHoverBorder();
			return borderResource.getActiveDefaultBorder();
		}
		
		if(hover)
			return borderResource.getInactiveHoverBorder();
		return borderResource.getInactiveDefaultBorder();
	}
	
	protected boolean isPressed(AbstractButton button) {
		ButtonModel model = button.getModel();
		return model.isArmed() && model.isPressed();
	}
	
	protected boolean isParentActive(AbstractButton button) {
		Container parent = button.getParent();
		return parent instanceof Titlebar? ((Titlebar)parent).isFocused(): false;
	}
	
	
	
	
	
	
	public void installUI(JComponent c) {
		super.installUI(c);
		AbstractButton button = (AbstractButton)c;
		button.setRolloverEnabled(true);
		button.setRequestFocusEnabled(false);
	}

    protected void installKeyboardActions(AbstractButton b){
    	// do nothing
    }
    
    protected BasicButtonListener createButtonListener(AbstractButton b) {
        return new ButtonListener(b);
    }
    
    protected class ButtonListener extends BasicButtonListener {
    	protected ButtonListener(AbstractButton b) {
    		super(b);
    	}
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
		}
    }


}