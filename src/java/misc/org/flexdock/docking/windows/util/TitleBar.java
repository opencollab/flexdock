package org.flexdock.docking.windows.util;

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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class TitleBar extends JPanel implements MouseListener, MouseMotionListener {
    // constants

    private static final Color GRAD_MID   = new Color(168, 203, 239);
    private static final Color GRAD_START = new Color(10, 36, 106);

    private static final Color GRAY_GRAD_MID   = Color.lightGray;
    private static final Color GRAY_GRAD_START = Color.darkGray;

    // local classes

    private static class Title extends JLabel {
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            Object renderingHint = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            super.paintComponent(g);

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, renderingHint);
        }
    } // class Title

    // instance data

    protected JLabel  mTitleLabel;
    protected boolean mDragEnabled = false;
    private Point     mCursor;
    private boolean   mSelected = false;

    // constructor

    public TitleBar() {
        initialize();
    }

    // public

    public void setSelected(boolean selected) {
        if (selected != mSelected) {
            mSelected = selected;

            repaint();
        } // if
    }

    public boolean isSelected() {
        return mSelected;
    }

    public JButton addButton(String name, ImageIcon icon, String tooltip) {
        JButton button = new JButton(icon);

        button.setToolTipText(tooltip);

        button.setName(name);
        button.setOpaque(false);
        button.setRequestFocusEnabled(false);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(1, 1, 1, 1));

        add(button);

        return button;
    }

    public void removeButton(JButton button) {
        remove(button);
    }

    public JButton findButton(String name) {
        for (int i = 0; i < getComponentCount(); i++) {
            Component component = getComponent(i);
            if (component instanceof JButton && ((JButton) component).getName().equals(name))
                return (JButton) component;
        } // for

        return null;
    }

    // private

    private void initialize() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(isSelected() ? GRAD_MID : GRAY_GRAD_MID);

        setBorder(BorderFactory.createEmptyBorder());

        add(Box.createHorizontalStrut(5));
        add(mTitleLabel = new Title());

        mTitleLabel.setForeground(isSelected() ? Color.white : Color.white);

        add(Box.createHorizontalGlue());

        // activated in any case!

        addMouseListener(this);
    }

    // public

    public void setTitle(String title) {
        mTitleLabel.setText(title);
    }

    public String getTitle() {
        return mTitleLabel.getText();
    }

    public void setIcon(Icon icon) {
        mTitleLabel.setIcon(icon);
    }

    public Icon getIcon() {
        return mTitleLabel.getIcon();
    }

    public void enableDrag(boolean enable) {
        if (enable != mDragEnabled) {
            if (enable)
                addMouseMotionListener(this);
            else
                removeMouseMotionListener(this);

            mDragEnabled = enable;
        } // if
    }

    // override

    public void mousePressed(MouseEvent e) {
        mCursor = (Point) e.getPoint().clone();

        SwingUtilities.convertPointToScreen(mCursor, this);
    }


    public void mouseDragged(MouseEvent e) {
        Point p = (Point) e.getPoint().clone();

        SwingUtilities.convertPointToScreen(p, this);

        double offsetX = p.getX() - mCursor.getX();
        double offsetY = p.getY() - mCursor.getY();

        Window window = SwingUtilities.getWindowAncestor(this);

        window.setLocation(window.getLocation().x + (int) offsetX, window.getLocation().y + (int) offsetY);

        mCursor = p;
    }

    public void mouseReleased(MouseEvent e) {
        mCursor = null;
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    // override

    public void paintComponent(Graphics g) {
        Insets in = getInsets();
        int mid = getWidth() / 2;
        int y = in.top + 13;
        int farRight = getWidth() - in.right;
        int w = farRight - in.left;

        GradientPaint firstHalf = new GradientPaint(in.left, y, isSelected() ? GRAD_START : GRAY_GRAD_START, mid, y, isSelected() ? GRAD_MID : GRAY_GRAD_MID);
        GradientPaint secondHalf = new GradientPaint(mid, y, isSelected() ? GRAD_MID : GRAY_GRAD_MID, farRight, y, getBackground());

        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(firstHalf);
        g2.fillRect(in.left, in.top, w / 2, 25);
        g2.setPaint(secondHalf);
        g2.fillRect(mid, in.top, w - w / 2, 25);
    }
}