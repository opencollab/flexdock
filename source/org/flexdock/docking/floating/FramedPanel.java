/* Copyright (c) 2004 Christopher M Butler

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
package org.flexdock.docking.floating;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventListener;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class FramedPanel extends JPanel {
	private JDesktopPane desktopPane;
	private FrameViewDelegate iFrame;
	private JComponent titlebarComponent;

	public FramedPanel() {
		// create a desktop pane and add it to the content pane
		desktopPane = new JDesktopPane();
		super.add(desktopPane);

		// create an internal frame and add it to the desktop pane.
		// we'll be using the internal frame's titlebar in lieu of our
		// 'real' titlebar, since we have much more control over its 
		// window decorations.
		iFrame = new FrameViewDelegate();
		desktopPane.add(iFrame);

		// set some default properties on the internal frame, just because we're
		// supposed to
		iFrame.setLocation(0, 0);
		iFrame.setSize(5, 5);

		// maximize the internal frame so we can't see the actual root container
		// behind it.  the internal frame will now look just like a top-level
		// frame, but with our own custom window decorations.
		iFrame.setMaximum(true);

		// initialize event handlers such that the internal frame's titlebar will
		// be treated as a drag source for the root frame itself.
		initEventHandlers();

		// create the content pane
		setContentPane(new JPanel());

		// now we can show the internal frame.  of course, this won't visually
		// take effect for the end-user until the root frame has been shown.		
		iFrame.setVisible(true);
	}

	private void initEventHandlers() {
		configureTitlebarListeners();

		// undecorated frames aren't resizable, so we'll have to implement
		// this feature on our own
		ResizeManager resizeMgr = new ResizeManager();
		iFrame.addMouseListener(resizeMgr);
		iFrame.addMouseMotionListener(resizeMgr);

		// ensure that closing the iFrame cascades out to the containing window
		iFrame.addInternalFrameListener(new FrameCloseHandler());
	}

	private void configureTitlebarListeners() {
		// reset the titlebar reference to make sure we have the latest copy
		titlebarComponent = null;
		titlebarComponent = getTitlebar();
		configureTitlebarListeners(titlebarComponent);
	}
	
	private void configureTitlebarListeners(JComponent titlebar) {
		// check the existing listeners so we don't end up re-adding them if 
		// they already exist on the titlebar
		boolean addMouseListener = true;
		boolean addMouseMotionListener = true;
		EventListener[] mouseListeners = titlebar.getListeners(MouseListener.class);
		EventListener[] motionListeners = titlebar.getListeners(MouseMotionListener.class);

		// look for MouseListeners
		for (int i = 0; i < mouseListeners.length; i++) {
			if (mouseListeners[i] instanceof TitlebarMouseListener) {
				addMouseListener = false;
				break;
			}
		}
		// look for MouseMotionListeners
		for (int i = 0; i < motionListeners.length; i++) {
			if (motionListeners[i] instanceof TitlebarMouseListener) {
				addMouseMotionListener = false;
				break;
			}
		}

		// if we need to add listeners to the titlebar, go ahead and do it here.
		if (addMouseListener || addMouseMotionListener) {
			// setup the titlebar to act as the drag source for the root frame
			TitlebarMouseListener tml = new TitlebarMouseListener();
			if (addMouseListener)
				titlebar.addMouseMotionListener(tml);
			if (addMouseMotionListener)
				titlebar.addMouseListener(tml);
		}
	}

	private JComponent getTitlebar() {
		// retrieve and hold a reference to the internal frame's titlebar.
		// this will be useful to us
		if (titlebarComponent == null) {
			BasicInternalFrameUI ui = (BasicInternalFrameUI) iFrame.getUI();
			titlebarComponent = ui.getNorthPane();

			// if the titlebar is still null, then we may be on OSX.  Use some reflection
			// to try to create the titlebar.  Bug reported and patch submitted by 
			// Hani Suleiman on 03-14-2004.
			if (titlebarComponent == null) {
				try {
					Method m = ui.getClass().getDeclaredMethod("createNorthPane", new Class[] { JInternalFrame.class });
					// playing with the accessibility here makes me (Chris) a little nervous.  
					// I will try to put in a more elegant solution later.
					m.setAccessible(true);
					titlebarComponent = (JComponent) m.invoke(ui, new Object[] { iFrame });
					if(titlebarComponent!=null) {
						configureTitlebarListeners(titlebarComponent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} // end of OSX patch.
		}
		return titlebarComponent;
	}

	public Component add(Component comp, int index) {
		return null;
	}

	public void add(Component comp, Object constraints, int index) {
	}

	public void add(Component comp, Object constraints) {
	}

	public Component add(Component comp) {
		return null;
	}

	public Component add(String name, Component comp) {
		return null;
	}

	public void doLayout() {
		desktopPane.setBounds(0, 0, getWidth(), getHeight());
	}

	public void setTitle(String title) {
		JComponent titlebar = getTitlebar();
		if (titlebar instanceof BasicInternalFrameTitlePane)
			iFrame.setTitle(title);
		else
			setTitlebarText(title);
	}

	public String getTitle() {
		return iFrame.getTitle();
	}

	private void setTitlebarText(String txt) {
		JComponent titlebar = getTitlebar();
		if (titlebar == null)
			return;

		try {
			Class clazz = titlebar.getClass();
			Method m = clazz.getMethod("setText", new Class[] { String.class });
			m.invoke(titlebar, new Object[] { txt });
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
	}

	public Container getContentPane() {
		return iFrame.getContentPane();
	}

	public int getDefaultCloseOperation() {
		return iFrame.getDefaultCloseOperation();
	}

	public JLayeredPane getLayeredPane() {
		return iFrame.getLayeredPane();
	}

	public JRootPane getRootPane() {
		return iFrame.getRootPane();
	}

	public void setContentPane(Container contentPane) {
		iFrame.setContentPane(contentPane);
	}

	public void setDefaultCloseOperation(int operation) {
		iFrame.setDefaultCloseOperation(operation);
	}

	public void setLayeredPane(JLayeredPane layeredPane) {
		iFrame.setLayeredPane(layeredPane);
	}

	public void setCloseable(boolean b) {
		iFrame.setClosable(b);
	}

	public void setMaximizable(boolean b) {
		iFrame.setMaximizable(b);
	}

	public void addWindowListener(WindowListener wl) {
		// remember to implement this method.
	}

	public void setTitlebar(JComponent comp) {
		String title = getTitle();
		iFrame.setTitlebar(comp);
		setTitle(title);
	}

	private boolean isCurrentlyFloating() {
		RootPaneContainer root = (RootPaneContainer) SwingUtilities.getAncestorOfClass(RootPaneContainer.class, this);
		return root == null ? false : root.getContentPane() == this;
	}

	private Container getWindow() {
		return SwingUtilities.getWindowAncestor(this);
	}

	private RootPaneContainer getRootPaneContainer() {
		return (RootPaneContainer) SwingUtilities.getAncestorOfClass(RootPaneContainer.class, this);
	}

	private class FrameViewDelegate extends JInternalFrame {
		private boolean rootPaneCheckingDisabled;

		public void setMaximum(boolean b) {
			try {
				// don't let them unmaximize
				super.setMaximum(true);
			} catch (PropertyVetoException ignored) {
			}
		}

		public void setSelected(boolean b) {
			try {
				super.setSelected(b);
			} catch (PropertyVetoException ignored) {
			}
		}

		public void setVisible(boolean b) {
			super.setVisible(b);
			if (b)
				ensureSelected();
		}

		private void ensureSelected() {
			// make sure we're selected so we display the correct titlebar
			setSelected(true);

			// due to threading issues (namely, uncertainty about the order in which
			// events may have been added to the EventDispatch thread), the selection
			// code we have above may or may not have taken effect.  if it hasn't, then
			// we'll just keep re-selecting until it does take effect.			
			final Thread t = new Thread() {
				public void run() {
					Runnable r = new Runnable() {
						public void run() {
							if (!isSelected())
								ensureSelected();
						}
					};
					EventQueue.invokeLater(r);
				}
			};
			t.start();
		}

		private void setTitlebar(JComponent comp) {
			rootPaneCheckingDisabled = true;
			try {
				BasicInternalFrameUI ui = (BasicInternalFrameUI) getUI();
				ui.setNorthPane(comp);
				configureTitlebarListeners();
			} finally {
				rootPaneCheckingDisabled = false;
			}

		}

		protected boolean isRootPaneCheckingEnabled() {
			if (rootPaneCheckingDisabled)
				return false;

			return super.isRootPaneCheckingEnabled();
		}

	}

	private class TitlebarMouseListener extends MouseAdapter implements MouseMotionListener {
		private Point mouseOffsetFromRoot;

		public void mouseMoved(MouseEvent e) {
			// do nothing
		}

		public void mouseDragged(MouseEvent e) {
			if (isCurrentlyFloating()) {
				// when then titlebar is dragged, we need to move the root frame 
				// around the screen to make it appear that the internal frame titlebar
				// is actually the root frame's titlebar.
				Point evtLoc = e.getPoint();
				SwingUtilities.convertPointToScreen(evtLoc, (Component) e.getSource());
				getWindow().setLocation(evtLoc.x - mouseOffsetFromRoot.x, evtLoc.y - mouseOffsetFromRoot.y);
			}
		}

		public void mousePressed(MouseEvent e) {
			if (isCurrentlyFloating()) {
				// keep track of the mouse offset from the window location
				Component src = (Component) e.getSource();
				mouseOffsetFromRoot = SwingUtilities.convertPoint(src, e.getPoint(), getWindow());
			}
		}
	}

	private class ResizeManager extends MouseAdapter implements MouseMotionListener {
		private int cursorRegion = Cursor.DEFAULT_CURSOR;
		private static final int INSET = 4;
		private static final int MIN_WIDTH = 75;
		private boolean dragging;
		private int southMouseInset;
		private int easeMouseInset;

		public void mouseDragged(MouseEvent e) {
			if (isCurrentlyFloating())
				handleResize(e);
		}

		public void mouseEntered(MouseEvent e) {
			if (isCurrentlyFloating() && !dragging)
				determineCursorRegion(e);
		}

		public void mouseReleased(MouseEvent e) {
			if (isCurrentlyFloating())
				dragging = false;
		}

		public void mousePressed(MouseEvent e) {
			if (isCurrentlyFloating()) {
				dragging = true;
				Point mouse = SwingUtilities.convertPoint(iFrame, e.getPoint(), getWindow());
				southMouseInset = getHeight() - mouse.y;
				easeMouseInset = getWidth() - mouse.x;
			}
		}

		public void mouseMoved(MouseEvent e) {
			if (isCurrentlyFloating() && !dragging)
				determineCursorRegion(e);
		}

		private void determineCursorRegion(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			int w = getWidth();
			int h = getHeight();

			if (x > w - INSET) {
				if (y > h - INSET)
					cursorRegion = Cursor.SE_RESIZE_CURSOR;
				else
					cursorRegion = Cursor.E_RESIZE_CURSOR;
			} else if (y > h - INSET) {
				cursorRegion = Cursor.S_RESIZE_CURSOR;
			} else
				cursorRegion = Cursor.DEFAULT_CURSOR;

			iFrame.setCursor(Cursor.getPredefinedCursor(cursorRegion));
		}

		private void handleResize(MouseEvent e) {
			if (!isValidResizeRegion())
				return;

			RootPaneContainer root = getRootPaneContainer();
			Window window = (Window) root;
			Point mouse = SwingUtilities.convertPoint(iFrame, e.getPoint(), window);

			// determine the new width
			int w = cursorRegion == Cursor.S_RESIZE_CURSOR ? window.getWidth() : Math.max(mouse.x + easeMouseInset, MIN_WIDTH);

			// determine the new height
			int h = cursorRegion == Cursor.E_RESIZE_CURSOR ? window.getHeight() : Math.max(mouse.y + southMouseInset, getMinHeight());

			// resize the root window
			window.setSize(w, h);

			// since we're in an undecorated state, the root window isn't expecting
			// resizing, and it won't automatically resize and revalidate the root
			// pane.  we're going to have to do it manually here.
			root.getRootPane().setSize(window.getSize());
			root.getRootPane().revalidate();
		}

		private boolean isValidResizeRegion() {
			return cursorRegion == Cursor.E_RESIZE_CURSOR || cursorRegion == Cursor.SE_RESIZE_CURSOR || cursorRegion == Cursor.S_RESIZE_CURSOR;
		}

		private int getMinHeight() {
			JComponent titlebar = getTitlebar();
			Point loc = titlebar.getLocation();
			loc = SwingUtilities.convertPoint(titlebar.getParent(), loc, getWindow());
			return loc.y + titlebar.getHeight() + 3;
		}
	}

	private class FrameCloseHandler extends InternalFrameAdapter {
		public void internalFrameClosing(InternalFrameEvent e) {
			if (!isCurrentlyFloating())
				return;

			Container win = getWindow();
			win.setVisible(false);
			super.internalFrameClosing(e);
			if (win instanceof Window)
				 ((Window) win).dispose();
		}
	}
}
