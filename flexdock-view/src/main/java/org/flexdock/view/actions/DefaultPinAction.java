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

import java.awt.event.ActionEvent;

import javax.swing.ButtonModel;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.state.DockingState;
import org.flexdock.view.Button;
import org.flexdock.view.View;
import org.flexdock.view.model.ViewButtonModel;

/**
 * @author Christopher Butler
 * @author Bobby Rosenberger
 */
public class DefaultPinAction extends ViewAction {

    public DefaultPinAction() {

    }

    @Override
    public void actionPerformed(View view, ActionEvent evt) {
        boolean minimize = view.isMinimized()? false: true;
        DockingManager.setMinimized(view, minimize);
    }

    public void updateState(View view, DockingState info, Button button) {
        button.getModel().setSelected(info.isMinimized());
    }

    @Override
    public ButtonModel createButtonModel() {
        return new PinButtonModel();
    }

    private static class PinButtonModel extends ViewButtonModel {
        @Override
        public boolean isSelected() {
            DockingState info = getDockingState();
            if(info==null) {
                return super.isSelected();
            }
            return info.isMinimized();
        }
    }

}
