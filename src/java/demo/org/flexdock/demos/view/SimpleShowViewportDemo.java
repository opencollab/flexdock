/*
 * Created on Mar 4, 2005
 */
package org.flexdock.demos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.flexdock.docking.DockingPort;
import org.flexdock.util.ResourceManager;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;
import org.flexdock.view.restore.ViewDockingInfo;
import org.flexdock.view.restore.ViewManager;

import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;

/**
 * @author Christopher Butler
 * @author Mateusz Szczap
 */
public class SimpleShowViewportDemo extends JFrame {

	private static View view1 = null;
	private static View view2 = null;
	private static View view3 = null;
	private static View view4 = null;

	public static void main(String[] args) throws Exception {
		Skin theSkinToUse = SkinLookAndFeel.loadThemePack("themepack.zip");
        SkinLookAndFeel.setSkin(theSkinToUse);
        
//		http://dev.l2fprod.com/javadoc/com/l2fprod/gui/plaf/skin/SkinLookAndFeel.html
		SwingUtility.setPlaf("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
//		SwingUtility.setPlaf("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		SwingUtility.setPlaf("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//		SwingUtility.setPlaf("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		
		JFrame f = new SimpleShowViewportDemo();
		f.setSize(800, 600);
		SwingUtility.centerOnScreen(f);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	public SimpleShowViewportDemo() {
		super("Simple Show Viewport Demo");
		setContentPane(createContentPane());
		setJMenuBar(createApplicationMenuBar());
	}	
	
	private JPanel createContentPane() {
		JPanel panel = new JPanel(new BorderLayout(0, 0));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		Viewport viewport = new Viewport();
		ViewManager.getInstance().registerCenterViewport(viewport);
		panel.add(viewport, BorderLayout.CENTER);
		
		View startPage = createStartPage();
		
		view1 = createView("solution.explorer", "Solution Explorer");
		view2 = createView("task.list", "Task List");
		view3 = createView("class.view", "Class View");
		view4 = createView("message.log", "Message Log");

		ViewManager.getInstance().registerTerritoralView(startPage);

		ViewManager.getInstance().registerViewDockingInfo(view1.getPersistentId(), ViewDockingInfo.createRelativeDockingInfo(startPage, DockingPort.WEST_REGION, .3f));
		ViewManager.getInstance().registerViewDockingInfo(view2.getPersistentId(), ViewDockingInfo.createRelativeDockingInfo(startPage, DockingPort.SOUTH_REGION, .3f));
		ViewManager.getInstance().registerViewDockingInfo(view3.getPersistentId(), ViewDockingInfo.createRelativeDockingInfo(view1, DockingPort.EAST_REGION, .3f));
		ViewManager.getInstance().registerViewDockingInfo(view4.getPersistentId(), ViewDockingInfo.createRelativeDockingInfo(startPage, DockingPort.EAST_REGION, .3f));
		
		viewport.dock(startPage);
		startPage.dock(view1, DockingPort.WEST_REGION, .3f);
		startPage.dock(view2, DockingPort.SOUTH_REGION, .3f);
		startPage.dock(view4, DockingPort.EAST_REGION, .3f);
		view1.dock(view3, DockingPort.SOUTH_REGION, .3f);
		
		return panel;
	}
	
	private View createView(String id, String text) {
		View view = new View(id, text);
		view.addAction(new CloseAction(view));
		view.addAction(new MaximizeViewAction(view));
		view.addAction(new UnmaximizeViewAction(view));
		
		JPanel p = new JPanel();
		p.setBorder(new LineBorder(Color.GRAY, 1));
		
		JTextField t = new JTextField(text);
		t.setPreferredSize(new Dimension(100, 20));
		p.add(t);
		view.setContentPane(p);

		return view;
	}
	
	private JMenuBar createApplicationMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu showViewMenu = new JMenu("Show View");
		
		showViewMenu.add(createShowViewActionFor(view1));
		showViewMenu.add(createShowViewActionFor(view2));
		showViewMenu.add(createShowViewActionFor(view3));
		showViewMenu.add(createShowViewActionFor(view4));

		menuBar.add(showViewMenu);
		
		return menuBar;
	}

	private Action createShowViewActionFor(View commonView) {
		ShowViewAction showViewAction = new ShowViewAction(commonView);
		showViewAction.putValue(Action.NAME, commonView.getTitle());
		
		return showViewAction;
	}
	
	private class ShowViewAction extends AbstractAction {
		
		private View m_commonView = null;
		
		private ShowViewAction(View commonView) {
			m_commonView = commonView;
		}
		
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			ViewManager.getInstance().showView(m_commonView);
		}

	}
	
	private static View createStartPage() {
		String name = "Start Page";
		String id = "start.page";
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
				g.drawRect(0, 0, getWidth()-1, getHeight()-1);
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
				int iconY = (tabLowerY)/2 - miscIcons.getIconHeight()/2 + 1; 
				miscIcons.paintIcon(this, g, iconX, iconY);

				g.setColor(orig);
				super.paintComponent(g);
				
				g.setColor(Color.WHITE);
				g.drawRect(0, 0, getWidth()-1, getHeight()-1);
				g.drawRect(1, tabLowerY-1, getWidth()-3, getHeight()-tabLowerY-1);
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
		
		return view;
	}

	private class CloseAction extends AbstractAction {

		private View m_view = null;
		
		private CloseAction(View view) {
			m_view = view;
			putValue(Action.NAME, "close");
			putValue(Action.SHORT_DESCRIPTION, "Close");
			putValue(Action.ACTION_COMMAND_KEY, "close");
		}

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
        	ViewManager.getInstance().hideView(m_view);
        }
		
	}

	private class MaximizeViewAction extends AbstractAction {

		private View m_view = null;

		private MaximizeViewAction(View view) {
			m_view = view;
			putValue(Action.NAME, "maximize");
			putValue(Action.SHORT_DESCRIPTION, "Maximize");
			putValue(Action.ACTION_COMMAND_KEY, "maximize");
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			ViewManager.getInstance().maximizeView(m_view);
		}
		
	}

	private class UnmaximizeViewAction extends AbstractAction {

		private View m_view = null;

		private UnmaximizeViewAction(View view) {
			m_view = view;
			putValue(Action.NAME, "unmaximize");
			putValue(Action.SHORT_DESCRIPTION, "Unmaximize");
			putValue(Action.ACTION_COMMAND_KEY, "unmaximize");
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			ViewManager.getInstance().unmaximizeView(m_view);
		}
		
	}

}
