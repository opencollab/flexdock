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
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class ToolWindow extends DockingWindow {
    // local classes

    class DragBorder extends JLabel implements MouseListener, MouseMotionListener, Border {
        // constants

        public static final int LEFT   = 0;
        public static final int RIGHT  = 1;
        public static final int TOP    = 2;

        // static

        private static final int BORDER_SIZE = 3;

        // instance data

        private DragBorder     mUpdateBorder;
        private int            mDirection;
        private Rectangle      mMouseLimits;
        private Cursor         mLastCursor;
        private Cursor         mDragCursor;
        private DockingWindow mPanel;
        private DockingFrame   mFrame;
        private int            mOffsetX;
        private int            mOffsetY;

        // constructor

        DragBorder(DockingFrame frame, DockingWindow panel, int direction) {
            mDirection = direction;

            setBorder(this);

            addMouseListener(this);
            addMouseMotionListener(this);

            mPanel = panel;
            mFrame = frame;

            String constraint = null;
            switch (direction) {
                case LEFT:
                    constraint = BorderLayout.WEST;
                    break;
                case RIGHT:
                    constraint = BorderLayout.EAST;
                    break;
                case TOP:
                    constraint = BorderLayout.NORTH;
                    break;
            } // switch

            mPanel.add(this, constraint);
        }

        // public

        public int getDirection() {
            return mDirection;
        }

        // implement Border

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
           Color background = Color.black;

            g.setColor(background);
            switch (mDirection) {
                case LEFT:
                    g.drawLine(x, y, x, y + height - 1);
                    break;

                case RIGHT:
                    g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
                    break;

                case TOP: {
                    ToolWindow panel;
                    int leftOffset  = ((panel = mFrame.getDockable(DockingFrame.LEFT))  != null && panel.isPinned()) ? panel.getWidth() : 0;
                    int rightOffset = ((panel = mFrame.getDockable(DockingFrame.RIGHT)) != null && panel.isPinned()) ? panel.getWidth() : 0;

                    g.drawLine(x + leftOffset -1, y, x + width - rightOffset, y); // x1, y1, x2, y2
                    break;
                }
            } // switch
        }


        public Insets getBorderInsets(Component c) {
            if (mDirection == BOTTOM )
                 return new Insets(BORDER_SIZE, 0, 0, 0); // top, left, bottom, right
            else
                return new Insets(0, BORDER_SIZE, 0, 0); // top, left, bottom, right
        }

        public boolean isBorderOpaque() {
            return false; // we don't draw the background
        }

        // private

        void pin(Point p, Rectangle limits) {
            // x

            if (p.x < limits.x)
                p.x = limits.x;

            else if (p.x > (limits.x + limits.width))
                p.x = limits.x + limits.width;

            // y

            if (p.y < limits.y)
                p.y = limits.y;

            else if (p.y > (limits.y + limits.height))
                p.y = limits.y + limits.height;
        }

        private Rectangle screenBounds(Component component) {
            Rectangle bounds = component.getBounds();
            Point p = bounds.getLocation();
            SwingUtilities.convertPointToScreen(p, component.getParent());
            bounds.setLocation(p);

            return bounds;
        }

        private void printRect(Rectangle r) {
            System.out.print(r.x + "@" + r.y + ", w: " + r.width + ", h: " + r.height);
        }

        void computeMouseLimits(Point p) {
            mMouseLimits   = screenBounds(mFrame.getContentPane());  // docking frame
            Rectangle area = screenBounds(mPanel);

            Dimension minSize = ToolWindow.this.getMinimumSize();

            //System.out.print("bounds: ");printRect(mMouseLimits);

            int right;

            switch (mDirection) {
                case RIGHT:
                    if ( isPinned()) { // include own space
                        mMouseLimits.x     -= ToolWindow.this.getWidth();
                        mMouseLimits.width += ToolWindow.this.getWidth();
                    } // if

                    mMouseLimits.x     += minSize.width;
                    mMouseLimits.width -= minSize.width;
                    mOffsetX = getWidth() - p.x;
                    mOffsetY = 0;
                    mDragCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
                    break;

                case LEFT:
                    right = area.x + area.width - minSize.width;
                    mMouseLimits.width = right - mMouseLimits.x;
                    mOffsetX = -p.x;
                    mOffsetY = 0;
                    mDragCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
                    break;

                case TOP:
                    if ( isPinned()) // include own space
                        mMouseLimits.height += ToolWindow.this.getHeight();

                    mMouseLimits.height -= minSize.height;
                    mOffsetX = 0;
                    mOffsetY = -p.y;
                    mDragCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
                    break;
            } // switch

            //System.out.print(" limited to: "); printRect(mMouseLimits);System.out.println();
        }

        // implement MouseListener, MouseMotionListener

        public void mousePressed(MouseEvent e) {
            computeMouseLimits(e.getPoint());

            if (e.getButton() == MouseEvent.BUTTON1)
                getSelectionHandler().setSelection(ToolWindow.this);

            if ( mFrame.getDockable(DockingFrame.BOTTOM) != null && isPinned())
                mUpdateBorder = mFrame.getDockable(DockingFrame.BOTTOM).mDragBorder;
        }

        public void mouseDragged(MouseEvent e) {
            Point p = (Point) e.getPoint().clone();

            p.x += mOffsetX;
            p.y += mOffsetY;

            SwingUtilities.convertPointToScreen(p, this); // in place

            //System.out.print("clicked " + p.x + "@" + p.y);

            pin(p, mMouseLimits);

            //System.out.println(" pinned " + p.x + "@" + p.y);

            SwingUtilities.convertPointFromScreen(p, mPanel.getParent()); // in place

            Rectangle bounds = mPanel.getBounds();

            switch (mDirection) {
                case LEFT:
                    mPanel.setBounds(p.x, bounds.y, bounds.x + bounds.width - p.x, bounds.height);
                    break;

                case RIGHT:
                    mPanel.setBounds(bounds.x, bounds.y, p.x - bounds.x, bounds.height);
                    break;

                case TOP:
                    mPanel.setBounds(bounds.x, p.y, bounds.width, bounds.y + bounds.height - p.y); // x, y, w, h
                    break;
            } // switch

            setCursor(mDragCursor);

            //if ( isPinned())
            mPanel.validate();
                mFrame.validate();
            //else
            //    mPanel.validate();

            if ( mUpdateBorder != null)
                mUpdateBorder.repaint();
        }

        public void mouseReleased(MouseEvent e) {
            mUpdateBorder = null;
        }


        public void mouseMoved(MouseEvent e) {
            switch ( mDirection ) {
                case LEFT:
                case RIGHT:
                    setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                    break;

                case TOP:
                    setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                    break;
            } // switch
        }

        public void mouseEntered(MouseEvent e) {
            mLastCursor = getCursor();
        }

        public void mouseExited(MouseEvent e) {
            setCursor(mLastCursor);
        }

        // defaults

        public void mouseClicked(MouseEvent e) {
        }
    } // class DragBorder

    // constants

    private static long ANIMATION_TIME = 200;
    private static int ANIMATION_INTERVAL = 15;

    // local classes

    abstract class Animator implements ActionListener, DockingFrame.Animation {
        // instance data

        private ToolWindow mPanel;
        private int   mRestore;
        private Timer mTimer;
        private long  mStartTime;
        private int   mStart;
        private int   mEnd;
        private int   mOrientation;
        private String mName;

        // constructor

        Animator(String name, ToolWindow panel, int orientation, int start, int end, int restore) {
            mPanel       = panel;
            mRestore     = restore;
            mOrientation = orientation;
            mStart       = start;
            mEnd         = end;
            mName = name;

            setSize(start);
        } // if

        public String getName() {
            return mName;
        }

        private void setSize(int size) {
            Rectangle bounds = mPanel.getBounds();
            int offset;
            switch (mOrientation) {
                case DockingFrame.BOTTOM:
                    offset = size - bounds.height;
                    bounds.height = size;
                    bounds.y -= offset;
                    break;

                case DockingFrame.RIGHT:
                    offset = size - bounds.width;
                    bounds.width = size;
                    bounds.x -= offset;
                    break;

                case DockingFrame.LEFT:
                    bounds.width = size;
                    break;
            } // switch

            mPanel.setBounds(bounds);
            if (isPinned())
                mFrame.validate();
            else
                mPanel.validate();
        }

        public void run() {
            //System.out.println("run action " + mName);
            mStartTime   = System.currentTimeMillis();
            (mTimer = new Timer(ANIMATION_INTERVAL, this)).start();
            mPanel.setVisible(true);
        }

        // public

        public void actionPerformed(ActionEvent e) {
            long now = System.currentTimeMillis();
            if (now - mStartTime < ANIMATION_TIME) {
                float percentage = (float)((now - mStartTime)) / ANIMATION_TIME;

                setSize((int)(mStart + (mEnd - mStart) * percentage));
                mFrame.validate();
            } // if
            else { // done...
                mTimer.stop();
                mTimer = null;

                setSize(mRestore);
                mFrame.validate();

                finished(); // callback

                mFrame.finishedAnimation(this);
            } // else
        }

        abstract void finished();
    };

    // instance data

    private DragBorder   mDragBorder;
    private DockingWindowBar     mDockingWindowBar;
    private DockingFrame mFrame;
    private boolean      mPinned = false;

    // constructor

    public ToolWindow(DockingFrame frame, DockingWindowBar dockingWindowBar, String name, String title, Icon icon, Component component) {
        super(title, icon, component);

        (mFrame = frame).newDockable(this); // add to global list

        setName(name);

        (mDockingWindowBar = dockingWindowBar).dock(this);
        setMinimized(true);

        int border = 0;

        switch (mDockingWindowBar.getOrientation()) {
            case DockingFrame.LEFT:
                border = DragBorder.RIGHT;
                break;

            case DockingFrame.RIGHT:
                border = DragBorder.LEFT;
                break;

            case DockingFrame.BOTTOM:
                border = DragBorder.TOP;
                break;

        } // switch

        setDragBorder(new DragBorder(frame, this, border));

        setSize(getPreferredSize());
    }

    public void updateBorder() {
        if (mDragBorder != null)
            mDragBorder.repaint();
    }

    protected void setupHeader() {
        // pin / unpin

        JButton button = mHeader.addButton("pin", isPinned() ? PINNED_ICON : UNPINNED_ICON, isPinned() ? "unpin" : "pin");

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isPinned())
                    unpinPanel();
                else
                    pinPanel();
            }
        });

        // dock / float

        button = mHeader.addButton("dock", isFloating() ? FIX_ICON : FLOATING_ICON, isFloating() ? "dock" : "float");

        final JButton button2 = button;
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isFloating()) {
                    dockPanel();
                    button2.setIcon(FLOATING_ICON);
                    button2.setToolTipText("float");
                } // if
                else {
                    floatPanel();
                    button2.setIcon(FIX_ICON);
                    button2.setToolTipText("dock");
                } // else
            }
        });

        // minimize

        mHeader.addButton("minimize", MINIMIZE_ICON, "minimize").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                minimizePanel();
            }
        });
    }

    void setDragBorder(DragBorder border) {
        add(mDragBorder = border);
    }

     protected class DockablePanelLayout extends PanelLayout {
        // implement

        public void layoutContainer(Container parent) {
            Rectangle bounds = getBounds();

            int w = bounds.width;
            int h = bounds.height;
            int x = 0;
            int y = 0;
            int size;

            // border

            if ( mDragBorder != null && mDragBorder.isVisible()) {
                switch ( mDragBorder.getDirection()) {
                    case DragBorder.LEFT:
                        mDragBorder.setBounds(x, y, size = mDragBorder.getPreferredSize().width, h); // x, y, w, h
                        x += size;
                        w -= size;
                        break;

                    case DragBorder.RIGHT:
                        size = mDragBorder.getPreferredSize().width;
                        mDragBorder.setBounds(w - size, y, size, h); // x, y, w, h
                        w -= size;
                        break;

                    case DragBorder.TOP:
                        mDragBorder.setBounds(x, y, w, size = DragBorder.BORDER_SIZE/*mDragBorder.getPreferredSize().height*/); // x, y, w, h
                        y += size;
                        h -= size;
                        break;
                } // switch
            } // if

            // header

            if (mHeader != null) {
                mHeader.setBounds(x, y, w, mHeader.getPreferredSize().height);
                y += mHeader.getPreferredSize().height;
                h -= mHeader.getPreferredSize().height;
            } // if

            // view

            if (mView != null)
                mView.setBounds(x, y, w, h);
        }


        public Dimension minimumLayoutSize(Container parent) {
            int w = 0;
            int h = 0;

            if ( mDragBorder != null) {
                w = Math.max(w, mDragBorder.getMinimumSize().width);
                h = Math.max(h, mDragBorder.getMinimumSize().height);
            } // if

            if ( mHeader != null) {
                w = Math.max(w, mHeader.getMinimumSize().width);
                h = Math.max(h, mHeader.getMinimumSize().height);
            } // if

             if ( mView != null) {
                w = Math.max(w, mView.getMinimumSize().width);
                h = Math.max(h, mView.getMinimumSize().height);
            } // if

            return new Dimension(w, h);
        }

        public Dimension preferredLayoutSize(Container parent) {
            int w = 0;
            int h = 0;

            if ( mDragBorder != null) {
                w = Math.max(w, mDragBorder.getPreferredSize().width);
                h = Math.max(h, mDragBorder.getPreferredSize().height);
            } // if

            if ( mHeader != null) {
                w = Math.max(w, mHeader.getPreferredSize().width);
                h = Math.max(h, mHeader.getPreferredSize().height);
            } // if

             if ( mView != null) {
                w = Math.max(w, mView.getPreferredSize().width);
                h = Math.max(h, mView.getPreferredSize().height);
            } // if

            return new Dimension(w, h);
        }

        // defaults

        public void removeLayoutComponent(Component comp) {
        }

        public void addLayoutComponent(String name, Component comp) {
        }
    } // class DockablePanelLayout

    protected LayoutManager makeLayout() {
        return new DockablePanelLayout();
    }

    // public

    public boolean isPinned() {
        return mPinned;
    }

    public void setPinned(boolean pinned) {
        JButton button =  mHeader.findButton("pin");

        if (mPinned = pinned) {
            button.setIcon(PINNED_ICON);
            button.setToolTipText("unpin");
        } // if
        else {
            button.setIcon(UNPINNED_ICON);
            button.setToolTipText("pin");
        } // else

        mFrame.validate();
    }

    protected void pinPanel() {
        setPinned(true);

        mFrame.validate();
    }

    //private static boolean sSilent = false;

    protected void unpinPanel() {
        setPinned(false);

        //sSilent = true;
        //mFrame.moveToFront(this); // make sure it is the topmost panel!
        //sSilent = false;

        mFrame.validate();
    }

    public DockingWindowBar getDockView() {
        return mDockingWindowBar;
    }

    // override

    // called by DockView

    public void minimizePanel() {
        super.minimizePanel(); // hides floating window...

        if ( mFrame.isDocked(mDockingWindowBar.getOrientation()))
            mFrame.removeDockable(this, mDockingWindowBar.getOrientation());

         if ( mFrame.isDocked(DockingFrame.BOTTOM))
            mFrame.getDockable(DockingFrame.BOTTOM).updateBorder();

        mDockingWindowBar.dock(this);

        setSelected(false);
    }

    public void dockPanel() {
       // unfloat

        if ( isFloating()) {
            mFloatingWindow.setVisible(false);

            addHeader(mFloatingWindow.removeTitleBar());
            mHeader.enableDrag(false);

            setFloating(false);
        } // if

        int finalSize = 0;

        // TODO maximum = panel-größe
        switch (mDockingWindowBar.getOrientation()) {
            case DockingFrame.BOTTOM:
                finalSize = getHeight();
                break;

            case DockingFrame.LEFT:
            case DockingFrame.RIGHT:
                finalSize = getWidth();
                break;
        } // switch

        finalSize = Math.max(finalSize, 10);

        //setVisible(true);

        mDragBorder.setVisible(true);
        mHeader.findButton("pin").setVisible(true);

        int direction;
        for (direction = 0; direction < 3; direction++)
            if (mFrame.isDocked(direction) && !mFrame.getDockable(direction).isPinned())
                break;

        if (direction < 3) { // remove visible _unpinned_ dockable first
            final ToolWindow mOldPanel = mFrame.getDockable(direction);

            final int finalSize1 = finalSize;
            int oldSize = direction == DockingFrame.BOTTOM ? mOldPanel.getHeight() : mOldPanel.getWidth();
            mFrame.pushAnimation(new Animator("mininized pinned", mOldPanel, direction, oldSize, 0, oldSize) {
                void finished() {
                    mOldPanel.minimizePanel(); // calls removeDockable
                }
            });

            mFrame.pushAnimation(new Animator("dock new ", ToolWindow.this, mDockingWindowBar.getOrientation(), 0, finalSize1, finalSize1) {
                public void run() {
                    // is there a pinned dockable?

                    if (mFrame.isDocked(mDockingWindowBar.getOrientation()))
                        mFrame.getDockable(mDockingWindowBar.getOrientation()).minimizePanel();

                    // dock myself

                    mFrame.addDockable(ToolWindow.this, mDockingWindowBar.getOrientation()); // will layout!

                    super.run();
                }

                void finished() {
                    setMinimized(false);
                    setDocked(true);

                    requestFocus();
                    validate();
                }
            });
        }
        else {
            // is there a pinned dockable?

            if ( mFrame.isDocked(mDockingWindowBar.getOrientation()))
                mFrame.getDockable(mDockingWindowBar.getOrientation()).minimizePanel();

            // dock myself

            mFrame.addDockable(this, mDockingWindowBar.getOrientation()); // will layout!

            mFrame.pushAnimation(new Animator("dock new", this, mDockingWindowBar.getOrientation(), 0, finalSize, finalSize) {
                void finished() {
                    setMinimized(false);
                    setDocked(true);

                    requestFocus();
                    validate();
                }
            });
        } // else
    }

    public void floatPanel() {
        getFloatingWindow(mFrame, true); // create on demand

        if ( mFrame.isDocked(mDockingWindowBar.getOrientation()))
            mFrame.removeDockable(this, mDockingWindowBar.getOrientation());

        if ( mFrame.isDocked(DockingFrame.BOTTOM))
            mFrame.getDockable(DockingFrame.BOTTOM).updateBorder();

        mDragBorder.setVisible(false);
        mHeader.findButton("pin").setVisible(false);

        super.floatPanel();
    }


    protected void setSelected(boolean selected) {
        if ( selected != mSelected) {
            super.setSelected(selected);

            // minimize?

            if (isDocked() && !selected && !isPinned()) {
                int size = mDockingWindowBar.getOrientation() == DockingFrame.BOTTOM ? getHeight() : getWidth();
                mFrame.pushAnimation(new Animator("minimize selection", this, mDockingWindowBar.getOrientation(), size, 0, size) {
                    void finished() {
                        minimizePanel();
                    }
                });
            }
        }
    }
}