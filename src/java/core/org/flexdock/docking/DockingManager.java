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
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.SwingUtilities;

import org.flexdock.docking.adapter.AdapterFactory;
import org.flexdock.docking.adapter.DockingAdapter;
import org.flexdock.docking.defaults.DefaultDockingStrategy;
import org.flexdock.docking.defaults.DockableComponentWrapper;
import org.flexdock.docking.drag.DragManager;
import org.flexdock.docking.drag.effects.DragPreview;
import org.flexdock.docking.drag.effects.EffectsManager;
import org.flexdock.docking.drag.effects.RubberBand;
import org.flexdock.docking.event.DockingEventHandler;
import org.flexdock.docking.event.hierarchy.DockingPortTracker;
import org.flexdock.docking.event.hierarchy.RootDockingPortInfo;
import org.flexdock.docking.floating.policy.FloatPolicyManager;
import org.flexdock.docking.props.DockablePropertySet;
import org.flexdock.docking.props.PropertyManager;
import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.FloatManager;
import org.flexdock.docking.state.LayoutManager;
import org.flexdock.docking.state.MinimizationManager;
import org.flexdock.docking.state.PersistenceException;
import org.flexdock.event.EventManager;
import org.flexdock.event.RegistrationEvent;
import org.flexdock.util.ClassMapping;
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

	private String defaultLayoutManagerClass;
	private String defaultMinimizeManagerClass;
	
	private DockingStrategy defaultDocker;
	private LayoutManager layoutManager;
	private MinimizationManager minimizeManager;
	private DockableFactory dockableFactory;
	private AutoPersist autoPersister;
	private float defaultSiblingSize;
	
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
		
		// ensure our Dockable adapters have been loaded
		AdapterFactory.prime();
		
		// make sure dockingEvents are properly intercepted
		EventManager.addHandler(new DockingEventHandler());
		EventManager.addListener(FloatPolicyManager.getInstance());
		
		Properties config = ResourceManager.getProperties(CONFIG_PROPS, true);
		DockingManager mgr = getDockingManager();
		// set the minimization manager
		mgr.defaultMinimizeManagerClass = config.getProperty(MINIMIZE_MANAGER);
		setMinimizeManager(mgr.defaultMinimizeManagerClass);
		// set the layout manager
		mgr.defaultLayoutManagerClass = config.getProperty(LAYOUT_MANAGER); 
		setLayoutManager(mgr.defaultLayoutManagerClass);
		// setup the default sibling size
		float siblingSize = Utilities.getFloat(System.getProperty(RegionChecker.DEFAULT_SIBLING_SIZE_KEY), RegionChecker.DEFAULT_SIBLING_SIZE);
		setDefaultSiblingSize(siblingSize);
		
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

	public static void addDragSource(Dockable dockable, Component dragSrc) {
		List sources = dockable==null? null: dockable.getDragSources();
		if(sources==null || dragSrc==null)
			return;
		
		if(!sources.contains(dragSrc)) {
			updateDragListeners(dockable);			
		}
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
		Dockable d = resolveDockable(dockable);
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
	
	private static Dockable resolveDockable(Component comp) {
		if(comp==null)
			return null;
		
		Dockable d = getDockable(comp);
		if(d==null)
			d = registerDockable(comp);
		return d;
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
		return dock(resolveDockable(dockable), resolveDockable(parent));
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
		Dockable newDockable = resolveDockable(dockable);
		Dockable parentDockable = resolveDockable(parent);
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
		return DockingUtility.dockRelative(dockable, parent, region, proportion);
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
	 * Indicates whether tabbed layouts are supported by default for <code>DockingPorts</code> 
	 * with a single <code>Dockable</code> in the CENTER region.  This is a global default setting
	 * and applies to any <cod>DockingPort</code> that does not have a specific contradictory
	 * local setting.
	 * </br>
	 * This method defers processing to 
	 * <code>org.flexdock.docking.props.PropertyManager.getDockingPortRoot()</code>.  As such, 
	 * there are multiple "scopes" at which this property may be overridden.
	 * 
	 * @return <code>true</code> if the default setting for <code>DockingPorts</code> allows
	 * a tabbed layout for a single <code>Dockable</code> in the CENTER region; <code>false</code>
	 * otherwise.
	 * @see PropertyManager#getDockingPortRoot()
	 * @see org.flexdock.docking.props.DockingPortPropertySet#isSingleTabsAllowed()
	 */
	public static boolean isSingleTabsAllowed() {
		return PropertyManager.getDockingPortRoot().isSingleTabsAllowed().booleanValue();
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
	 * Creates, registers, and returns a <code>Dockable</code> for the specified <code>Component</code>.
	 * If the specified <code>Component</code> implements the <code>Dockable</code> interface, 
	 * then this method dispatches to <code>registerDockable(Dockable dockable)</code>.  Otherwise,
	 * this method dispatches to <code>registerDockable(Component comp, String tabText)</code>.
	 * <br/>
	 * This method attempts to resolve an appropriate value for <code>tabText</code> by calling
	 * <code>getName()</code> on the specified <code>Component</code>.  If the resolved value
	 * is <code>null</code> or empty, then the value <code>"null"</code> is used.
	 * <br/>
	 * If <code>comp</code> is <code>null</code>, no exception is thrown and no action is performed.
	 *
	 * @param comp the target component for the <code>Dockable</code>.
	 * @return the <code>Dockable</code> that has been registered for the supplied <code>Component</code>
	 * @see #registerDockable(Dockable)
	 * @see #registerDockable(Component, String)
	 */
	public static Dockable registerDockable(Component comp) {
		if (comp == null)
			return null;

		if(comp instanceof Dockable)
			return registerDockable((Dockable)comp);
		
		return registerDockable(comp, null, null);
	}
	
	private static String determineTabText(Component comp, String persistId) {
		String tabText = null;
		// if 'comp' is a DockingStub, then we may be able to
		// pull the tab text from it
		if(comp instanceof DockingStub) {
			tabText = ((DockingStub)comp).getTabText();
		}
		else {
			// if we can find an adapter mapping, then try to pull
			// the tab text from there
			DockingAdapter adapter = AdapterFactory.getAdapter(comp);
			if(adapter!=null)
				tabText = adapter.getTabText();
		}

		// if 'comp' wasn't a DockingStub, or the stub returned a null tabText, 
		// then try the component name
		if(tabText==null)
			tabText = comp.getName();

		// if tabText is still null, then use the persistentId
		if(tabText==null)
			tabText = persistId;
		
		// get rid of null and empty cases.  use the string "null" if nothing
		// else can be found
		tabText = tabText==null? "null": tabText.trim();
		if(tabText.length()==0)
			tabText = "null";
		
		return tabText;
	}
	
	/**
	 * Creates a <code>Dockable</code> for the specified <code>Component</code> and dispatches to 
	 * <code>registerDockable(Dockable init)</code>. If <code>comp</code> is <code>null</code>, 
	 * no exception is thrown and no action is performed.
	 *
	 * @param comp the target component for the Dockable, both drag-starter and docking source
	 * @param tabText the description of the docking source.  Used as the tab-title of docked in a tabbed pane
	 * @return the <code>Dockable</code> that has been registered for the supplied <code>Component</code>
	 * @see #registerDockable(Dockable)
	 */
	public static Dockable registerDockable(Component comp, String tabText) {
		return registerDockable(comp, tabText, null);
	}
	
	private static Dockable registerDockable(Component comp, String tabText, String dockingId) {
		if (comp == null)
			return null;

		if(tabText==null)
			tabText = determineTabText(comp, dockingId);
		
		Dockable dockable = getDockableForComponent(comp, tabText, dockingId);
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

		// cache the dockable by ID
		DOCKABLES_BY_ID.put(dockable.getPersistentId(), dockable);
		
		// make sure we have docking-properties initialized (must come after ID-caching)
		DockablePropertySet props = PropertyManager.getDockablePropertySet(dockable);
		
		// dispatch a registration event
		EventManager.dispatch(new RegistrationEvent(dockable, DockingManager.SINGLETON, true));
		
		// return the dockable
		return dockable;
	}
	
	public static void unregisterDockable(Component comp) {
		Dockable dockable = getDockable(comp);
		unregisterDockable(dockable);
	}
	
	public static void unregisterDockable(String dockingId) {
		Dockable dockable = getDockableImpl(dockingId);
		unregisterDockable(dockable);
	}
	
	public static void unregisterDockable(Dockable dockable) {
		if(dockable==null)
			return;
		
		synchronized(DOCKABLES_BY_COMPONENT) {
			DOCKABLES_BY_COMPONENT.remove(dockable.getComponent());	
		}
		
		// flag the component as dockable, in case it doesn't 
		// implement the interface directly
		Component c = dockable.getComponent();
		SwingUtility.removeClientProperty(c, Dockable.DOCKABLE_INDICATOR);
		
		// remove the drag listeners
		removeDragListeners(dockable);
		
		// remove the dockable as its own listener
		dockable.removeDockingListener(dockable);
		
		// unlink the propertySet
		PropertyManager.removePropertySet(dockable);
		
		// remove the dockable by ID
		synchronized(DOCKABLES_BY_ID) {
			DOCKABLES_BY_ID.remove(dockable.getPersistentId());
		}
		
		// dispatch a registration event
		EventManager.dispatch(new RegistrationEvent(dockable, DockingManager.SINGLETON, false));
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
	 * <code>org.flexdock.util.ClassMapping</code>.  If a mapping for <code>classKey</code>
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
	
	/**
	 * Sends the application's current layout model to external storage.  This method 
	 * defers processing to the currently installed 
	 * <code>org.flexdock.docking.state.LayoutManager</code> by invoking its <code>store()</code>
	 * method.  If there is no <code>LayoutManager</code> installed, then this method
	 * returns <code>false</code>.
	 * <br/>
	 * The layout model itself, along with storage mechanism, is abstract and dependent 
	 * upon the particular <code>LayoutManager</code> implementation.  As such, it may be 
	 * possible that the <code>LayoutManager</code> is unable to persist the current layout 
	 * state for non-Exceptional reasons.  This method returns <code>true</code> if the layout
	 * model was successfully stored and <code>false</code> if the layout model could not be
	 * stored under circumstances that do not generate an <code>Exception</code> (for instance, 
	 * if there is no persistence implementation currently installed).  If a problem occurs 
	 * during the persistence process, an <code>IOException</code> is thrown.
	 * 
	 * @return <code>true</code> if the current layout model was succesfully stored, 
	 * <code>false</code> otherwise.
	 * @throws IOException
	 * @throws PersisterException 
	 * @see #getLayoutManager()
	 * @see #setLayoutManager(LayoutManager)
	 * @see LayoutManager#store()
	 */
	public static boolean storeLayoutModel() throws IOException, PersistenceException {
		LayoutManager mgr = getLayoutManager();
		return mgr==null? false: mgr.store();
	}
	
	/**
	 * Loads a previously stored layout model into the currently installed <code>LayoutManager</code>.
	 * This method defers processing to <code>loadLayoutModel(boolean restore)</code> with
	 * an argument of <code>false</code> to indicate that the stored data model should merely 
	 * be loaded into memory and the <code>LayoutManager</code> should not attempt to subsequently
	 * restore the application view by synchronizing it against the newly loaded data model.
	 * <br/>
	 * The layout model itself, along with storage mechanism, is abstract and dependent 
	 * upon the particular <code>LayoutManager</code> implementation.  As such, it may be 
	 * possible that the <code>LayoutManager</code> is unable to load the previous layout 
	 * state for non-Exceptional reasons.  This method returns <code>true</code> if the layout
	 * model was successfully loaded and <code>false</code> if the layout model could not be
	 * loaded under circumstances that do not generate an <code>Exception</code> (for instance, 
	 * if there was no previous layout model found in storage).  If a problem occurs 
	 * during the loading process, an <code>IOException</code> is thrown.
	 * 
	 * @return <code>true</code> if the current layout model was succesfully loaded, 
	 * <code>false</code> otherwise.
	 * @throws IOException
	 * @throws PersisterException 
	 * @see #loadLayoutModel(boolean)
	 * @see LayoutManager#load()
	 */
	public static boolean loadLayoutModel() throws IOException, PersistenceException {
		return loadLayoutModel(false);
	}
	
	/**
	 * Loads a previously stored layout model into the currently installed <code>LayoutManager</code>
	 * and attempts to synchronize the application view with the newly loaded layout model if the 
	 * <code>restore</code> parameter is <code>true</code>. If there is no currently installed 
	 * <code>LayoutManager</code>, then this method 
	 * returns <code>false</code>.  If the <code>restore</code> parameter is <code>true</code>, 
	 * then this method defers processing to <code>restoreLayout(boolean loadFromStorage)</code> 
	 * with an argument of <code>true</code>.  Otherwise, this method defers processing to the 
	 * currently installed <code>org.flexdock.docking.state.LayoutManager</code> by invoking 
	 * its <code>load()</code> method.
	 * <br/>
	 * The layout model itself, along with storage mechanism, is abstract and dependent 
	 * upon the particular <code>LayoutManager</code> implementation.  As such, it may be 
	 * possible that the <code>LayoutManager</code> is unable to load the previous layout 
	 * state for non-Exceptional reasons.  This method returns <code>true</code> if the layout
	 * model was successfully loaded and <code>false</code> if the layout model could not be
	 * loaded under circumstances that do not generate an <code>Exception</code> (for instance, 
	 * if there was no previous layout model found in storage).  If a problem occurs 
	 * during the loading process, an <code>IOException</code> is thrown.
	 * 
	 * @return <code>true</code> if the current layout model was succesfully loaded, 
	 * <code>false</code> otherwise.
	 * @throws IOException
	 * @throws PersisterException 
	 * @see #getLayoutManager()
	 * @see #setLayoutManager(LayoutManager)
	 * @see #restoreLayout(boolean)
	 * @see LayoutManager#load()
	 */
	public static boolean loadLayoutModel(boolean restore) throws IOException, PersistenceException {
		LayoutManager mgr = getLayoutManager();
		if(mgr==null)
			return false;

		return restore? restoreLayout(true): mgr.load();
	}
	
	
	/**
	 * Synchronizes the application view with the current in-memory layout model.  This method 
	 * defers processing to <code>restoreLayout(boolean loadFromStorage)</code> with an 
	 * argument of <code>false</code>. This instructs the currently installed 
	 * <code>LayoutManager</code> to restore the application view to match the current 
	 * in-memory layout model without reloading from storage prior to restoration.  This method
	 * is useful for developers who choose to construct a layout model programmatically and
	 * wish to "commit" it to the application view, restoring their own in-memory layout model
	 * rather than a model persisted in external storage.
	 * <br/>
	 * If there is no <code>LayoutManager</code> currently installed, then this method returns 
	 * <code>false</code>.
	 *  
	 * @return <code>true</code> if the in-memory layout model was properly restored to the 
	 * application view, <code>false</code> otherwise.
	 * @throws PersisterException 
	 * @see #restoreLayout(boolean)
	 * @see #getLayoutManager()
	 * @see #setLayoutManager(LayoutManager)
	 * @see LayoutManager#restore(boolean)
	 */
	public static boolean restoreLayout() {
		try {
			return restoreLayout(false);
		} catch(IOException e) {
			// shouldn't happen since we're not intending to load from storage
			e.printStackTrace();
			return false;
		} catch (PersistenceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
		}
	}
	
	/**
	 * Synchronizes the application view with the current in-memory layout model.  This method 
	 * defers processing to the currently installed 
	 * <code>org.flexdock.docking.state.LayoutManager</code> by invoking its  
	 * <code>restore(boolean loadFromStorage)</code> method. If there is no 
	 * <code>LayoutManager</code> currently installed, then this method returns <code>false</code>.
	 * <br/>
	 * If the <code>loadFromStorage</code> parameter is <code>true</code>, then the 
	 * <code>LayoutManager</code> is instructed to load any persisted layout model from 
	 * external storage into memory before synchronizing the application view.  If a
	 * problem occurs while loading from exernal storage, this method throws an 
	 * <code>IOException</code>. 
	 *  
	 * @param loadFromStorage instructs whether to load any layout model from external storage
	 * into memory before synchronizing the application view.
	 * @return <code>true</code> if the in-memory layout model was properly restored to the 
	 * application view, <code>false</code> otherwise.
	 * @throws PersisterException 
	 * @see #getLayoutManager()
	 * @see #setLayoutManager(LayoutManager)
	 * @see LayoutManager#restore(boolean)
	 */
	public static boolean restoreLayout(boolean loadFromStorage) throws IOException, PersistenceException {
		LayoutManager mgr = getLayoutManager();
		return mgr==null? false: mgr.restore(loadFromStorage);
	}
	
	private static Dockable loadAndRegister(String id) {
		DockableFactory factory = id==null? null: getDockingManager().dockableFactory;
		if(factory==null)
			return null;
		
		// the getDockableComponent() implementation may or may not
		// automatically register a dockable before returning.
		
		// first, try to get a Dockable from the factory
		Dockable dockable = factory.getDockable(id);
		if(dockable!=null) {
			// check to see if the dockable is already registered.
			Dockable tmp = getDockableImpl(dockable.getPersistentId());
			if(tmp==null)
				registerDockable(dockable);
		}
		// if we couldn't find a dockable from the factory, then try getting
		// a component.
		else {
			Component comp = factory.getDockableComponent(id);
			// we already weren't able to get a Dockable from the factory.  If
			// we couldn't get a Component either, then give up.
			if(comp==null)
				return null;

			// if the newly created dockable has not yet been registered, 
			// then register it.
			dockable = getDockable(comp);
			if(dockable==null) {
				dockable = registerDockable(comp, null, id);
			}
		}
		
		return dockable;
	}

	private static Dockable getDragInitiator(Component c) {
		return getDockableForComponent(c, null, null);
	}

	private static Dockable getDockableForComponent(Component c, String desc, String dockingId) {
		if (c == null)
			return null;

		// return the dockable if it has already been registered
		Dockable dockable = getDockable(c);
		if(dockable!=null)
			return dockable;
		
		// if we need to create a dockable, first try to do it with an adapter
		DockingAdapter adapter = AdapterFactory.getAdapter(c);
		if(adapter!=null) {
			dockable = DockableComponentWrapper.create(adapter);
		}

		// if we weren't able to create from an adapter, then create the 
		// dockable manually
		if(dockable==null) {
			if(c instanceof DockingStub) {
				dockable = DockableComponentWrapper.create((DockingStub)c);
			}
			else {
				String persistentId = dockingId==null? generatePersistentId(c): dockingId;
				dockable = DockableComponentWrapper.create(c, persistentId, desc);				
			}
		}
		
		// make sure the specified description is applied
		if(desc!=null)
			dockable.getDockingProperties().setDockableDesc(desc);
		
		// cache the dockable for future use
		DOCKABLES_BY_COMPONENT.put(c, dockable);
		
		// now we can return
		return dockable;
	}
	
	/**
	 * Returns the <code>DockingPort</code> that contains the specified <code>Component</code>.
	 * If the <code>Component</code> is <code>null</code>, then a <code>null</code> reference
	 * is returned.
	 * <br/>
	 * This method will only return the immediate parent <code>DockingPort</code> of the 
	 * specified <code>Component</code>  This means that the <code>DockingPort</code> returned 
	 * by this method will not only be an ancestor <code>Container</code> of the specified 
	 * <code>Component</code>, but invoking its <code>isParentDockingPort(Component comp)</code>
	 * with the specified <code>Component</code> will also return <code>true</code>.  If both 
	 * of these conditions cannot be satisfied, then this method returns a <code>null</code> 
	 * reference.
	 * 
	 * @param dockable the <code>Component</code> whose parent <code>DockingPort</code> is to 
	 * be returned.
	 * @return the imediate parent <code>DockingPort</code> that contains the specified 
	 * <code>Component</code>.
	 */
	public static DockingPort getDockingPort(Component dockable) {
		return DockingUtility.getParentDockingPort(dockable);
	}	
	
	/**
	 * Returns the <code>DockingPort</code> that contains the specified <code>Dockable</code>.
	 * If the <code>Dockable</code> is <code>null</code>, then a <code>null</code> reference
	 * is returned.
	 * <br/>
	 * This method will only return the immediate parent <code>DockingPort</code> of the 
	 * specified <code>Dockable</code>  This means that a check is performed for the 
	 * <code>Component</code> returned by the <code>Dockable's</code> <code>getComponent()</code>
	 * method.  The <code>DockingPort</code> returned 
	 * by this method will not only be an ancestor <code>Container</code> of this 
	 * <code>Component</code>, but invoking the <code>DockingPort's</code> 
	 * <code>isParentDockingPort(Component comp)</code>
	 * with the this <code>Component</code> will also return <code>true</code>.  If both 
	 * of these conditions cannot be satisfied, then this method returns a <code>null</code> 
	 * reference.
	 * 
	 * @param dockable the <code>Dockable</code> whose parent <code>DockingPort</code> is to 
	 * be returned.
	 * @return the imediate parent <code>DockingPort</code> that contains the specified 
	 * <code>Dockable</code>.
	 */
	public static DockingPort getDockingPort(Dockable dockable) {
		return DockingUtility.getParentDockingPort(dockable);
	}
	
	/**
	 * Returns the <code>Dockable</code> instance that models the specified <code>Component</code>.
	 * The <code>Dockable</code> returned by this method will return a reference to <code>comp</code>
	 * when its <code>getComponent()</code> method is called.  If <code>comp</code> is 
	 * <code>null</code>, then this method will return a <code>null</code> reference.
	 * <br/>
	 * The association between <code>Dockable</code> and <code>Component</code> is established
	 * internally during <code>registerDockable(Dockable dockable)</code>.  Thus, 
	 * <code>registerDockable(Dockable dockable)</code> must have been called previously for 
	 * a mapping to be found and a <code>Dockable</code> to be returned by this method.  If
	 * no mapping is found for the specified <code>Component</code>, then this method returns
	 * a <code>null</code> reference.
	 * 
	 * @param comp the <code>Component</code> whose <code>Dockable</code> instance is to be returned.
	 * @return the <code>Dockable</code> that models the specified <code>Component</code>
	 * @see #registerDockable(Dockable)
	 * @see Dockable#getComponent()
	 */
	public static Dockable getDockable(Component comp) {
		return comp==null? null: (Dockable)DOCKABLES_BY_COMPONENT.get(comp);
	}
	
	/**
	 * Returns the <code>Dockable</code> instance with the specified ID.
	 * The <code>Dockable</code> returned by this method will return a String 
	 * equal <code>id</code> when its <code>getPersistentId()</code> method is called.  
	 * If <code>id</code> is <code>null</code>, then this method will return a <code>null</code> 
	 * reference.
	 * <br/>
	 * The association between <code>Dockable</code> and <code>id</code> is established
	 * internally during <code>registerDockable(Dockable dockable)</code>.  Thus, 
	 * <code>registerDockable(Dockable dockable)</code> must have been called previously for 
	 * a mapping to be found and a <code>Dockable</code> to be returned by this method.  If
	 * no mapping is found for the specified <code>id</code>, then this method returns
	 * a <code>null</code> reference.
	 * 
	 * @param id the persistent ID of the <code>Dockable</code> instance is to be returned.
	 * @return the <code>Dockable</code> that has the specified perstent ID.
	 * @see #registerDockable(Dockable)
	 * @see Dockable#getPersistentId()
	 */
	public static Dockable getDockable(String id) {
		if(id==null)
			return null;
		
		Dockable dockable = getDockableImpl(id);
		if(dockable==null)
			dockable = loadAndRegister(id);
		return dockable;
	}
	
	private static Dockable getDockableImpl(String id) {
		synchronized(DOCKABLES_BY_ID) {
			return id==null? null: (Dockable)DOCKABLES_BY_ID.get(id);
		}
	}
	
	/**
	 * Returns a <code>Set</code> of <code>String</code> IDs for all <code>Dockables</code>
	 * registered with the framework.  The IDs returned by this method will correspond to
	 * the values returned for the <code>getPersistentId()</code> method for each 
	 * <code>Dockable</code> registered with the framework.
	 * 
	 * <code>Dockable</code> IDs are cached during <code>registerDockable(Dockable dockable)</code>.
	 * Thus, for an ID to appear within the <code>Set</code> returned by this method, the
	 * corresponding <code>Dockable</code> must have first been registered via
	 * <code>registerDockable(Dockable dockable)</code>.
	 * 
	 * If no <code>Dockables</code> have been registered with the framework, then an 
	 * empty <code>Set</code> is returned.  This method will never return a <code>null</code>
	 * reference.
	 * 
	 * @return a <code>Set</code> of <code>String</code> IDs for all <code>Dockables</code>
	 * registered with the framework.
	 * @see #registerDockable(Dockable)
	 * @see Dockable#getPersistentId()
	 */
	public static Set getDockableIds() {
		synchronized(DOCKABLES_BY_ID) {
			return new HashSet(DOCKABLES_BY_ID.keySet());
		}
	}
	
	/**
	 * Returns the listener object responsible for managing drag-to-dock mouse
	 * events for the specified <code>Dockable</code>.  During registration, the
	 * listener is added to each of the <code>Components</code> within the 
	 * <code>Dockable's</code> <code>getDragSources()</code> <code>List</code>.  Thus, 
	 * for this method to return a valid <code>DragManager</code> instance, the 
	 * <code>Dockable</code> must first have been registered via 
	 * <code>registerDockable(Dockable dockable)</code>.  If the specified <code>Dockable</code>
	 * is <code>null</code> or its <code>getDragSources()</code> method returns a <code>null</code>, 
	 * or if the <code>Dockable</code> has not previously been registered, this method will
	 * return a <code>null</code> reference.
	 * 
	 * @param dockable the <code>Dockable</code> whose drag listener is to be returned.
	 * @return the <code>DragManager</code> responsible for listening to an managing drag-related
	 * mouse events for the specified <code>Dockable</code>.
	 * @see DragManager
	 * @see Dockable#getDragSources()
	 * @see #registerDockable(Dockable)
	 */
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

	
	/**
	 * Returns the currently installed <code>LayoutManager</code>.  The <code>LayoutManager</code>
	 * is responsible for managing docking layout state.  This includes tracking the state
	 * for all <code>Dockables</code> as they are embedded, minimized, floated, or hidden.  If
	 * a <code>Dockable</code> is embedded, the <code>LayoutManager</code> is responsible for
	 * tracking its position and size relative to other embedded <code>Dockables</code>.  If 
	 * floating, the <code>LayoutManager</code> is responsible for supplying a 
	 * <code>FloatManager</code> to maintain <code>Dockable</code> groupings within dialogs as well
	 * as dialog size and positioning.
	 * </br>
	 * The <code>LayoutManager</code> is responsible for providing a persistence mechanism to 
	 * save and restore layout states.  Depending on the <code>LayoutManager</code> implementation, 
	 * it may or may not support multiple layout models that may be loaded and switched between
	 * at runtime.
	 * <br/>
	 * Because the <code>LayoutManager</code> is a critical piece of the docking infrastructure, 
	 * it is not possible to install a <code>null</code> <code>LayoutManager</code>.  Therefore, 
	 * this method will always return a valid <code>LayoutManager</code> and never a 
	 * <code>null</code> reference.
	 * 
	 * @return the currently installed <code>LayoutManager</code>
	 * @see LayoutManager
	 * @see #setLayoutManager(LayoutManager)
	 * @see #setLayoutManager(String)
	 */
	public static LayoutManager getLayoutManager() {
		return getDockingManager().layoutManager;
	}
	
	/**
	 * Returns the currently installed <code>MinimizationManager</code>.  The 
	 * <code>MinimizationManager</code> is responsible for minimizing and unminimizing
	 * <code>Dockables</code>, removing from and restoring to the embedded docking layout
	 * through the currently installed <code>LayoutManager</code>.  
	 * <br/>
	 * The visual representation
	 * of a "minimized" <code>Dockable</code> is somewhat abstract, although it is commonly 
	 * expressed in user interfaces with the disappearance of the <code>Dockable</code> from
	 * the layout and the addition of a tab or label on one or more edges of the application 
	 * window.  The <code>MinimizationManager</code> implementation itself is responsible
	 * for interpreting the visual characteristics and behavior of a minimized <code>Dockable</code>, 
	 * but it must provide a "preview" feature to allow viewing of minimized <code>Dockables</code>, 
	 * on demand without actually restoring them to the embedded docking layout.  
	 * <code>Dockables</code> may or may not have limited docking functionality while in 
	 * minimized and/or preview state, depending upon the <code>MinimizationManager</code>
	 * implementation.
	 * <br/>
	 * Because the <code>MinimizationManager</code> is a critical piece of the docking 
	 * infrastructure, it cannot be set to <code>null</code>.  Therefore, this method will
	 * always return a valid <code>MinimizationManager<code> and never a <code>null</code>
	 * reference.
	 * 
	 * @return the currently installed <code>MinimizationManager</code>.
	 * @see MinimizationManager
	 * @see #setMinimizeManager(MinimizationManager)
	 * @see #setMinimizeManager(String)
	 */
	public static MinimizationManager getMinimizeManager() {
		MinimizationManager mgr = getDockingManager().minimizeManager;
		return mgr==null? MinimizationManager.DEFAULT_STUB: mgr;
	}

	/**
	 * Returns the currently installed <code>FloatManager</code>.  The <code>FloatManager</code>
	 * is actually provided by the currently installed <code>LayoutManager</code>.  As such, 
	 * this method is merely for convenience.  It internally obtains the installed 
	 * <code>LayoutManager</code> via <code>getLayoutManager()</code> and invokes its 
	 * <code>getFloatManager()</code> method.
	 * <br/>
	 * The <code>FloatManager</code> maintains information relevant to floating <code>Dockables</code> 
	 * including grouping them together within dialogs and tracking dialog size and position.  
	 * The <code>FloatManager</code> is responsible for generating new dialogs, parenting on the 
	 * proper application window(s), and sending <code>Dockables</code> to the proper dialogs.  
	 * It may be used by the <code>LayoutManager</code> to restore hidden <code>Dockables</code> 
	 * to proper floating state as needed.
	 * <br/>
	 * Since the <code>FloatManager</code> is provided by the currently installed 
	 * <code>LayoutManager</code>, it cannot be set from within the <code>DockingManager</code>.
	 * To change the installed <code>FloatManager</code>, one must work directly with the 
	 * installed <code>LayoutManager</code> implementation per its particular custom API.
	 * <br/>
	 * Since the <code>FloatManager</code> is a critical piece of the docking insfrastructure, 
	 * this method will never return a <code>null</code> reference.
	 * 
	 * @return the <code>FloatManager</code> provided by the currently installed <code>LayoutManager</code>
	 * @see #getLayoutManager()
	 * @see #setLayoutManager(LayoutManager)
	 * @see LayoutManager#getFloatManager()
	 */
	public static FloatManager getFloatManager() {
		return getLayoutManager().getFloatManager();
	}
	
	/**
	 * Returns the <code>DockingState</code> for the <code>Dockable</code> with the specified
	 * ID.  The <code>DockingState</code> is used by the currently installed <code>LayoutManager</code>
	 * to track information regarding a <code>Dockable's</code> current state in the docking layout.
	 * This includes relative size and positioning to other <code>Dockables</code>, minimization
	 * status, floating status, and any other information used to track and potentially restore a 
	 * the <code>Dockable</code> to the layout if it is currently hidden.
	 * <br/>
	 * The <code>Dockable</code> whose current <code>DockingState</code> is resolved will 
	 * map to the specified <code>dockableId</code> via its <code>getPersistentId()</code> method.
	 * The semantics of this mapping relationship are the same as 
	 * <code>DockingManager.getDockable(String id)</code>.  If a valid <code>Dockable</code>
	 * cannot be found for the specified ID, then this method returns a <code>null</code> 
	 * reference.
	 * <br/>
	 * The <code>DockingState</code> for any given <code>Dockable</code> is ultimately managed by
	 * the currently installed <code>LayoutManager</code>.  Therefore, this method resolves the
	 * <code>LayoutManager</code> via <code>getLayoutManager()</code> and defers processing to 
	 * its <code>getDockingState(String dockableId)</code> method.
	 * <br/>
	 * The underlying <code>LayoutManager</code> does not provide any guarantees that the same
	 * <code>DockingState</code> reference always will be returned for a given <code>Dockable</code>; 
	 * only that the returned <code>DockingState<code> will accurately reflect the current state
	 * maintained by the <code>LayoutManager</code> for that <code>Dockable</code>.  For instance, 
	 * if the <code>LayoutManager</code> is capable of maintaining multiple layouts for an
	 * application (as Eclipse does between perspectives), then the <code>LayoutManager</code> may
	 * or may not maintain multiple <code>DockingState</code> instances for a single 
	 * <code>Dockable</code>, one within each layout context.  Therefore, it is not a good idea to
	 * cache references to the <code>DockingState</code> instance returned by this method for future
	 * use as the reference itself may possibly become stale over time depending on the 
	 * <code>LayoutManager</code> implementation.
	 * 
	 * @param dockableId the persistent ID of the <code>Dockable</code> whose current <code>DockingState</code>
	 * is to be returned
	 * @return the current <code>DockingState</code> maintained by the <code>LayoutManager<code> for
	 * the specified <code>Dockable</code>
	 * @see DockingState
	 * @see #getLayoutManager()
	 * @see LayoutManager#getDockingState(String)
	 * @see #getDockable(String)
	 * @see Dockable#getPersistentId()
	 */
	public static DockingState getDockingState(String dockableId) {
		return getLayoutManager().getDockingState(dockableId);
	}
	
	/**
	 * Returns the <code>DockingState</code> for the specified <code>Dockable</code>.
	 * The <code>DockingState</code> is used by the currently installed <code>LayoutManager</code>
	 * to track information regarding a <code>Dockable's</code> current state in the docking layout.
	 * This includes relative size and positioning to other <code>Dockables</code>, minimization
	 * status, floating status, and any other information used to track and potentially restore a 
	 * the <code>Dockable</code> to the layout if it is currently hidden.
	 * <br/>
	 * If the <code>dockable</code> parameter is <code>null</code>, then this method returns a 
	 * <code>null</code> reference.
	 * <br/>
	 * The <code>DockingState</code> for any given <code>Dockable</code> is ultimately managed by
	 * the currently installed <code>LayoutManager</code>.  Therefore, this method resolves the
	 * <code>LayoutManager</code> via <code>getLayoutManager()</code> and defers processing to 
	 * its <code>getDockingState(String dockableId)</code> method.
	 * <br/>
	 * The underlying <code>LayoutManager</code> does not provide any guarantees that the same
	 * <code>DockingState</code> reference always will be returned for a given <code>Dockable</code>; 
	 * only that the returned <code>DockingState<code> will accurately reflect the current state
	 * maintained by the <code>LayoutManager</code> for that <code>Dockable</code>.  For instance, 
	 * if the <code>LayoutManager</code> is capable of maintaining multiple layouts for an
	 * application (as Eclipse does between perspectives), then the <code>LayoutManager</code> may
	 * or may not maintain multiple <code>DockingState</code> instances for a single 
	 * <code>Dockable</code>, one within each layout context.  Therefore, it is not a good idea to
	 * cache references to the <code>DockingState</code> instance returned by this method for future
	 * use as the reference itself may possibly become stale over time depending on the 
	 * <code>LayoutManager</code> implementation.
	 * 
	 * @param dockable the <code>Dockable</code> whose current <code>DockingState</code>
	 * is to be returned
	 * @return the current <code>DockingState</code> maintained by the <code>LayoutManager<code> for
	 * the specified <code>Dockable</code>
	 * @see #getLayoutManager()
	 * @see LayoutManager#getDockingState(String)
	 */
	public static DockingState getDockingState(Dockable dockable) {
		return getLayoutManager().getDockingState(dockable);
	}

	/**
	 * Returns the currently installed <code>DockableFactory</code>.  The <code>DockableFactory</code>
	 * installed by default is <code>null</code>.  Therefore, this method will return a 
	 * <code>null</code> reference until the application developer explicitly provides a 
	 * <code>DockableFactory</code> implementation via 
	 * <code>setDockableFactory(DockableFactory factory)</code>.
	 * <br/>
	 * Installing a <code>DockableFactory</code> allows FlexDock to seamlessly create and register
	 * <code>Dockables</code> within <code>getDockable(String id)</code>.  Generally, 
	 * <code>getDockable(String id)</code> will lookup the requested <code>Dockable</code> within the
	 * internal registry.  If not found, and there is no <code>DockableFactory</code> installed, 
	 * <code>getDockable(String id)</code> returns a <code>null</code> reference.  When a 
	 * <code>DockableFactory</code> is installed, however, failure to lookup a valid <code>Dockable</code>
	 * will cause <code>getDockable(String id)</code> to invoke the installed <code>DockableFactory's</code>
	 * <code>getDockable(String dockableId)</code> method, transparently registering and returning
	 * the newly created <code>Dockable</code> from <code>getDockable(String id)</code>.
	 * 
	 * @return the currently installed <code>DockableFactory</code>
	 * @see #getDockable(String)
	 * @see DockableFactory#getDockable(String)
	 */
	public static DockableFactory getDockableFactory() {
		return getDockingManager().dockableFactory;
	}
	
	/**
	 * Enables and disables auto-persistence of the current docking layout model when the application exits.
	 * Auto-persistence is disabled by default.
	 * <br/>
	 * The <code>storeLayoutModel()</code> provides a means of manually sending the docking layout model
	 * to some type of external storage.  When the <code>DockingManager</code> class loads, a shutdown hook 
	 * is added to the <code>Runtime</code>.  If auto-persist is enabled when the JVM exits, the shutdown hook
	 * automatically calls <code>storeLayoutModel()</code>, catching and reporting any <code>IOExceptions</code>
	 * that may occur.
	 * 
	 * @param enabled <code>true</code> if automatic persistence is desired; <code>false</code> otherwise.
	 * @see #storeLayoutModel()
	 * @see Runtime#addShutdownHook(java.lang.Thread)
	 */
	public static void setAutoPersist(boolean enabled) {
		getDockingManager().autoPersister.setEnabled(enabled);
	}
	
	/**
	 * Sets the divider location of the split layout containing the specified dockable <code>Component</code>.
	 * The <code>Dockable</code> instance associated with the specified <code>Component</code> is resolved
	 * via <code>getDockable(Component comp)</code> and processing is dispatched to 
	 * <code>setSplitProportion(Dockable dockable, float proportion)</code>.
	 * <br/>
	 * The resulting divider location will be a percentage of the split layout size based upon the 
	 * <code>proportion</code> parameter.  Valid values for <code>proportion</code> range from <code>0.0F<code>
	 * to <code>1.0F</code>.  For example, a <code>proportion</code> of <code>0.3F</code> will move the 
	 * divider to 30% of the "size" (<i>width</i> for horizontal split, <i>height</i> for vertical split) of the 
	 * split container that contains the specified <code>Component</code>.  If a <code>proportion</code> of less 
	 * than <code>0.0F</code> is supplied, the value </code>0.0F</code> is used.  If a <code>proportion</code> 
	 * greater than <code>1.0F</code> is supplied, the value </code>1.0F</code> is used.
	 * <br/>
	 * It is important to note that the split divider location is only a percentage of the container size 
	 * from left to right or top to bottom.  A <code>proportion</code> of <code>0.3F</code> does not imply 
	 * that <code>dockable</code> itself will be allotted 30% of the available space.  The split divider will 
	 * be moved to the 30% position of the split container regardless of the region in which the specified 
	 * <code>Component</code> resides (which may possibly result in <code>dockable</code> being allotted 70% of 
	 * the available space). 
	 * <br/>
	 * This method should be effective regardless of whether the split layout in question has been fully realized
	 * and is currently visible on the screen.  This should alleviate common problems associated with setting
	 * percentages of unrealized <code>Component</code> dimensions, which are initially <code>0x0</code> before
	 * the <code>Component</code> has been rendered to the screen.
	 * <br/>
	 * If the specified <code>Component</code> is <code>null</code>, then no <code>Exception</code> is thrown
	 * and no action is taken.  Identical behavior occurs if a valid <code>Dockable</code> cannot be resolved
	 * for the specified <code>Component</code>, or the <code>Dockable</code> does not reside within a split 
	 * layout.  
	 * <br/>
	 * If the <code>Dockable</code> resides within a tabbed layout, a check is done to see if the 
	 * tabbed layout resides within a parent split layout.  If so, the resolved split layout is resized.  
	 * Otherwise no action is taken.
	 * 
	 * @param dockable the <code>Component</code> whose containing split layout is to be resized.
	 * @param proportion the percentage of containing split layout size to which the split divider should be set.
	 * @see #setSplitProportion(Dockable, float)
	 * @see #getDockable(Component) 
	 */
	public static void setSplitProportion(Component dockable, float proportion) {
		setSplitProportion(getDockable(dockable), proportion);
	}
	
	/**
	 * Sets the divider location of the split layout containing the specified dockable <code>Component</code>.
	 * <br/>
	 * The resulting divider location will be a percentage of the split layout size based upon the 
	 * <code>proportion</code> parameter.  Valid values for <code>proportion</code> range from <code>0.0F<code>
	 * to <code>1.0F</code>.  For example, a <code>proportion</code> of <code>0.3F</code> will move the 
	 * divider to 30% of the "size" (<i>width</i> for horizontal split, <i>height</i> for vertical split) of the 
	 * split container that contains the specified <code>Dockable</code>.  If a <code>proportion</code> of less 
	 * than <code>0.0F</code> is supplied, the value </code>0.0F</code> is used.  If a <code>proportion</code> 
	 * greater than <code>1.0F</code> is supplied, the value </code>1.0F</code> is used.
	 * <br/>
	 * It is important to note that the split divider location is only a percentage of the container size 
	 * from left to right or top to bottom.  A <code>proportion</code> of <code>0.3F</code> does not imply 
	 * that <code>dockable</code> itself will be allotted 30% of the available space.  The split divider will 
	 * be moved to the 30% position of the split container regardless of the region in which the specified 
	 * <code>Dockable</code> resides (which may possibly result in <code>dockable</code> being allotted 70% of 
	 * the available space). 
	 * <br/>
	 * This method should be effective regardless of whether the split layout in question has been fully realized
	 * and is currently visible on the screen.  This should alleviate common problems associated with setting
	 * percentages of unrealized <code>Component</code> dimensions, which are initially <code>0x0</code> before
	 * the <code>Component</code> has been rendered to the screen.
	 * <br/>
	 * If the specified <code>Dockable</code> is <code>null</code>, then no <code>Exception</code> is thrown
	 * and no action is taken.  Identical behavior occurs if the <code>Dockable</code> does not reside within a 
	 * split layout.  
	 * <br/>
	 * If the <code>Dockable</code> resides within a tabbed layout, a check is done to see if the 
	 * tabbed layout resides within a parent split layout.  If so, the resolved split layout is resized.  
	 * Otherwise no action is taken.
	 * 
	 * @param dockable the <code>Dockable</code> whose containing split layout is to be resized.
	 * @param proportion the percentage of containing split layout size to which the split divider should be set.
	 * @see #getDockable(Component) 
	 */
	public static void setSplitProportion(Dockable dockable, float proportion) {
		DockingUtility.setSplitProportion(dockable, proportion);
	}
	
	/**
	 * Sets the divider location of the split layout embedded within the specified 
	 * <code>DockingPort</code>.  This method differs from both 
	 * <code>setSplitProportion(Component dockable, float proportion)</code> and
	 * <code>setSplitProportion(Dockable dockable, float proportion)</code> in that this method
	 * resolves the split layout embedded <b>within</b> the specified <code>DockingPort</code>, 
	 * whereas the other methods modify the split layout <b>containing</b> their respective 
	 * <code>Dockable</code> parameters.
	 * <br/>
	 * The resulting divider location will be a percentage of the split layout size based upon the 
	 * <code>proportion</code> parameter.  Valid values for <code>proportion</code> range from <code>0.0F<code>
	 * to <code>1.0F</code>.  For example, a <code>proportion</code> of <code>0.3F</code> will move the 
	 * divider to 30% of the "size" (<i>width</i> for horizontal split, <i>height</i> for vertical split) of the 
	 * split container embedded within the specified <code>DockingPort</code>.  If a <code>proportion</code> of less 
	 * than <code>0.0F</code> is supplied, the value </code>0.0F</code> is used.  If a <code>proportion</code> 
	 * greater than <code>1.0F</code> is supplied, the value </code>1.0F</code> is used.
	 * <br/>
	 * This method should be effective regardless of whether the split layout in question has been fully realized
	 * and is currently visible on the screen.  This should alleviate common problems associated with setting
	 * percentages of unrealized <code>Component</code> dimensions, which are initially <code>0x0</code> before
	 * the <code>Component</code> has been rendered to the screen.
	 * <br/>
	 * If the specified <code>DockingPort</code> is <code>null</code>, then no <code>Exception</code> is 
	 * thrown and no action is taken.  Identical behavior occurs if the <code>DockingPort</code> does not 
	 * contain split layout.  
	 * 
	 * @param port the <code>DockingPort</code> containing the split layout is to be resized.
	 * @param proportion the percentage of split layout size to which the split divider should be set.
	 */
	public static void setSplitProportion(DockingPort port, float proportion) {
		DockingUtility.setSplitProportion(port, proportion);
	}
	
	/**
	 * Sets the currently installed <code>DockableFactory</code>.  <code>null</code> values 
	 * for the <code>factory</code> parameter are acceptable.  
	 * <br/>
	 * Installing a <code>DockableFactory</code> allows FlexDock to seamlessly create and register
	 * <code>Dockables</code> within <code>getDockable(String id)</code>.  Generally, 
	 * <code>getDockable(String id)</code> will lookup the requested <code>Dockable</code> within the
	 * internal registry.  If not found, and there is no <code>DockableFactory</code> installed, 
	 * <code>getDockable(String id)</code> returns a <code>null</code> reference.  When a 
	 * <code>DockableFactory</code> is installed, however, failure to lookup a valid <code>Dockable</code>
	 * will cause <code>getDockable(String id)</code> to invoke the installed <code>DockableFactory's</code>
	 * <code>getDockable(String dockableId)</code> method, transparently registering and returning
	 * the newly created <code>Dockable</code> from <code>getDockable(String id)</code>.
	 * 
	 * @param factory the <code>DockableFactory</code> to install
	 * @see #getDockableFactory()
	 * @see #getDockable(String)
	 * @see DockableFactory#getDockable(String)
	 */
	public static void setDockableFactory(DockableFactory factory) {
		getDockingManager().dockableFactory = factory;
	}
	
	/**
	 * Sets the minimized state for the specified <code>Dockable</code>.  This method defers processing
	 * to <code>setMinimized(Dockable dockable, boolean minimized, Component window)</code>, passing the
	 * current <code>Window</code> ancestor of the specified <code>Dockable</code> as the 
	 * <code>window</code> parameter. Minimization processessing is ultimately deferred to the currently 
	 * installed <code>MinimizationManager</code> with a constraint of 
	 * <code>MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT</code>.
	 * <br/>
	 * The current <code>MinimizationManager</code> is responsible for updating the underlying 
	 * <code>DockingState</code> model for the specified <code>Dockable</code> as well as rendering 
	 * its own interpretation of the corresponding visual state on the screen.  If the supplied
	 * <code>minimized</code> parameter matches the current <code>DockingState</code>, the 
	 * <code>MinimizationManager</code> is responsible for providing the appropriate visual indications, 
	 * or lack thereof.  If the specified <code>Dockable</code> is <code>null</code>, no 
	 * <code>Exception</code> is thrown and no action is taken.
	 * 
	 * @param dockable the <code>Dockable</code> whose minimzed state is to be modified
	 * @param minimized <code>true</code> if the specified <code>Dockable</code> should be
	 * minimized, <code>false</code> otherwise.
	 * @see #setMinimized(Dockable, boolean, Component)
	 * @see #getMinimizeManager()
	 * @see MinimizationManager#setMinimized(Dockable, boolean, Component, int)
	 * @see DockingState#getMinimizedConstraint()
	 */
	public static void setMinimized(Dockable dockable, boolean minimized) {
		Component cmp = dockable==null? null: dockable.getComponent();
		Window window = cmp==null? null: SwingUtilities.getWindowAncestor(cmp);
		setMinimized(dockable, minimized, window);
	}
	
	/**
	 * Sets the minimized state for the specified <code>Dockable</code>.  This method defers processing
	 * to <code>setMinimized(Dockable dockable, boolean minimizing, Component window, int constraint)</code>, 
	 * passing <code>MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT</code> for the 
	 * <code>constraint</code> parameter.  Minimization processessing is ultimately deferred to the 
	 * currently installed <code>MinimizationManager</code>.
	 * <br/>
	 * The <code>window</code> parameter is passed to the <code>MinimizationManager</code> to indicate
	 * that minimization should be handled with respect to the specified root window, or the root window
	 * containing the specified <code>Component</code>.  <code>null</code> values are acceptable for
	 * this parameter.
	 * <br/>
	 * The current <code>MinimizationManager</code> is responsible for updating the underlying 
	 * <code>DockingState</code> model for the specified <code>Dockable</code> as well as rendering 
	 * its own interpretation of the corresponding visual state on the screen.  If the supplied
	 * <code>minimized</code> parameter matches the current <code>DockingState</code>, the 
	 * <code>MinimizationManager</code> is responsible for providing the appropriate visual indications, 
	 * or lack thereof.  If the specified <code>Dockable</code> is <code>null</code>, no 
	 * <code>Exception</code> is thrown and no action is taken.
	 * 
	 * @param dockable the <code>Dockable</code> whose minimzed state is to be modified
	 * @param minimized <code>true</code> if the specified <code>Dockable</code> should be
	 * minimized, <code>false</code> otherwise.
	 * @param the <code>Component</code> whose root window will be used by the underlying 
	 * <code>MinimizationManager</code> for rendering the <code>Dockable</code> in its new
	 * minimized state.
	 * @see #setMinimized(Dockable, boolean, Component, int)
	 * @see #getMinimizeManager()
	 * @see MinimizationManager#setMinimized(Dockable, boolean, Component, int)
	 * @see DockingState#getMinimizedConstraint()
	 */
	public static void setMinimized(Dockable dockable, boolean minimized, Component window) {
		setMinimized(dockable, minimized, window, MinimizationManager.UNSPECIFIED_LAYOUT_CONSTRAINT);
	}

	/**
	 * Sets the minimized state for the specified <code>Dockable</code>.  This method defers processing
	 * to <code>setMinimized(Dockable dockable, boolean minimizing, Component window, int constraint)</code>, 
	 * passing <code>null</code> for the <code>window</code> parameter.  Minimization processessing is 
	 * ultimately deferred to the currently installed <code>MinimizationManager</code>.
	 * <br/>
	 * Valid values for the <code>constraint</code> parameter may be found on the 
	 * <code>MinimizationManager</code> interface and include UNSPECIFIED_LAYOUT_CONSTRAINT, TOP, LEFT, 
	 * BOTTOM, RIGHT, and CENTER.  However, constraint values must ultimately be interpreted by the 
	 * current <code>MinimizationManager</code> implementation and, thus any integer value may 
	 * theoretically be valid for <code>constraint</code>.
	 * <br/>
	 * The current <code>MinimizationManager</code> is responsible for updating the underlying 
	 * <code>DockingState</code> model for the specified <code>Dockable</code> as well as rendering 
	 * its own interpretation of the corresponding visual state on the screen.  If the supplied
	 * <code>minimized</code> parameter matches the current <code>DockingState</code>, the 
	 * <code>MinimizationManager</code> is responsible for providing the appropriate visual indications, 
	 * or lack thereof.  If the specified <code>Dockable</code> is <code>null</code>, no 
	 * <code>Exception</code> is thrown and no action is taken.
	 * 
	 * @param dockable the <code>Dockable</code> whose minimzed state is to be modified
	 * @param minimized <code>true</code> if the specified <code>Dockable</code> should be
	 * minimized, <code>false</code> otherwise.
	 * @param constraint a value to indicate to the <code>MinimizationManager</code> desired
	 * rendering of the minimized <code>Dockable</code>
	 * @see #setMinimized(Dockable, boolean, Component, int)
	 * @see #getMinimizeManager()
	 * @see MinimizationManager#setMinimized(Dockable, boolean, Component, int)
	 * @see DockingState#getMinimizedConstraint()
	 */
	public static void setMinimized(Dockable dockable, boolean minimizing, int constraint) {
		setMinimized(dockable, minimizing, null, constraint);
	}
	
	/**
	 * Sets the minimized state for the specified <code>Dockable</code>.  This method defers processing
	 * to the currently installed <code>MinimizationManager</code>.
	 * <br/>
	 * The <code>window</code> parameter is passed to the <code>MinimizationManager</code> to indicate
	 * that minimization should be handled with respect to the specified root window, or the root window
	 * containing the specified <code>Component</code>.  If a <code>null</code> values is supplied for 
	 * this parameter, the currently active window is used.  If no currently active window can be
	 * determined, then this method exits with no action taken.
	 * <br/>
	 * The current <code>MinimizationManager</code> is responsible for updating the underlying 
	 * <code>DockingState</code> model for the specified <code>Dockable</code> as well as rendering 
	 * its own interpretation of the corresponding visual state on the screen.  If the supplied
	 * <code>minimized</code> parameter matches the current <code>DockingState</code>, the 
	 * <code>MinimizationManager</code> is responsible for providing the appropriate visual indications, 
	 * or lack thereof.  If the specified <code>Dockable</code> is <code>null</code>, no 
	 * <code>Exception</code> is thrown and no action is taken.
	 * <br/>
	 * Valid values for the <code>constraint</code> parameter may be found on the 
	 * <code>MinimizationManager</code> interface and include UNSPECIFIED_LAYOUT_CONSTRAINT, TOP, LEFT, 
	 * BOTTOM, RIGHT, and CENTER.  However, constraint values must ultimately be interpreted by the 
	 * current <code>MinimizationManager</code> implementation and, thus any integer value may 
	 * theoretically be valid for <code>constraint</code>.
	 * 
	 * @param dockable the <code>Dockable</code> whose minimzed state is to be modified
	 * @param minimized <code>true</code> if the specified <code>Dockable</code> should be
	 * minimized, <code>false</code> otherwise.
	 * @param the <code>Component</code> whose root window will be used by the underlying 
	 * <code>MinimizationManager</code> for rendering the <code>Dockable</code> in its new
	 * minimized state.
	 * @param constraint a value to indicate to the <code>MinimizationManager</code> desired
	 * rendering of the minimized <code>Dockable</code>
	 * @see #getMinimizeManager()
	 * @see MinimizationManager#setMinimized(Dockable, boolean, Component, int)
	 * @see DockingState#getMinimizedConstraint()
	 */
	public static void setMinimized(Dockable dockable, boolean minimizing, Component window, int constraint) {
		if(dockable==null)
			return;
		
		if(window==null)
			window = SwingUtility.getActiveWindow();
		if(window==null)
			return;
		
		getMinimizeManager().setMinimized(dockable, minimizing, window, constraint);
	}

	/**
	 * Sets the "main" <code>DockingPort</code> within the application window containing the
	 * specified <code>Component</code>.  Just as desktop applications will tend to have a "main" 
	 * application window, perhaps surrounded with satellite windows or dialogs, the "main" 
	 * <code>DockingPort</code> within a given window will be considered by the application developer to
	 * contain the primary docking layout used by the enclosing window.
	 * <br/>
	 * The <code>Component</code> parameter may or may not be a root window container.  If not, the ancestor
	 * window of <code>comp</code> is determined and a set of docking ports encapsulated by a 
	 * <code>RootDockingPortInfo</code> instance is returned by a call to 
	 * <code>getRootDockingPortInfo(Component comp)</code>.  The resolved <code>RootDockingPortInfo</code> 
	 * instance's main <code>DockingPort</code> is set via its method <code>setMainPort(String portId)</code>.
	 * <br/>
	 * By default, the "main" <code>DockingPort</code> assigned to any <code>RootDockingPortInfo</code>
	 * instance associated with a window will happen to be the first root <code>DockingPort</code> detected
	 * for that window.  This method is used to alter that setting.
	 * <br/>
	 * If <code>comp</code> is <code>null</code> or the root window cannot be resolved, then this method returns
	 * with no action taken.
	 *
	 * @param window the <code>Component</code> whose root window will be checked for a main <code>DockingPort</code>
	 * @param portId the persistent ID of the <code>DockingPort</code> to use as the main <code>DockingPort</code>
	 * for the specified window.
	 * @see #getRootDockingPortInfo(Component)
	 * @see #getRootDockingPort(Component)
	 * @see DockingPortTracker#getRootDockingPortInfo(Component)
	 * @see RootDockingPortInfo#getMainPort()
	 * @see RootDockingPortInfo#setMainPort(String)
	 */
	public static void setMainDockingPort(Component window, String portId) {
		RootDockingPortInfo info = getRootDockingPortInfo(window);
		if(info!=null)
			info.setMainPort(portId);
	}
	
	/**
	 * Sets the currently installed <code>MinimizationManager</code>.  The 
	 * <code>MinimizationManager</code> is responsible for minimizing and unminimizing
	 * <code>Dockables</code>, removing from and restoring to the embedded docking layout
	 * through the currently installed <code>LayoutManager</code>.  
	 * <br/>
	 * The visual representation
	 * of a "minimized" <code>Dockable</code> is somewhat abstract, although it is commonly 
	 * expressed in user interfaces with the disappearance of the <code>Dockable</code> from
	 * the layout and the addition of a tab or label on one or more edges of the application 
	 * window.  The <code>MinimizationManager</code> implementation itself is responsible
	 * for interpreting the visual characteristics and behavior of a minimized <code>Dockable</code>, 
	 * but it must provide a "preview" feature to allow viewing of minimized <code>Dockables</code>, 
	 * on demand without actually restoring them to the embedded docking layout.  
	 * <code>Dockables</code> may or may not have limited docking functionality while in 
	 * minimized and/or preview state, depending upon the <code>MinimizationManager</code>
	 * implementation.
	 * <br/>
	 * Because the <code>MinimizationManager</code> is a critical piece of the docking 
	 * infrastructure, it cannot be set to <code>null</code>.  If a <code>null</code> value
	 * is passed into this method, the default <code>MinimizationManager</code> provided by
	 * the framework is used instead.
	 * 
	 * @param the <code>MinimizationManager</code> to be installed
	 * @see MinimizationManager
	 * @see #getMinimizeManager()
	 * @see #setMinimizeManager(String)
	 */
	public static void setMinimizeManager(MinimizationManager mgr) {
		DockingManager dockingManager = getDockingManager();
		if(mgr==null)
			// do not allow null minimization managers
			setMinimizeManager(dockingManager.defaultMinimizeManagerClass);
		else
			dockingManager.minimizeManager = mgr;
	}
	
	/**
	 * Sets the currently installed <code>MinimizationManager</code> using the specfied
	 * class name.  An attempt is make to instantiate a <code>MinimizationManager</code> based 
	 * upon the supplied class name <code>String</code>.  If the class cannot be instaniated, 
	 * a stacktrace is reported to the System.err and the default <code>MinimizationManager</code>
	 * supplied by the framework is used.  If the <code>String</code> parameter is <code>null</code>, 
	 * no error occurs and the default <code>MinimizationManager</code> is used.  If the 
	 * instantiated class is not a valid instance of <code>MinimizationManager</code>, then a 
	 * <code>ClassCastException</code> is thrown.
	 * <br/>
	 * The <code>MinimizationManager</code> is responsible for minimizing and unminimizing
	 * <code>Dockables</code>, removing from and restoring to the embedded docking layout
	 * through the currently installed <code>LayoutManager</code>.  
	 * <br/>
	 * The visual representation
	 * of a "minimized" <code>Dockable</code> is somewhat abstract, although it is commonly 
	 * expressed in user interfaces with the disappearance of the <code>Dockable</code> from
	 * the layout and the addition of a tab or label on one or more edges of the application 
	 * window.  The <code>MinimizationManager</code> implementation itself is responsible
	 * for interpreting the visual characteristics and behavior of a minimized <code>Dockable</code>, 
	 * but it must provide a "preview" feature to allow viewing of minimized <code>Dockables</code>, 
	 * on demand without actually restoring them to the embedded docking layout.  
	 * <code>Dockables</code> may or may not have limited docking functionality while in 
	 * minimized and/or preview state, depending upon the <code>MinimizationManager</code>
	 * implementation.
	 * <br/>
	 * Because the <code>MinimizationManager</code> is a critical piece of the docking 
	 * infrastructure, it cannot be set to <code>null</code>.  If a <code>null</code> value
	 * is passed into this method, the default <code>MinimizationManager</code> provided by
	 * the framework is used instead.
	 * 
	 * @param mgrClass the class name of the <code>MinimizationManager</code> to be installed
	 * @see MinimizationManager
	 * @see #getMinimizeManager()
	 * @see #setMinimizeManager(String)
	 */
	public static void setMinimizeManager(String mgrClass) {
		Object instance = Utilities.getInstance(mgrClass);
		setMinimizeManager((MinimizationManager)instance);
	}
	
	/**
	 * Sets whether global floating support should be enabled.
	 * Defers processing to 
	 * <code>FloatPolicyManager.setGlobalFloatingEnabled(boolean globalFloatingEnabled)</code>.
	 * 
	 * @param <code>true</code> if global floating support should be enabled, 
	 * <code>false</code> otherwise.
	 * @see FloatPolicyManager#setGlobalFloatingEnabled(boolean)
	 * @see FloatPolicyManager#isGlobalFloatingEnabled()
	 */
	public static void setFloatingEnabled(boolean enabled) {
		FloatPolicyManager.setGlobalFloatingEnabled(enabled);
	}
	
	public static void setDefaultPersistenceKey(String key) {
		getLayoutManager().setDefaultPersistenceKey(key);
	}
	
	public static String getDefaultPersistenceKey() {
		return getLayoutManager().getDefaultPersistenceKey(); 
	}

	/**
	 * Sets whether tabbed layouts are supported by default for <code>DockingPorts</code> 
	 * with a single <code>Dockable</code> in the CENTER region.  This is a global default setting
	 * and applies to any <code>DockingPort</code> that does not have a specific contradictory
	 * local setting.
	 * </br>
	 * This method defers processing to 
	 * <code>org.flexdock.docking.props.PropertyManager.getDockingPortRoot()</code>.  As such, 
	 * there are multiple "scopes" at which this property may be overridden.
	 * 
	 * @param <code>true</code> if the default setting for <code>DockingPorts</code> should allow
	 * a tabbed layout for a single <code>Dockable</code> in the CENTER region; <code>false</code>
	 * otherwise.
	 * @see PropertyManager#getDockingPortRoot()
	 * @see org.flexdock.docking.props.DockingPortPropertySet#setSingleTabsAllowed(boolean)
	 */
	public static void setSingleTabsAllowed(boolean allowed) {
		PropertyManager.getDockingPortRoot().setSingleTabsAllowed(allowed);
	}

	/**
	 * Sets the currently installed <code>LayoutManager</code>.  The <code>LayoutManager</code>
	 * is responsible for managing docking layout state.  This includes tracking the state
	 * for all <code>Dockables</code> as they are embedded, minimized, floated, or hidden.  If
	 * a <code>Dockable</code> is embedded, the <code>LayoutManager</code> is responsible for
	 * tracking its position and size relative to other embedded <code>Dockables</code>.  If 
	 * floating, the <code>LayoutManager</code> is responsible for supplying a 
	 * <code>FloatManager</code> to maintain <code>Dockable</code> groupings within dialogs as well
	 * as dialog size and positioning.
	 * </br>
	 * The <code>LayoutManager</code> is responsible for providing a persistence mechanism to 
	 * save and restore layout states.  Depending on the <code>LayoutManager</code> implementation, 
	 * it may or may not support multiple layout models that may be loaded and switched between
	 * at runtime.
	 * <br/>
	 * Because the <code>LayoutManager</code> is a critical piece of the docking infrastructure, 
	 * it is not possible to install a <code>null</code> <code>LayoutManager</code>.  FlexDock 
	 * provides a default <code>LayoutManager</code> implementation.  If this method is passed 
	 * a <code>null</code> argument, the default <code>LayoutManager</code> is used instead.
	 *
	 * @param mgr the <code>LayoutManager</code> to install.
	 * @see LayoutManager
	 * @see #setLayoutManager(String)
	 * @see #getLayoutManager()
	 */
	public static void setLayoutManager(LayoutManager mgr) {
		DockingManager dockingManager = getDockingManager();
		if(mgr==null)
			// do not allow a null layout manager.
			setLayoutManager(dockingManager.defaultLayoutManagerClass);
		else
			getDockingManager().layoutManager = mgr;
	}
	
	/**
	 * Sets the currently installed <code>LayoutManager</code> using the specified
	 * class name.  An attempt is make to instantiate a <code>LayoutManager</code> based 
	 * upon the supplied class name <code>String</code>.  If the class cannot be instaniated, 
	 * a stacktrace is reported to the System.err and the default <code>LayoutManager</code>
	 * supplied by the framework is used.  If the <code>String</code> parameter is <code>null</code>, 
	 * no error occurs and the default <code>LayoutManager</code> is used.  If the 
	 * instantiated class is not a valid instance of <code>LayoutManager</code>, then a 
	 * <code>ClassCastException</code> is thrown.  
	 * <br/>
	 * The <code>LayoutManager</code>
	 * is responsible for managing docking layout state.  This includes tracking the state
	 * for all <code>Dockables</code> as they are embedded, minimized, floated, or hidden.  If
	 * a <code>Dockable</code> is embedded, the <code>LayoutManager</code> is responsible for
	 * tracking its position and size relative to other embedded <code>Dockables</code>.  If 
	 * floating, the <code>LayoutManager</code> is responsible for supplying a 
	 * <code>FloatManager</code> to maintain <code>Dockable</code> groupings within dialogs as well
	 * as dialog size and positioning.
	 * </br>
	 * The <code>LayoutManager</code> is responsible for providing a persistence mechanism to 
	 * save and restore layout states.  Depending on the <code>LayoutManager</code> implementation, 
	 * it may or may not support multiple layout models that may be loaded and switched between
	 * at runtime.
	 * <br/>
	 * Because the <code>LayoutManager</code> is a critical piece of the docking infrastructure, 
	 * it is not possible to install a <code>null</code> <code>LayoutManager</code>.  FlexDock 
	 * provides a default <code>LayoutManager</code> implementation.  If this method is passed 
	 * a <code>null</code> argument, the default <code>LayoutManager</code> is used instead.
	 *
	 * @param mgrClass the class name of the <code>LayoutManager</code> to install.
	 * @see LayoutManager
	 * @see #setLayoutManager(LayoutManager)
	 * @see #getLayoutManager()
	 */
	public static void setLayoutManager(String mgrClass) {
		Object instance = Utilities.getInstance(mgrClass);
		setLayoutManager((LayoutManager)instance);
	}
	
	/**
	 * Sets the <code>DockingStrategy</code> associated with specified <code>Class</code>.
	 * This method returns with no action taken if the specified <code>Class</code> paramter is
	 * <code>null</code>.  If the <code>strategy</code> parameter is <code>null</code> then 
	 * any existing <code>DockingStrategy</code> association with the specified </code>Class</code>
	 * is removed.  Otherwise, a new <code>DockingStrategy</code> association is added for the 
	 * specified <code>Class</code>.
	 * <br/>
	 * <code>DockingStrategy</code> association follows a strict inheritance chain using 
	 * <code>org.flexdock.util.ClassMapping</code>.  This means that the association created
	 * by this method applies for the specified <code>Class</code> and all direct subclasses, but
	 * associations for interfaces are ignored.  Associations also do not apply for subclasses
	 * that have their own specific <code>DockingStrategy</code> mapping.
	 * <br/>
	 * 
	 * @param classKey the <code>Class</code> whose <code>DockingStrategy</code> association we wish to set
	 * @param strategy the <code>DockingStrategy</code> to be associated with the specified <code>Class</code>.
	 * @see #getDockingStrategy(Class)
	 * @see #getDockingStrategy(Object)
	 * @see ClassMapping#addClassMapping(Class, Class, Object)
	 * @see ClassMapping#removeClassMapping(Class)
	 */
	public static void setDockingStrategy(Class classKey, DockingStrategy strategy) {
		if(classKey==null)
			return;
		
		if(strategy==null)
			DOCKING_STRATEGIES.removeClassMapping(classKey);
		else
			DOCKING_STRATEGIES.addClassMapping(classKey, strategy.getClass(), strategy);
	}
	
	/**
	 * Undocks the specified <code>Dockable</code> from its parent <code>DockingPort</code>.
	 * If the <code>Dockable</code> is <code>null</code>, or it does not currently reside within
	 * a <code>DockingPort</code>, then this method returns <code>false</code> with no action 
	 * taken.  Otherwise, this method returns <code>true</code> if the undocking operation was 
	 * successful and <code>false</code> if the undocking operation could not be completed.
	 * 
	 * This method determines the <code>DockingStrategy</code> to be used for <code>DockingPort</code>
	 * containing the specified <code>Dockable</code> and defers processing to the  
	 * <code>undock(Dockable dockable)</code> method on the <code>DockingStrategy</code>.  This
	 * method's return value will be based upon the <code>DockingStrategy</code> implementation 
	 * returned by a call to <code>getDockingStrategy(Object obj)</code>.  The 
	 * <code>DockingStrategy</code> used may be controlled via 
	 * <code>setDockingStrategy(Class c, DockingStrategy strategy)</code>, supplying a 
	 * <code>DockingPort</code> implementation class and a customized <code>DockingStrategy</code>. 
	 * 
	 * @param dockable the <code>Dockable</code> to be undocked.
	 * @return <code>true</code> if the undocking operation was successful, <code>false</code> otherwise.
	 * @see DockingStrategy#undock(Dockable)
	 * @see #getDockingStrategy(Object)
	 * @see #setDockingStrategy(Class, DockingStrategy)
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
	
	/**
	 * Ensures that a valid <code>DragManager</code> has been installed as a listener for all of the
	 * specified <code>Dockable's</code> drag source <code>Components</code>.  This method invokes the
	 * <code>getDragSources()</code> method on the specified <code>Dockable</code> and iterates over 
	 * each <code>Component</code> in the returned <code>List</code>.  If any <code>Component</code>
	 * does not have a valid <code>DragManager</code> listener installed, an appropriate listener is
	 * added to enable drag-to-dock functionality.
	 * </br>
	 * This method is useful to application developers who manually attempt to add new 
	 * <code>Components</code> to a <code>Dockable's</code> drag source <code>List</code>.  However, 
	 * it is not necessary to call this method unless the drag source list has been updated <b>after</b>
	 * calling <code>registerDockable(Dockable dockable)</code>, since
	 * <code>registerDockable(Dockable dockable)</code> will automatically initialize each drag source
	 * for the specified <code>Dockable</code>. 
	 * <br/>
	 * If the specified <code>Dockable</code> is <code>null</code>, then no <code>Exception</code> is 
	 * thrown and no action is taken.
	 * 
	 * @param dockable the <code>Dockable</code> whose drag sources are to be checked for 
	 * <code>DragManagers</code> and updated accordingly.
	 * @see #registerDockable(Dockable)
	 * @see Dockable#getDragSources()
	 * @see DragManager
	 */
	public static void updateDragListeners(Dockable dockable) {
		if(dockable==null)
			return;
		
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
	
	private static void removeDragListeners(Dockable dockable) {
		if(dockable==null)
			return;
		
		for(Iterator it=dockable.getDragSources().iterator(); it.hasNext();) {
			Object obj = it.next();
			if(obj instanceof Component) {
				removeDragListeners((Component)obj);				
			}
		}
	}	
	
	public static float getDefaultSiblingSize() {
		return getDockingManager().defaultSiblingSize;
	}
	
	public static void setDefaultSiblingSize(float size) {
		size = Math.max(size, 0);
		size = Math.min(size, 1);
		getDockingManager().defaultSiblingSize = size;
	}
    
	public static void setRubberBand(RubberBand rubberBand) {
	    EffectsManager.setRubberBand(rubberBand);
	}

    public static void setDragPreview(DragPreview dragPreview) {
        EffectsManager.setPreview(dragPreview);
    }
    
    private static class AutoPersist extends Thread {
        
        private boolean enabled;
        
        public void run() {
            store();
        }
        
        private synchronized void store() {
            try {
                if(isEnabled())
                    storeLayoutModel();
            } catch(IOException e) {
                e.printStackTrace();
            } catch (PersistenceException e) {
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

}
