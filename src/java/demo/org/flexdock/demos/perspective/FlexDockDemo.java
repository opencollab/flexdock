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

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockableBuilder;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.perspective.LayoutSequence;
import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.PerspectiveBuilder;
import org.flexdock.perspective.PerspectiveManager;
import org.flexdock.util.DockingConstants;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * Created on 2005-04-17
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: FlexDockDemo.java,v 1.5 2005-05-30 22:25:57 marius Exp $
 */
public class FlexDockDemo extends JFrame {
	public static final String APP_KEY = "PerspectiveDemo";
	private static final String MAIN_VIEW = "main.view";
	private static final String BIRD_VIEW = "bird.view";
	private static final String MESSAGE_VIEW = "message.log";
	private static final String PROBLEM_VIEW = "problem";
	private static final String CONSOLE_VIEW = "console";
	
	private static final String P1 = "p1";
	private static final String P2 = "p2";
	private static final String P3 = "p3";
	
	public FlexDockDemo() {
		super("FlexDock Demo");
		setContentPane(createContentPane());
		setJMenuBar(createApplicationMenuBar());
	}	

	private JPanel createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		//tworzymy glowny view port do dokowania
		Viewport viewport = new Viewport();
		//rejestrujemy glowny view port

		contentPane.add(viewport, BorderLayout.CENTER);
		return contentPane;
	}
	


	private JMenuBar createApplicationMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu showViewMenu = new JMenu("Show View");

		showViewMenu.add(createShowViewActionFor(BIRD_VIEW));
		showViewMenu.add(createShowViewActionFor(MESSAGE_VIEW));
		showViewMenu.add(createShowViewActionFor(PROBLEM_VIEW));
		showViewMenu.add(createShowViewActionFor(CONSOLE_VIEW));

		JMenu perspectiveMenu = new JMenu("Perspective");
		//pobieramy perspektywe nr 1
		perspectiveMenu.add(createOpenPerspectiveActionFor(P1));
		perspectiveMenu.add(createOpenPerspectiveActionFor(P2));
		perspectiveMenu.add(createOpenPerspectiveActionFor(P3));
		
		menuBar.add(showViewMenu);
		menuBar.add(perspectiveMenu);
		
		return menuBar;
	}
	
	private Action createShowViewActionFor(String viewId) {
		View commonView = View.getInstance(viewId);
		ShowViewAction showViewAction = new ShowViewAction(commonView.getPersistentId());
		showViewAction.putValue(Action.NAME, commonView.getTitle());
		return showViewAction;
	}

	private Action createOpenPerspectiveActionFor(String perspectiveId) {
		Perspective perspective = PerspectiveManager.getInstance().getPerspective(perspectiveId);
		OpenPerspectiveAction openPerspectiveAction = new OpenPerspectiveAction(perspective.getPersistentId());
		openPerspectiveAction.putValue(Action.NAME, perspective.getName());
		return openPerspectiveAction;
	}

	private class ShowViewAction extends AbstractAction {
		private String m_view = null;
		
		private ShowViewAction(String view) {
			if (view == null) throw new IllegalArgumentException("view cannot be null");
			m_view = view;
		}
		public void actionPerformed(ActionEvent e) {
			DockingManager.restore(m_view);
		}

	}
	
	private class OpenPerspectiveAction extends AbstractAction {
		private String m_perspective;
		
		private OpenPerspectiveAction(String perspectiveId) {
			if (perspectiveId == null) throw new IllegalArgumentException("perspectiveId cannot be null");
			m_perspective = perspectiveId;
		}
		public void actionPerformed(ActionEvent e) {
			if (m_perspective != null) {
				PerspectiveManager.getInstance().load(m_perspective);
			}
		}
	}
	


	
	
	private static class DemoPerspectiveBuilder implements PerspectiveBuilder {
		
		public Perspective createPerspective(String persistentId) {
			if(P1.equals(persistentId))
				return createPerspective1();
			if(P2.equals(persistentId))
				return createPerspective2();
			if(P3.equals(persistentId))
				return createPerspective3();
			return null;
		}
		
		private Perspective createPerspective1() {
			Perspective perspective = new Perspective(P1, "Perspective1");
			LayoutSequence sequence = perspective.getInitialSequence(true);
			
			sequence.add("main.view");
			sequence.add(BIRD_VIEW, "main.view", DockingPort.EAST_REGION, .3f);
			sequence.add(MESSAGE_VIEW, "main.view", DockingPort.WEST_REGION, .3f);
			sequence.add(PROBLEM_VIEW, MESSAGE_VIEW);
			sequence.add(CONSOLE_VIEW, MESSAGE_VIEW);
			
			return perspective;
		}

		private Perspective createPerspective2() {
			Perspective perspective = new Perspective(P2, "Perspective2");
			LayoutSequence sequence = perspective.getInitialSequence(true);

			sequence.add("main.view");
			sequence.add(BIRD_VIEW, "main.view", DockingPort.WEST_REGION, .3f);
			sequence.add(MESSAGE_VIEW, BIRD_VIEW, DockingPort.SOUTH_REGION, .5f);
			sequence.add(PROBLEM_VIEW, MESSAGE_VIEW);
			sequence.add(CONSOLE_VIEW, MESSAGE_VIEW, DockingPort.EAST_REGION, .5f);
			
			return perspective;
		}

		private Perspective createPerspective3() {
			Perspective perspective = new Perspective(P3, "Perspective3");
			LayoutSequence sequence = perspective.getInitialSequence(true);
			sequence.add("main.view");
			return perspective;
		}
	}
	
	private static class ViewBuilder implements DockableBuilder {
		
		public Dockable createDockable(String dockableId) {
			if(MAIN_VIEW.equals(dockableId))
				return createMainView();
			if(BIRD_VIEW.equals(dockableId))
				return createView(BIRD_VIEW, "Bird View");
			if(MESSAGE_VIEW.equals(dockableId))
				return createView(MESSAGE_VIEW, "Message Log");
			if(PROBLEM_VIEW.equals(dockableId))
				return createView(PROBLEM_VIEW, "Problems");
			if(CONSOLE_VIEW.equals(dockableId))
				return createView(CONSOLE_VIEW, "Console");
			return null;
		}
		
		private View createView(String id, String text) {
			View view = new View(id, text);
			//Dodajemy akcje close to tego view
			view.addAction(DockingConstants.CLOSE_ACTION);
			view.addAction(DockingConstants.PIN_ACTION);
			
			JPanel panel = new JPanel();
			panel.setBorder(new LineBorder(Color.GRAY, 1));
			
			JTextField textField = new JTextField(text);
			textField.setPreferredSize(new Dimension(100, 20));
			panel.add(textField);
			view.setContentPane(panel);

			return view;
		}
		
		private static View createMainView() {
			
			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.addTab("Sample1", new JTextArea("Sample1"));
			tabbedPane.addTab("Sample2", new JTextArea("Sample2"));
			tabbedPane.addTab("Sample3", new JTextArea("Sample3"));
			
			//to view nie bedzie mialo tytulu, wiec przekazujemy null
			View mainView = new View(MAIN_VIEW, null, null);

			//blokujemy mozliwosc dokowania do tego view w regionie CENTER
			mainView.setTerritoryBlocked(DockingPort.CENTER_REGION, true);
			//wylaczamy pasek tytulowy
			mainView.setTitlebar(null);
			//ustawiamy komponent GUI, ktory chcemy aby byl wyswietalny w tym view
			mainView.setContentPane(new JScrollPane(tabbedPane));
			
			return mainView;
		}
	}
	
	
	
	
	private static void setupPerspectives() {
		PerspectiveManager.setBuilder(new DemoPerspectiveBuilder());
		PerspectiveManager mgr = PerspectiveManager.getInstance();
		mgr.setDefaultPerspective(P3);
		
		// load on startup
		DockingManager.loadLayouts(APP_KEY);
		// remember to store on shutdown
		PerspectiveManager.addShutdownStorageHook(APP_KEY);
	}
	
	
	
	public static void main(String[] args) {
		SwingUtility.setPlaf(UIManager.getSystemLookAndFeelClassName());
		DockingManager.setDockableBuilder(new ViewBuilder());
		setupPerspectives();
		
		FlexDockDemo flexDockDemo = new FlexDockDemo();
		flexDockDemo.setSize(800, 600);
		SwingUtility.centerOnScreen(flexDockDemo);
		flexDockDemo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		flexDockDemo.setVisible(true);
		
		PerspectiveManager.getInstance().reload();
	}

}
