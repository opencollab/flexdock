/*
 * Created on Feb 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.icons;

import javax.swing.Icon;
import javax.swing.plaf.UIResource;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IconResource implements UIResource {
	private Icon icon;
	private Icon iconHover;
	private Icon iconActive;
	private Icon iconActiveHover;
	private Icon iconPressed;
	
	/**
	 * @return Returns the icon.
	 */
	public Icon getIcon() {
		return icon;
	}
	/**
	 * @param icon The icon to set.
	 */
	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	/**
	 * @return Returns the iconActive.
	 */
	public Icon getIconActive() {
		return iconActive;
	}
	/**
	 * @param iconActive The iconActive to set.
	 */
	public void setIconActive(Icon iconActive) {
		this.iconActive = iconActive;
	}
	/**
	 * @return Returns the iconActiveHover.
	 */
	public Icon getIconActiveHover() {
		return iconActiveHover;
	}
	/**
	 * @param iconActiveHover The iconActiveHover to set.
	 */
	public void setIconActiveHover(Icon iconActiveHover) {
		this.iconActiveHover = iconActiveHover;
	}
	/**
	 * @return Returns the iconHover.
	 */
	public Icon getIconHover() {
		return iconHover;
	}
	/**
	 * @param iconHover The iconHover to set.
	 */
	public void setIconHover(Icon iconHover) {
		this.iconHover = iconHover;
	}
	/**
	 * @return Returns the iconPressed.
	 */
	public Icon getIconPressed() {
		return iconPressed;
	}
	/**
	 * @param iconPressed The iconPressed to set.
	 */
	public void setIconPressed(Icon iconPressed) {
		this.iconPressed = iconPressed;
	}

}
