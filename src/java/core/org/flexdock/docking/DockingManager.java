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
package org.flexdock.docking;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.SwingUtilities;

import org.flexdock.docking.defaults.DefaultDockingStrategy;
import org.flexdock.docking.defaults.DockableComponentWrapper;
import org.flexdock.docking.drag.DragManager;
import org.flexdock.docking.event.DockingEventHandler;
import org.flexdock.docking.event.hierarchy.DockingPortTracker;
import org.flexdock.docking.event.hierarchy.RootDockingPortInfo;
import org.flexdock.docking.props.DockableProps;
import org.flexdock.docking.props.DockingPortProps;
import org.flexdock.docking.props.PropertyManager;
import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.FloatManager;
import org.flexdock.docking.state.LayoutManager;
import org.flexdock.docking.state.MinimizationManager;
import org.flexdock.event.EventDispatcher;
import org.flexdock.event.RegistrationEvent;
import org.flexdock.util.ClassMapping;
import org.flexdock.util.DockingUtility;
import org.flexdock.util.ResourceManager;
import org.flexdock.util.RootWindow;
import org.flexdock.util.SwingUtility;
import org.flexdock.util.Utilities;


/**
 * This class is used to manage drag operations for <code>Dockable</code> components.  Application 
 * code should interact with this class through <code>static</code> utility methods.
 * <p>
 * Any component that wishes to have docking capabilities enabled should call 
 * <code>registerDockable(Component evtSrc, String desc, boolean allowResize)</code>.  
 * <code>registerDockable(Component evtSrc, String desc, boolean allowResize)</code> will create a 
 * <code>Dockable</code> instance that this class can work with during drag operations.  Likewise, when
 * dealing strictly with bare <code>Components</code>, most methods have been overloaded with a 
 * <code>Component</code> version and a <code>Dockable</code> version.  <code>Component</code>
 * versions always create (or pull from a cache) a corresponding <code>Dockable</code> instance and 
 * dispatch to the overloaded <code>Dockable</code> version of the method.
 * <p>
 * <code>registerDockable(Component evtSrc, String desc, boolean allowResize)</code> adds required
 * <code>MouseMotionListeners</code> to the source <code>Component</code>, which automatically handle 
 * method dispatching to <code>startDrag()</code>, so explicitly initiating a drag in this manner, while
 * not prohibited, is typically not required.
 * <p>
 * During drag operations, an outline of the <code>Dockable.getDockable()</code> is displayed on the 
 * GlassPane and moves with the mouse cursor.  The <code>DockingManager</code> monitors the docking region 
 * underneath the mouse cursor for underlying <code>DockingPorts</code> and the mouse cursor icon will 
 * reflect this appropriately.  The image displayed by for the mouse cursor may be altered by returning a 
 * custom <code>CursorProvider</code> for the currentl <code>Dockable</code>. 
 * When the mouse has been released, a call to <code>stopDrag()</code> is 
 * issued.  If the current docking region allows docking, then the <code>DockingManager</code> removes 
 * the drag source from its original parent and docks it into its new <code>DockingPort</code>, subsequently
 * issuing callbacks to <code>DockingPort.dockingComplete(String region)</code> and then 
 * <code>Dockable.dockingCompleted()</code>.  If docking is not allowed, then no docking operation is 
 * performed and a callback is issued to <code>Dockable.dockingCanceled()</code>.
 * <p>
 * Whenever a <code>Dockable</code> is removed from a <code>DockingPort</code>, the <code>DockingManager</code>
 * takes care of making the requisite call to <code>DockingPort.undock()</code>.
 * 
 * @author Chris Butler
 */
public class DockingManager {
	public static final String MINIMIZE_MANAGER = "minimize.manager";
	public static final String LAYOUT_MANAGER = "layout.manager";
	private static final String DEV_PROPS = "org/flexdock/util/dev-props.properties";
	private static final String CONFIG_PROPS = "org/flexdock/docking/flexdock-core.properties";
	private static final DockingManager SINGLETON = new DockingManager();
	private static final HashMap DOCKABLES_BY_ID = new HashMap();
	private static final WeakHashMap DOCKABLES_BY_COMPONENT = new WeakHashMap();
	private static final HashMap DOCKING_PORTS_BY_ID = new HashMap();
	private static final ClassMapping DOCKING_STRATEGIES = new ClassMapping(DefaultDockingStrategy.class, new DefaultDockingStrategy());
	private static Object persistentIdLock = new Object();
	
	private DockingStrategy defaultDocker;
	private LayoutManager layoutManager;
	private MinimizationManager minimizeManager;
	private DockableBuilder dockableBuilder;
	private String applicationKey;
	private AutoPersist autoPersister;
	
	static {
		// call this method to preload any framework resources
		// we might need later
		init();
	}
	

	private static void init() {
		// load the dev system properties
		Properties p = ResourceManager.getProperties(DEV_PROPS, true);
		if(p!=null)
			System.getProperties().putAll(p);
		
		// prime the drag manager for use
		DragManager.prime();
		
		// make sure dockingEvents are properly intercepted
		EventDispatcher.addHandler(new DockingEventHandler());
		
		Properties config = ResourceManager.getProperties(CONFIG_PROPS, true);
		// set the minimization manager
		setMinimizeManager(config.getProperty(MINIMIZE_MANAGER));
		// set the layout manager
		setLayoutManager(config.getProperty(LAYOUT_MANAGER));
		
		// setup auto-persistence
		Runtime.getRuntime().addShutdownHook(getDockingManager().autoPersister);
	}

	private DockingManager() {
		defaultDocker = new DefaultDockingStrategy();
		autoPersister = new AutoPersist();
	}
	
	
	private static DockingManager getDockingManager() {
		return SINGLETON;
	}

	
	
	
	
	
	
	
	
	
	/**
	 * Undocks the specified <code>Dockable</code> instance from its containing <code>DockingPort</code>.  This 
	 * method locates the containing <code>DockingPort</code> for the specified <code>Dockable</code>.  If no 
	 * parent container is found at all, no exception is thrown and no action is taken.  If a containing 
	 * <code>DockingPort</code> is found, then <code>undock()</code> is called against the <code>DockingPort</code>
	 * instance to allow the <code>DockingPort</code> to handle its own cleanup operations.  If no containing 
	 * <code>DockingPort</code> is located, but a parent <code>Container</code> is found, then <code>remove()</code>
	 * is called against the parent <code>Container</code>.  
	 *
	 * @param dockable the <code>Dockable</code> we wish to undock
	 */
	public static boolean undock(Dockable dockable) {
		if(dockable==null)
			return false;
		
		DockingStrategy strategy = findDockingStrategy(dockable);
		if (strategy != null) {
			return strategy.undock(dockable);
		}

		return false; //TODO think of changing it to runtime exception I don't see a situation
		//when there would be no default docker.
	}

	public static boolean dock(Dockable dockable, DockingPort port, String region) {
		if(dockable==null)
			return false;
		
		DockingStrategy strategy = getDockingStrategy(port);
		if (strategy != null) {
			return strategy.dock(dockable, port,  region);
		}
	
		return false; //TODO think of changing it to runtime exception I don't see a situation
		//when there would be no docker.
	}
	
	private static DockingStrategy findDockingStrategy(Dockable dockable) {
		DockingPort port = dockable.getDockingPort();
		DockingStrategy strategy = port==null? null: port.getDockingStrategy();
		if(strategy==null) {
			DockingManager mgr = getDockingManager();
			strategy = mgr==null? null: mgr.defaultDocker;
		}
		return strategy;
	}

	public static boolean isValidDockingRegion(String region) {
		return DockingPort.CENTER_REGION.equals(region) || DockingPort.NORTH_REGION.equals(region) || 
			DockingPort.SOUTH_REGION.equals(region) || DockingPort.EAST_REGION.equals(region) || 
			DockingPort.WEST_REGION.equals(region); 
	}

	/**
	 * Checks whether a supplied dockable is docked in a supplied dockingPort instance.
	 */
	public static boolean isDocked(DockingPort dockingPort, Dockable dockable) {
		return dockingPort.isParentDockingPort(dockable.getDockable());
	}
	
	public static boolean isDocked(Dockable dockable) {
		return getDockingPort(dockable) != null;
	}

	public static boolean isDocked(Component component) {
		return getDockingPort(component) != null;
	}

	public static DockingPort getDockingPort(Dockable dockable) {
		return DockingUtility.getParentDockingPort(dockable);
	}
	
	public static DockingPort getDockingPort(Component dockable) {
		return DockingUtility.getParentDockingPort(dockable);
	}


	
	
	
	
	
	
	
	
	/**
	 * Creates a Dockable for the specified component and dispatches to 
	 * <code>registerDockable(Dockable init)</code>. If evtSrc is null, no exception is 
	 * thrown and no action is performed.
	 *
	 * @param evtSrc   the target component for the Dockable, both drag-starter and docking source
	 * @param desc     the description of the docking source.  Used as the tab-title of docked in a tabbed pane
	 * @param allowResize  specifies whether or not a resultant split-view docking would be fixed or resizable  
	 */
	public static Dockable registerDockable(Component evtSrc, String desc) {
		if (evtSrc == null)
			return null;

		Dockable dockable = getDockableForComponent(evtSrc, desc);
		return registerDockable(dockable);
	}

	/**
	 * Initializes the specified Dockable.  Adds a MouseMotionListener to 
	 * <code>init.getInitiator()</code> to detect drag events and call <code>startDrag()</code> when 
	 * detected.  Caches <code>init</code> in a <code>WeakHashMap</code> by <code>init.getDockable()</code>
	 * for subsequent internal lookups. If the MouseMotionListener is already registerd with the 
	 * initiator component, it will not be added again.  <code>init</code> may not be null and both 
	 * <code>init.getDockable()</code> and , <code>init.getInitiator()</code> may not return null.  
	 * If any of these checks fail, no exception is thrown and no action is performed.
	 *
	 * @param init the Dockable that is being initialized.
	 */
	public static Dockable registerDockable(Dockable dockable) {
		if (dockable == null || dockable.getDockable() == null || dockable.getDragSources()==null)
			return null;
		
		if(dockable.getPersistentId()==null)
			throw new IllegalArgumentException("Dockable must have a non-null persistent ID.");
		
		DOCKABLES_BY_COMPONENT.put(dockable.getDockable(), dockable);
		
		// flag the component as dockable, in case it doesn't 
		// implement the interface directly
		Component c = dockable.getDockable();
		SwingUtility.putClientProperty(c, Dockable.DOCKABLE_INDICATOR, Boolean.TRUE);
		
		// add drag listeners
		updateDragListeners(dockable);
		
		// add the dockable as its own listener
		dockable.addDockingListener(dockable);
		
		// make sure we have docking-properties initialized
		DockableProps props = PropertyManager.getDockableProps(dockable);
		
		// cache the dockable by ID
		DOCKABLES_BY_ID.put(dockable.getPersistentId(), dockable);
		
		// dispatch a registration event
		EventDispatcher.dispatch(new RegistrationEvent(dockable, DockingManager.SINGLETON, true));
		
		// return the dockable
		return dockable;
	}
	
	public static Dockable getDockable(Component comp) {
		return comp==null? null: (Dockable)DOCKABLES_BY_COMPONENT.get(comp);
	}
	
	public static Dockable getDockable(String id) {
		Dockable dockable = getDockableImpl(id);
		if(dockable==null)
			dockable = loadAndRegister(id);
		return dockable;
	}
	
	private static Dockable getDockableImpl(String id) {
		return id==null? null: (Dockable)DOCKABLES_BY_ID.get(id);
	}
	
	public static Set getDockableIds() {
		synchronized(DOCKABLES_BY_ID) {
			return new HashSet(DOCKABLES_BY_ID.keySet());
		}
	}
	
	private static Dockable loadAndRegister(String id) {
		DockableBuilder builder = getDockingManager().dockableBuilder;
		if(builder==null)
			return null;
		
		// the createDockable() implementation may or may not
		// automatically register the dockable before returning.
		Dockable dockable = builder.createDockable(id);
		if(dockable==null)
			return null;
		
		// if the newly created dockable has not yet been registered, 
		// then register it.
		boolean registered = getDockableImpl(dockable.getPersistentId())!=null;
		if(!registered) {
			registerDockable(dockable);
		}
		return dockable;
	}

	private static Dockable getDragInitiator(Component c) {
		return getDockableForComponent(c, null);
	}

	private static Dockable getDockableForComponent(Component c, String desc) {
		if (c == null)
			return null;

		Dockable dockable = getDockable(c);
		if (dockable == null) {
			String persistentId = generatePersistentId(c);
			dockable = DockableComponentWrapper.create(c, persistentId, desc);
			DOCKABLES_BY_COMPONENT.put(c, dockable);
		}
		return dockable;
	}

	public static DragManager getDragListener(Dockable dockable) {
		if(dockable==null || dockable.getDragSources()==null)
			return null;
		
		for(Iterator it=dockable.getDragSources().iterator(); it.hasNext();) {
			Object obj = it.next();
			if(obj instanceof Component) {
				DragManager listener = getDragListener((Component)obj);
				if(listener!=null)
					return listener;
			}
		}
		return null;
	}
	
	private static DragManager getDragListener(Component c) {
		EventListener[] listeners = c.getMouseMotionListeners();
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] instanceof DragManager)
				return (DragManager) listeners[i];
		}
		return null;
	}


	
	
	
	
	
	
	
	
	
	public static void updateDragListeners(Dockable dockable) {
		DragManager dragListener = getDragListener(dockable);
		if (dragListener == null) {
			dragListener = new DragManager(dockable);
		}
		
		for(Iterator it=dockable.getDragSources().iterator(); it.hasNext();) {
			Object obj = it.next();
			if(obj instanceof Component) {
				updateDragListeners((Component)obj, dragListener);				
			}
		}
	}
	
	private static void updateDragListeners(Component dragSrc, DragManager listener) {
		MouseMotionListener motionListener = null;
		EventListener[] listeners = dragSrc.getMouseMotionListeners();
		for(int i=0; i<listeners.length; i++) {
			if(listeners[i] instanceof DragManager) {
				motionListener = (MouseMotionListener)listeners[i];
				break;
			}
		}
		if(motionListener!=listener) {
			if(motionListener!=null)
				dragSrc.removeMouseMotionListener(motionListener);
			dragSrc.addMouseMotionListener(listener);
		}
		
		MouseListener mouseListener = null;
		listeners = dragSrc.getMouseListeners();
		for(int i=0; i<listeners.length; i++) {
			if(listeners[i] instanceof DragManager) {
				mouseListener = (MouseListener)listeners[i];
				break;
			}
		}
		if(mouseListener!=listener) {
			if(mouseListener!=null)
				dragSrc.removeMouseListener(mouseListener);
			dragSrc.addMouseListener(listener);
		}
	}
	
	public static void removeDragListeners(Component c) {
		if(c==null)
			return;
		
		MouseMotionListener motionListener = null;
		EventListener[] listeners = c.getMouseMotionListeners();
		for(int i=0; i<listeners.length; i++) {
			if(listeners[i] instanceof DragManager) {
				motionListener = (MouseMotionListener)listeners[i];
				break;
			}
		}
		if(motionListener!=null) {
			c.removeMouseMotionListener(motionListener);
		}
		
		MouseListener mouseListener = null;
		listeners = c.getMouseListeners();
		for(int i=0; i<listeners.length; i++) {
			if(listeners[i] instanceof DragManager) {
				mouseListener = (MouseListener)listeners[i];
				break;
			}
		}
		if(mouseListener!=null) {
			c.removeMouseListener(mouseListener);
		}
	}
	

	
	
	
	
	
	
	
	
	
	

	private static String generatePersistentId(Object obj) {
		return generatePersistentId(obj, null);
	}
	
	private static String generatePersistentId(Object obj, String desiredId) {
		if(obj==null)
			return null;
		
		synchronized(persistentIdLock) {
			String pId = desiredId==null? obj.getClass().getName(): desiredId;
			StringBuffer baseId = new StringBuffer(pId);
			for(int i=1; hasRegisteredDockableId(pId); i++) {
				baseId.append("_").append(i);
				pId = baseId.toString();
			}
			return pId;
		}
	}
	
	private static boolean hasRegisteredDockableId(String id) {
		return DOCKABLES_BY_ID.containsKey(id);
	}
	
	public static void setDockablePropertyManager(Class dockable, Class propType) {
		PropertyManager.setDockablePropertyType(dockable, propType);
	}
	
	public static void setDockingStrategy(Class c, DockingStrategy strategy) {
		if(strategy==null)
			DOCKING_STRATEGIES.removeClassMapping(c);
		else
			DOCKING_STRATEGIES.addClassMapping(c, strategy.getClass(), strategy);
	}
	
	public static DockingStrategy getDockingStrategy(Object obj) {
		Class key = obj==null? null: obj.getClass();
		return getDockingStrategy(key);
	}
	
	public static DockingStrategy getDockingStrategy(Class classKey) {
		DockingStrategy strategy = (DockingStrategy)DOCKING_STRATEGIES.getClassInstance(classKey);
		return strategy;
	}
	
	public static RootWindow[] getDockingWindows() {
		Set windowSet = DockingPortTracker.getDockingWindows();
		return windowSet==null? new RootWindow[0]: (RootWindow[])windowSet.toArray(new RootWindow[0]);
	}
	
	public static DockingPort getMainDockingPort(Component comp) {
		RootDockingPortInfo info = getRootDockingPortInfo(comp);
		return info==null? null: info.getMainPort();
	}
	
	public static void setMainDockingPort(Component comp, String portId) {
		RootDockingPortInfo info = getRootDockingPortInfo(comp);
		if(info!=null)
			info.setMainPort(portId);		
	}
	
	public static RootDockingPortInfo getRootDockingPortInfo(Component comp) {
		return DockingPortTracker.getRootDockingPortInfo(comp);
	}
	
	public static DockingPort getRootDockingPort(Component comp) {
		return DockingPortTracker.findByWindow(comp);
	}
	
	public static DockingPort getRootDockingPort(String portId) {
		
		return DockingPortTracker.findById(portId);
	}	
	
	
	
	
	
	public static DockingPortProps getDockingPortRoot() {
		return PropertyManager.getDockingPortRoot();
	}
	
	public static DockableProps getDockableRoot() {
		return PropertyManager.getDockableRoot();
	}
	
	public static void setMinimized(Dockable dockable, boolean minimized) {
		Component cmp = dockable==null? null: dockable.getDockable();
		Window window = cmp==null? null: SwingUtilities.getWindowAncestor(cmp);
		setMinimized(dockable, minimized, window);
	}
	
	public static void setMinimized(Dockable dockable, boolean minimized, Window window) {
		setMinimized(dockable, minimized, window, MinimizationManager.UNSPECIFIED_LAYOUT_EDGE);
	}

	public static void setMinimized(Dockable dockable, boolean minimizing, int edge) {
		setMinimized(dockable, minimizing, null, edge);
	}
	
	public static void setMinimized(Dockable dockable, boolean minimizing, Window window, int edge) {
		if(dockable==null)
			return;
		
		if(window==null)
			window = SwingUtility.getActiveWindow();
		if(window==null)
			return;
		
		getMinimizeManager().setMinimized(dockable, minimizing, window, edge);
	}
	
	public static void restore(Dockable dockable) {
		getLayoutManager().restore(dockable);
	}
	
	public static void restore(String dockable) {
		getLayoutManager().restore(getDockable(dockable));
	}
	

	public static LayoutManager getLayoutManager() {
		return getDockingManager().layoutManager;
	}
	
	public static void setLayoutManager(LayoutManager mgr) {
		getDockingManager().layoutManager = mgr;
	}
	
	public static void setLayoutManager(String mgrClass) {
		Object instance = Utilities.getInstance(mgrClass);
		setLayoutManager((LayoutManager)instance);
	}
	
	public static MinimizationManager getMinimizeManager() {
		MinimizationManager mgr = getDockingManager().minimizeManager;
		return mgr==null? MinimizationManager.DEFAULT_STUB: mgr;
	}
	
	public static void setMinimizeManager(MinimizationManager mgr) {
		getDockingManager().minimizeManager = mgr;
	}
	
	public static void setMinimizeManager(String mgrClass) {
		Object instance = Utilities.getInstance(mgrClass);
		setMinimizeManager((MinimizationManager)instance);
	}
	
	public static FloatManager getFloatManager() {
		return getLayoutManager().getFloatManager();
	}
	
	public static DockingState getDockingState(String dockable) {
		return getLayoutManager().getDockingState(dockable);
	}
	
	public static DockingState getDockingState(Dockable dockable) {
		return getLayoutManager().getDockingState(dockable);
	}
	
	
	public static DockableBuilder getDockableBuilder() {
		return getDockingManager().dockableBuilder;
	}
	
	public static void setDockableBuilder(DockableBuilder builder) {
		getDockingManager().dockableBuilder = builder;
	}
	
	public static boolean persistLayouts() {
		String appKey = getApplicationKey();
		return persistLayouts(appKey);
	}
	
	public static boolean persistLayouts(String applicationKey) {
		LayoutManager mgr = getLayoutManager();
		return mgr==null || applicationKey==null? false: mgr.persist(applicationKey);
	}
	
	public static boolean loadLayouts() {
		String appKey = getApplicationKey();
		return loadLayouts(appKey);
	}
	
	public static boolean loadLayouts(String applicationKey) {
		LayoutManager mgr = getLayoutManager();
		return mgr==null || applicationKey==null? false: mgr.loadFromStorage(applicationKey);		
	}
	
	public static void replaceDockingPort(String oldId, String newId, DockingPort port) {
		if(DOCKING_PORTS_BY_ID.containsKey(oldId))
			DOCKING_PORTS_BY_ID.remove(oldId);
			
		if(newId!=null && port!=null)
			DOCKING_PORTS_BY_ID.put(newId, port);
	}
	
	
	public static void setApplicationKey(String appKey) {
		getDockingManager().applicationKey = appKey;
	}
	
	public static String getApplicationKey() {
		return getDockingManager().applicationKey;
	}
	
	public static void setAutoPersist(boolean b) {
		getDockingManager().autoPersister.setEnabled(b);
	}
	
	
	private static class AutoPersist extends Thread {
		private boolean enabled;
		
		public void run() {
			store();
		}
		
		private synchronized void store() {
			String key = getApplicationKey();
			if(key!=null && isEnabled())
				getLayoutManager().persist(key);
		}
		
		public synchronized boolean isEnabled() {
			return enabled;
		}
		
		public synchronized void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}
}
