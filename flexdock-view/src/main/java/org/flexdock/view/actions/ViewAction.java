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
package org.flexdock.view.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonModel;
import javax.swing.SwingUtilities;

import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public abstract class ViewAction extends AbstractAction {
    /**
     * @deprecated unused
     */
    public static final ViewAction EMPTY_ACTION = createDefault();

    protected String viewId;

    protected View getView(ActionEvent evt) {
        View view = viewId == null ? null : View.getInstance(viewId);
        if (view == null) {
            Component c = (Component) evt.getSource();
            view = (View) SwingUtilities.getAncestorOfClass(View.class, c);
        }
        return view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        View view = getView(e);
        actionPerformed(view, e);
    }

    public abstract void actionPerformed(View view, ActionEvent evt);

    private static ViewAction createDefault() {
        return new ViewAction() {
            @Override
            public void actionPerformed(View view, ActionEvent evt) {

            }
        };
    }

    public ButtonModel createButtonModel() {
        return null;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

}
