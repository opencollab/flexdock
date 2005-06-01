package org.flexdock.demos.raw.elegant;

import java.awt.Container;

import javax.swing.JFrame;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;


public class ElegantDemo extends JFrame {
	private ElegantPanel j2eeHierarchyView;
	private ElegantPanel j2eeNavView;
	private ElegantPanel consoleView;
	private ElegantPanel serversView;
	private ElegantPanel tasksView;
	private ElegantPanel searchView;
	private ElegantPanel synchronizeView;
	private ElegantPanel outlineView;
	private ElegantPanel editorView;
	
	private DockingPort rootDockingPort;
	
	public ElegantDemo() {
		super("Elegant Docking Demo");
		init();
	}

	private void init() {
		createViews();
		initLayout();
		setContentPane((Container)rootDockingPort);
	}
	
	private void createViews() {
		j2eeHierarchyView = new ElegantPanel("J2EE Hierarchy");
		j2eeNavView = new ElegantPanel("J2EE Navigator");
		consoleView = new ElegantPanel("Console");
		serversView = new ElegantPanel("Servers");
		tasksView = new ElegantPanel("Tasks");
		searchView = new ElegantPanel("Search");
		synchronizeView = new ElegantPanel("Synchronize");
		outlineView = new ElegantPanel("Outline");
		editorView = new ElegantPanel("Editor");
	}
	

	private void initLayout() {
		rootDockingPort = new ElegantDockingPort();
		
		// setup 4 quadrants
		// dock the editor into the root dockingport
		DockingManager.dock(editorView, rootDockingPort);
		// dock the hierarchy-view to the west of the editor
		DockingManager.dock(j2eeHierarchyView, editorView, DockingPort.WEST_REGION);
		// dock the outline to the south of the hierarchy
		DockingManager.dock(outlineView, j2eeHierarchyView, DockingPort.SOUTH_REGION);
		// dock the task-view to the south of the editor
		DockingManager.dock(tasksView, editorView, DockingPort.SOUTH_REGION);

		// tab the nav-view onto the hierarchy view
		DockingManager.dock(j2eeNavView, j2eeHierarchyView);
		
		// tab the rest of the views onto the task-view
		DockingManager.dock(serversView, tasksView);
		DockingManager.dock(consoleView, tasksView);
		DockingManager.dock(searchView, tasksView);
		DockingManager.dock(synchronizeView, tasksView);
		
		// resize the immediate splitPane child of the root dockingport
		DockingManager.setSplitProportion(rootDockingPort, 0.3f);
		// resize the splitPane containing the hierarchy-view
		DockingManager.setSplitProportion(j2eeHierarchyView, 0.75f);
		// resize the splitPane containing the editor
		DockingManager.setSplitProportion(editorView, 0.75f);
	}

	
	public static void main(String[] args) {
		ElegantDemo demo = new ElegantDemo();
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(800, 600);
		demo.setVisible(true);
	}
}
