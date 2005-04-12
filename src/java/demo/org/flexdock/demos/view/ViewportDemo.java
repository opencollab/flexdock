/*
 * Created on Mar 4, 2005
 */
package org.flexdock.demos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.util.ResourceManager;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * @author Christopher Butler
 */
public class ViewportDemo extends JFrame {
	private JDialog siblingTestDialog;
	
	public static void main(String[] args) {
		SwingUtility.setPlaf("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		//		SwingUtility.setPlaf("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
		//		SwingUtility.setPlaf("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

		JFrame f = new ViewportDemo();
		f.setSize(800, 600);
		SwingUtility.centerOnScreen(f);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	public ViewportDemo() {
		super("Viewport Demo");
		setContentPane(createContentPane());
	}

	private JPanel createContentPane() {
		JPanel p = new JPanel(new BorderLayout(0, 0));
		p.setBorder(new EmptyBorder(5, 5, 5, 5));

		Viewport viewport = new Viewport();
		p.add(viewport, BorderLayout.CENTER);

		View startPage = createStartPage();
		View view1 = createView("solution.explorer", "Solution Explorer");
		View view2 = createView("task.list", "Task List");
		View view3 = createView("class.view", "Class View");
		View view4 = createView("message.log", "Message Log");

		viewport.dock(startPage);
		startPage.dock(view1, DockingPort.WEST_REGION, .3f);
		startPage.dock(view2, DockingPort.SOUTH_REGION, .3f);
		startPage.dock(view4, DockingPort.EAST_REGION, .3f);
		view1.dock(view3, DockingPort.SOUTH_REGION, .3f);

		return p;
	}

	private View createView(String id, String text) {
		View view = new View(id, text);
		view.addAction(createAction("close", "Close"));
		view.addAction(createAction("pin", "Pin"));

		JPanel p = new JPanel();
		//		p.setBackground(Color.WHITE);
		p.setBorder(new LineBorder(Color.GRAY, 1));

		JTextField t = new JTextField(text);
		t.setPreferredSize(new Dimension(100, 20));
		p.add(t);

		view.setContentPane(p);
		return view;
	}

	private Action createAction(String name, String tooltip) {
		Action a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
			}
		};
		a.putValue(Action.NAME, name);
		a.putValue(Action.SHORT_DESCRIPTION, tooltip);
		return a;
	}
	
	private JDialog getSiblingTestDialog() {
		if(siblingTestDialog==null) {
			siblingTestDialog = new JDialog(this, "Sibling Test");
			siblingTestDialog.setContentPane(new SiblingTestPanel());
		}
		return siblingTestDialog;
	}

	private View createStartPage() {
		String name = "Start Page";
		String id = "startPage";
		final Icon miscIcons = ResourceManager.createIcon("org/flexdock/demos/view/ms_misc_icons001.png");
		final Image tabsImg = ResourceManager.createImage("org/flexdock/demos/view/ms_tabs001.png");
		final Color tabRunBG = new Color(247, 243, 233);
		final Color contentBG1 = new Color(246, 246, 246);
		final Color contentBG2 = new Color(102, 153, 204);
		final Color tableBG1 = new Color(154, 154, 143);
		final Font labelFont = new Font("Dialog", Font.BOLD, 11);
		final JPanel page = new JPanel(new BorderLayout(0, 0));

		final JButton button1 = new JButton("New Project");
		final JButton button2 = new JButton("Open Project");

		final JPanel table = new JPanel() {
			protected void paintComponent(Graphics g) {
				g.setColor(tableBG1);
				g.fillRect(0, 0, getWidth(), 20);
				g.setColor(page.getBackground());
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				g.setColor(Color.BLACK);
				g.setFont(labelFont);
				g.drawString("Name", 5, 15);
				g.drawString("Modified", 350, 15);
			}
		};

		JPanel content = new JPanel(null) {
			public void doLayout() {
				int tableH = getHeight() - 120 - 55;
				tableH = Math.max(tableH, 25);
				table.setBounds(12, 120, 475, tableH);

				int buttonY = 120 + tableH + 18;
				Dimension d = button1.getPreferredSize();
				button1.setBounds(12, buttonY, d.width, d.height);
				button2.setBounds(24 + d.width, buttonY, button2.getPreferredSize().width, d.height);
			}

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				int w = getWidth();

				Color origC = g.getColor();
				Font origF = g.getFont();

				g.setColor(Color.WHITE);
				g.fillRect(0, 0, w, getHeight());

				g.setColor(contentBG1);
				g.fillRect(0, 0, w, 48);
				g.setColor(contentBG2);
				g.fillRect(0, 48, w, 23);
				g.drawImage(tabsImg, 0, 0, null, this);

				g.setColor(Color.BLACK);
				g.setFont(labelFont);
				g.drawString("Open an Existing Project", 12, 100);

				g.setFont(origF);
				g.setColor(origC);
			}
		};
		content.add(table);
		content.add(button1);
		content.add(button2);

		JTabbedPane tabPane = new JTabbedPane(SwingConstants.TOP) {
			protected void paintComponent(Graphics g) {
				Color orig = g.getColor();
				Rectangle tabBounds = getBoundsAt(0);
				int tabLowerY = tabBounds.y + tabBounds.height;

				g.setColor(tabRunBG);
				g.fillRect(0, 0, getWidth(), tabLowerY);

				int iconX = getWidth() - miscIcons.getIconWidth();
				int iconY = (tabLowerY) / 2 - miscIcons.getIconHeight() / 2 + 1;
				miscIcons.paintIcon(this, g, iconX, iconY);

				g.setColor(orig);
				super.paintComponent(g);

				g.setColor(Color.WHITE);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				g.drawRect(1, tabLowerY - 1, getWidth() - 3, getHeight() - tabLowerY - 1);
				g.setColor(orig);
			}
		};
		tabPane.addTab(name, content);
		tabPane.setBorder(null);
		page.add(tabPane, BorderLayout.CENTER);
		page.setBorder(new LineBorder(Color.GRAY, 1));

		View view = new View(id, null, null);
		view.setTerritoryBlocked(DockingPort.CENTER_REGION, true);
		view.setTitlebar(null);
		view.setContentPane(page);

		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				JDialog dialog = getSiblingTestDialog();
				if(dialog.isVisible())
					return;
				
				dialog.setVisible(true);
				dialog.pack();
				testSiblings();
				
				/*
				String id = null;
				id = "startPage";
				id = "solution.explorer";
				//				id = "task.list";
				//				id = "class.view";
				//				id = "message.log";
				Dockable dockable = DockingManager.getRegisteredDockable(id);

				Dockable sibling = null;
				sibling = dockable.getSibling(DockingPort.EAST_REGION);
				System.out.println(sibling);
				*/
			}
		});

		return view;
	}
	
	private void testSiblings() {
		SiblingTestPanel panel = (SiblingTestPanel)getSiblingTestDialog().getContentPane();
		panel.sync();
	}

	


	private class SiblingTestPanel extends JPanel {
		private JComboBox dockableList;
		private JComboBox regionList;
		private JLabel siblingLabel;
		
		private SiblingTestPanel() {
			init();
		}

		private JComboBox getDockableList() {
			if (dockableList != null)
				return dockableList;

			ArrayList list = new ArrayList(DockingManager.getDockableIds());
			Collections.sort(list);
			String[] dockableIds = (String[]) list.toArray(new String[0]);
			dockableList = new JComboBox(dockableIds);
			return dockableList;
		}

		private JComboBox getRegionList() {
			if (regionList != null)
				return regionList;

			String[] regions = { DockingPort.NORTH_REGION, DockingPort.SOUTH_REGION, DockingPort.EAST_REGION, DockingPort.WEST_REGION };
			regionList = new JComboBox(regions);
			return regionList;
		}
		
		private JLabel getSiblingLabel() {
			if(siblingLabel==null)
				siblingLabel = new JLabel();
			return siblingLabel;
		}
		

		private void init() {
			setLayout(new GridBagLayout());
			setBorder(new EmptyBorder(5, 5, 5, 10));
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(6, 6, 0, 0);
			gbc.gridx = GridBagConstraints.RELATIVE;
			gbc.gridy = 0;

			add(new JLabel("Dockable:"), gbc);
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(getDockableList(), gbc);

			gbc.gridy++;
			gbc.gridwidth = 1;
			add(new JLabel("Region:"), gbc);
			add(getRegionList(), gbc);

			gbc.gridy++;
			gbc.gridwidth = 1;
			add(new JLabel("Sibling:"), gbc);
			add(getSiblingLabel(), gbc);
			
			ItemListener syncher = new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					sync();
				}
			};
			getDockableList().addItemListener(syncher);
			getRegionList().addItemListener(syncher);
		}
		
		public void sync() {
			String viewId = (String)getDockableList().getSelectedItem();
			String region = (String)getRegionList().getSelectedItem();
			
			Dockable dockable = DockingManager.getRegisteredDockable(viewId);
			Dockable sibling = dockable.getSibling(region);
			getSiblingLabel().setText(sibling==null? "null": sibling.toString());
		}
	}

}