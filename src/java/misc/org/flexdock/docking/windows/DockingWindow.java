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

import org.flexdock.docking.windows.util.Floater;
import org.flexdock.docking.windows.util.TitleBar;
import org.flexdock.util.SwingUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DockingWindow extends JPanel {
    // constants

    protected static final ImageIcon FLOATING_ICON  = new ImageIcon(DockingWindow.class.getResource("floating.png"));
    protected static final ImageIcon FIX_ICON       = new ImageIcon(DockingWindow.class.getResource("fix.png"));
    protected static final ImageIcon MINIMIZE_ICON  = new ImageIcon(DockingWindow.class.getResource("hideToolWindow.png"));
    protected static final ImageIcon PINNED_ICON    = new ImageIcon(DockingWindow.class.getResource("pinned.png"));
    protected static final ImageIcon UNPINNED_ICON  = new ImageIcon(DockingWindow.class.getResource("unpinned.png"));
    protected static final ImageIcon CLOSE_ICON     = new ImageIcon(DockingWindow.class.getResource("close.png"));
    protected static final ImageIcon SEPARATOR_ICON = new ImageIcon(DockingWindow.class.getResource("separator.png"));

    // local classes

    protected class PanelLayout implements LayoutManager {
        // implement

        public void layoutContainer(Container parent) {
            Rectangle bounds = getBounds();

            int y = 0;
            // header

            if (mHeader != null) {
                mHeader.setBounds(0, 0, bounds.width, y = mHeader.getPreferredSize().height);
            } // if

            // view

            if (mView != null) {
                mView.setBounds(0, y, bounds.width, bounds.height - y);
            } // if
        }

        public Dimension minimumLayoutSize(Container parent) {
            int w = 0;
            int h = 0;

            if (mHeader != null) {
                w = Math.max(w, mHeader.getMinimumSize().width);
                h = Math.max(h, mHeader.getMinimumSize().height);
            } // if

            if (mView != null) {
                w = Math.max(w, mView.getMinimumSize().width);
                h = Math.max(h, mView.getMinimumSize().height);
            } // if

            return new Dimension(w, h);
        }

        public Dimension preferredLayoutSize(Container parent) {
            int w = 0;
            int h = 0;

            if (mHeader != null) {
                w = Math.max(w, mHeader.getPreferredSize().width);
                h = Math.max(h, mHeader.getPreferredSize().height);
            } // if

            if (mView != null) {
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
    } // class PanelLayout


    public class FloatingWindow extends Floater implements WindowListener {
        // constructor

        public FloatingWindow(Frame owner, String title, boolean modal) {
            super(owner, title, modal);

            addWindowListener(this);
        }

        public FloatingWindow(Dialog owner, String title, boolean modal) {
            super(owner, title, modal);

            addWindowListener(this);
        }

        // implement WindowListener

         public void windowActivated(WindowEvent e) {
            getSelectionHandler().setSelection(DockingWindow.this);
        }


        public void windowClosing(WindowEvent e) {}
        public void windowClosed(WindowEvent e) {}
        public void windowDeactivated(WindowEvent e) {}
        public void windowDeiconified(WindowEvent e) {}
        public void windowIconified(WindowEvent e) {}
        public void windowOpened(WindowEvent e) {}

        // override

        public Component add(DockingWindow panel) {
            Header header = (Header) panel.removeHeader();

            setTitleBar(header);
            header.enableDrag(true);

            getContentPane().add(panel, BorderLayout.CENTER);

            panel.setVisible(true);

            return panel;
        }
    } // class FloatingWindow

    // class Header

    class Header extends TitleBar {
        // instance data

        // constructor

        public Header() {
        }

        // public

        public JButton addButton(String name, ImageIcon icon, String tooltip) {
            JButton button = super.addButton(name, icon, tooltip);

            button.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1)
                        getSelectionHandler().setSelection(DockingWindow.this);
                }
            });

            return button;
        }

        // override

        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);

            if ( e.getButton() == MouseEvent.BUTTON1)
                getSelectionHandler().setSelection(DockingWindow.this);

            super.mousePressed(e);
        }
    }

    static class SelectionHandler implements PropertyChangeListener {
        // instance data

        private DockingWindow mSelection;

        // constructor

        SelectionHandler() {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", this);
        }

        // public


        public void setSelection(DockingWindow panel) {
            if (panel != null && panel != mSelection) {
                if (mSelection != null)
                    mSelection.setSelected(false);

                if ((mSelection = panel) != null)
                    mSelection.setSelected(true);
            } // if
        }

        // implement PropertyChangeListener

        public void propertyChange(PropertyChangeEvent e) {
            Component selection = (Component) e.getNewValue();
            while ( selection != null && !(selection instanceof DockingWindow))
                selection = selection.getParent();

            if ( selection != null)
                setSelection((DockingWindow) selection); // only FloatablePanels!
        }
    }

    // static data

    static SelectionHandler sSelectionHandler;

    // static

    static SelectionHandler getSelectionHandler() {
        if ( sSelectionHandler == null)
            sSelectionHandler = new SelectionHandler();

        return sSelectionHandler;
    }

    // instance data

    protected boolean mMinimized = false;
    protected boolean mFloating  = false;
    protected boolean mDocked    = false;
    protected FloatingWindow mFloatingWindow;
    protected Header    mHeader;
    protected Component mView;
    protected boolean   mSelected = false;
    protected Rectangle mFloatingHints = null;

    // constructor

    public DockingWindow(String title, Icon icon, Component component) {
        init(mView = component);

        setTitle(title);
        if ( icon != null)
            setIcon(icon);

        getSelectionHandler(); // instanciate
    }

    // public

    public void setFloatingHints(Rectangle floatingHints) {
        mFloatingHints = floatingHints;
    }

    public void setTitle(String title) {
        mHeader.setTitle(title);
    }

    public String getTitle() {
        return mHeader.getTitle();
    }

    public void setIcon(Icon icon) {
        mHeader.setIcon(icon);
    }

    public Icon getIcon() {
        return mHeader.getIcon();
    }

    // protected

    protected void setSelected(boolean selected) {
        if (selected != mSelected)
            mHeader.setSelected(mSelected = selected);
    }

    protected boolean isSelected() {
        return mSelected;
    }

    protected LayoutManager makeLayout() {
        return new PanelLayout();
    }

    protected void addHeader(Component header) {
        add(mHeader = (Header) header);
        invalidate();
    }

     protected Component removeHeader() {
         mHeader.getParent().remove(mHeader);
         Component header = mHeader;
         //mHeader = null;

         return header;
    }

    protected void addView(Component component) {
        add(mView = component);
        invalidate();
    }

    // private

    private void init(Component component) {
        // border

        setBorder(BorderFactory.createEmptyBorder());

        // layout

        setLayout(makeLayout());
        addHeader(new Header());
        addView(component);

        setupHeader();
    }

    protected void setupHeader() {
        // dock / float

        final JButton dockableButton = mHeader.addButton("dock", isFloating() ? FIX_ICON : FLOATING_ICON, isFloating() ? "dock" : "float");

        dockableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isFloating()) {
                    dockPanel();
                    dockableButton.setIcon(FLOATING_ICON);
                    dockableButton.setToolTipText("float");
                } // if
                else {
                    floatPanel();
                    dockableButton.setIcon(FIX_ICON);
                    dockableButton.setToolTipText("dock");
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

    protected FloatingWindow createFloatingWindow(Window window, DockingWindow panel) {
        FloatingWindow floatingWindow;

        if (window instanceof Frame)
            floatingWindow = new FloatingWindow((Frame) window, panel.getName(), false);

        else if (window instanceof Dialog)
            floatingWindow = new FloatingWindow((Dialog) window, panel.getName(), false);

        else
            floatingWindow = new FloatingWindow((Frame) null, panel.getName(), false);

        floatingWindow.setTitle(panel.getTitle());

        if ( mFloatingHints != null)
            floatingWindow.setBounds(mFloatingHints);

        else {
            floatingWindow.setSize(panel.getSize());

            SwingUtility.center(floatingWindow, window);
        } // else

        floatingWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dockPanel();
            }
        });

        return floatingWindow;
    }

    public FloatingWindow getFloatingWindow(Window window, boolean create) {
        if (mFloatingWindow == null && create)
            mFloatingWindow = createFloatingWindow(window, this);

        return (FloatingWindow)mFloatingWindow;
    }

    public void setFloating(boolean floating) {
        mFloating = floating;
    }

    public boolean isFloating() {
        return mFloating;
    }

    public void setMinimized(boolean minimized) {
        mMinimized = minimized;
    }

    public boolean isMinimized() {
        return mMinimized;
    }

    public void setDocked(boolean docked) {
        mDocked = docked;
    }

    public boolean isDocked() {
        return mDocked;
    }

    // abstract

    public void minimizePanel() {
        // unfloat
        
        if ( isFloating()) {
            mFloatingWindow.setVisible(false);

            addHeader(mFloatingWindow.removeTitleBar());
            mHeader.enableDrag(false);
        } // if

        setMinimized(true);
    }

    public void maximizePanel() {
        if ( isFloating())
            floatPanel();
        else
            dockPanel();

        setMinimized(false);
    }

    public void dockPanel() {
        // unfloat

        if ( isFloating()) {
            mFloatingWindow.setVisible(false);

            addHeader(mFloatingWindow.removeTitleBar());
            mHeader.enableDrag(false);

            setFloating(false);
        } // if

        setMinimized(false);
        setDocked(true);
    }

    public void floatPanel() {
        FloatingWindow floatingWindow = getFloatingWindow(SwingUtilities.getWindowAncestor(this), true); // create on demand

        floatingWindow.add(this); // removes header

        floatingWindow.validate(); // whatever...
        ((Window) floatingWindow).show();

        setFloating(true);
        setMinimized(false);
        setDocked(false);
    }
}