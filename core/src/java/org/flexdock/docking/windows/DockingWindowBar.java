package org.flexdock.docking.windows;

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

import org.flexdock.docking.windows.util.TextIcon;
import org.flexdock.docking.windows.util.RoundedLineBorder;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

public class DockingWindowBar extends JPanel {
    // constants

    public static final Color COLOR = new Color(247, 243, 239);

    // local classes

    public class Dock extends JLabel implements MouseListener {
        // instance data

        private DockingWindow mDockable;
        private boolean  mSelected = false;
        private boolean mDragging = false;
        private RoundedLineBorder mBorder;
        private boolean mInPaint = false;
        private boolean mActive = false;

        // static

        // constructor

        Dock(DockingWindow panel, int orientation) {
            this(panel.getTitle(), panel.getIcon(), orientation);

            mDockable = panel;
        }

        Dock(String title, Icon icon, int orientation) {
            Insets insets = null;
            int rotation = TextIcon.ROTATE_NONE;
            switch (orientation) {
                case DockingFrame.LEFT:
                    insets = new Insets(1, 1, 2, 1); // left, top, bottom, right
                    rotation = TextIcon.ROTATE_LEFT;
                    break;

                case DockingFrame.RIGHT:
                    insets = new Insets(1, 1, 2, 1); // left, top, bottom, right
                    rotation = TextIcon.ROTATE_RIGHT;
                    break;

                case DockingFrame.BOTTOM:
                    insets = new Insets(1, 1, 1, 2); // left, top, bottom, right
                    rotation = TextIcon.ROTATE_NONE;
                    break;
            } // switch

            setIcon(new TextIcon(this, title, icon, rotation, 2, 1));

            mBorder = new RoundedLineBorder(Color.lightGray, 3);

            setBorder(new CompoundBorder(new EmptyBorder(insets), mBorder));

            addMouseListener(this);
        }

        // stuff

        DockingWindow getPanel() {
            return mDockable;
        }

        // hack

        public Border getBorder() {
            return mInPaint ? null : super.getBorder();
        }

        // override

        public void paintComponent(Graphics g) {
            mInPaint = false;

                paintBorder(g);

            mInPaint = true;

            super.paintComponent(g);
        }

        public void paint(Graphics g) {
            mInPaint = true;

                super.paint(g); // will call paintComponent, paintBorder

            mInPaint = false;
        }

        public void setActive(boolean active) {
            if ( mActive != active) {
                mActive = active;

                updateBorder();

                repaint();
            } // if
        }

        private void updateBorder() {
             mBorder.setFilled(mSelected || mActive);
        }

        // protected

        protected void activate() {
            if (mDockable instanceof ToolWindow) {
                ToolWindow mToolWindow = (ToolWindow) mDockable;

                if ( mToolWindow.isMinimized()) {
                    setActive(true);
                    mDockable.maximizePanel(); // float or dock!
                } // if
                else {
                    setActive(false);
                    mToolWindow.minimizePanel();
                } // else
            } // if
            else {
                removeDock(this);
                mDockable.maximizePanel(); // float or dock!
            } // else
        }

        // private

        private void setSelected(boolean selected) {
            if ( mSelected != selected) {
                mSelected = selected;

                updateBorder();

                repaint();
            } // if
        }

        // override MouseListener

        public void mousePressed(MouseEvent e) {
            mDragging = e.getButton() == MouseEvent.BUTTON1;

            setSelected(mDragging);
        }

        public void mouseReleased(MouseEvent e) {
            if ( mSelected )
                activate();

            setSelected(false);
            mDragging = false;
        }

        public void mouseClicked(MouseEvent e) {}

        public void mouseEntered(MouseEvent e) {
            if ( mDragging )
                setSelected(true);
        }

        public void mouseExited(MouseEvent e) {
            if ( mDragging )
                setSelected(false);
        }
    }

    // instance data

    private ArrayList mDocks = new ArrayList();
    private int mOrientation;

    // constructor

    public DockingWindowBar(int orientation) {
        setup(orientation);

        setBackground(COLOR);
    }

    // private

    void setup(int orientation) {
        int height = new Dock("B", null, DockingFrame.BOTTOM).getPreferredSize().height;

        switch (mOrientation = orientation) {
            case DockingFrame.LEFT:
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                //add(Box.createRigidArea(new Dimension(height, 0)));
                break;

            case DockingFrame.RIGHT:
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                //add(Box.createRigidArea(new Dimension(height, 0)));
                break;

            case DockingFrame.BOTTOM:
                setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
                add(Box.createRigidArea(new Dimension(0, height)));
                break;
        } // switch
    }

    void removeDock(Dock dock) {
        remove(dock);
        mDocks.remove(dock);
        getParent().validate();
        repaint();
    }

    // public

    public int getOrientation() {
        return mOrientation;
    }


    public Dock findDock(DockingWindow panel) {
         for ( Iterator docks = mDocks.iterator(); docks.hasNext(); ) {
            Dock dock = (Dock) docks.next();

            if ( dock.getPanel() == panel)
                return dock;
        } // for

        return null;
    }

    public void dock(DockingWindow panel) {
        for ( Iterator docks = mDocks.iterator(); docks.hasNext(); ) {
            Dock dock = (Dock) docks.next();

            if ( dock.getPanel() == panel) {
                dock.setActive(false);
                return;
            } // if
        } // for

        Dock newDock;

        add(newDock = new Dock(panel, mOrientation));

        mDocks.add(newDock);

        getParent().validate();
        repaint();
    }
}