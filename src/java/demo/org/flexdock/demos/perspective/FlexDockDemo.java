package org.flexdock.demos.perspective;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.flexdock.docking.DockingPort;
import org.flexdock.util.DockingConstants;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;
import org.flexdock.view.perspective.IPerspective;
import org.flexdock.view.perspective.IPerspectiveManager;
import org.flexdock.view.perspective.Perspective;
import org.flexdock.view.perspective.PerspectiveManager;
import org.flexdock.view.restore.IViewManager;
import org.flexdock.view.restore.ViewDockingInfo;
import org.flexdock.view.restore.ViewManager;

/**
 * Created on 2005-04-17
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: FlexDockDemo.java,v 1.4 2005-05-04 02:12:01 marius Exp $
 */
public class FlexDockDemo extends JFrame {

	public FlexDockDemo() {
		super("FlexDock Demo");
		setContentPane(createContentPane());
		setJMenuBar(createApplicationMenuBar());
	}	

	private JPanel createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		IViewManager viewManager = ViewManager.getInstance();

		//tworzymy glowny view port do dokowania
		Viewport viewport = new Viewport();
		//rejestrujemy glowny view port
		ViewManager.getInstance().registerCenterDockingPort(viewport);
		
		//tworzymy glowny widok
		View mainView = createMainView();

		View birdViewView = createView("bird.view", "Bird View");
		View messageLogView = createView("message.log", "Message Log");
		View problemView = createView("problem", "Problems");
		View consoleView = createView("console", "Console");

		viewManager.registerView(mainView);
		viewManager.registerView(birdViewView);
		viewManager.registerView(messageLogView);
		viewManager.registerView(problemView);
		viewManager.registerView(consoleView);
		
		viewManager.registerTerritoralView(mainView);

		viewManager.registerViewDockingInfo(birdViewView.getPersistentId(), ViewDockingInfo.createRelativeDockingInfo(mainView, DockingPort.WEST_REGION, .3f));
		viewManager.registerViewDockingInfo(messageLogView.getPersistentId(), ViewDockingInfo.createRelativeDockingInfo(mainView, DockingPort.SOUTH_REGION, .3f));
		viewManager.registerViewDockingInfo(problemView.getPersistentId(), ViewDockingInfo.createRelativeDockingInfo(mainView, DockingPort.SOUTH_REGION, .3f));
		viewManager.registerViewDockingInfo(consoleView.getPersistentId(), ViewDockingInfo.createRelativeDockingInfo(mainView, DockingPort.SOUTH_REGION, .3f));

		IPerspective perspective1 = createPerspective1(viewport, mainView);
		IPerspective perspective2 = createPerspective2(viewport, mainView);
		IPerspective perspective3 = createPerspective3(viewport, mainView);

		IPerspectiveManager perspectiveManager = PerspectiveManager.getInstance();
		perspectiveManager.addPerspective(perspective1);
		perspectiveManager.addPerspective(perspective2);
		perspectiveManager.addPerspective(perspective3, true);
		
		perspectiveManager.applyDefaultPerspective();

		contentPane.add(viewport, BorderLayout.CENTER);
		
		return contentPane;
	}

	private static View createMainView() {
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Sample1", new JTextArea("Sample1"));
		tabbedPane.addTab("Sample2", new JTextArea("Sample2"));
		tabbedPane.addTab("Sample3", new JTextArea("Sample3"));
		
		//to view nie bedzie mialo tytulu, wiec przekazujemy null
		View mainView = new View("main.view", null, null);

		//blokujemy mozliwosc dokowania do tego view w regionie CENTER
		mainView.setTerritoryBlocked(DockingPort.CENTER_REGION, true);
		//wylaczamy pasek tytulowy
		mainView.setTitlebar(null);
		//ustawiamy komponent GUI, ktory chcemy aby byl wyswietalny w tym view
		mainView.setContentPane(new JScrollPane(tabbedPane));
		
		return mainView;
	}
	
	private View createView(String id, String text) {
		View view = new View(id, text);
		//Dodajemy akcje close to tego view
		view.addAction(new CloseAction(view));
		view.addAction(DockingConstants.PIN_ACTION);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.GRAY, 1));
		
		JTextField textField = new JTextField(text);
		textField.setPreferredSize(new Dimension(100, 20));
		panel.add(textField);
		view.setContentPane(panel);

		return view;
	}

	private JMenuBar createApplicationMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu showViewMenu = new JMenu("Show View");
		View birdViewView = ViewManager.getInstance().getRegisteredView("bird.view");
		View messageLogView = ViewManager.getInstance().getRegisteredView("message.log");
		View problemView = ViewManager.getInstance().getRegisteredView("problem");
		View consoleView = ViewManager.getInstance().getRegisteredView("console");
		
		showViewMenu.add(createShowViewActionFor(birdViewView));
		showViewMenu.add(createShowViewActionFor(messageLogView));
		showViewMenu.add(createShowViewActionFor(problemView));
		showViewMenu.add(createShowViewActionFor(consoleView));

		JMenu perspectiveMenu = new JMenu("Perspective");
		//pobieramy perspektywe nr 1
		IPerspective perspective1 = PerspectiveManager.getInstance().getPerspective("p1");
		IPerspective perspective2 = PerspectiveManager.getInstance().getPerspective("p2");
		IPerspective perspective3 = PerspectiveManager.getInstance().getPerspective("p3");

		perspectiveMenu.add(createOpenPerspectiveActionFor(perspective1));
		perspectiveMenu.add(createOpenPerspectiveActionFor(perspective2));
		perspectiveMenu.add(createOpenPerspectiveActionFor(perspective3));
		
		menuBar.add(showViewMenu);
		menuBar.add(perspectiveMenu);
		
		return menuBar;
	}

	private IPerspective createPerspective1(Viewport viewport, View mainView) {
		IPerspective perspective = new Perspective("p1", "Perspective1");
		//ustawiamy glowny view port na perspektywie
		perspective.setMainDockingPort(viewport);

		View birdViewView = ViewManager.getInstance().getRegisteredView("bird.view");
		View messageLogView = ViewManager.getInstance().getRegisteredView("message.log");
		View problemView = ViewManager.getInstance().getRegisteredView("problem");
		View consoleView = ViewManager.getInstance().getRegisteredView("console");

		perspective.addView(mainView);
		perspective.addView(birdViewView);
		perspective.addView(messageLogView);
		perspective.addView(problemView);
		perspective.addView(consoleView);
		
		perspective.dockToCenterDockingPort("main.view", true); //tru here means that when we reset perspective it will be default
		perspective.dock("main.view", "bird.view", DockingPort.EAST_REGION, .3f, true);
		perspective.dock("main.view", "message.log", DockingPort.WEST_REGION, .3f, true);

		perspective.dock("message.log", "problem", true);
		perspective.dock("message.log", "console", true);
		
		return perspective;
	}

	private IPerspective createPerspective2(Viewport viewport, View mainView) {
		IPerspective perspective = new Perspective("p2", "Perspective2");
		//ustawiamy glowny view port na perspektywie
		perspective.setMainDockingPort(viewport);

		View birdViewView = ViewManager.getInstance().getRegisteredView("bird.view");
		View messageLogView = ViewManager.getInstance().getRegisteredView("message.log");
		View problemView = ViewManager.getInstance().getRegisteredView("problem");
		View consoleView = ViewManager.getInstance().getRegisteredView("console");
		
		perspective.addView(mainView);
		perspective.addView(birdViewView);
		perspective.addView(messageLogView);
		perspective.addView(problemView);
		perspective.addView(consoleView);
		
		//dokujemy main.view do glownego view portu
		perspective.dockToCenterDockingPort("main.view", true);
		perspective.dock("main.view", "bird.view", DockingPort.WEST_REGION, .3f, true);
		perspective.dock("bird.view", "message.log", DockingPort.SOUTH_REGION, .5f, true);
		perspective.dock("message.log", "problem", true);
		perspective.dock("message.log", "console", DockingPort.EAST_REGION, .5f, true);
		
		return perspective;
	}

	private IPerspective createPerspective3(Viewport viewport, View mainView) {
		IPerspective perspective = new Perspective("p3", "Perspective3");
		perspective.setMainDockingPort(viewport);

		perspective.addView(mainView);

		perspective.dockToCenterDockingPort("main.view", true);
		
		return perspective;
	}
	
	private Action createShowViewActionFor(View commonView) {
		ShowViewAction showViewAction = new ShowViewAction(commonView);
		showViewAction.putValue(Action.NAME, commonView.getTitle());
		
		return showViewAction;
	}

	private Action createOpenPerspectiveActionFor(IPerspective perspective) {
		OpenPerspectiveAction openPerspectiveAction = new OpenPerspectiveAction(perspective);
		openPerspectiveAction.putValue(Action.NAME, perspective.getPerspectiveName());
		
		return openPerspectiveAction;
	}

	private class ShowViewAction extends AbstractAction {
		
		private View m_view = null;
		
		private ShowViewAction(View view) {
			if (view == null) throw new IllegalArgumentException("view cannot be null");
			m_view = view;
		}
		
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			ViewManager.getInstance().showView(m_view);
		}

	}
	
	private class OpenPerspectiveAction extends AbstractAction {
		
		private IPerspective m_perspective = null;
		
		private OpenPerspectiveAction(IPerspective perspective) {
			if (perspective == null) throw new IllegalArgumentException("perspective cannot be null");
			m_perspective = perspective;
		}
		
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (m_perspective != null) {
				PerspectiveManager.getInstance().applyPerspective(m_perspective);
			}
		}

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

	public static void main(String[] args) {
		SwingUtility.setPlaf(UIManager.getSystemLookAndFeelClassName());
		
		FlexDockDemo flexDockDemo = new FlexDockDemo();
	
		flexDockDemo.setSize(800, 600);
		SwingUtility.centerOnScreen(flexDockDemo);
		flexDockDemo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		flexDockDemo.setVisible(true);
	}

}
