package org.flexdock.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.flexdock.docking.CursorProvider;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.defaults.DockableAdapter;
import org.flexdock.util.ResourceManager;

public class CursorDemo extends JPanel {
	private JLabel titlebar;
	private CursorProvider cursorProvider;
	private Dockable dockableImpl;

	public CursorDemo(String title) {
		super();
		titlebar = createTitlebar(" " + title);
		add(titlebar);
		setBorder(new LineBorder(Color.black));
		dockableImpl = new DockableImpl();
	}

	private JLabel createTitlebar(String title) {
		JLabel lbl = new JLabel(title);
		lbl.setForeground(Color.white);
		lbl.setBackground(Color.blue);
		lbl.setOpaque(true);
		return lbl;
	}

	public void doLayout() {
		Insets in = getInsets();
		titlebar.setBounds(in.left, in.top, getWidth() - in.left - in.right, 25);
	}
	
	private Dockable getDockable() {
		return dockableImpl;
	}

	
	
	private class DockableImpl extends DockableAdapter {
		public Component getDockable() {
			return CursorDemo.this;
		}

		public Component getInitiator() {
			// the titlebar will the the 'hot' component that initiates dragging
			return titlebar;
		}
	}
	
	
	
	
	
	

	private static JPanel createContentPane() {
		JPanel p = new JPanel(new BorderLayout(5, 5));
		p.add(buildDockingPort("North"), BorderLayout.NORTH);
		p.add(buildDockingPort("South"), BorderLayout.SOUTH);
		p.add(buildDockingPort("East"), BorderLayout.EAST);
		p.add(buildDockingPort("West"), BorderLayout.WEST);
		p.add(createDockingPort(), BorderLayout.CENTER);
		return p;
	}

	private static DefaultDockingPort buildDockingPort(String desc) {
		// create the DockingPort
		DefaultDockingPort port = createDockingPort();

		// create the Dockable panel
		CursorDemo cd = new CursorDemo(desc);
		DockingManager.registerDockable(cd.getDockable());
		// use a custom cursor provider for the north panel
//		if("North".equals(desc))
//			cd.setCursorProvider(new CursorDelegate());

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
		JFrame f = new JFrame("Custom Cursor Docking Demo");
		f.setContentPane(createContentPane());
		f.setSize(600, 400);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	private static class CursorDelegate implements CursorProvider {
		private static final Cursor NORTH_CURSOR = createCursor("tileHorizontal16.gif");
		private static final Cursor EAST_CURSOR = createCursor("tileVertical16.gif");
		private static final Cursor CENTER_CURSOR = createCursor("cascadeWindows16.gif");
		private static final Cursor BLOCKED_CURSOR = createCursor("closeAllWindows16.gif");
		
		private static Cursor createCursor(String imgName) {
			URL resource = CursorDemo.class.getResource(imgName);
			return ResourceManager.createCursor(resource, new Point(), null);
		}
		
		public Cursor getCenterCursor() {
			return CENTER_CURSOR;
		}

		public Cursor getDisallowedCursor() {
			return BLOCKED_CURSOR;
		}

		public Cursor getEastCursor() {
			return EAST_CURSOR;
		}

		public Cursor getNorthCursor() {
			return NORTH_CURSOR;
		}

		public Cursor getSouthCursor() {
			// same image as north
			return NORTH_CURSOR;
		}

		public Cursor getWestCursor() {
			// same image as east
			return EAST_CURSOR;
		}
	}
}
