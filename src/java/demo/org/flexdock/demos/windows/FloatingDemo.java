package org.flexdock.demos.windows;

/* Copyright (c) 2004 Andreas Ernst

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in the
Software without restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
Software, and to permit persons to whom the Software is furnished to do so, subject
to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. */

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.defaults.DockableAdapter;
import org.flexdock.docking.windows.DockingFrame;
import org.flexdock.docking.windows.DockingWindow;
import org.flexdock.docking.windows.DockingWindowBar;
import org.flexdock.docking.windows.ToolWindow;

import javax.swing.*;
import java.awt.*;

public class FloatingDemo extends DockingWindow {
    // constants

    static final Icon ICON = new ImageIcon(FloatingDemo.class.getResource("icon.png"));

    // instance data

    private Dockable mDockable;
    private DockingPort mPort;
    private static DockingWindowBar mDockBar;

    // constructor

    public FloatingDemo(String title, DockingPort port) {
        super(title, ICON, new JButton("Super"));

        mDockable = new DockableImpl();

        mPort = port;
    }

    // override FloatablePanel

    public void minimizePanel() {
        super.minimizePanel(); // make sure an open window is closed!

        mPort.undock(this);
        ((DefaultDockingPort) mPort).repaint();

        mDockBar.dock(this);
    }

    public void floatPanel() {
        getFloatingWindow(SwingUtilities.getWindowAncestor(this), true); // make sure, the initial size will be remembered!

        mPort.undock(this);

        super.floatPanel();

        ((DefaultDockingPort) mPort).repaint();
    }

    public void dockPanel() {
        super.dockPanel();

        mPort.dock(getDockable(), DockingPort.CENTER_REGION);

        ((DefaultDockingPort) mPort).validate();
        ((DefaultDockingPort) mPort).repaint();
    }

    // private

    public FloatingDemo getThis() {
        return this;
    }

    public Dockable getDockable() {
        return mDockable;
    }

    private class DockableImpl extends DockableAdapter {
        public Component getDockable() {
            return getThis();
        }

        public String getDockableDesc() {
            return getTitle().trim();
        }

        public Icon getIcon() {
            return ICON;
        }

        public Component getInitiator() {
            return mHeader; // the titlebar will the the 'hot' component that initiates dragging
        }

        public boolean isDockingEnabled() {
            return !isFloating();
        }

        public void dockingCompleted(DockingPort port) {
            mPort = port;
        }
    }


    private static JPanel createContentPane() {
        JPanel p = new JPanel(new BorderLayout(5, 5));

        p.add(buildDockingPort("North"), BorderLayout.NORTH);
        p.add(buildDockingPort("South"), BorderLayout.SOUTH);
        p.add(buildDockingPort("East"), BorderLayout.EAST);
        p.add(buildDockingPort("South"), BorderLayout.SOUTH);
        p.add(buildDockingPort("Center"), BorderLayout.CENTER);

        return p;
    }

    private static DefaultDockingPort buildDockingPort(String desc) {
        // create the DockingPort

        DefaultDockingPort port = createDockingPort();

        // create the Dockable panel

        FloatingDemo cd = new FloatingDemo(desc, port);

        DockingManager.registerDockable(cd.getDockable());

        // dock the panel and return the DockingPort

        port.dock(cd.getDockable(), DockingPort.CENTER_REGION);

        return port;
    }

    private static DefaultDockingPort createDockingPort() {
        DefaultDockingPort port = new DefaultDockingPort();
        port.setBackground(Color.gray);
        port.setPreferredSize(new Dimension(100, 100));
        return port;
    }

    public static void main(String[] args) {
        DockingFrame frame = new DockingFrame("Floating Windows Demo");

        frame.addDockView(mDockBar = new DockingWindowBar(DockingFrame.BOTTOM));

        frame.setContentPane(createContentPane());

        new ToolWindow(frame, mDockBar, "Super", "Super", ICON, new JButton("Cool"));

        frame.addDockView(mDockBar = new DockingWindowBar(DockingFrame.LEFT));

        new ToolWindow(frame, mDockBar, "Super", "Super", ICON, new JButton("Cool"));

        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
