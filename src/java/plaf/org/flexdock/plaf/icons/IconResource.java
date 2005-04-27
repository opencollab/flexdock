/*
 * Created on Feb 28, 2005
 */
package org.flexdock.plaf.icons;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;

/**
 * @author Christopher Butler
 */
public class IconResource implements UIResource {
	private Icon icon;
	private Icon iconHover;
	private Icon iconActive;
	private Icon iconActiveHover;
	private Icon iconPressed;
	
	private Icon iconSelected;
	private Icon iconSelectedHover;
	private Icon iconSelectedActive;
	private Icon iconSelectedActiveHover;
	private Icon iconSelectedPressed;
	
	private Action action;
	
	private String tooltip;
	private String tooltipSelected;
	
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

	public Icon getIconSelected() {
		return iconSelected;
	}
	public void setIconSelected(Icon iconSelected) {
		this.iconSelected = iconSelected;
	}
	public Icon getIconSelectedActive() {
		return iconSelectedActive;
	}
	public void setIconSelectedActive(Icon iconSelectedActive) {
		this.iconSelectedActive = iconSelectedActive;
	}
	public Icon getIconSelectedActiveHover() {
		return iconSelectedActiveHover;
	}
	public void setIconSelectedActiveHover(Icon iconSelectedActiveHover) {
		this.iconSelectedActiveHover = iconSelectedActiveHover;
	}
	public Icon getIconSelectedHover() {
		return iconSelectedHover;
	}
	public void setIconSelectedHover(Icon iconSelectedHover) {
		this.iconSelectedHover = iconSelectedHover;
	}
	public Icon getIconSelectedPressed() {
		return iconSelectedPressed;
	}
	public void setIconSelectedPressed(Icon iconSelectedPressed) {
		this.iconSelectedPressed = iconSelectedPressed;
	}
	public String getTooltip() {
		return tooltip;
	}
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	public String getTooltipSelected() {
		return tooltipSelected;
	}
	public void setTooltipSelected(String tooltipSelected) {
		this.tooltipSelected = tooltipSelected;
	}
	
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
}
