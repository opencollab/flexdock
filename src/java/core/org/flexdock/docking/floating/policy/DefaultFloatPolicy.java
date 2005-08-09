/*
 * Created on May 31, 2005
 */
package org.flexdock.docking.floating.policy;

import java.util.Set;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.floating.frames.FloatingDockingPort;
import org.flexdock.docking.floating.policy.FloatPolicy.NullFloatPolicy;

/**
 * This class provides an implementation of the <code>FloatPolicy</code> interface
 * to provide default behavior for the framework.  It blocks floating operations
 * for <code>Dockables</code> without any frame drag sources, for already floating 
 * <code>Dockables</code> that cannot be reparented within a new dialog, or if 
 * global floating support has been disabled.
 * 
 * @author Christopher Butler
 */
public class DefaultFloatPolicy extends NullFloatPolicy {
	
	private static final DefaultFloatPolicy SINGLETON = new DefaultFloatPolicy();
	
	/**
	 * Returns a singleton instance of <code>DefaultFloatPolicy</code>.
	 * 
	 * @return a singleton instance of <code>DefaultFloatPolicy</code>. 
	 */
	public static DefaultFloatPolicy getInstance() {
		return SINGLETON;
	}
	
	/**
	 * Checks the previous <code>DockingPort</code> for the specified <code>DockingEvent</code>
	 * and returns <code>false</code> if it is in a floating dialog and contains less than
	 * two <code>Dockables</code>.  A floating dialog may contain multiple 
	 * <code>Dockables</code>, each of which may be dragged out of the current dialog to 
	 * float in their own dialog.  However, if a floating dialog only contains a single
	 * <code>Dockable</code>, it makes no sense to remove the <code>Dockable</code> only to
	 * float it within another dialog.  This situation is caught by this method and the 
	 * docking operation is blocked.
	 * 
	 * @param evt the <code>DockingEvent</code> to be checked for drop-to-float support
	 * @return <code>false</code> if the <code>DockingEvent</code> is attempting to float
	 * an already floating <code>Dockable</code> with no other <code>Dockables</code> in its 
	 * current dialog; <code>true</code> otherwise.
	 * @see FloatPolicy#isFloatDropAllowed(DockingEvent)
	 * @see DockingEvent#getOldDockingPort()
	 * @see FloatingDockingPort#getDockableCount()
	 * @see DockingEvent#consume()
	 */
	public boolean isFloatDropAllowed(DockingEvent evt) {
		DockingPort oldPort = evt.getOldDockingPort();
		// if we're already floating, and we're the only dockable
		// in a floating dockingport, then we don't want to undock
		// from the port and re-float (dispose and create a new DockingFrame).
		if(oldPort instanceof FloatingDockingPort) {
			FloatingDockingPort dockingPort = (FloatingDockingPort)oldPort;
			if(dockingPort.getDockableCount()<2) {
				evt.consume();
				return false;
			}
		}
		
		return super.isFloatDropAllowed(evt);
	}
	
	/**
	 * Blocks floating support (returns false) if <code>dockable</code> is <code>null</code>, if 
	 * <code>FloatPolicyManager.isGlobalFloatingEnabled()</code> returns <code>false</code>, 
	 * or if there are no entries within the <code>Set</code> returned by 
	 * <code>dockable.getFrameDragSources()</code>.  Otherwise, this method returns 
	 * <code>true</code>.
	 * 
	 * @param dockable the <code>Dockable</code> to be checked for floating support
	 * @return <code>false</code> if floating is blocked for the specified <code>Dockable</code>; 
	 * <code>true</code> otherwise.
	 * @see Dockable#getFrameDragSources()
	 * @see FloatPolicyManager#isGlobalFloatingEnabled()
	 */
	public boolean isFloatingAllowed(Dockable dockable) {
		if(dockable==null || !FloatPolicyManager.isGlobalFloatingEnabled())
			return false;
		
		Set frameDragSources = dockable.getFrameDragSources();
		if(frameDragSources==null || frameDragSources.size()==0)
			return false;
		
		return super.isFloatingAllowed(dockable);
	}
    
}
