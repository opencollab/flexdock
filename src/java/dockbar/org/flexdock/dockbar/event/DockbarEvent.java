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
	public static final int ACTIVATED = 0;
	public static final int DEACTIVATED = 1;
	
	private int edge;
	private int type;
	
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
}
