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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

public class DockingFrame extends JFrame {
    // constants

    public static final int LEFT   = 0;
	public static final int RIGHT  = 1;
	public static final int BOTTOM = 2;

    // instance data

    private DockingWindowBar[] mDockViews = new DockingWindowBar[3];
    private ToolWindow[] mDockables = new ToolWindow[3];
    private ArrayList mAllDockables = new ArrayList();
    private JComponent mStatusBar;
    private JComponent mToolBar;
    private Stack mAnimations = new Stack();

    // interfaces

     interface Animation extends Runnable {
        public String getName();
    }

    // local classes

    class RootPane extends JRootPane {
        // local classes

        protected class RootLayout implements LayoutManager2, Serializable {
            // implement

            public Dimension preferredLayoutSize(Container parent) {
                Insets insets = getInsets();

                int preferredWidth = 0;
                int preferredHeight = 0;

                // content pane

                if (contentPane != null) {
                    preferredWidth = contentPane.getPreferredSize().width;
                    preferredHeight = contentPane.getPreferredSize().height;
                } // if
                else {
                    preferredWidth = parent.getSize().width;
                    preferredHeight = parent.getSize().height;
                } // else

                // menu

                if (menuBar != null && menuBar.isVisible()) {
                    preferredWidth = Math.max(preferredWidth, menuBar.getPreferredSize().width);
                    preferredHeight = Math.max(preferredHeight, menuBar.getPreferredSize().height);
                } // if

                // toolbars

                 if (mToolBar != null && mToolBar.isVisible()) {
                    preferredWidth = Math.max(preferredWidth, mToolBar.getPreferredSize().width);
                    preferredHeight = Math.max(preferredHeight, mToolBar.getPreferredSize().height);
                } // if

                // docks

                if ( mDockViews[LEFT] != null)
                    preferredWidth = Math.max(preferredWidth,  mDockViews[LEFT].getPreferredSize().width);

                if ( mDockViews[RIGHT] != null)
                    preferredWidth = Math.max(preferredWidth,  mDockViews[RIGHT].getPreferredSize().width);

                if (mDockViews[BOTTOM] != null)
                    preferredHeight = Math.max(preferredHeight, mDockViews[BOTTOM].getPreferredSize().height);

                // done

                return new Dimension(preferredWidth + insets.left + insets.right,
                        preferredHeight + insets.top + insets.bottom);
            }


            public Dimension minimumLayoutSize(Container parent) {
                Insets insets = getInsets();

                int minimumWidth = 0;
                int minimumHeight = 0;

                // content pane

                if (contentPane != null) {
                    minimumWidth = contentPane.getMinimumSize().width;
                    minimumHeight = contentPane.getMinimumSize().height;
                } // if
                else {
                    minimumWidth = parent.getSize().width;
                    minimumHeight = parent.getSize().height;
                } // else

                // menu

                if (menuBar != null && menuBar.isVisible()) {
                    minimumWidth = Math.max(minimumWidth, menuBar.getMinimumSize().width);
                    minimumHeight = Math.max(minimumHeight, menuBar.getMinimumSize().height);
                } // if

                // toolbar

                if (mToolBar != null && mToolBar.isVisible()) {
                    minimumWidth = Math.max(minimumWidth, mToolBar.getMinimumSize().width);
                    minimumHeight = Math.max(minimumHeight, mToolBar.getMinimumSize().height);
                } // if

                // docks

                if (mDockViews[LEFT] != null)
                    minimumWidth = Math.max(minimumWidth, mDockViews[LEFT].getMinimumSize().width);

                if (mDockViews[RIGHT] != null)
                    minimumWidth = Math.max(minimumWidth, mDockViews[RIGHT].getMinimumSize().width);

                if (mDockViews[BOTTOM] != null)
                    minimumWidth = Math.max(minimumWidth, mDockViews[BOTTOM].getMinimumSize().height);

                // done

                return new Dimension(minimumWidth + insets.left + insets.right,
                        minimumHeight + insets.top + insets.bottom);
            }

            public Dimension maximumLayoutSize(Container target) {
                Dimension rd, mbd;
                Insets i = getInsets();
                if (menuBar != null && menuBar.isVisible())
                    mbd = menuBar.getMaximumSize();

                else
                    mbd = new Dimension(0, 0);

                if (contentPane != null)
                    rd = contentPane.getMaximumSize();

                else
                    // This is silly, but should stop an overflow error
                    rd = new Dimension(
                            Integer.MAX_VALUE,
                            Integer.MAX_VALUE - i.top - i.bottom - mbd.height - 1);

                return new Dimension(
                        Math.min(rd.width, mbd.width) + i.left + i.right,
                        rd.height + mbd.height + i.top + i.bottom);
            }

            // layout engine...

            public void layoutContainer(Container parent) {
                Rectangle bounds = parent.getBounds();
                Insets insets = getInsets();

                // substract insets

                int w = bounds.width - insets.right - insets.left;
                int h = bounds.height - insets.top - insets.bottom;

                if (layeredPane != null)
                    layeredPane.setBounds(insets.left, insets.top, w, h); // x, y, w, h

                if (glassPane != null)
                    glassPane.setBounds(insets.left, insets.top, w, h);

                // Note: This is laying out the children in the layeredPane,
                // technically, these are not our children.

                int contentTop = 0;

                // menu bar

                if (menuBar != null && menuBar.isVisible()) {
                    Dimension preferredSize = menuBar.getPreferredSize();

                    menuBar.setBounds(0, contentTop, w, preferredSize.height); // x, y, w, h

                    contentTop += preferredSize.height;
                } // if

                // tool bar

                 if (mToolBar != null && mToolBar.isVisible()) {
                    Dimension preferredSize = mToolBar.getPreferredSize();

                    mToolBar.setBounds(0, contentTop, w, preferredSize.height); // x, y, w, h

                    contentTop += preferredSize.height;
                } // if

                // dock views

                int statusBarHeight = mStatusBar != null ? mStatusBar.getPreferredSize().height : 0;

                int leftDockWidth    = mDockViews[LEFT] != null ? mDockViews[LEFT].getPreferredSize().width: 0;
                int x = 0;

                DockingWindowBar mDockingWindowBar;

                // left

                if ((mDockingWindowBar = mDockViews[LEFT]) != null) {
                    mDockingWindowBar.setBounds(x, contentTop, mDockingWindowBar.getPreferredSize().width, h - contentTop - statusBarHeight);

                    x += mDockingWindowBar.getPreferredSize().width;
                    w -= mDockingWindowBar.getPreferredSize().width;
                } // if

                // right

                if ((mDockingWindowBar = mDockViews[RIGHT]) != null) {
                    mDockingWindowBar.setBounds(w - mDockingWindowBar.getPreferredSize().width + x, contentTop, mDockingWindowBar.getPreferredSize().width, h - contentTop - statusBarHeight);

                    w -= mDockingWindowBar.getPreferredSize().width;
                } // if

                // bottom

                if ((mDockingWindowBar = mDockViews[BOTTOM]) != null) {
                    mDockingWindowBar.setBounds(leftDockWidth, h - statusBarHeight - mDockingWindowBar.getPreferredSize().height, w, mDockingWindowBar.getPreferredSize().height);

                    h -= mDockingWindowBar.getPreferredSize().height;
                } // if

                // dockables

                int bottomDockableHeight =  mDockables[BOTTOM] != null ? mDockables[BOTTOM].getHeight() : 0;// (mDockables[BOTTOM].isPinned() ? mDockables[BOTTOM].getHeight() : 0) : 0;

                ToolWindow mToolWindow;

                // right

                if ((mToolWindow = mDockables[RIGHT]) != null) {
                    int ww = bounds.width - insets.right - insets.left;
                    if ( mDockViews[RIGHT] != null)
                        ww -= mDockViews[RIGHT].getWidth();

                    mToolWindow.setBounds(ww - mToolWindow.getWidth(), contentTop, mToolWindow.getWidth(), h - contentTop - statusBarHeight - bottomDockableHeight);//(dockablePanel.isPinned() ? bottomDockableHeight : 0)); // x, y, width, height
                    if (mToolWindow.isPinned())
                        w -= mToolWindow.getWidth();
                } // if

                // left

                if ((mToolWindow = mDockables[LEFT]) != null) {
                    mToolWindow.setBounds(x, contentTop, mToolWindow.getWidth(), h - contentTop - statusBarHeight - bottomDockableHeight);// (dockablePanel.isPinned() ? bottomDockableHeight : 0)); // x, y, width, height

                    if ( mToolWindow.isPinned()) {
                        x += mToolWindow.getWidth();
                        w -= mToolWindow.getWidth();
                    } // if
                } // if

                // bottom

                if ((mToolWindow = mDockables[BOTTOM]) != null) {
                    int leftDock  = mDockViews[LEFT] != null ?  mDockViews[LEFT].getWidth() : 0;
                    int rightDock = mDockViews[RIGHT] != null ?  mDockViews[RIGHT].getWidth() : 0;
                    mToolWindow.setBounds(leftDock, h - statusBarHeight - mToolWindow.getHeight(), bounds.width - leftDock - rightDock, mToolWindow.getHeight()); // x, y, width, height
                    if ( mToolWindow.isPinned())
                        h -= mToolWindow.getHeight();
                } // if

                // status bar

                if ( mStatusBar != null)
                    mStatusBar.setBounds(0, bounds.height - statusBarHeight, bounds.width - insets.right - insets.left, statusBarHeight); // x, y, w, h

                // content pane

                if (contentPane != null)
                    contentPane.setBounds(x, contentTop, w, h - contentTop - statusBarHeight); // x, y, w, h
            }

            // more...

            public void addLayoutComponent(String name, Component comp) {
            }

            public void removeLayoutComponent(Component comp) {
            }

            public void addLayoutComponent(Component comp, Object constraints) {
            }

            public float getLayoutAlignmentX(Container target) {
                return 0.0f;
            }

            public float getLayoutAlignmentY(Container target) {
                return 0.0f;
            }

            public void invalidateLayout(Container target) {
            }
        }

          protected LayoutManager createRootLayout() {
              return new RootLayout();
          }


        class DockingGlassPane extends JPanel implements MouseListener, MouseMotionListener {
            // instance data

            private boolean mLocked = false;
            private Component mCurrentComponent;

            // constructor

            DockingGlassPane() {
                setOpaque(false);

                addMouseListener(this);
                addMouseMotionListener(this);
            }

            // private

            public void lockView(boolean lock) {
                setVisible(mLocked = lock);
            }

            // private

            private void handleMouseEvent(MouseEvent e, boolean rememberComponent) {
                if ( mLocked )
                    e.consume();
                else
                    redispatchMouseEvent(e, rememberComponent);
            }

            private void redispatchMouseEvent(MouseEvent e, boolean rememberComponent) {
                Point glassPanePoint = e.getPoint();
                Container container = DockingFrame.this.getLayeredPane();
                Point containerPoint = SwingUtilities.convertPoint(glassPane, glassPanePoint, container);

                Component component;

                if ( mCurrentComponent != null)
                    component = mCurrentComponent;

                else {
                    component = SwingUtilities.getDeepestComponentAt(container, containerPoint.x, containerPoint.y);
                    if (rememberComponent)
                        mCurrentComponent = component;
                } // else


                if (component != null) { // forward events over the check box.
                    setCursor(component.getCursor());

                    Point componentPoint = SwingUtilities.convertPoint(glassPane, glassPanePoint, component);

                    component.dispatchEvent(new MouseEvent(component,
                            e.getID(),
                            e.getWhen(),
                            e.getModifiers(),
                            componentPoint.x,
                            componentPoint.y,
                            e.getClickCount(),
                            e.isPopupTrigger()));
                } // if
            }

            // override MouseMotionListener

            public void mouseMoved(MouseEvent e) {
                handleMouseEvent(e, false);
            }

            public void mouseDragged(MouseEvent e) {
                handleMouseEvent(e, false);
            }

            // override MouseListener

            public void mouseClicked(MouseEvent e) {
                handleMouseEvent(e, false);
                mCurrentComponent = null;
            }

            public void mouseEntered(MouseEvent e) {
                handleMouseEvent(e, false);
            }

            public void mouseExited(MouseEvent e) {
                handleMouseEvent(e, false);
            }

            public void mousePressed(MouseEvent e) {
                handleMouseEvent(e, true);
            }

            public void mouseReleased(MouseEvent e) {
                handleMouseEvent(e, false);
                mCurrentComponent = null;
            }

        } // class DockingGlassPane

        protected Component createGlassPane() {
            return new DockingGlassPane();
        }
    }

    // constructor

    public DockingFrame(String title) {
        super(title);
    }

    // ...

    protected void lockView(boolean lock) {
        ((RootPane.DockingGlassPane)getGlassPane()).lockView(lock);
    }

    public void pushAnimation(Animation animation) {
        //System.out.println("push animation " + animation.getName());
        boolean start;
        if ((start = mAnimations.isEmpty()))
            lockView(true);

        mAnimations.push(animation);

        if ( start)
            SwingUtilities.invokeLater(animation);
    }

    public void finishedAnimation(Animation animation) {
        //System.out.println("finished animation " + animation.getName());
        mAnimations.remove(animation);

        if ( mAnimations.isEmpty())
            lockView(false);
        else
            ((Animation)mAnimations.get(0)).run();
    }

    // public

    public void setStatusBar(JComponent statusBar) {
        //Assertion.checkNull(mStatusBar, "statusbar already inserted");

        getLayeredPane().add(mStatusBar = statusBar, JLayeredPane.FRAME_CONTENT_LAYER);
        validate();
    }

    public JComponent getStatusBar() {
        return mStatusBar;
    }

    public void setToolBar(JComponent toolBar) {
        //Assertion.checkNull(mToolBar, "statusbar already inserted");

        getLayeredPane().add(mToolBar = toolBar, JLayeredPane.FRAME_CONTENT_LAYER);
        validate();
    }

    public JComponent getToolBar() {
        return mToolBar;
    }

    public DockingWindowBar getDockView(int direction) {
        return mDockViews[direction];
    }

    public void addDockView(DockingWindowBar view) {
        //Assertion.checkNull(mDockViews[view.getOrientation()], "dock already inserted");

        mDockViews[view.getOrientation()] = view;

        getLayeredPane().add(view, JLayeredPane.FRAME_CONTENT_LAYER);
    }

    public boolean isDocked(int direction) {
        return mDockables[direction] != null;
    }

     public ToolWindow getDockable(int direction) {
        return mDockables[direction];
    }

    public void newDockable(ToolWindow mDockable) {
        mAllDockables.add(mDockable);
    }

    public ToolWindow findDockable(String name) {
        for (int i = 0; i < mAllDockables.size(); i++) {
            ToolWindow mDockable = (ToolWindow) mAllDockables.get(i);

            if ( mDockable.getName().equals(name))
                return mDockable;
        } // for

        return null;
    }

    public ArrayList getDockables() {
        return mAllDockables;
    }

    public void addDockable(ToolWindow panel, int direction) {
        if (mDockables[direction] != null )
            mDockables[direction].minimizePanel();

        getLayeredPane().add(mDockables[direction] = panel, JLayeredPane.POPUP_LAYER);
        if (panel.isVisible())
            validate();
    }

    public void removeDockable(ToolWindow panel, int direction) {
        //Assertion.checkNotNull(mDockables[direction], "dockable not inserted");

        mDockables[direction] = null;

        panel.setVisible(false);
        getLayeredPane().remove(panel);
    }

    public void moveToFront(ToolWindow mToolWindow) {
        getLayeredPane().moveToFront(mToolWindow);
    }

    // override JFrame

    protected JRootPane createRootPane() {
        return new RootPane();
    }
}
