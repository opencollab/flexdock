/*
 * Created on May 31, 2005
 */
package org.flexdock.docking.floating.policy;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.drag.DragManager;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.util.Utilities;

/**
 * This class provides centralized control over the framework's floating behavior.  This includes
 * global behavior and behavior local to each individual docking operation.
 * <br/>
 * This class contains a method <code>isGlobalFloatingEnabled()</code> that indicates whether
 * global floating support is enabled.  If global floating support is disabled, then the 
 * setting governs all docking operations and blocks floating in a global sense.  If global
 * floating support is enabled, then floating is allowed or disallowed on an individual 
 * operation-by-operation basis through a set of <code>FloatPolicy</code> implementations.
 * <br/>
 * The default setting for global floating support is <code>false</code>.  This, however, 
 * may be controlled by the system property <code>FLOATING_ALLOWED</code>.  If the framework
 * starts up with a case-sensitive <code>String</code> value of <code>"true"</code> for this
 * system property, then global floating support will be turned on by default.  Otherwise, 
 * global floating support may be modified via 
 * <code>setGlobalFloatingEnabled(boolean globalFloatingEnabled)</code>.
 * <br/>
 * This class provides methods <code>addPolicy(FloatPolicy policy)</code> and 
 * <code>removePolicy(FloatPolicy policy)</code>, allowing the user to implement custom
 * behavior to control floating support for individual docking operations on an event-by-event
 * basis.  By default, the <code>FloatPolicyManager</code> has a single <code>FloatPolicy</code>
 * installed of type <code>DefaultFloatPolicy</code>.
 * 
 * @author Christopher Butler
 */
public class FloatPolicyManager extends DockingListener.Stub {
	private static final FloatPolicyManager SINGLETON = new FloatPolicyManager();
	
	/**
	 * Key constant used within the drag context <code>Map</code> to indicate whether 
	 * floating is allowed for a given drag operation.
	 * @see DragManager#getDragContext(Dockable)
	 */
	public static final String FLOATING_ALLOWED = "FloatPolicyManager.FLOATING_ALLOWED";
	
	/**
	 * System property key used during framework initialization to determine the 
	 * default setting for global floating support.
	 */
	public static final String GLOBAL_FLOATING_ENABLED =  "global.floating.enabled";
	
	private Vector policies;
	private boolean globalFloatingEnabled;
	
	/**
	 * Returns a singleton instance of the <code>FloatPolicyManager</code> class.
	 * 
	 * @return a singleton instance of the <code>FloatPolicyManager</code> class.
	 */
	public static FloatPolicyManager getInstance() {
		return SINGLETON;
	}
	
	private FloatPolicyManager() {
		policies = new Vector();
		addPolicy(DefaultFloatPolicy.getInstance());
		globalFloatingEnabled = Utilities.sysTrue(GLOBAL_FLOATING_ENABLED);
	}
	
	/**
	 * This method catches <code>DockingEvents</code> per the <code>DockingListener</code>
	 * interface at the start of a drag operation and initializes floating support 
	 * within the the context <code>Map</code> of the drag operation.  This method 
	 * retrieves the <code>Dockable</code> for the event via its <code>getDockable()</code>
	 * method.  It also retrieves the drag context <code>Map</code> for the 
	 * <code>DockingEvent</code> by invoking its <code>getDragContext()</code> method.
	 * This map is the same <code>Map</code> returned by 
	 * <code>DragManager.getDragContext(Dockable dockable)</code>.  It then calls 
	 * <code>isPolicyFloatingSupported(Dockable dockable)</code> for the <code>Dockable</code>
	 * and places either <code>Boolean.TRUE</code> or <code>Boolean.FALSE</code> within
	 * the drag context <code>Map</code>, caching the value for use throughout the 
	 * drag operation to avoid successive iterations through the entire installed 
	 * <code>FloatPolicy</code> collection.  The <code>Map</code>-key used is
	 * <code>FLOATING_ALLOWED</code>.
	 * 
	 * @param evt the <code>DockingEvent</code> whose drag context is to be initialized
	 * for floating support
	 * @see DockingEvent#getDragContext()
	 * @see DockingEvent#getDockable()
	 * @see #isPolicyFloatingSupported(Dockable)
	 * @see #FLOATING_ALLOWED
	 */
	public void dragStarted(DockingEvent evt) {
		Map context = evt.getDragContext();
		Dockable d = evt.getDockable();
		Boolean allowed = isPolicyFloatingSupported(d)? Boolean.TRUE: Boolean.FALSE;
		context.put(FLOATING_ALLOWED, allowed);
	}
	
	/**
	 * This method catches <code>DockingEvents</code> per the <code>DockingListener</code>
	 * interface at the end of a drag operation and determines whether or not to 
	 * block attempts to float within the docking operation.
	 * <br/>
	 * If <code>evt.isOverWindow()</code> returns <code>true</code>, then the drop 
	 * operation is over an existing window and will be interpreted as an attempt to 
	 * dock within the window, not an attempt to float into a new dialog.  In this case, 
	 * this method returns immediately with no action taken.
	 * <br/>
	 * This method calls <code>isFloatingAllowed(Dockable dockable)</code> using the 
	 * <code>DockingEvent's</code> <code>Dockable</code>, retrieved from 
	 * <code>getDockable()</code>.  If this method returns <code>false</code>, then
	 * the <code>DockingEvent</code> is consumed and this method returns.
	 * <br/>
	 * If <code>isFloatingAllowed(Dockable dockable)</code> returns <code>true</code>, then
	 * the internal <code>FloatPolicy</code> collection is iterated through, allowing each
	 * installed <code>FloatPolicy</code> to confirm the drop operation via
	 * <code>isFloatDropAllowed(DockingEvent evt)</code>.  If any of the installed
	 * <code>FloatPolicies</code> returns <code>false</code> for 
	 * <code>isFloatDropAllowed(DockingEvent evt)</code>, then the <code>DockingEvent</code>
	 * is consumed and the method exits.
	 * <br/>
	 * If this method completes without the <code>DockingEvent</code> being consumed, 
	 * then the docking operation will proceed and attempts to float will be allowed. 
	 * 
	 * @param evt the <code>DockingEvent</code> to be examined for floating support
	 * @see DockingEvent#isOverWindow()
	 * @see DockingEvent#getDockable()
	 * @see DockingEvent#consume()
	 * @see #isFloatingAllowed(Dockable)
	 * @see FloatPolicy#isFloatDropAllowed(DockingEvent)
	 */
	public void dropStarted(DockingEvent evt) {
		if(evt.isOverWindow())
			return;
		
		if(!isFloatingAllowed(evt.getDockable())) {
			evt.consume();
			return;
		}
		
		for(Iterator it=policies.iterator(); it.hasNext();) {
			FloatPolicy policy = (FloatPolicy)it.next();
			if(!policy.isFloatDropAllowed(evt)) {
				evt.consume();
				return;
			}
		}
	}

	/**
	 * Indicates whether floating is allowed for the specified <code>Dockable</code>.
	 * If <code>dockable</code> is <code>null</code>, this method returns <code>false</code>.
	 * <br/>
	 * This method first calls <code>DragManager.getDragContext(Dockable dockable)</code>
	 * to see if a drag operation is in progress.  If so, it returns the <code>boolean</code>
	 * value contained within the drag context map using the key 
	 * <code>FLOATING_ALLOWED</code>.  If no mapping exists for the specified key, this method
	 * returns <code>false</code>.
	 * <br/>
	 * If no drag operation is currently in progress and no drag context can be found, 
	 * this method dispatches to <code>isPolicyFloatingSupported(Dockable dockable)</code>, 
	 * which iterates through all installed <code>FloatPolicies</code> to determine
	 * whether floating support is allowed.
	 * 
	 * @param dockable the <code>Dockable</code> whose floating support is to be checked
	 * @return <code>true</code> if floating is allowed for the specified <code>Dockable</code>; 
	 * <code>false</code> otherwise.
	 * @see DragManager#getDragContext(Dockable)
	 * @see #getInstance()
	 * @see #isPolicyFloatingSupported(Dockable)
	 * @see #FLOATING_ALLOWED
	 */
	public static boolean isFloatingAllowed(Dockable dockable) {
		if(dockable==null)
			return false;
		
		Map context = DragManager.getDragContext(dockable);
		if(context==null)
			return getInstance().isPolicyFloatingSupported(dockable);
		
		Boolean floatAllowed = (Boolean)context.get(FLOATING_ALLOWED);
		return floatAllowed==null? true: floatAllowed.booleanValue();		
	}

	
	/**
	 * Indicates whether floating is allowed for the specified <code>Dockable</code> 
	 * strictly by checking the installed <code>FloatPolicies</code>. If <code>dockable</code> 
	 * is <code>null</code>, this method returns <code>false</code> immediately without
	 * checking through the installed <code>FloatPolicies</code>.
	 * <br/>
	 * This method iterates through all installed <code>FloatPolicies</code> to determine
	 * whether floating support is allowed.  If any <code>FloatPolicy</code> within the 
	 * internal collection returns <code>false</code> from its 
	 * <code>isFloatingAllowed(Dockable dockable)</code> method, this method returns 
	 * <code>false</code>.  Otherwise, this method returns <code>true</code>.
	 * 
	 * @param dockable the <code>Dockable</code> whose floating support is to be checked
	 * @return <code>true</code> if floating is allowed for the specified <code>Dockable</code>; 
	 * <code>false</code> otherwise.
	 * @see FloatPolicy#isFloatingAllowed(Dockable)
	 */
	public boolean isPolicyFloatingSupported(Dockable dockable) {
		if(dockable==null)
			return false;
		
		for(Iterator it=policies.iterator(); it.hasNext();) {
			FloatPolicy policy = (FloatPolicy)it.next();
			if(!policy.isFloatingAllowed(dockable))
				return false;
		}
		return true;
	}

	/**
	 * Adds the specified <code>FloatPolicy</code> to the internal policy collection.  This
	 * <code>FloatPolicy</code> will now take part in framework determinations as to whether 
	 * floating should be supported during docking operations.  If <code>policy</code>
	 * is <code>null</code>, no action is taken.
	 * 
	 * @param policy the <code>FloatPolicy</code> to add to the system
	 * @see #removePolicy(FloatPolicy)
	 */
	public void addPolicy(FloatPolicy policy) {
		if(policy!=null)
			policies.add(policy);
	}
	
	/**
	 * Removes the specified <code>FloatPolicy</code> from the internal policy collection.
	 * <code>FloatPolicy</code> will no longer take part in framework determinations as to 
	 * whether floating should be supported during docking operations.  If <code>policy</code>
	 * is <code>null</code> or was not previously installed, no action is taken.
	 * 
	 * @param policy the <code>FloatPolicy</code> to remove from the system
	 * @see #addPolicy(FloatPolicy)
	 */
	public void removePolicy(FloatPolicy policy) {
		if(policy!=null)
			policies.remove(policy);
	}
	
	/**
	 * Returns a global setting used to control default framework floating behavior.
	 * If this method returns <code>false</code>, all floating support for the entire
	 * framework is turned off.  If this method returns <code>true</code>, then floating
	 * support for individual docking operations is deferred to the installed
	 * <code>FloatPolicies</code>.
	 * 
	 * @return <code>true</code> if global floating support is enabled; <code>false</code> 
	 * otherwise.
	 * @see #setGlobalFloatingEnabled(boolean)
	 */
	public static boolean isGlobalFloatingEnabled() {
		return getInstance().globalFloatingEnabled;
	}
	
	/**
	 * Sets the global setting used to control default framework floating behavior.
	 * If <code>globalFloatingEnabled</code> is <code>false</code>, all floating support for 
	 * the entire framework is turned off.  If <code>globalFloatingEnabled</code> is 
	 * <code>true</code>, then floating support for individual docking operations is deferred 
	 * to the installed <code>FloatPolicies</code>.
	 * 
	 * @param globalFloatingEnabled <code>true</code> if global floating support is to be
	 * enabled; <code>false</code> otherwise.
	 * @see #isGlobalFloatingEnabled()
	 */
	public static void setGlobalFloatingEnabled(boolean globalFloatingEnabled) {
		getInstance().globalFloatingEnabled = globalFloatingEnabled;
	}
}
