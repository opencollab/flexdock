/*
 * Created on Mar 8, 2005
 */
package org.flexdock.docking.event;

import java.awt.AWTEvent;
import java.util.EventObject;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;

/**
 * @author Kevin Duffey
 * @author Christopher Butler
 */
public class DockingEvent extends EventObject {
	public static final int DRAG_STARTED = 0;
	public static final int DROP_STARTED = 1;
	public static final int DOCKING_COMPLETE = 2;
	public static final int DOCKING_CANCELED = 3;
	public static final int UNDOCKING_COMPLETE = 4;

	private DockingPort oldPort;
	private DockingPort newPort;
	private int eventType;
	private boolean consumed;
	private AWTEvent trigger;
	private String region;
	private boolean overWindow;

	/**
	 * Constructor to create a DockingEvent object with the provided Dockable,
	 * the originating docking part, the destination docking port and whether
	 * the dock is completed or canceled.
	 */
	public DockingEvent(Dockable source, DockingPort oldPort, DockingPort newPort, int eventType) {
		this(source, oldPort, newPort, eventType, null);
	}

	/**
	 * Constructor to create a DockingEvent object with the provided Dockable,
	 * the originating docking part, the destination docking port and whether
	 * the dock is completed or canceled.
	 */
	public DockingEvent(Dockable source, DockingPort oldPort, DockingPort newPort, int eventType, AWTEvent trigger) {
		super(source);
		this.oldPort = oldPort;
		this.newPort = newPort;
		this.eventType = eventType;
		this.trigger = trigger;
		this.region = DockingPort.UNKNOWN_REGION;
		setOverWindow(true);
	}

	/**
	 * Returns the old docking port which the source <code>Dockable</code> was
	 * originally docked to.
	 * 
	 * @return DockingPort the old docking port
	 */
	public DockingPort getOldDockingPort() {
		return oldPort;
	}

	/**
	 * Returns the new docking port the source <code>Dockable</code> has been
	 * docked to.
	 * 
	 * @return DockingPort the new docking port
	 */
	public DockingPort getNewDockingPort() {
		return newPort;
	}

	/**
	 * Returns the integer value of the type of event this DockingEvent
	 * represents. One of DOCKING_COMPLETE or DOCKING_CANCELED should be
	 * returned.
	 * 
	 * @return int the integer value of this event type.
	 */
	public int getEventType() {
		return eventType;
	}

	public boolean isConsumed() {
		return consumed;
	}

	public void consume() {
		this.consumed = true;
	}

	public AWTEvent getTrigger() {
		return trigger;
	}

	public void setTrigger(AWTEvent trigger) {
		this.trigger = trigger;
	}

	public Object getTriggerSource() {
		return trigger == null ? null : trigger.getSource();
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		if (!DockingManager.isValidDockingRegion(region))
			region = DockingPort.UNKNOWN_REGION;
		this.region = region;
	}

	public boolean isOverWindow() {
		return overWindow;
	}

	public void setOverWindow(boolean overWindow) {
		this.overWindow = overWindow;
	}
}