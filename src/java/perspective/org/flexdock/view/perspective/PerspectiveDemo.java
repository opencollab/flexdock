/*
 * Created on Mar 24, 2005
 */
package org.flexdock.view.perspective;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
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

/**
 * @author Mateusz Szczap
 */
public class PerspectiveDemo extends JFrame {

	private View startPage = null;
	private View solutionExplorerView = null;
	private View taskListView = null;
	private View classViewView = null;
	
	private IPerspective perspective1 = null;
	private IPerspective perspective2 = null;

	public static void main(String[] args) {
		SwingUtility.setPlaf("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		
		JFrame f = new PerspectiveDemo();
		f.setSize(800, 600);
		SwingUtility.centerOnScreen(f);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	public PerspectiveDemo() {
		super("Perspective Demo");
		setContentPane(createContentPane());
	}	
	
	private class B1ActionHandler implements ActionListener {

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			IPerspectiveManager perspectiveRegistry = PerspectiveManager.getInstance();
			perspectiveRegistry.applyPerspective(perspective1);
		}
	}

	private class B2ActionHandler implements ActionListener {

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			IPerspectiveManager perspectiveRegistry = PerspectiveManager.getInstance();
			perspectiveRegistry.applyPerspective(perspective2);
		}
	}

	private JPanel createContentPane() {
		JPanel p = new JPanel(new BorderLayout(0, 0));
		p.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		Viewport mainViewPort = new Viewport("main.port");
		this.startPage = createStartPage();
		
		this.solutionExplorerView = createView("solution.explorer", "Solution Explorer");
		this.taskListView = createView("task.list", "Task List");
		this.classViewView = createView("class.view", "Class View");

		this.perspective1 = createPerspective1(mainViewPort, startPage);
		this.perspective2 = createPerspective2(mainViewPort, startPage);

		p.add(mainViewPort, BorderLayout.CENTER);
		p.add(createSouthPanel(), BorderLayout.SOUTH);
		
		return p;
	}

	private JPanel createSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		
		JButton b1 = new JButton("Perspective1");
		JButton b2 = new JButton("Perspective2");
		
		b1.addActionListener(new B1ActionHandler());
		b2.addActionListener(new B2ActionHandler());
		
		panel.add(b1);
		panel.add(b2);
		
		return panel;
	}
	
	private View createView(String id, String text) {
		View view = new View(id, text);
		view.addAction(createAction("close", "Close"));
		view.addAction(createAction("pin", "Pin"));
		
		JPanel p = new JPanel();
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
	
	private IPerspective createPerspective1(Viewport viewport, View centerView) {
		IPerspective perspective = new Perspective("test1");
		
		perspective.addView("start.page", centerView);
		perspective.addView("solution.explorer", solutionExplorerView);
//		perspective.addView("task.list", taskListView);
//		perspective.addView("class.view", classViewView);
		
		perspective.setMainViewport(viewport);
		perspective.setTerritoralView(centerView);
		
		perspective.dock("start.page", "solution.explorer", DockingPort.WEST_REGION, .3f);
		//perspective.dock("solution.explorer", "task.list", DockingPort.CENTER_REGION, -1.0f);
//		perspective.dock("start.page", "class.view", DockingPort.EAST_REGION, .3f);
		
		return perspective;
	}

	private IPerspective createPerspective2(Viewport viewport, View centerView) {
		IPerspective perspective = new Perspective("test2");

		perspective.setMainViewport(viewport);
		perspective.setTerritoralView(centerView);

		perspective.addView("start.page", centerView);
//		perspective.addView("solution.explorer", this.solutionExplorerView);
		perspective.addView("task.list", this.taskListView);
		perspective.addView("class.view", this.classViewView);
		
//		perspective.dock("start.page", "solution.explorer", DockingPort.SOUTH_REGION, .3f);
//		perspective.dock("start.page", "task.list", DockingPort.WEST_REGION, .3f);
		perspective.dock("start.page", "class.view", DockingPort.EAST_REGION, .3f);
		
		return perspective;
	}
	
	private static View createStartPage() {
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
	
}