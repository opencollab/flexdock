/*
 * Created on Mar 4, 2005
 */
package org.flexdock.demos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.flexdock.demos.util.DemoUtility;
import org.flexdock.demos.util.VSNetStartPage;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * @author Christopher Butler
 */
public class ViewDemo extends JFrame implements DockingConstants {

    public static void main(String[] args) {
        SwingUtility.setPlaf("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		SwingUtility.setPlaf("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//		SwingUtility.setPlaf("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                startup();
            }
        });
    }

    private static void startup() {
        // turn on floating support
        DockingManager.setFloatingEnabled(true);

        JFrame f = new ViewDemo();
        f.setSize(800, 600);
        SwingUtility.centerOnScreen(f);
        DemoUtility.setCloseOperation(f);
        f.setVisible(true);
    }

    public ViewDemo() {
        super("Viewport Demo");
        setContentPane(createContentPane());
    }

    private JPanel createContentPane() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBorder(new EmptyBorder(5, 5, 5, 5));

        Viewport viewport = new Viewport();
        p.add(viewport, BorderLayout.CENTER);

        View startPage = createStartPage();
        View view1 = createView("solution.explorer", "Solution Explorer");
        View view2 = createView("task.list", "Task List");
        View view3 = createView("class.view", "Class View");
        View view4 = createView("message.log", "Message Log");

        viewport.dock(startPage);
        startPage.dock(view1, WEST_REGION, .3f);
        startPage.dock(view2, SOUTH_REGION, .3f);
        startPage.dock(view4, EAST_REGION, .3f);
        view1.dock(view3, SOUTH_REGION, .3f);

        return p;
    }

    private View createView(String id, String text) {
        View view = new View(id, text);
        view.addAction(CLOSE_ACTION);
        view.addAction(PIN_ACTION);

        JPanel p = new JPanel();
        //		p.setBackground(Color.WHITE);
        p.setBorder(new LineBorder(Color.GRAY, 1));

        JTextField t = new JTextField(text);
        t.setPreferredSize(new Dimension(100, 20));
        p.add(t);

        view.setContentPane(p);
        return view;
    }

    private View createStartPage() {
        String id = "startPage";
        View view = new View(id, null, null);
        view.setTerritoryBlocked(CENTER_REGION, true);
        view.setTitlebar(null);
        view.setContentPane(new VSNetStartPage());
        return view;
    }

}