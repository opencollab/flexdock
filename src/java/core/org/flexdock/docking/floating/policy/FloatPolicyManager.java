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
 * @author Christopher Butler
 */
public class FloatPolicyManager extends DockingListener.Stub {
	private static final FloatPolicyManager SINGLETON = new FloatPolicyManager();
	public static final String FLOATING_ALLOWED = "FloatPolicyManager.FLOATING_ALLOWED";
	public static final String GLOBAL_FLOATING_BLOCKED =  "global.floating.blocked";
	
	private Vector policies;
	private boolean globalFloatingBlocked;
	
	public static FloatPolicyManager getInstance() {
		return SINGLETON;
	}
	
	private FloatPolicyManager() {
		policies = new Vector();
		addPolicy(DefaultFloatPolicy.getInstance());
		globalFloatingBlocked = Utilities.sysTrue(GLOBAL_FLOATING_BLOCKED);
	}
	
	public void dragStarted(DockingEvent evt) {
		Map context = evt.getDragContext();
		Boolean allowed = willFloatingBeAllowed(evt)? Boolean.TRUE: Boolean.FALSE;
		context.put(FLOATING_ALLOWED, allowed);
	}
	
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

	
	public static boolean isFloatingAllowed(Dockable dockable) {
		if(dockable==null)
			return false;
		
		Map context = DragManager.getDragContext(dockable);
		if(context==null)
			return getInstance().willFloatingBeAllowed(dockable);
		
		Boolean floatAllowed = (Boolean)context.get(FLOATING_ALLOWED);
		return floatAllowed==null? true: floatAllowed.booleanValue();		
	}
	
	
	
	private boolean willFloatingBeAllowed(DockingEvent evt) {
		return willFloatingBeAllowed(evt.getDockable());
	}
	
	public boolean willFloatingBeAllowed(Dockable dockable) {
		for(Iterator it=policies.iterator(); it.hasNext();) {
			FloatPolicy policy = (FloatPolicy)it.next();
			if(!policy.isFloatingAllowed(dockable))
				return false;
		}
		return true;
	}

	public void addPolicy(FloatPolicy policy) {
		if(policy!=null)
			policies.add(policy);
	}
	
	public void removePolicy(FloatPolicy policy) {
		if(policy!=null)
			policies.remove(policy);
	}
	
	
	public static boolean isGlobalFloatingBlocked() {
		return getInstance().globalFloatingBlocked;
	}
	
	public static void setGlobalFloatingBlocked(boolean globalFloatingBlocked) {
		getInstance().globalFloatingBlocked = globalFloatingBlocked;
	}
}
