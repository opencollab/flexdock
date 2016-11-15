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
package org.flexdock.demos.maximizing;

import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.defaults.DefaultDockingStrategy;
import org.flexdock.docking.defaults.StandardBorderManager;
import org.flexdock.plaf.common.border.ShadowBorder;

public class MyDockingPort extends DefaultDockingPort {

    static {
        initStatic();
    }

    private static void initStatic() {
        DockingManager.setDockingStrategy(MyDockingPort.class, new MyDockingStrategy());
    }

    public MyDockingPort() {
        this(new ShadowBorder());
    }

    public MyDockingPort(Border portletBorder) {
        super();
        if (portletBorder != null) {
            setBorderManager(new StandardBorderManager(portletBorder));
        }
    }

    protected JTabbedPane createTabbedPane() {
        JTabbedPane tabbed = super.createTabbedPane();
        tabbed.putClientProperty("jgoodies.embeddedTabs", Boolean.TRUE);
        return tabbed;
    }

    // ***************

    private static class MyDockingStrategy extends DefaultDockingStrategy {
        protected DockingPort createDockingPortImpl(DockingPort base) {
            return new MyDockingPort();
        }


    }

}