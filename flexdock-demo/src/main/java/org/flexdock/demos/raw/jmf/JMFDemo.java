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
package org.flexdock.demos.raw.jmf;

import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JFrame;



import org.flexdock.demos.util.DemoUtility;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;

/**
 * @author Christopher Butler
 */
public class JMFDemo extends JFrame {

    private DefaultDockingPort port;

    public static void main(String[] args) {
        DockingManager.setFloatingEnabled(true);
        System.setProperty(DockingConstants.HEAVYWEIGHT_DOCKABLES, "true");

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    createAndShowGui();
                } catch(Throwable t) {
                    String message = "Unable to initialize JMFDemo";
                    DemoUtility.showErrorDialog(null, message, t);
                }
            }
        });
    }

    private static void createAndShowGui() {
        JFrame frame = new JMFDemo();
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DemoUtility.setCloseOperation(frame);
        frame.setVisible(true);
    }

    public JMFDemo() {
        super("Adapter Demo");
        setContentPane(createContentPane());
    }

    private Container createContentPane() {
        port = new DefaultDockingPort();
        MediaPanel pane1 = new MediaPanel("video.1", "Video 1", "fish.mov");
        MediaPanel pane2 = new MediaPanel("video.2", "Video 2", "lung02a.mov");
        MediaPanel pane3 = new MediaPanel("video.3", "Video 3", "fish.mov");
        MediaPanel pane4 = new MediaPanel("video.4", "Video 4", "lung02a.mov");
        MediaPanel pane5 = new MediaPanel("video.5", "Video 5", "fish.mov");

        DockingManager.dock(pane1, (DockingPort)port);
        DockingManager.dock(pane2, pane1, DockingConstants.NORTH_REGION, 0.3f);
        DockingManager.dock(pane3, pane1, DockingConstants.SOUTH_REGION);
        DockingManager.dock(pane4, pane1, DockingConstants.EAST_REGION, 0.3f);
        DockingManager.dock(pane5, pane1, DockingConstants.WEST_REGION);

        return port;
    }

}
