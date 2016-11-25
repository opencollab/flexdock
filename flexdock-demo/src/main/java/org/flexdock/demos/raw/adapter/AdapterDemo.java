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
package org.flexdock.demos.raw.adapter;

import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.flexdock.demos.util.DemoUtility;
import org.flexdock.demos.util.Titlepane;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.adapter.AdapterFactory;
import org.flexdock.docking.defaults.DefaultDockingPort;

/**
 * @author Christopher Butler
 */
public class AdapterDemo extends JFrame {
    private DefaultDockingPort port;

    public static void main(String[] args) {
        System.setProperty(AdapterFactory.ADAPTER_RESOURCE_KEY, "org/flexdock/demos/raw/adapter/docking-adapter.xml");
        DockingManager.setFloatingEnabled(true);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGui();
            }
        });
    }

    private static void createAndShowGui() {
        JFrame frame = new AdapterDemo();
        frame.setSize(500, 500);
        DemoUtility.setCloseOperation(frame);
        frame.setVisible(true);
    }

    public AdapterDemo() {
        super("Adapter Demo");


        setContentPane(createContentPane());
    }

    private Container createContentPane() {
        port = new DefaultDockingPort();
        Titlepane pane1 = new Titlepane("View 1");
        Titlepane pane2 = new Titlepane("View 2");
        Titlepane pane3 = new Titlepane("View 3");
        Titlepane pane4 = new Titlepane("View 4");
        Titlepane pane5 = new Titlepane("View 5");

        DockingManager.dock(pane1, (DockingPort)port);
        DockingManager.dock(pane2, pane1, DockingConstants.NORTH_REGION, 0.3f);
        DockingManager.dock(pane3, pane1, DockingConstants.SOUTH_REGION);
        DockingManager.dock(pane4, pane1, DockingConstants.EAST_REGION, 0.3f);
        DockingManager.dock(pane5, pane1, DockingConstants.WEST_REGION);

        return port;
    }

}
