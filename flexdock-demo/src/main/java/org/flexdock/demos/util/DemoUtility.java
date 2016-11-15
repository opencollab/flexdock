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
package org.flexdock.demos.util;

import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DemoUtility {
    public static void setDemoDisableExitOnClose() {
        System.setProperty("disable.system.exit", "true");
    }

    public static void setCloseOperation(JFrame f) {
        if (!Boolean.getBoolean("disable.system.exit")) {
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
            f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }
    }

    /**
     * Opens a JOptionPane with the error message and formatted stack trace of the throwable in a scrollable text area.
     *
     * @param c
     *            optional argument for parent component to open modal error
     *            dialog relative to
     * @param errorMessage
     *            short string description of failure, must be non-null
     * @param t
     *            the throwable that's being reported, must be non-null
     */
    public static void showErrorDialog(Component c, String errorMessage,
                                       Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(errorMessage);
        pw.print("Exception is: ");
        t.printStackTrace(pw);
        pw.flush();

        JTextArea ta = new JTextArea(sw.toString(), 15, 60);
        JScrollPane sp = new JScrollPane(ta);
        JOptionPane.showMessageDialog(c, sp, errorMessage,
                                      JOptionPane.ERROR_MESSAGE);
    }
}
