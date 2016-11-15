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
package org.flexdock.view;

import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.flexdock.plaf.PlafManager;
import org.flexdock.view.model.ViewButtonModel;

/**
 * @author Christopher Butler
 */
public class Button extends JToggleButton {

    public static final String UI_CLASS_ID = "Flexdock.titlebar.button";

    public Button(Action action) {
        setAction(action);
        setModel(new ViewButtonModel());
    }

    public void setModel(ButtonModel newModel) {
        ButtonModel oldModel = getModel();
        if(newModel!=null && oldModel!=null) {
            newModel.setSelected(oldModel.isSelected());
        }
        super.setModel(newModel);
    }

    public void updateUI() {
        setUI(PlafManager.getUI(this));
    }

    public String getUIClassID() {
        return UI_CLASS_ID;
    }

    public View getView() {
        return (View)SwingUtilities.getAncestorOfClass(View.class, this);
    }

    public String getActionName() {
        Action action = getAction();
        if(action==null) {
            return null;
        }
        return (String)action.getValue(Action.NAME);
    }
}
