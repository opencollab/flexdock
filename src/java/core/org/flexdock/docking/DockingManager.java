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
import java.awt.event.MouseMotionListener;
import java.util.EventListener;
import java.util.HashMap;
import java.util.WeakHashMap;

import org.flexdock.docking.config.ConfigurationManager;
import org.flexdock.docking.defaults.DefaultDockingStrategy;
import org.flexdock.docking.defaults.DockableComponentWrapper;
import org.flexdock.docking.drag.DragManager;


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
	private static final DockingManager SINGLETON = new DockingManager();
	private static final WeakHashMap CACHED_DRAG_INITIATORS_BY_COMPONENT = new WeakHashMap();
	private static final HashMap DOCKING_STRATEGIES = new HashMap();
	protected static final DockingStrategy DEFAULT_STRATEGY = new DefaultDockingStrategy();
	private static Object persistentIdLock = new Object();
	
	private DockingStrategy defaultDocker;
	
	static {
		// call this method to preload any framework resources
		// we might need later
		init();
	}
	

	private static void init() {
		// prime the drag manager for use
		DragManager.prime();
	}

	private DockingManager() {
		defaultDocker = new DefaultDockingStrategy();
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
	public static void undock(Dockable dockable) {
		DockingManager mgr = getDockingManager();
		if (mgr != null)
			mgr.defaultDocker.undock(dockable);
	}

	public static void dock(Dockable dockable, DockingPort port, String region) {
		DockingManager mgr = getDockingManager();
		if (mgr != null)
			mgr.defaultDocker.dock(dockable, port,  region);
	}
	

	public static boolean isValidDockingRegion(String region) {
		return DockingPort.CENTER_REGION.equals(region) || DockingPort.NORTH_REGION.equals(region) || 
			DockingPort.SOUTH_REGION.equals(region) || DockingPort.EAST_REGION.equals(region) || 
			DockingPort.WEST_REGION.equals(region); 
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
	public static Dockable registerDockable(Component evtSrc, String desc, boolean allowResize) {
		if (evtSrc == null)
			return null;

		Dockable dockable = getDockableForComponent(evtSrc, desc, allowResize);
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
		if (dockable == null || dockable.getDockable() == null || dockable.getInitiator()==null)
			return null;

		DragManager dragListener = getDragListener(dockable);
		if (dragListener == null) {
			dragListener = new DragManager(dockable);
			dockable.getInitiator().addMouseMotionListener(dragListener);
			dockable.getInitiator().addMouseListener(dragListener);
		}
		CACHED_DRAG_INITIATORS_BY_COMPONENT.put(dockable.getDockable(), dockable);
		
		// add the dockable as its own listener
		dockable.addDockingListener(dockable);
		
		// allow the configuration manager to keep track of this dockable.  This 
		// will allow docking configurations to survive JVM instances.
		ConfigurationManager.registerDockable(dockable);
		
		// return the dockable
		return dockable;
	}

	public static Dockable getRegisteredDockable(Component comp) {
		return comp==null? null: (Dockable)CACHED_DRAG_INITIATORS_BY_COMPONENT.get(comp);
	}

	private static Dockable getDragInitiator(Component c) {
		return getDockableForComponent(c, null, false);
	}

	private static Dockable getDockableForComponent(Component c, String desc, boolean allowResize) {
		if (c == null)
			return null;

		Dockable initiator = getRegisteredDockable(c);
		if (initiator == null) {
			String persistentId = generatePersistentId(c);
			initiator = DockableComponentWrapper.create(c, persistentId, desc, allowResize);
			CACHED_DRAG_INITIATORS_BY_COMPONENT.put(c, initiator);
		}
		return initiator;
	}

	private static DragManager getDragListener(Dockable dockable) {
		EventListener[] listeners = dockable.getInitiator().getListeners(MouseMotionListener.class);

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] instanceof DragManager)
				return (DragManager) listeners[i];
		}
		return null;
	}

	/**
	 * Sets the docking description for the specified component.  Te docking description is used as the
	 * tab-title when docking within a tabbed pane.  A Dockable is looked up from an internal cache using
	 * the specified component as a key.  If not found, a dockabe is created an cached by the same key.
	 * The Dockable's description is set to <code>desc</code>.   
	 *
	 * @param c the Component that is the drag-source for a given Dockable instance
	 * @param desc the dockable description for the specified component
	 */
	public static void setDockingDescription(Component c, String desc) {
		Dockable init = getDragInitiator(c);
		setDockingDescription(init, desc);
	}

	/**
	 * Sets the docking description for the Dockable instance.  The docking description is used as the
	 * tab-title when docking within a tabbed pane. 
	 *
	 * @param dockable the Dockable instance we're describing
	 * @param desc the description of the Dockable instance.  used as a tab-title.
	 */
	public static void setDockingDescription(Dockable dockable, String desc) {
		if (dockable != null)
			dockable.setDockableDesc(desc);
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
			for(int i=1; ConfigurationManager.hasRegisteredDockableId(pId); i++) {
				baseId.append("_").append(i);
				pId = baseId.toString();
			}
			return pId;
		}
	}
	
	public static void setDockingStrategy(Class c, DockingStrategy strategy) {
		if(c==null)
			return;
		
		synchronized(DOCKING_STRATEGIES) {
			if(strategy==null)
				DOCKING_STRATEGIES.remove(c);
			else
				DOCKING_STRATEGIES.put(c, strategy);
		}
	}
	
	public static DockingStrategy getDockingStrategy(Dockable dockable) {
		if(dockable==null)
			return null;
		
		DockingStrategy strategy = null;
		
		synchronized(DOCKING_STRATEGIES) {
			for(Class c=dockable.getClass();c!=null && strategy==null; c=c.getSuperclass()) {
				strategy = (DockingStrategy)DOCKING_STRATEGIES.get(c);
			}
		}
	 
		if(strategy==null)
			strategy = DEFAULT_STRATEGY;
		return strategy;
	}

}
