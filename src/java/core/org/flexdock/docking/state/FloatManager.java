/*
 * Created on May 26, 2005
 */
package org.flexdock.docking.state;

import java.awt.Component;
import java.awt.Rectangle;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.floating.frames.DockingFrame;

/**
 * @author Christopher Butler
 */
public interface FloatManager {
	public static final FloatManager DEFAULT_STUB = new Stub();
	
	public FloatingGroup getGroup(String groupName);

	public FloatingGroup getGroup(Dockable dockable);

	public void addToGroup(Dockable dockable, String groupId);

	public void removeFromGroup(Dockable dockable);

	public DockingFrame floatDockable(Dockable dockable, Component frameOwner);

	public DockingFrame floatDockable(Dockable dockable, Component frameOwner, Rectangle screenBounds);

	public static class Stub implements FloatManager {

		public void addToGroup(Dockable dockable, String groupId) {
		}

		public DockingFrame floatDockable(Dockable dockable, Component frameOwner, Rectangle screenBounds) {
			return null;
		}

		public DockingFrame floatDockable(Dockable dockable, Component frameOwner) {
			return null;
		}

		public FloatingGroup getGroup(Dockable dockable) {
			return null;
		}

		public FloatingGroup getGroup(String groupName) {
			return null;
		}

		public void removeFromGroup(Dockable dockable) {
		}
	}
}