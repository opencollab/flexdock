/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
    private Icon iconDisabled;
    private Icon iconHover;
    private Icon iconActive;
    private Icon iconActiveDisabled;
    private Icon iconActiveHover;
    private Icon iconPressed;

    private Icon iconSelected;
    private Icon iconSelectedDisabled;
    private Icon iconSelectedHover;
    private Icon iconSelectedActive;
    private Icon iconSelectedActiveHover;
    private Icon iconSelectedActiveDisabled;
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
    /**
     * @return Returns the iconDisabled.
     */
    public Icon getIconDisabled() {
        return iconDisabled;
    }
    /**
     * @param iconDisabled The iconDisabled to set.
     */
    public void setIconDisabled(Icon iconDisabled) {
        this.iconDisabled = iconDisabled;
    }

    /**
     * @return Returns the iconActiveDisabled.
     */
    public Icon getIconActiveDisabled() {
        return iconActiveDisabled;
    }
    /**
     * @param iconActiveDisabled The iconActiveDisabled to set.
     */
    public void setIconActiveDisabled(Icon iconActiveDisabled) {
        this.iconActiveDisabled = iconActiveDisabled;
    }
    /**
     * @return Returns the iconSelectedActiveDisabled.
     */
    public Icon getIconSelectedActiveDisabled() {
        return iconSelectedActiveDisabled;
    }
    /**
     * @param iconSelectedActiveDisabled The iconSelectedActiveDisabled to set.
     */
    public void setIconSelectedActiveDisabled(Icon iconSelectedActiveDisabled) {
        this.iconSelectedActiveDisabled = iconSelectedActiveDisabled;
    }
    /**
     * @return Returns the iconSelectedDisabled.
     */
    public Icon getIconSelectedDisabled() {
        return iconSelectedDisabled;
    }
    /**
     * @param iconSelectedDisabled The iconSelectedDisabled to set.
     */
    public void setIconSelectedDisabled(Icon iconSelectedDisabled) {
        this.iconSelectedDisabled = iconSelectedDisabled;
    }
}
