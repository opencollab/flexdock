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
package org.flexdock.dockbar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.state.MinimizationManager;
import org.flexdock.plaf.common.border.SlideoutBorder;

/**
 * A special dockbar which can be used to hold a statusbar panel at the bottom.
 *
 * @author Wolfgang Zitzelsberger
 */
public class StatusDockbar extends Dockbar {
    private JPanel labelPanel;

    private JComponent statusBarComponent;

    public StatusDockbar(DockbarManager manager, int orientation) {
        super(manager, orientation);
        labelPanel = new JPanel();
        setOrientation(orientation);
        setLayout(new BorderLayout());
        super.add(labelPanel);
    }

    public void setStatusBarComponent(JComponent c) {
        statusBarComponent = c;
        add(statusBarComponent, BorderLayout.SOUTH);
    }

    public JComponent getStatusBarComponent() {
        return statusBarComponent;
    }

    @Override
    public Component add(Component c) {
        return labelPanel.add(c);
    }

    @Override
    void undock(Dockable dockable) {
        DockbarLabel label = getLabel(dockable);

        labelPanel.remove(label);
        mDocks.remove(label);
        getParent().validate();
        repaint();
    }

    private void setOrientation(int orientation) {
        orientation = getValidOrientation(orientation);
        this.orientation = orientation;

        Border border = labelPanel.getBorder();
        if (border instanceof SlideoutBorder) {
            ((SlideoutBorder) border).setOrientation(orientation);
        }

        int boxConstraint = orientation == MinimizationManager.TOP
                            || orientation == MinimizationManager.BOTTOM ? BoxLayout.LINE_AXIS
                            : BoxLayout.PAGE_AXIS;
        labelPanel.setLayout(new BoxLayout(labelPanel, boxConstraint));
    }

    @Override
    public Dimension getPreferredSize() {
        if (statusBarComponent == null || statusBarComponent.getComponentCount() == 0) {
            if (mDocks.isEmpty()) {
                return new Dimension(0, 0);
            } else {
                return labelPanel.getComponent(0).getPreferredSize();
            }
        }

        if (labelPanel.getComponentCount() == 0) {
            return statusBarComponent.getPreferredSize();
        }

        DockbarLabel label = (DockbarLabel) labelPanel.getComponent(0);
        return new Dimension(label.getPreferredSize().width
                             + statusBarComponent.getPreferredSize().width, label
                             .getPreferredSize().height
                             + statusBarComponent.getPreferredSize().height);
    }

}
