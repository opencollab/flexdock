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
package org.flexdock.docking.config;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockableComponentWrapper;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.util.RootWindow;


public class ConfigurationManager {
	private static final HashMap DOCKING_PORTS_BY_ID = new HashMap();
	private static final HashMap DOCKABLES_BY_ID = new HashMap();
	private RootWindow rootContainer;

			
	private ConfigurationManager(RootWindow root) {
		rootContainer = root;
	}


	public static DockingConfiguration load(String url) {
		return XMLConfigHandler.load(url);
	}
	
	public static DockingConfiguration load(File file) {
		return XMLConfigHandler.load(file);
	}
	
	public static DockingConfiguration load(URL url) {
		return XMLConfigHandler.load(url);
	}
	
	public static DockingConfiguration load(InputStream inStream) {
		return XMLConfigHandler.load(inStream);
	}

	public static void store(DockingConfiguration config, String filePath) {
		XMLConfigHandler.store(config, filePath);
	}
	
	public static void store(DockingConfiguration config, File file) {
		XMLConfigHandler.store(config, file);
	}
	
	public static void store(DockingConfiguration config, OutputStream outStream) {
		XMLConfigHandler.store(config, outStream);
	}




	
	
	
	public static boolean hasRegisteredDockableId(String id) {
		return DOCKABLES_BY_ID.containsKey(id);
	}
	
	public static void replaceDockingPort(String oldId, String newId, DockingPort port) {
		if(DOCKING_PORTS_BY_ID.containsKey(oldId))
			DOCKING_PORTS_BY_ID.remove(oldId);
			
		if(newId!=null && port!=null)
			DOCKING_PORTS_BY_ID.put(newId, port);
	}
	
	public static boolean registerDockable(Dockable d) {
		if(d==null || d.getPersistentId()==null)
			return false;
			
		DOCKABLES_BY_ID.put(d.getPersistentId(), d);
		return true;
	}


	



	
	public static DockingConfiguration createDockingConfiguration() {
		DockingConfiguration config = new DockingConfiguration();

		for(Iterator it=DOCKING_PORTS_BY_ID.keySet().iterator(); it.hasNext();) {
			String portId = (String)it.next();
			DockingPort port = (DockingPort)DOCKING_PORTS_BY_ID.get(portId);
			config.addDockingPort(createConfiguration(portId, port));
		}
		
		return config;
	}
	
	private static DockingPortConfiguration createConfiguration(String portId, DockingPort port) {
		DockingPortConfiguration config = new DockingPortConfiguration(portId);
		
		Component docked = port.getDockedComponent();
		if(docked==null)
			return config;
			
		if(docked instanceof JSplitPane)
			config.setChild(createConfiguration((JSplitPane)docked));
		else if(docked instanceof JTabbedPane)
			config.setChild(createConfiguration((JTabbedPane)docked));
		else {
			Dockable dockable = DockingManager.getRegisteredDockable(docked);
			if(dockable!=null)
				config.setChild(new DockableInstance(dockable.getPersistentId()));
		}
		return config;
	}
	
	private static SplitConfiguration createConfiguration(JSplitPane split) {
		int orient = split.getOrientation();
		int divLoc = split.getDividerLocation();
		
		SplitConfiguration config = new SplitConfiguration(orient, divLoc);

		Component left = split.getLeftComponent();
		config.setLeftComponent(createDockedItem(left));
		
		Component right = split.getRightComponent();
		config.setRightComponent(createDockedItem(right));
				
		return config;
	}
	
	private static TabbedConfiguration createConfiguration(JTabbedPane tabs) {
		TabbedConfiguration config = new TabbedConfiguration(tabs.getTabPlacement());
		int tabCount = tabs.getTabCount();
		for(int i=0; i<tabCount; i++) 
			config.addTab(createDockedItem(tabs.getComponentAt(i)));
		return config;
	}
	
	private static DockedItem createDockedItem(Component docked) {
		if(docked instanceof DockingPort) {
			DockingPort port = (DockingPort)docked;
			return createConfiguration(port.getPersistentId(), port);
		}
		
		if(docked instanceof JSplitPane)
			return createConfiguration((JSplitPane)docked);
			
		if(docked instanceof JTabbedPane)
			createConfiguration((JTabbedPane)docked);
			
		Dockable dockable = DockingManager.getRegisteredDockable(docked);
		if(dockable!=null)
			return new DockableInstance(dockable.getPersistentId());

		return null;
	}
	
	
	
	
	
	
	
	
	
	
	public static void applyConfiguration(DockingConfiguration config) {
		if(config==null)
			return;
			
		List ports = config.getDockingPorts();
		HashMap temp = new HashMap();
		
		for(Iterator it=ports.iterator(); it.hasNext();) {
			DockingPortConfiguration portConfig = (DockingPortConfiguration)it.next();
			DockingPort port = (DockingPort)DOCKING_PORTS_BY_ID.get(portConfig.getId());
			if(port==null)
				continue;
			
			Window win = port.getWindowAncestor();
			RootWindow root = RootWindow.getRootContainer(win);
			if(root==null)
				continue;
				
			
			ConfigurationManager mgr = (ConfigurationManager)temp.get(root);
			if(mgr==null) {
				mgr = new ConfigurationManager(root);
				temp.put(root, mgr);
			}
			mgr.applyConfiguration(port, portConfig);
		}
	}
	
	private void applyConfiguration(DockingPort port, DockingPortConfiguration config) {
		if(port==null || config==null)
			return;

		port.clear();			
		DockedItem docked = config.getChild();

		if(docked instanceof SplitConfiguration)
			applyConfiguration(port, (SplitConfiguration)docked);
		else if(docked instanceof TabbedConfiguration)
			applyConfiguration(port, (TabbedConfiguration)docked);
		else if(docked instanceof DockableInstance) 
			applyConfiguration(port, (DockableInstance)docked);
	}
	
	private void applyConfiguration(DockingPort port, SplitConfiguration config) {
		DockingPortConfiguration leftConfig = (DockingPortConfiguration)config.getLeftComponent();
		DockingPortConfiguration rightConfig = (DockingPortConfiguration)config.getRightComponent();
		String region = config.getOrientation()==JSplitPane.VERTICAL_SPLIT? 
								DockingPort.SOUTH_REGION: DockingPort.EAST_REGION;

		DockedItem leftChild = leftConfig.getChild();
		DockedItem rightChild = rightConfig.getChild();
		
		Dockable splitDummyLeft = DockableComponentWrapper.create(new JLabel("left"), "splitDummyLeft", "splitDummyLeft", true);
		Dockable splitDummyRight = DockableComponentWrapper.create(new JLabel("right"), "splitDummyRight", "splitDummyRight", true);
		
		dock(port, splitDummyLeft, DockingPort.CENTER_REGION);
		dock(port, splitDummyRight, region);
		
		JSplitPane split = (JSplitPane)port.getDockedComponent();
		DockingPort dockingPortLeft = (DockingPort)split.getLeftComponent();
		DockingPort dockingPortRight = (DockingPort)split.getRightComponent();

		applyConfiguration(dockingPortLeft, leftConfig);
		applyConfiguration(dockingPortRight, rightConfig);
		
		int divLoc = config.getDividerLocation();
		split.setDividerLocation(divLoc);
		
		// very bad code.  see the inner class declaration for details.
		DividerLocationEnforcer listener = new DividerLocationEnforcer(split, divLoc);
		split.addComponentListener(listener);
	}
	
	private void applyConfiguration(DockingPort port, TabbedConfiguration config) {
		List childConfigs = config.getTabs();
		if(childConfigs.size()==0)
			return;
		
		for(Iterator it=childConfigs.iterator(); it.hasNext();) {
			DockedItem docked = (DockedItem)it.next();
			if(docked instanceof DockableInstance)
				applyConfiguration(port, (DockableInstance)docked);
		}
	}
	
	private void applyConfiguration(DockingPort port, DockableInstance config) {
		Dockable dockable = (Dockable)DOCKABLES_BY_ID.get(config.getId());
		if(dockable!=null)
			dock(port, dockable, DockingPort.CENTER_REGION);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void dock(DockingPort target, Dockable dockableImpl, String region) {
		Component docked = target.getDockedComponent();
		Component dragSrc = dockableImpl.getDockable();
		if (dragSrc == docked)
			return;

		Container parent = dragSrc.getParent();
		if(parent!=null) {
			DockingPort oldPort = getParentDockingPort(dragSrc);
			if(oldPort!=null)
				// if 'dragSrc' was previously docked, then undock it instead of using a 
				// simple remove().  this will allow the DockingPort to do any of its own 
				// cleanup operations associated with component removal.
				oldPort.undock(dragSrc);
			else
				// otherwise, just remove the component
				parent.remove(dragSrc);
			revalidateComponent(rootContainer.getContentPane());
		}

		String desc = dockableImpl.getDockableDesc();
		boolean doResize = dockableImpl.isDockedLayoutResizable();
		
		target.dock(dragSrc, desc, region, doResize);
		revalidateComponent((Component) target);
	}
	
	
	// Copy-n-pasted from DockingManager
	private DockingPort getParentDockingPort(Component comp) {
		DockingPort port = (DockingPort)SwingUtilities.getAncestorOfClass(DockingPort.class, comp);
		if(port==null)
			return null;
			
		return port.hasDockedChild(comp)? port: null;
	}

	// Copy-n-pasted from DockingManager
	private void revalidateComponent(Component comp) {
		if (comp instanceof JComponent)
			 ((JComponent) comp).revalidate();
	}
	
	
	
	
	/* This code here is really terrible!  What was happening was that every time
	 * a vertical split configuration was restored, the divider location kept being
	 * reset to 0 over and over again.  I've been too busy to take the time to hunt
	 * down the real cause of this, so this temporary hack is in here just to make 
	 * the thing function correctly.
	 * 
	 * While reconstructing the split layout, the JSplitPane itself is resized several
	 * times in succession, at most maybe a couple hundred milliseconds apart.  We cache 
	 * the desired split pane divider location and, every time we catch a resize event, 
	 * we reset the divider location to where it should be.  That is, unless the 
	 * interval between the current and last resize event doesn't exceed a certain 
	 * threshold.  If it's been over a second since the last resize event, then this is
	 * something that was user-initiated, not part of the GUI reconstruction.  In this 
	 * case, we forego the divider location reset and go ahead and remove the listener
	 * from the JSplitPane itself.
	 */
	private static class DividerLocationEnforcer extends ComponentAdapter {
		private JSplitPane split;
		private int dividerLocation;
		long lastAccessed = -1;
		
		private DividerLocationEnforcer(JSplitPane sp, int divLoc) {
			split = sp;
			dividerLocation = divLoc;
		}

		public void componentResized(ComponentEvent e) {
			long now = System.currentTimeMillis();
			long interval = lastAccessed==-1? 0: now-lastAccessed;
			lastAccessed = now;

			if(interval<1000)
				split.setDividerLocation(dividerLocation);
			else
				split.removeComponentListener(this);
		}
	}
}
