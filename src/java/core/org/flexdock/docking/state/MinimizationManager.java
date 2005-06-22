/*
 * Created on May 26, 2005
 */
package org.flexdock.docking.state;

import java.awt.Component;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;

/**
 * @author Christopher Butler
 */
public interface MinimizationManager {
	public static final MinimizationManager DEFAULT_STUB = new Stub();
	
	public static final int UNSPECIFIED_LAYOUT_CONSTRAINT = -1;
	
	public static final int TOP = DockingConstants.TOP;
	
	public static final int LEFT = DockingConstants.LEFT;
	
	public static final int BOTTOM = DockingConstants.BOTTOM;
	
	public static final int RIGHT = DockingConstants.RIGHT;
	
	public static final int CENTER = DockingConstants.CENTER;
	
	public boolean close(Dockable dockable);
	
	public void preview(Dockable dockable, boolean locked);
	
	public void setMinimized(Dockable dockable, boolean minimized, Component window, int constraint);
	
	public static class Stub implements MinimizationManager {
		public boolean close(Dockable dockable) {
			return false;
		}
		public void preview(Dockable dockable, boolean locked) {
		}
		public void setMinimized(Dockable dockable, boolean minimized, Component window, int edge) {
		}
	}
}
