package org.flexdock.demos.elegant;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.flexdock.docking.config.ConfigurationManager;
import org.flexdock.docking.config.DockingConfiguration;


public class ElegantPersistenceDemo extends JFrame {
	private JSplitPane horizontalSplit;
	private JSplitPane vertSplitRight;
	private JSplitPane vertSplitLeft;
	
	private ElegantPanel j2eeHierarchyView;
	private ElegantPanel j2eeNavView;
	private ElegantPanel consoleView;
	private ElegantPanel serversView;
	private ElegantPanel tasksView;
	private ElegantPanel searchView;
	private ElegantPanel synchronizeView;
	private ElegantPanel outlineView;
	private ElegantPanel editorView;
	
	private ElegantDockingPort topLeft;
	private ElegantDockingPort bottomLeft;
	private ElegantDockingPort topRight;
	private ElegantDockingPort bottomRight;
	
	public ElegantPersistenceDemo() {
		super("Elegant Docking Demo");
		init();
	}

	private void init() {
		addWindowListener(new WindowCloser());
		createViews();
		horizontalSplit = createSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		vertSplitLeft = createSplitPane(JSplitPane.VERTICAL_SPLIT);
		vertSplitRight = createSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		horizontalSplit.setLeftComponent(vertSplitLeft);
		horizontalSplit.setRightComponent(vertSplitRight);
		initDockingPorts();
		
		setContentPane(horizontalSplit);
		
		loadConfiguration();
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
	

	private void initDockingPorts() {
		topLeft = new ElegantDockingPort("topLeft");
		bottomLeft = new ElegantDockingPort("bottomLeft");
		topRight = new ElegantDockingPort("topRight");
		bottomRight = new ElegantDockingPort("bottomRight");
		
		topLeft.add(j2eeHierarchyView);
		topLeft.add(j2eeNavView);
		bottomLeft.add(outlineView);
		topRight.add(editorView);
		bottomRight.add(tasksView);
		bottomRight.add(serversView);
		bottomRight.add(consoleView);
		bottomRight.add(searchView);
		bottomRight.add(synchronizeView);		

		vertSplitLeft.setLeftComponent(topLeft);
		vertSplitLeft.setRightComponent(bottomLeft);
		vertSplitRight.setLeftComponent(topRight);
		vertSplitRight.setRightComponent(bottomRight);
	}


	private void postInit() {
		horizontalSplit.setDividerLocation(0.3d);
		vertSplitLeft.setDividerLocation(0.75d);
		vertSplitRight.setDividerLocation(0.75d);
	}

	private static JSplitPane createSplitPane(int orientation) {
		JSplitPane split = new JSplitPane(orientation);
		// remove the border from the split pane
		split.setBorder(null);
         
		// set the divider size for a more reasonable, less bulky look 
		split.setDividerSize(3);

		// check the UI.  If we can't work with the UI any further, then
		// exit here.
		if (!(split.getUI() instanceof BasicSplitPaneUI))
		   return split;

		//  grab the divider from the UI and remove the border from it
		BasicSplitPaneDivider divider =
					   ((BasicSplitPaneUI) split.getUI()).getDivider();
		if (divider != null)
		   divider.setBorder(null);

		return split;
	}
	
	private void saveConfiguration() {
		String fileSep = System.getProperty("file.separator");
		String outDirPath = System.getProperty("user.home");
		if(!outDirPath.endsWith(fileSep))
			outDirPath += fileSep;
		outDirPath += "eleritec" + fileSep + "ElegantPersistenceDemo";
		
		File outDir = new File(outDirPath);
		outDir.mkdirs();
		
		DockingConfiguration config = ConfigurationManager.createDockingConfiguration();
		File outFile = new File(outDirPath + fileSep + ".layoutConfig");
		ConfigurationManager.store(config, outFile);
	}
	
	private void loadConfiguration() {
		String fileSep = System.getProperty("file.separator");
		String inPath = System.getProperty("user.home");
		if(!inPath.endsWith(fileSep))
			inPath += fileSep;
		inPath += "eleritec" + fileSep + "ElegantPersistenceDemo";
		inPath += fileSep + ".layoutConfig";
		
		File inFile = new File(inPath);
		if(!inFile.exists())
			return;

		DockingConfiguration config = ConfigurationManager.load(inFile);
		ConfigurationManager.applyConfiguration(config);
	}
	
	public static void main(String[] args) {
		ElegantPersistenceDemo demo = new ElegantPersistenceDemo();
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(800, 600);
		demo.setVisible(true);
		
		// now that we're visible and validated, move the split pane
		// dividers to their proper locations.
		demo.postInit();
	}
	
	private class WindowCloser extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			saveConfiguration();
		}
	}
}
