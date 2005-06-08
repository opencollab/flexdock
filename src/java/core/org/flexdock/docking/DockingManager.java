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
import java.io.IOException;
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
import org.flexdock.docking.floating.policy.FloatPolicyManager;
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
import org.flexdock.util.DockingConstants;
import org.flexdock.util.DockingUtility;
import org.flexdock.util.ResourceManager;
import org.flexdock.util.RootWindow;
import org.flexdock.util.SwingUtility;
import org.flexdock.util.Utilities;


/**
 * This class is used as a public facade into the framework docking system.  It provides a straightforward
 * public API for managing and manipulating the various different subcomponents that make up the docking 
 * framework through a single class.  <code>DockingManager</code> cannot be instantiated.  Rather, its methods
 * are accessed statically from within application code and it generally defers processing to a set of abstract
 * handlers hidden from the application layer.
 * 
 * Among <code>DockingManager's</code> responsibilities are as follows:
 * 
 * <ol>
 * <li>
 * 	<b>Maintaining a component repository.</b><br/>
 * 	All <code>Dockables</code> and <code>DockingPorts</code> are cached within an and accessible through 
 * 	and internal registry.
 * </li>
 * <li>
 * 	<b>Maintaining framework state.</b><br/>
 * 	<code>DockingManager</code> provides APIs for managing various different global framework settings, including
 * 	application-key, floating support, auto-persistence, <code>LayoutManagers</code>, and 
 * 	<code>MinimizationManagers</code>. 
 * </li>
 * <li>
 * 	<b>Behavioral auto-configuration.</b><br/>
 * 	<code>DockingManager</code> automatically adds and removes necessary event listeners to enable/disable 
 *  drag-to-dock behavior as components are registered and unregistered.
 * </li>
 * <li>
 * 	<b>Programmatic access to docking operations.</b><br/>
 * 	<code>DockingManager</code> provides public APIs for programmatically dock, undock, minimize, persist, and load
 * 	<code>Dockables</code> from storage.
 * </li>
 * </ol>
 *  
 * @author Christopher Butler
 */
public class DockingManager implements DockingConstants {
	public static final String MINIMIZE_MANAGER = "minimize.manager";
	public static final String LAYOUT_MANAGER = "layout.manager";
	private static final String DEV_PROPS = "org/flexdock/util/dev-props.properties";
	private static final String CONFIG_PROPS = "org/flexdock/docking/flexdock-core.properties";
	private static final DockingManager SINGLETON = new DockingManager();
	private static final HashMap DOCKABLES_BY_ID = new HashMap();
	private static final WeakHashMap DOCKABLES_BY_COMPONENT = new WeakHashMap();
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
	
	private static class AutoPersist extends Thread {
		private boolean enabled;
		
		public void run() {
			store();
		}
		
		private synchronized void store() {
			String key = getApplicationKey();
			try {
				if(key!=null && isEnabled())
					getLayoutManager().persist(key);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		public synchronized boolean isEnabled() {
			return enabled;
		}
		
		public synchronized void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
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
		EventDispatcher.addListener(FloatPolicyManager.getInstance());
		
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
	 * Convenience method that removes the specified <code>Dockable</code> from the layout.  
	 * If the <code>Dockable</code>is embedded within the main application frame or a floating 
	 * dialog, it is removed from the container hierarchy.  If the <code>Dockable</code> is presently 
	 * minimized, it is removed from the current minimization manager.  If the <code>Dockable</code>
	 * is already "closed" or is <code>null</code>, no operation is performed.  "Closing" a 
	 * <code>Dockable</code> only removes it from the visual layout.  It does not remove it from
	 * the internal <code>Dockable</code> registry and all underlying <code>DockingState</code>
	 * information remains consistent so that the <code>Dockable</code> may later be restored to
	 * its original location within the application.
	 * 
	 * @param dockable the <code>Dockable</code> to be closed.
	 */
	public static void close(Dockable dockable) {
		if(dockable==null)
			return;
		
		if(isDocked(dockable)) {
			undock(dockable);
		} else if (DockingUtility.isMinimized(dockable)) {
			getMinimizeManager().close(dockable);
		}
	}
	

	/**
	 * Docks the specified <code>Component</code> into the CENTER region of the specified 
	 * <code>DockingPort</code>.  If the <code>DockingManager</code> finds a valid 
	 * <code>Dockable</code> instance mapped to the specified <code>Component</code>, the
	 * <code>Dockable</code> will be docked into the <code>DockingPort</code>.  If the 
	 * <code>Component</code> or <code>DockingPort</code> is <code>null</code>, or a valid
	 * <code>Dockable</code> cannot be found for the specified <code>Component</code>, this 
	 * method returns <code>false</code>.  Otherwise, this method returns <code>true</code>
	 * if the docking operation was successful and <code>false</code> if the docking operation
	 * cannot be completed.  This method defers processing to 
	 * <code>dock(Component dockable , DockingPort port, String region)</code>.
	 * 
	 * @param dockable the <code>Component</code> to be docked.
	 * @param port the <code>DockingPort</code> into which the specified <code>Component</code>
	 * will be docked.
	 * @return <code>true</code> if the docking operation was successful, <code>false</code> otherwise.
	 * @see #dock(Component, DockingPort, String)
	 */
	public static boolean dock(Component dockable, DockingPort port) {
		return dock(dockable, port, CENTER_REGION);
	}
	
	/**
	 * Docks the specified <code>Component</code> into the supplied region of the specified 
	 * <code>DockingPort</code>.  If the <code>DockingManager</code> finds a valid 
	 * <code>Dockable</code> instance mapped to the specified <code>Component</code>, the
	 * <code>Dockable</code> will be docked into the <code>DockingPort</code>.  If the 
	 * <code>Component</code> or <code>DockingPort</code> is <code>null</code>, or a valid
	 * <code>Dockable</code> cannot be found for the specified <code>Component</code>, this 
	 * method returns <code>false</code>.  Otherwise, this method returns <code>true</code>
	 * if the docking operation was successful and <code>false</code>  if the docking operation
	 * cannot be completed.  This method defers processing to 
	 * <code>dock(Dockable dockable, DockingPort port, String region)</code>.
	 * 
	 * @param dockable the <code>Component</code> to be docked.
	 * @param port the <code>DockingPort</code> into which the specified <code>Component</code>
	 * will be docked.
	 * @param region the region into which to dock the specified <code>Component</code>
	 * @return <code>true</code> if the docking operation was successful, <code>false</code> 
	 * if the docking operation cannot be completed.
	 * @see #dock(Dockable, DockingPort, String)
	 */
	public static boolean dock(Component dockable , DockingPort port, String region) {
		Dockable d = getDockable(dockable);
		return dock(d, port, region);
	}
	
	/**
	 * Docks the specified <code>Dockable</code> into the supplied region of the specified 
	 * <code>DockingPort</code>.  If the <code>Dockable</code> or <code>DockingPort</code> is 
	 * <code>null</code>, this method returns <code>false</code>.  Otherwise, this method returns 
	 * <code>true</code> if the docking operation was successful and <code>false</code> if
	 * the docking operation cannot be completed.
	 * 
	 * This method determines the <code>DockingStrategy</code> to be used for the specified
	 * <code>DockingPort</code> and defers processing to the <code>DockingStrategy</code>.  This
	 * method's return value will be based upon the <code>DockingStrategy</code> implementation 
	 * and is subject to conditions such as whether the supplied region is deemed valid, whether
	 * the <code>DockingStrategy</code> allows this particular <code>Dockable</code> to be docked
	 * into the supplied region of the specified <code>DockingPort</code>, and so on.  The 
	 * <code>DockingStrategy</code> used is obtained by a call to 
	 * <code>getDockingStrategy(Object obj)</code> and may be controlled via 
	 * <code>setDockingStrategy(Class c, DockingStrategy strategy)</code>, supplying a 
	 * <code>DockingPort</code> implementation class and a customized <code>DockingStrategy</code>. 
	 * 
	 * @param dockable the <code>Dockable</code> to be docked.
	 * @param port the <code>DockingPort</code> into which the specified <code>Component</code>
	 * will be docked.
	 * @param region the region into which to dock the specified <code>Dockable</code>
	 * @return <code>true</code> if the docking operation was successful, <code>false</code> otherwise.
	 * @see DockingStrategy#dock(Dockable, DockingPort, String)
	 * @see #getDockingStrategy(Object)
	 * @see #setDockingStrategy(Class, DockingStrategy)
	 */
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
	
	
	/**
	 * Docks the specified <code>Component</code> relative to another already-docked 
	 * <code>Component</code> in the CENTER region.  Valid <code>Dockable</code> instances
	 * are looked up for both <code>Component</code> parameters and processing is deferred to
	 * <code>dock(Dockable dockable, Dockable parent)</code>.  If a valid <code>Dockable</code>
	 * cannot be resolved for either <code>Component</code>, then this method returns 
	 * <code>false</code>.  The "parent" <code>Dockable</code> must currently be docked.  If not, 
	 * this method will return <code>false</code>.  Otherwise, its parent <code>DockingPort</code>
	 * will be resolved and the new <code>Dockable</code> will be docked into the 
	 * <code>DockingPort</code> relative to the "parent" <code>Dockable</code>.
	 * 
	 * @param dockable the <code>Component</code> to be docked
	 * @param parent the <code>Component</code> used as a reference point for docking
	 * @return <code>true</code> if the docking operation was successful; <code>false</code> otherwise.
	 * @see DockingManager#dock(Dockable, Dockable)
	 */
	public static boolean dock(Component dockable, Component parent) {
		return dock(getDockable(dockable), getDockable(parent));
	}
	
	/**
	 * Docks the specified <code>Dockable</code> relative to another already-docked 
	 * <code>Dockable</code> in the CENTER region.  The "parent" <code>Dockable</code> 
	 * must currently be docked.  If not, this method will return <code>false</code>.  
	 * Otherwise, its parent <code>DockingPort</code>
	 * will be resolved and the new <code>Dockable</code> will be docked into the 
	 * <code>DockingPort</code> relative to the "parent" <code>Dockable</code>.
	 * This method defers processing to 
	 * <code>dock(Dockable dockable, Dockable parent, String region)</code> and returns
	 * <code>false</code> if any of the input parameters are <code>null</code>.
	 * 
	 * @param dockable the <code>Dockable</code> to be docked
	 * @param parent the <code>Dockable</code> used as a reference point for docking
	 * @return <code>true</code> if the docking operation was successful; <code>false</code> otherwise.
	 * @see #dock(Dockable, Dockable, String)
	 */
	public static boolean dock(Dockable dockable, Dockable parent) {
		return dock(dockable, parent, CENTER_REGION);
	}
	
	/**
	 * Docks the specified <code>Component</code> relative to another already-docked 
	 * <code>Component</code> in the specified region.  Valid <code>Dockable</code> instances
	 * will be looked up for each of the <code>Component</code> parameters.  If a valid
	 * <code>Dockable</code> is not found for either <code>Component</code>, then this method
	 * returns <code>false</code>.  The "parent" <code>Dockable</code> 
	 * must currently be docked.  If not, this method will return <code>false</code>.  
	 * Otherwise, its parent <code>DockingPort</code>
	 * will be resolved and the new <code>Dockable</code> will be docked into the 
	 * <code>DockingPort</code> relative to the "parent" <code>Dockable</code>.
	 * This method defers processing to 
	 * <code>dock(Component dockable, Component parent, String region, float proportion)</code> 
	 * and returns <code>false</code> if any of the input parameters are <code>null</code>.
	 * If the specified region is other than CENTER, then a split layout should result.
	 * This method supplies a split proportion of 0.5F, resulting in equal distribution
	 * of space between the dockable and parent parameters if docking is successful. 
	 * 
	 * @param dockable the <code>Component</code> to be docked
	 * @param parent the <code>Component</code> used as a reference point for docking
	 * @param region the relative docking region into which <code>dockable</code> will be docked
	 * @return <code>true</code> if the docking operation was successful; <code>false</code> otherwise.
	 * @see #dock(Component, Component, String, float)
	 */
	public static boolean dock(Component dockable, Component parent, String region) {
		return dock(dockable, parent, region, 0.5f);
	}
	
	/**
	 * Docks the specified <code>Dockable</code> relative to another already-docked 
	 * <code>Dockable</code> in the specified region.  The "parent" <code>Dockable</code> 
	 * must currently be docked.  If not, this method will return <code>false</code>.  
	 * Otherwise, its parent <code>DockingPort</code>
	 * will be resolved and the new <code>Dockable</code> will be docked into the 
	 * <code>DockingPort</code> relative to the "parent" <code>Dockable</code>.
	 * This method defers processing to 
	 * <code>dock(Dockable dockable, Dockable parent, String region, float proportion)</code> 
	 * and returns <code>false</code> if any of the input parameters are <code>null</code>.
	 * If the specified region is other than CENTER, then a split layout should result.
	 * This method supplies a split proportion of 0.5F, resulting in equal distribution
	 * of space between the dockable and parent parameters if docking is successful. 
	 * 
	 * @param dockable the <code>Dockable</code> to be docked
	 * @param parent the <code>Dockable</code> used as a reference point for docking
	 * @param region the docking region into which <code>dockable</code> will be docked
	 * @return <code>true</code> if the docking operation was successful; <code>false</code> otherwise.
	 * @see #dock(Dockable, Dockable, String, float)
	 */
	public static boolean dock(Dockable dockable, Dockable parent, String region) {
		return dock(dockable, parent, region, 0.5f);
	}
	
	/**
	 * Docks the specified <code>Component</code> relative to another already-docked 
	 * <code>Component</code> in the specified region with the specified split proportion.
	 * Valid <code>Dockable</code> instances will be looked up for each of the 
	 * <code>Component</code> parameters.  If a valid <code>Dockable</code> is not found for 
	 * either <code>Component</code>, then this method returns <code>false</code>.
	 * The "parent" <code>Dockable</code> must currently be docked.  If not, this method will 
	 * return <code>false</code>.  Otherwise, its parent <code>DockingPort</code>
	 * will be resolved and the new <code>Dockable</code> will be docked into the 
	 * <code>DockingPort</code> relative to the "parent" <code>Dockable</code>.
	 * If the specified region is CENTER, then the <code>proportion</code> parameter is 
	 * ignored.  Otherwise, a split layout should result with the proportional space specified
	 * in the <code>proportion</code> parameter allotted to the <code>dockable</code> argument.
	 * This method defers processing to 
	 * <code>dock(Dockable dockable, Dockable parent, String region, float proportion)</code>.
	 * 
	 * @param dockable the <code>Component</code> to be docked
	 * @param parent the <code>Component</code> used as a reference point for docking
	 * @param region the relative docking region into which <code>dockable</code> will be docked
	 * @param proportion the proportional space to allot the <code>dockable</code> argument 
	 * if the docking operation results in a split layout.
	 * @return <code>true</code> if the docking operation was successful; <code>false</code> otherwise.
	 */
	public static boolean dock(Component dockable, Component parent, String region, float proportion) {
		Dockable newDockable = getDockable(dockable);
		Dockable parentDockable = getDockable(parent);
		return dock(newDockable, parentDockable, region, proportion);
	}
	
	/**
	 * Docks the specified <code>Dockable</code> relative to another already-docked 
	 * <code>Dockable</code> in the specified region with the specified split proportion.
	 * The "parent" <code>Dockable</code> must currently be docked.  If not, this method will 
	 * return <code>false</code>.  Otherwise, its parent <code>DockingPort</code>
	 * will be resolved and the new <code>Dockable</code> will be docked into the 
	 * <code>DockingPort</code> relative to the "parent" <code>Dockable</code>.
	 * If the specified region is CENTER, then the <code>proportion</code> parameter is 
	 * ignored.  Otherwise, a split layout should result with the proportional space specified
	 * in the <code>proportion</code> parameter allotted to the <code>dockable</code> argument.
	 * 
	 * @param dockable the <code>Dockable</code> to be docked
	 * @param parent the <code>Dockable</code> used as a reference point for docking
	 * @param region the docking region into which <code>dockable</code> will be docked
	 * @param proportion the proportional space to allot the <code>dockable</code> argument 
	 * if the docking operation results in a split layout.
	 * @return <code>true</code> if the docking operation was successful; <code>false</code> otherwise.
	 */
	public static boolean dock(Dockable dockable, Dockable parent, String region, float proportion) {
		return DockingUtility.dockRelative(parent, dockable, region, proportion);
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

	
	
	
	
	
	
	
	/**
	 * Indicates whether the specified <code>Component</code> is currently docked.
	 * This method looks up a parent <code>DockingPort</code> for the specified
	 * <code>Component</code> via a call to <code>getDockingPort(Component dockable)</code>.
	 * This method returns <code>true</code> if a parent <code>DockingPort</code> is found
	 * and <code>false</code> if no parent <code>DockingPort</code> is present.  This method
	 * returns <code>false</code> if the <code>Component</code> parameter is <code>null</code>.
	 * 
	 * @param component the <code>Component</code> whose docking status is to be examined
	 * @return <code>true</code> if the <code>Component</code> is currently docked; otherwise <code>false</code>.
	 */
	public static boolean isDocked(Component component) {
		return getDockingPort(component) != null;
	}

	/**
	 * Indicates whether the specified <code>Dockable</code> is currently docked.
	 * This method looks up a parent <code>DockingPort</code> for the specified
	 * <code>Dockable</code> via a call to <code>getDockingPort(Dockable dockable)</code>.
	 * This method returns <code>true</code> if a parent <code>DockingPort</code> is found
	 * and <code>false</code> if no parent <code>DockingPort</code> is present.  This method
	 * returns <code>false</code> if the <code>Dockable</code> parameter is <code>null</code>.
	 * 
	 * @param dockable the <code>Dockable</code> whose docking status is to be examined
	 * @return <code>true</code> if the <code>Dockable</code> is currently docked; otherwise <code>false</code>.
	 */
	public static boolean isDocked(Dockable dockable) {
		return getDockingPort(dockable) != null;
	}
	
	/**
	 * Checks whether a supplied <code>Dockable</code> is docked within a 
	 * supplied <code>DockingPort</code> instance.  Returns <code>true</code>
	 * if the <code>DockingPort</code> contains the specified <code>Dockable</code>; 
	 * <code>false</code> otherwise.  This method returns <code>false</code> if either 
	 * of the input parameters are <code>null</code>.
	 * 
	 * @param dockingPort the <code>DockingPort</code> to be tested
	 * @param dockable the <code>Dockable</code> instance to be examined
	 * @return <code>true</code> if the supplied <code>DockingPort</code> contains
	 * the specified <code>Dockable</code>; <code>false</code> otherwise.
	 */
	public static boolean isDocked(DockingPort dockingPort, Dockable dockable) {
		return dockingPort==null || dockable==null? false: 
			dockingPort.isParentDockingPort(dockable.getComponent());
	}

	/**
	 * Indicates whether global floating support is currently enabled.
	 * Defers processing to <code>FloatPolicyManager.isGlobalFloatingEnabled()</code>.
	 * 
	 * @return <code>true</code> if global floating support is enabled, <code>false</code> otherwise.
	 * @see FloatPolicyManager#isGlobalFloatingEnabled()
	 */
	public static boolean isFloatingEnabled() {
		return FloatPolicyManager.isGlobalFloatingEnabled();
	}
	
	/**
	 * Indicates whether the supplied parameter is considered a valid docking region.
	 * Valid values are those defined in <code>DockingConstants</code> and include
	 * <code>NORTH_REGION</code>, <code>SOUTH_REGION</code>, <code>EAST_REGION</code>, 
	 * <code>WEST_REGION</code>, and <code>CENTER_REGION</code>.  This method returns 
	 * <code>true</code> if the supplied parameter is equal to one of these values.
	 * 
	 * @param region the region value to be tested
	 * @return <code>true</code> if the supplied parameter is a valid docking region; 
	 * <code>false</code> otherwise.
	 */
	public static boolean isValidDockingRegion(String region) {
		return CENTER_REGION.equals(region) || NORTH_REGION.equals(region) || 
			SOUTH_REGION.equals(region) || EAST_REGION.equals(region) || 
			WEST_REGION.equals(region); 
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
	
	
	
	
	
	
	
	/**
	 * Creates a Dockable for the specified component and dispatches to 
	 * <code>registerDockable(Dockable init)</code>. If evtSrc is null, no exception is 
	 * thrown and no action is performed.
	 *
	 * @param evtSrc   the target component for the Dockable, both drag-starter and docking source
	 * @param desc     the description of the docking source.  Used as the tab-title of docked in a tabbed pane
	 * @return the <code>Dockable</code> that has been registered for the supplied <code>Component</code>
	 * @see #registerDockable(Dockable)
	 */
	public static Dockable registerDockable(Component evtSrc, String desc) {
		if (evtSrc == null)
			return null;

		Dockable dockable = getDockableForComponent(evtSrc, desc);
		return registerDockable(dockable);
	}

	/**
	 * Registers and initializes the specified <code>Dockable</code>.  All <code>Dockables</code>
	 * managed by the framework must, at some point, be registered via this method.  This method
	 * adds the <code>Dockable</code> to the internal registry, allowing querying by ID and
	 * <code>Component</code>.  Drag listeners are added to the <code>Dockable</code> to enable
	 * drag-n-drop docking support.  Docking properties are also initialized for the 
	 * <code>Dockable</code>.  This method fires a <code>RegistrationEvent</code> once the <code>Dockable</code> has 
	 * been registered.  If the <code>Dockable</code> is <code>null</code>, no <code>Exception</code> 
	 * is thrown and no action is taken.  The <code>Dockable</code> returned by this method
	 * will be the same object passed in as an argument.
	 *
	 * @param dockable the Dockable that is being registered.
	 * @return the <code>Dockable</code> that has been registered.
	 * @see org.flexdock.event.RegistrationEvent
	 */
	public static Dockable registerDockable(Dockable dockable) {
		if (dockable == null || dockable.getComponent() == null || dockable.getDragSources()==null)
			return null;
		
		if(dockable.getPersistentId()==null)
			throw new IllegalArgumentException("Dockable must have a non-null persistent ID.");
		
		DOCKABLES_BY_COMPONENT.put(dockable.getComponent(), dockable);
		
		// flag the component as dockable, in case it doesn't 
		// implement the interface directly
		Component c = dockable.getComponent();
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
	
	/**
	 * Removes the event listeners that manage drag-n-drop docking operations from the
	 * specified <code>Component</code>.  If the specific listeners are not present, then
	 * no action is taken.  Drag listeners used by the docking system are of type
	 * <code>org.flexdock.docking.drag.DragManager</code>.
	 * 
	 * @param comp the <code>Component</code> from which to remove drag listeners.
	 * @see DragManager
	 */
	public static void removeDragListeners(Component comp) {
		if(comp==null)
			return;
		
		MouseMotionListener motionListener = null;
		EventListener[] listeners = comp.getMouseMotionListeners();
		for(int i=0; i<listeners.length; i++) {
			if(listeners[i] instanceof DragManager) {
				motionListener = (MouseMotionListener)listeners[i];
				break;
			}
		}
		if(motionListener!=null) {
			comp.removeMouseMotionListener(motionListener);
		}
		
		MouseListener mouseListener = null;
		listeners = comp.getMouseListeners();
		for(int i=0; i<listeners.length; i++) {
			if(listeners[i] instanceof DragManager) {
				mouseListener = (MouseListener)listeners[i];
				break;
			}
		}
		if(mouseListener!=null) {
			comp.removeMouseListener(mouseListener);
		}
	}
	
	/**
	 * Displays the specified <code>Dockable</code> in the application's docking layout.  If the
	 * <code>Dockable</code> has not previously been docked, a suitable location is determined
	 * within the layout and the <code>Dockable</code> is docked to that location.  If the
	 * <code>Dockable</code> has previously been docked within the layout and subsequently
	 * removed, as with a call to <code>DockingManager.close()</code>, the <code>Dockable</code>
	 * will be restored to its prior state within the layout.
	 * This method defers processing to the <code>display(Dockable dockable)</code> method for 
	 * the currently installed <code>org.flexdock.docking.state.LayoutManager</code>.  The 
	 * <code>LayoutManager</code> implementation is responsible for handling the semantics of 
	 * determining an initial docking location or restoring a <code>Dockable</code> to its 
	 * previous layout state.  If the <code>Dockable</code> parameter is <code>null</code>, 
	 * no <code>Exception</code> is thrown and no action is taken.
	 * 
	 * @param dockable the <code>Dockable</code> to be displayed.
	 * @return <code>true</code> if the <code>Dockable</code> was successfully displayed; 
	 * <code>false</code> otherwise.
	 * @see #getLayoutManager()
	 * @see LayoutManager#display(Dockable)  
	 */
	public static boolean display(Dockable dockable) {
		return getLayoutManager().display(dockable);
	}
	
	/**
	 * Displays the <code>Dockable</code> with the specified ID within the application's docking layout.
	 * A valid <code>Dockable</code> is looked up for the supplied ID.  If none is found, this
	 * method returns <code>false<code>. Otherwise, processing is dispatched to 
	 * <code>display(Dockable dockable)</code>. 
	 * If the <code>Dockable</code> has not previously been docked, a suitable location is determined
	 * within the layout and the <code>Dockable</code> is docked to that location.  If the
	 * <code>Dockable</code> has previously been docked within the layout and subsequently
	 * removed, as with a call to <code>DockingManager.close()</code>, the <code>Dockable</code>
	 * will be restored to its prior state within the layout.
	 * This method defers processing to the <code>display(Dockable dockable)</code> method for 
	 * the currently installed <code>org.flexdock.docking.state.LayoutManager</code>.  The 
	 * <code>LayoutManager</code> implementation is responsible for handling the semantics of 
	 * determining an initial docking location or restoring a <code>Dockable</code> to its 
	 * previous layout state.  If the <code>Dockable</code> parameter is <code>null</code>, 
	 * no <code>Exception</code> is thrown and no action is taken.
	 * 
	 * @param dockable the ID of the <code>Dockable</code> to be displayed.
	 * @return <code>true</code> if the <code>Dockable</code> was successfully displayed; 
	 * <code>false</code> otherwise.
	 * @see #display(Dockable)
	 * @see #getLayoutManager()
	 * @see LayoutManager#display(Dockable)  
	 */
	public static boolean display(String dockable) {
		return getLayoutManager().display(getDockable(dockable));
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
	
	
	
	
	

	
	/**
	 * Returns the <code>DockingStrategy</code> associated with the <code>Class</code> of the
	 * <code>Object</code> parameter.  This method returns <code>null</code> if the parameter
	 * is <code>null</code>.  Otherwise, the method retrieves the <code>Object's</code>
	 * <code>Class</code> and dispatches to <code>getDockingStrategy(Class classKey)</code>.
	 * <br/>
	 * <code>DockingStrategy</code> association follows a strict inheritance chain using 
	 * <code>org.flexdock.util.ClassMapping</code>.  If a mapping for <code>obj.getClass()</code>
	 * is not found, then the superclass is tested, and so on until <code>java.lang.Object</code>
	 * is reached.  Thus, if a <code>DockingStrategy</code> mapping of <code>Foo</code> exists for 
	 * class <code>Bar</code>, and class <code>Baz</code> extends <code>Bar</code>, then calling this
	 * method for an instance of <code>Baz</code> will return an instance of <code>Foo</code>. The
	 * inheritance chain is <i>strict</i> in the sense that only superclasses are checked.  
	 * Implemented interfaces are ignored.
	 * <br/>
	 * If a class association is never found, then an instance of <code>DefaultDockingStrategy</code>
	 * is returned.
	 * 
	 * @param obj the object whose <code>DockingStrategy</code> association we wish to test
	 * @return the <code>DockingStrategy</code> associated with the <code>Class</code> type of the 
	 * <code>Object</code> parameter.
	 * @see #getDockingStrategy(Class)
	 * @see #setDockingStrategy(Class, DockingStrategy)
	 * @see ClassMapping#getClassInstance(Class)    
	 */
	public static DockingStrategy getDockingStrategy(Object obj) {
		Class key = obj==null? null: obj.getClass();
		return getDockingStrategy(key);
	}
	
	/**
	 * Returns the <code>DockingStrategy</code> associated with specified <code>Class</code>.
	 * This method returns <code>null</code> if the parameter is <code>null</code>.
	 * <br/>
	 * <code>DockingStrategy</code> association follows a strict inheritance chain using 
	 * <code>org.flexdock.util.ClassMapping</code>.  If a mapping for <code>obj.getClass()</code>
	 * is not found, then the superclass is tested, and so on until <code>java.lang.Object</code>
	 * is reached.  Thus, if a <code>DockingStrategy</code> mapping of <code>Foo</code> exists for 
	 * class <code>Bar</code>, and class <code>Baz</code> extends <code>Bar</code>, then calling this
	 * method for class <code>Baz</code> will return an instance of <code>Foo</code>. The
	 * inheritance chain is <i>strict</i> in the sense that only superclasses are checked.  
	 * Implemented interfaces are ignored.
	 * <br/>
	 * If a class association is never found, then an instance of <code>DefaultDockingStrategy</code>
	 * is returned.
	 * 
	 * @param classKey the <code>Class</code> whose <code>DockingStrategy</code> association we wish to test
	 * @return the <code>DockingStrategy</code> associated with the specified <code>Class</code>.
	 * @see #setDockingStrategy(Class, DockingStrategy)
	 * @see ClassMapping#getClassInstance(Class)
	 */
	public static DockingStrategy getDockingStrategy(Class classKey) {
		DockingStrategy strategy = (DockingStrategy)DOCKING_STRATEGIES.getClassInstance(classKey);
		return strategy;
	}
	
	/**
	 * Returns an array of <code>RootWindows</code> known to the docking framework that contain <code>DockingPorts</code>.
	 * Any <code>Frame</code>, <code>Applet</code>, <code>Dialog</code>, or <code>Window</code> that 
	 * has a <code>DockingPort</code> added as a descendent <code>Component</code> will automatically have
	 * an <code>org.flexdock.util.RootWindow</code> wrapper instance associated with it.  This method
	 * will return an array of all known RootWindows that contain <code>DockingPorts</code>.  
	 * Ordering of the array may be based off of a <code>java.util.Set</code> and is <b>not</b> 
	 * guaranteed.
	 * 
	 * @return an array of all known <code>RootWindows</code> that contain <code>DockingPorts</code>
	 * @see RootWindow
	 * @see DockingPortTracker#getDockingWindows()
	 */
	public static RootWindow[] getDockingWindows() {
		Set windowSet = DockingPortTracker.getDockingWindows();
		return windowSet==null? new RootWindow[0]: (RootWindow[])windowSet.toArray(new RootWindow[0]);
	}
	
	/**
	 * Returns the <code>DockingPort</code> with the specified ID.  If the <code>portId</code> parameter is 
	 * <code>null</code>, or a <code>DockingPort</code> with the specified ID is not found, a <code>null</code>
	 * reference is returned.  This method internally dispatches to 
	 * <code>org.flexdock.docking.event.hierarchy.DockingPortTracker.findById(String portId)</code>.
	 * <code>portId</code> should match the value returned by a <code>DockingPort's</code> 
	 * <code>getPersistentId()</code> method.
	 * 
	 * @param portId the ID of the <code>DockingPort</code> to be looked up
	 * @return the <code>DockingPort</code> with the specified ID
	 * @see DockingPort#getPersistentId()
	 * @see DockingPortTracker#findById(String)
	 */
	public static DockingPort getDockingPort(String portId) {
		return DockingPortTracker.findById(portId);
	}
	
	/**
	 * Returns the "main" <code>DockingPort</code> within the application window containing the
	 * specified <code>Component</code>.  Just as desktop applications will tend to have a "main" 
	 * application window, perhaps surrounded with satellite windows or dialogs, the "main" 
	 * <code>DockingPort</code> within a given window will be considered by the application developer to
	 * contain the primary docking layout used by the enclosing window.
	 * <br/>
	 * The <code>Component</code> parameter may or may not be a root window container.  If not, the ancestor
	 * window of <code>comp</code> is determined and a set of docking ports encapsulated by a 
	 * <code>RootDockingPortInfo</code> instance is returned by a call to 
	 * <code>getRootDockingPortInfo(Component comp)</code>.  The resolved <code>RootDockingPortInfo</code> 
	 * instance's main <code>DockingPort</code> is returned via its method <code>getMainPort()</code>.
	 * <br/>
	 * By default, the "main" <code>DockingPort</code> assigned to any <code>RootDockingPortInfo</code>
	 * instance associated with a window will happen to be the first root <code>DockingPort</code> detected
	 * for that window.  In essence, the default settings make this method identical to 
	 * <code>getRootDockingPort(Component comp)</code>.  This, however, may be altered by 
	 * <code>RootDockingPortInfo's</code> <code>setMainPort(String portId)</code> method  
	 * based upon the needs of the application developer.  In contrast,
	 * <code>getMainDockingPort(Component comp)</code> will always return the first root <code>DockingPort</code>
	 * found within a window.
	 * <br/>
	 * If <code>comp</code> is <code>null</code> or the root window cannot be resolved, then this method returns
	 * a <code>null</code> reference.  A <code>null</code> reference is also returned if the root window does 
	 * not contain any <code>DockingPorts</code>.
	 *
	 * @param comp the <code>Component</code> whose root window will be checked for a main <code>DockingPort</code>
	 * @return the main <code>DockingPort</code> within the root window that contains <code>comp</code>
	 * @see #getRootDockingPortInfo(Component)
	 * @see #getRootDockingPort(Component)
	 * @see DockingPortTracker#getRootDockingPortInfo(Component)
	 * @see RootDockingPortInfo#getMainPort()
	 * @see RootDockingPortInfo#setMainPort(String)
	 */
	public static DockingPort getMainDockingPort(Component comp) {
		RootDockingPortInfo info = getRootDockingPortInfo(comp);
		return info==null? null: info.getMainPort();
	}

	/**
	 * Returns the first root <code>DockingPort</code> found within the application window containing the
	 * specified <code>Component</code>.  A "root" <code>DockingPort</code> is a <code>DockingPort</code>
	 * embedded within a window/frame/applet/dialog that is not nested within any other parent <code>DockingPorts</code>. 
	 * The <code>Component</code> parameter may or may not be a root window 
	 * container itself.  If not, the root window containing <code>comp</code> is resolved and the first root
	 * <code>DockingPort</code> found within it is returned.
	 * This method defers actual processing to 
	 * <code>org.flexdock.docking.event.hierarchy.DockingPortTracker.findByWindow(Component comp)</code>.
	 * <br/>
	 * If <code>comp</code> is <code>null</code> or the root window cannot be resolved, then this method returns
	 * a <code>null</code> reference.  A <code>null</code> reference is also returned if the root window does 
	 * not contain any <code>DockingPorts</code>.
	 * <br/>
	 * This method differs from <code>getMainDockingPort(Component comp)</code> in that the "main" 
	 * <code>DockingPort</code> for a given window is configurable by the application developer, whereas this
	 * method will always return the "first" <code>DockingPort</code> found within the window.  However, if
	 * the "main" <code>DockingPort</code> has not been manually configured by the application developer, then
	 * this method and <code>getMainDockingPort(Component comp)</code> will exhibit identical behavior.
	 *
	 * @param comp the <code>Component</code> whose root window will be checked for a root <code>DockingPort</code>
	 * @return the first root <code>DockingPort</code> found within the root window that contains <code>comp</code>
	 * @see #getMainDockingPort(Component)
	 * @see DockingPortTracker#findByWindow(Component)
	 * @see RootDockingPortInfo
	 */
	public static DockingPort getRootDockingPort(Component comp) {
		return DockingPortTracker.findByWindow(comp);
	}

	/**
	 * Returns the <code>RootDockingPortInfo</code> instance associated with the root window containing the
	 * specified <code>Component</code>.  The <code>Component</code> parameter may or may not be a root window 
	 * container itself.  If not, the root window containing <code>comp</code> is resolved and the 
	 * <code>RootDockingPortInfo</code> instance associated with the window is returned.
	 * <code>RootDockingPortInfo</code> will contain information regarding all of the "root" 
	 * <code>DockingPorts</code> embedded within a root window where a "root" <code>DockingPort</code> is any
	 * <code>DockingPort</code> embedded within the window that does not have any other 
	 * <code>DockingPort</code> ancestors in it's container hierarchy.
	 * <br/>
	 * If <code>comp</code> is <code>null</code> or the root window cannot be resolved, then this method returns
	 * a <code>null</code> reference.  A <code>null</code> reference is also returned if the root window does 
	 * not contain any <code>DockingPorts</code>.
	 * <br/>
	 * This method dispatches internally to 
	 * <code>org.flexdock.docking.event.hierarchy.DockingPortTracker.getRootDockingPortInfo(Component comp)</code>.
	 * 
	 * @param comp the <code>Component</code> whose root window will be checked for an associated 
	 * <code>RootDockingPortInfo</code>.
	 * @return the <code>RootDockingPortInfo</code> instance associated with the root window containing <code>comp</code>.
	 * @see RootDockingPortInfo
	 * @see DockingPortTracker#getRootDockingPortInfo(Component) 
	 */
	public static RootDockingPortInfo getRootDockingPortInfo(Component comp) {
		return DockingPortTracker.getRootDockingPortInfo(comp);
	}
	

	
	
	
	
	

	

	



	

	public static boolean persistLayouts() throws IOException {
		String appKey = getApplicationKey();
		return persistLayouts(appKey);
	}
	
	public static boolean persistLayouts(String applicationKey) throws IOException {
		LayoutManager mgr = getLayoutManager();
		return mgr==null || applicationKey==null? false: mgr.persist(applicationKey);
	}
	
	public static boolean loadLayouts() throws IOException {
		String appKey = getApplicationKey();
		return loadLayouts(appKey);
	}
	
	public static boolean loadLayouts(String applicationKey) throws IOException {
		LayoutManager mgr = getLayoutManager();
		return mgr==null || applicationKey==null? false: mgr.loadFromStorage(applicationKey);		
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
	

	
	
	
	
	
	
	

	
	public static String getApplicationKey() {
		return getDockingManager().applicationKey;
	}
	
	public static DockingPort getDockingPort(Component dockable) {
		return DockingUtility.getParentDockingPort(dockable);
	}	
	
	public static DockingPort getDockingPort(Dockable dockable) {
		return DockingUtility.getParentDockingPort(dockable);
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
	
	public static DockingPortProps getDockingPortRoot() {
		return PropertyManager.getDockingPortRoot();
	}
	
	public static DockableProps getDockableRoot() {
		return PropertyManager.getDockableRoot();
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

	public static LayoutManager getLayoutManager() {
		return getDockingManager().layoutManager;
	}
	
	public static MinimizationManager getMinimizeManager() {
		MinimizationManager mgr = getDockingManager().minimizeManager;
		return mgr==null? MinimizationManager.DEFAULT_STUB: mgr;
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
	
	
	
	
	
	
	
	
	
	
	
	
	public static void setApplicationKey(String appKey) {
		getDockingManager().applicationKey = appKey;
	}
	
	public static void setAutoPersist(boolean b) {
		getDockingManager().autoPersister.setEnabled(b);
	}
	
	public static void setSplitProportion(Component dockable, float proportion) {
		setSplitProportion(getDockable(dockable), proportion);
	}
	
	public static void setSplitProportion(Dockable dockable, float proportion) {
		DockingUtility.setSplitProportion(dockable, proportion);
	}
	
	public static void setSplitProportion(DockingPort port, float proportion) {
		DockingUtility.setSplitProportion(port, proportion);
	}
	
	public static void setDockableBuilder(DockableBuilder builder) {
		getDockingManager().dockableBuilder = builder;
	}
	
	public static void setMinimized(Dockable dockable, boolean minimized) {
		Component cmp = dockable==null? null: dockable.getComponent();
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

	public static void setMainDockingPort(Component comp, String portId) {
		RootDockingPortInfo info = getRootDockingPortInfo(comp);
		if(info!=null)
			info.setMainPort(portId);		
	}
	
	public static void setMinimizeManager(MinimizationManager mgr) {
		getDockingManager().minimizeManager = mgr;
	}
	
	public static void setMinimizeManager(String mgrClass) {
		Object instance = Utilities.getInstance(mgrClass);
		setMinimizeManager((MinimizationManager)instance);
	}
	
	public static void setFloatingEnabled(boolean enabled) {
		FloatPolicyManager.setGlobalFloatingEnabled(enabled);
	}
	
	public static void setLayoutManager(LayoutManager mgr) {
		getDockingManager().layoutManager = mgr;
	}
	
	public static void setLayoutManager(String mgrClass) {
		Object instance = Utilities.getInstance(mgrClass);
		setLayoutManager((LayoutManager)instance);
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
	
	
	
	/**
	 * Undocks the specified <code>Dockable</code> instance from its containing <code>DockingPort</code>.
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
	
	
	
	
	
	

}
