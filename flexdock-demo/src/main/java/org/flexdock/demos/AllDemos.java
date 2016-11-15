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
package org.flexdock.demos;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;



import org.flexdock.demos.util.DemoUtility;
import org.flexdock.util.SwingUtility;

/**
 * Generic launcher for all demos.
 * It allows us to deliver a single entry point demo via a runnable jar or JNLP launched
 * app that runs all of our demonstration apps.
 */
public class AllDemos extends JFrame {

    private static final String[] DEMO_CLASS_NAMES = new String[] {
        "org.flexdock.demos.maximizing.MaximizationDemo",
        "org.flexdock.demos.perspective.PerspectivesDemo",
        "org.flexdock.demos.perspective.XMLPerspectivesDemo",
        "org.flexdock.demos.raw.adapter.AdapterDemo",
        "org.flexdock.demos.raw.border.BorderDemo",
        "org.flexdock.demos.raw.CompoundDemo",
        "org.flexdock.demos.raw.elegant.ElegantDemo",
        "org.flexdock.demos.raw.jmf.JMFDemo",
        "org.flexdock.demos.raw.SimpleDemo",
        "org.flexdock.demos.raw.SplitPaneDemo",
        "org.flexdock.demos.raw.TabbedPaneDemo",
        "org.flexdock.demos.view.ViewDemo",
    };

    public AllDemos() {
        super("FlexDock Demos");

        TreeMap sortedClassNames = new TreeMap();
        for (int i = 0; i < DEMO_CLASS_NAMES.length; i++) {
            String fullClassName = DEMO_CLASS_NAMES[i];
            String justClassName = fullClassName.substring(fullClassName
                                     .lastIndexOf('.') + 1);
            sortedClassNames.put(justClassName, fullClassName);
        }

        getContentPane().setLayout(new GridLayout(0, 1, 3, 3));
        for (Iterator iter = sortedClassNames.entrySet().iterator(); iter
                .hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();

            final String fullClassName = (String) entry.getValue();
            final String justClassName = (String) entry.getKey();

            JButton button = new JButton(justClassName);
            button.setToolTipText("Runs " + fullClassName);

            getContentPane().add(button);

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    launchClass(fullClassName);
                }
            });
        }

        pack();
    }

    private void launchClass(String fullClassName) {
        Class c;
        try {
            c = Class.forName(fullClassName);
            Method m = c.getMethod("main", new Class[] { String[].class });
            m.invoke(null, new Object[] { null });
        } catch (Throwable t) {
            String message = "Error occurred when calling main(String[]) on class " + fullClassName;
            DemoUtility.showErrorDialog(this, message, t);
        }
    }

    public static void main(String[] args) {
        try {
            final AllDemos a = new AllDemos();
            a.setDefaultCloseOperation(AllDemos.EXIT_ON_CLOSE);
            DemoUtility.setDemoDisableExitOnClose();
            SwingUtility.centerOnScreen(a);
            a.setVisible(true);

            for (int i = 0; i < args.length; i++) {
                final String fullClassName = args[i];
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        a.launchClass(fullClassName);
                    }
                });
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
    }
}
