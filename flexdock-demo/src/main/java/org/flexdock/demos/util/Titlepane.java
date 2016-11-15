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

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 * @author Christopher Butler
 */
public class Titlepane extends JPanel {
    private Titlebar titlebar;
    private JComponent contentPane;

    public Titlepane(String title) {
        setLayout(new BorderLayout());

        titlebar = createTitlebar(title);
        add(titlebar, BorderLayout.NORTH);
        setContentPane(createContentPane());
    }

    public String getTitle() {
        return titlebar.getText();
    }

    public void setTitle(String title) {
        titlebar.setTitle(title);
    }

    public JLabel getTitlebar() {
        return titlebar;
    }

    protected Titlebar createTitlebar(String title) {
        return new Titlebar(title, new Color(183, 201, 217));
    }

    public void setContentPane(JComponent comp) {
        if(contentPane!=null) {
            remove(contentPane);
        }
        if(comp!=null) {
            add(comp, BorderLayout.CENTER);
        }
        contentPane = comp;
    }

    protected JComponent createContentPane() {
        JPanel pane = new JPanel();
        pane.setBorder(new LineBorder(Color.DARK_GRAY));
        pane.setBackground(Color.WHITE);
        return pane;
    }
}
