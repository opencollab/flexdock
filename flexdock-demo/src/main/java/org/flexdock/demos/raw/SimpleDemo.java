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
package org.flexdock.demos.raw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.flexdock.demos.util.DemoUtility;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;

public class SimpleDemo extends JFrame implements DockingConstants {
    public static void main(String[] args) {
        JFrame f = new SimpleDemo();
        f.setSize(600, 400);
        DemoUtility.setCloseOperation(f);
        f.setVisible(true);
    }

    public SimpleDemo() {
        super("Simple Docking Demo");
        setContentPane(createContentPane());
    }

    private JPanel createContentPane() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.add(buildDockingPort(Color.blue, "Blue"), BorderLayout.NORTH);
        p.add(buildDockingPort(Color.red, "Red"), BorderLayout.SOUTH);
        p.add(buildDockingPort(Color.green, "Green"), BorderLayout.EAST);
        p.add(buildDockingPort(Color.yellow, "Yellow"), BorderLayout.WEST);
        p.add(createDockingPort(), BorderLayout.CENTER);
        return p;
    }

    private DefaultDockingPort buildDockingPort(Color color, String desc) {
        // create the DockingPort
        DefaultDockingPort port = createDockingPort();

        // create and register the Dockable panel
        JPanel p = new JPanel();
        p.setBackground(color);
        p.add(new JLabel("Drag Me"));
        DockingManager.registerDockable(p, desc);

        // dock the panel and return the DockingPort
        port.dock(p, CENTER_REGION);
        return port;
    }

    private DefaultDockingPort createDockingPort() {
        DefaultDockingPort port = new DefaultDockingPort();
        port.setBackground(Color.ORANGE);
        port.setPreferredSize(new Dimension(100, 100));
        return port;
    }
}
