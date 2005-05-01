/*
 * Created on Apr 18, 2005
 */
package org.flexdock.dockbar.event;

import java.util.EventObject;

import org.flexdock.docking.Dockable;

/**
 * @author Christopher Butler
 */
public class DockbarEvent extends EventObject {
	public static final int EXPANDED = 0;
	public static final int LOCKED = 1;
	public static final int COLLAPSED = 2;
	
	public static final int MINIMIZE_STARTED = 10;
	public static final int MINIMIZE_COMPLETED = 11;
	
	private int edge;
	private int type;
	private boolean consumed;
	
	public DockbarEvent(Dockable dockable, int type, int edge) {
		super(dockable);
		this.type = type;
		this.edge = edge;
	}

	public int getEdge() {
		return edge;
	}
	
	public int getType() {
		return type;
	}

	public boolean isConsumed() {
		return consumed;
	}
	
	public void consume() {
		consumed = true;
	}
}
