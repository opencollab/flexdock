/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.titlebar.buttons;

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
import org.flexdock.windowing.plaf.icons.IconResource;

/**
 * @author Christopher Butler
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ButtonUI extends BasicButtonUI {
	public static final String ICON_RESOURCE = "flexdock.button.icon.resource";
	
	protected String uiName;
	protected Border borderDefault;
	protected Border borderDefaultHover;
	protected Border borderActive;
	protected Border borderActiveHover;
	protected Border borderPressed;
	
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
		Border border = borderPressed;
		if(border==null)
			border = getHoverBorder(button, true);
		return border;
	}
	
	protected Border getHoverBorder(AbstractButton button, boolean active) {
		Border border = active? borderActiveHover: borderDefaultHover;
		if(border==null)
			border = getDefaultBorder(button, active);
		return border;
	}
	
	protected Border getDefaultBorder(AbstractButton button, boolean active) {
		return active? borderActive: borderDefault;
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
			icon = getActionIcon(button, true, true, true);
		if(icon==null)
			icon = getHoverIcon(button, true);
		return icon;
	}
	
	protected Icon getHoverIcon(AbstractButton button, boolean active) {
		Icon icon = button.getRolloverIcon();
		if(icon==null)
			icon = getActionIcon(button, false, active, true);
		if(icon==null)
			icon = getDefaultIcon(button, active);
		return icon;
	}
	
	protected Icon getDefaultIcon(AbstractButton button, boolean active) {
		Icon icon = button.getIcon();
		if(icon==null)
			icon = getActionIcon(button, false, active, false);
		return icon;
	}
	
	protected Icon getActionIcon(AbstractButton button, boolean pressed, boolean active, boolean hover) {
		Action action = button.getAction();
		IconResource resource = action==null? null: (IconResource)action.getValue(ICON_RESOURCE);
		if(resource==null)
			return null;
		
		if(pressed)
			return resource.getIconPressed();
		if(active) {
			if(hover)
				return resource.getIconActiveHover();
			return resource.getIconActive();
		}
		if(hover)
			return resource.getIconHover();
		return resource.getIcon();
	}
	
	protected boolean isPressed(AbstractButton button) {
		ButtonModel model = button.getModel();
		return model.isArmed() && model.isPressed();
	}
	
	protected boolean isParentActive(AbstractButton button) {
		Container parent = button.getParent();
		return parent instanceof Titlebar? ((Titlebar)parent).isActive(): false;
	}
	
	
	public void installUI(JComponent c) {
		super.installUI(c);
		AbstractButton button = (AbstractButton)c;
		button.setRolloverEnabled(true);
		button.setRequestFocusEnabled(false);
	}


	public String getUiName() {
		return uiName;
	}

	public void setUiName(String uiName) {
		this.uiName = uiName;
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



	public void setBorderActive(Border borderActive) {
		this.borderActive = borderActive;
	}

	public void setBorderActiveHover(Border borderActiveHover) {
		this.borderActiveHover = borderActiveHover;
	}

	public void setBorderDefault(Border borderDefault) {
		this.borderDefault = borderDefault;
	}

	public void setBorderDefaultHover(Border borderDefaultHover) {
		this.borderDefaultHover = borderDefaultHover;
	}

	public void setBorderPressed(Border borderPressed) {
		this.borderPressed = borderPressed;
	}
}