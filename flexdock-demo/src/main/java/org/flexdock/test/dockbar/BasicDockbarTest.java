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
package org.flexdock.test.dockbar;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.view.View;

/**
 * @author Bobby Rosenberger
 */
public class BasicDockbarTest {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch(Exception e) {
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        View view = createView();
        //Create and set up the window.
        JFrame frame = new JFrame("Basic Dockbar Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        //Grab the contentpane and add elements
        Container cp = frame.getContentPane();
        cp.setLayout(new FlowLayout());
        // push the buttons 20px down from the top
        ((JComponent)cp).setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton leftButton = new JButton("Pin Left");
        JButton bottomButton = new JButton("Pin Bottom");
        JButton rightButton = new JButton("Pin Right");

        leftButton.addActionListener(createMinimizeAction(DockingConstants.LEFT));
        bottomButton.addActionListener(createMinimizeAction(DockingConstants.BOTTOM));
        rightButton.addActionListener(createMinimizeAction(DockingConstants.RIGHT));

        cp.add(leftButton);
        cp.add(bottomButton);
        cp.add(rightButton);

        // Display the window.
        frame.setVisible(true);
    }

    private static ActionListener createMinimizeAction(final int edge) {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                View view = createView();
                DockingManager.setMinimized(view, true, edge);
            }
        };
    }

    private static int viewCount = 0;

    private static View createView() {
        String id = "test.view." + viewCount;
        String txt = "Test View " + viewCount;
        viewCount++;
        return new View(id, txt);
    }
}