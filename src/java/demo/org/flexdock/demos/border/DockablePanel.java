package org.flexdock.demos.border;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DockableAdapter;

public class DockablePanel extends JPanel {
	private String title;
	private JPanel dragInit;
	private Dockable dockableImpl;

	public DockablePanel(String title) {
		super(new BorderLayout());
		dragInit = new JPanel();
		dragInit.setBackground(getBackground().darker());
		dragInit.setPreferredSize(new Dimension(10, 10));
		add(dragInit, BorderLayout.EAST);
		setBorder(new TitledBorder(title));
		setTitle(title);
		dockableImpl = new DockableImpl();
		DockingManager.registerDockable(dockableImpl);
	}

	private void setTitle(String title) {
		this.title = title;
	}

	Dockable getDockable() {
		return dockableImpl;
	}

	private class DockableImpl extends DockableAdapter {
		private DockableImpl() {
			super();
			// the titlebar will the the 'hot' component that initiates dragging
			getDragSources().add(dragInit);
		}
		
		public Component getDockable() {
			return DockablePanel.this;
		}
	}
}
