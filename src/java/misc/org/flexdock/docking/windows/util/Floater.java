package org.flexdock.docking.windows.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

public class Floater extends JDialog {
    // constants

    private static int CORNER_MARGIN = 5;

    // local classes

    class RootPane extends JRootPane implements MouseListener, MouseMotionListener, Border {
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

                // title

                if (mTitleBar != null && mTitleBar.isVisible()) {
                    preferredWidth = Math.max(preferredWidth, mTitleBar.getPreferredSize().width);
                    preferredHeight = Math.max(preferredHeight, mTitleBar.getPreferredSize().height);
                } // if

                // menu

                if (menuBar != null && menuBar.isVisible()) {
                    preferredWidth = Math.max(preferredWidth, menuBar.getPreferredSize().width);
                    preferredHeight = Math.max(preferredHeight, menuBar.getPreferredSize().height);
                } // if

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

                // title

                if (mTitleBar != null && mTitleBar.isVisible()) {
                    minimumWidth = Math.max(minimumWidth, mTitleBar.getMinimumSize().width);
                    minimumHeight = Math.max(minimumHeight, mTitleBar.getMinimumSize().height);
                } // if

                // menu

                if (menuBar != null && menuBar.isVisible()) {
                    minimumWidth = Math.max(minimumWidth, menuBar.getMinimumSize().width);
                    minimumHeight = Math.max(minimumHeight, menuBar.getMinimumSize().height);
                } // if

                // done

                return new Dimension(minimumWidth + insets.left + insets.right,
                        minimumHeight + insets.top + insets.bottom);
            }

            public Dimension maximumLayoutSize(Container target) {
                Dimension rd, mbd;
                Insets i = getInsets();
                if (menuBar != null && menuBar.isVisible()) {
                    mbd = menuBar.getMaximumSize();
                }
                else {
                    mbd = new Dimension(0, 0);
                }

                if (contentPane != null) {
                    rd = contentPane.getMaximumSize();
                }
                else {
                    // This is silly, but should stop an overflow error
                    rd = new Dimension(Integer.MAX_VALUE,
                            Integer.MAX_VALUE - i.top - i.bottom - mbd.height - 1);
                }

                return new Dimension(Math.min(rd.width, mbd.width) + i.left + i.right,
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

                int contentY = 0;

                // title

                if (mTitleBar != null && mTitleBar.isVisible()) {
                    Dimension preferredSize = mTitleBar.getPreferredSize();
                    mTitleBar.setBounds(0, contentY, w, preferredSize.height);
                    contentY += preferredSize.height;
                } // if

                // menu bar

                if (menuBar != null && menuBar.isVisible()) {
                    Dimension preferredSize = menuBar.getPreferredSize();
                    menuBar.setBounds(0, contentY, w, preferredSize.height); // x, y, w, h
                    contentY += preferredSize.height;
                } // if

                if (contentPane != null)
                    contentPane.setBounds(0, contentY, w, h - contentY); // x, y, w, h
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

        // instance data

        private JComponent mTitleBar;
        private int mRegion;
        private Rectangle mMouseLimits;
        private Cursor mLastCursor;
        private int mOffsetX = 0;
        private int mOffsetY = 0;

        // constructor

        RootPane() {
            setBorder(this);

            addMouseListener(this);
            addMouseMotionListener(this);
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

        void computeMouseLimits(int region, Point p) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            mMouseLimits = new Rectangle(0, 0, screenSize.width, screenSize.height);

            Rectangle area = Floater.this.getBounds(); // in screen-coordinates

            Dimension minSize = getLayout().minimumLayoutSize(getParent());

            int minWidth = minSize.width;
            int minHeight = minSize.height;

            int bottom;
            int right;

            switch (region) {
                case Cursor.NW_RESIZE_CURSOR:
                    right = (area.x + area.width) - minWidth;
                    bottom = (area.y + area.height) - minHeight;

                    mMouseLimits.width = right - mMouseLimits.x;
                    mMouseLimits.height = bottom - mMouseLimits.y;
                    mOffsetY = -p.y;
                    mOffsetX = -p.x;
                    break;

                case Cursor.N_RESIZE_CURSOR:
                    bottom = area.y + area.height - minHeight;
                    mMouseLimits.height = bottom - mMouseLimits.y;
                    mOffsetY = -p.y;
                    mOffsetX = 0;
                    break;

                case Cursor.E_RESIZE_CURSOR:
                    mMouseLimits.x = area.x + minWidth;
                    mOffsetX = getWidth() - p.x;
                    mOffsetY = 0;
                    break;

                case Cursor.SE_RESIZE_CURSOR:
                    mMouseLimits.y = area.y + minHeight;
                    mMouseLimits.x = area.x + minWidth;
                    mOffsetY = getHeight() - p.y;
                    mOffsetX = getWidth() - p.x;
                    break;

                case Cursor.S_RESIZE_CURSOR:
                    mMouseLimits.y = area.y + minHeight;
                    mOffsetY = getHeight() - p.y;
                    mOffsetX = 0;
                    break;

                case Cursor.W_RESIZE_CURSOR:
                    right = area.x + area.width - minWidth;
                    mMouseLimits.width = right - mMouseLimits.x;
                    mOffsetX = -p.x;
                    mOffsetY = 0;
                    break;

                case Cursor.NE_RESIZE_CURSOR:
                    mMouseLimits.x = area.x + minWidth;
                    bottom = area.y + area.height - minHeight;
                    mMouseLimits.height = bottom - mMouseLimits.y;
                    mOffsetY = -p.y;
                    mOffsetX = getWidth() - p.x;
                    break;

                case Cursor.SW_RESIZE_CURSOR:
                    right = area.x + area.width - minWidth;
                    mMouseLimits.y = area.y + minHeight;
                    mMouseLimits.width = right - mMouseLimits.x;
                    mOffsetY = getHeight() - p.y;
                    mOffsetX = -p.x;
                    break;
            } // switch
        }

        private int getCursor(Point p) {
            Insets insets = getInsets();

            // left

            if (p.x <= insets.left) {
                if (p.y <= CORNER_MARGIN)
                    return Cursor.NW_RESIZE_CURSOR;
                else if (p.y >= getHeight() - CORNER_MARGIN)
                    return Cursor.SW_RESIZE_CURSOR;
                else
                    return Cursor.W_RESIZE_CURSOR;
            } // if

            // right

            else if (p.x >= getWidth() - insets.right) {
                if (p.y <= CORNER_MARGIN)
                    return Cursor.NE_RESIZE_CURSOR;
                else if (p.y >= getHeight() - CORNER_MARGIN)
                    return Cursor.SE_RESIZE_CURSOR;
                else
                    return Cursor.E_RESIZE_CURSOR;
            } // if

            // top

            else if (p.y <= insets.top) {
                if (p.x <= CORNER_MARGIN)
                    return Cursor.NW_RESIZE_CURSOR;
                else if (p.x >= getWidth() - CORNER_MARGIN)
                    return Cursor.NE_RESIZE_CURSOR;
                else
                    return Cursor.N_RESIZE_CURSOR;
            } // if

            // bottom

            else if (p.y >= getHeight() - insets.bottom) {
                if (p.x <= CORNER_MARGIN)
                    return Cursor.SW_RESIZE_CURSOR;
                else if (p.x >= getWidth() - CORNER_MARGIN)
                    return Cursor.SE_RESIZE_CURSOR;
                else
                    return Cursor.S_RESIZE_CURSOR;
            } // if

            else
                return Cursor.DEFAULT_CURSOR;
        }

        // implement Border

        public boolean isBorderOpaque() {
            return false;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Color background = getBackground();
            Color darker = getBackground().darker();
            Color evenDarker = darker.darker();
            Color brighter = getBackground().brighter();

            g.setColor(background);

            // brighter top

            g.drawLine(x, y, x + width, y);
            g.drawLine(x, y + 2, x + width, y + 2);
            g.setColor(brighter);
            g.drawLine(x + 1, y + 1, x + width - 1, y + 1);

            // left

            g.setColor(background);
            g.drawLine(x, y + 1, x, y + height);
            g.drawLine(x + 2, y + 2, x + 2, y + height);

            g.setColor(brighter);
            g.drawLine(x + 1, y + 2, x + 1, y + height);

            // bottom

            g.setColor(background);
            g.drawLine(x + 2, y + height - 3, x + width - 4, y + height - 3);
            g.setColor(darker);
            g.drawLine(x + 1, y + height - 2, x + width - 2, y + height - 2);
            g.setColor(evenDarker);
            g.drawLine(x, y + height - 1, x + width, y + height - 1);

            // right

            g.setColor(background);
            g.drawLine(x + width - 3, y + 2, x + width - 3, y + height - 4);
            g.setColor(darker);
            g.drawLine(x + width - 2, y + 1, x + width - 2, y + height - 2);
            g.setColor(evenDarker);
            g.drawLine(x + width - 1, y, x + width - 1, y + height);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(3, 3, 3, 3);
        }

        // implement MouseListener, MouseMotionListener

        public void mousePressed(MouseEvent e) {
            mRegion = getCursor(e.getPoint());

            computeMouseLimits(mRegion, e.getPoint());
        }

        public void mouseDragged(MouseEvent e) {
            if (mRegion != 0) {
                Point p = (Point) e.getPoint().clone();

                p.x += mOffsetX;
                p.y += mOffsetY;

                SwingUtilities.convertPointToScreen(p, this);

                pin(p, mMouseLimits);

                Rectangle bounds = Floater.this.getBounds();

                switch (mRegion) {
                    case Cursor.NW_RESIZE_CURSOR:
                        Floater.this.setBounds(p.x, p.y, bounds.width + bounds.x - p.x, bounds.height + bounds.y - p.y);
                        break;

                    case Cursor.N_RESIZE_CURSOR:
                        Floater.this.setBounds(bounds.x, p.y, bounds.width, bounds.height + bounds.y - p.y);
                        break;

                    case Cursor.NE_RESIZE_CURSOR:
                        Floater.this.setBounds(bounds.x, p.y, p.x - bounds.x, bounds.height + bounds.y - p.y);
                        break;

                    case Cursor.W_RESIZE_CURSOR:
                        Floater.this.setBounds(p.x, bounds.y, bounds.x + bounds.width - p.x, bounds.height);
                        break;

                    case Cursor.E_RESIZE_CURSOR:
                        Floater.this.setBounds(bounds.x, bounds.y, p.x - bounds.x, bounds.height);
                        break;

                    case Cursor.SW_RESIZE_CURSOR:
                        Floater.this.setBounds(p.x, bounds.y, bounds.width + bounds.x - p.x, p.y - bounds.y);
                        break;

                    case Cursor.S_RESIZE_CURSOR:
                        Floater.this.setBounds(bounds.x, bounds.y, bounds.width, p.y - bounds.y);
                        break;

                    case Cursor.SE_RESIZE_CURSOR:
                        Floater.this.setBounds(bounds.x, bounds.y, p.x - bounds.x, p.y - bounds.y);
                        break;
                } // switch

                setCursor(Cursor.getPredefinedCursor(mRegion));

                Floater.this.validate();
            } // if
        }

        public void mouseReleased(MouseEvent e) {
            mRegion = 0;
        }

        public void mouseMoved(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(getCursor(e.getPoint())));
        }

        public void mouseEntered(MouseEvent e) {
            if (mRegion == 0) // no dragging on!
                mLastCursor = getCursor();
        }

        public void mouseExited(MouseEvent e) {
            setCursor(mLastCursor);
        }

        // defaults

        public void mouseClicked(MouseEvent e) {
        }

        // public

        public void setTitleBar(JComponent titleBar) {
            layeredPane.add(mTitleBar = titleBar, JLayeredPane.FRAME_CONTENT_LAYER);
        }

        public JComponent removeTitleBar() {
            layeredPane.remove(mTitleBar);

            JComponent titleBar = mTitleBar;

            mTitleBar = null;

            return titleBar;
        }

        protected LayoutManager createRootLayout() {
            return new RootPane.RootLayout();
        }
    } // class RootPane

    // instance data

    // constructor

    public Floater(Frame owner, String title, boolean modal) {
        super(owner, title, modal);

        initialize();
    }

    public Floater(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);

        initialize();
    }

    // public

    public void setJMenuBar(JMenuBar menubar) {
        getRootPane().setJMenuBar(menubar);
    }

    public JMenuBar getJMenuBar() {
        return getRootPane().getJMenuBar();
    }

    public void setTitleBar(JComponent titleBar) {
        ((RootPane) getRootPane()).setTitleBar(titleBar);
    }

    public JComponent removeTitleBar() {
        return ((RootPane) getRootPane()).removeTitleBar();
    }

    // private

    private void initialize() {
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
    }

    // override

    protected JRootPane createRootPane() {
        return new RootPane();
    }
}