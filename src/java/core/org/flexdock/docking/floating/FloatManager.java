/*
 * Created on May 3, 2005
 */
package org.flexdock.docking.floating;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.Hashtable;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.floating.frames.DockingFrame;
import org.flexdock.util.RootWindow;

/**
 * @author Christopher Butler
 */
public class FloatManager {
	private static final FloatManager SINGLETON = new FloatManager();
	private static final Hashtable FLOATING_GROUPS = new Hashtable();
	
	public static FloatManager getInstance() {
		return SINGLETON;
	}
	
	
	private DockingFrame getDockingFrame(Dockable dockable, Component frameOwner) {
		FloatingGroup group = getGroup(dockable);
		if(group==null)
			group = new FloatingGroup(dockable.getDockingProperties().getFloatingGroup());
		
		DockingFrame frame = group.getFrame();
		if(frame==null) {
			frame = DockingFrame.create(frameOwner, group.getName());
			group.setFrame(frame);
			FLOATING_GROUPS.put(group.getName(), group);
		}
		return frame;
	}
	
	public DockingFrame floatDockable(Dockable dockable, Component frameOwner, Rectangle screenBounds) {
		if(dockable==null || screenBounds==null)
			return null;
		
		// create the frame
		DockingFrame frame = getDockingFrame(dockable, frameOwner);
		if(screenBounds!=null)
			frame.setBounds(screenBounds);

		// undock the current Dockable instance from it's current parent container
		DockingManager.undock(dockable);

		// add to the floating frame
		frame.addDockable(dockable);
		
		// display and return
		if(!frame.isVisible())
			frame.setVisible(true);
		return frame;
	}
	
	public DockingFrame floatDockable(Dockable dockable, Component frameOwner) {
		FloatingGroup group = getGroup(dockable);
		Rectangle bounds = group==null? null: group.getBounds();
		if(bounds==null) {
			if(dockable.getDockable().isValid()) {
				bounds = dockable.getDockable().getBounds();
			}
			else
				bounds = new Rectangle(0, 0, 200, 200);
			
			Rectangle ownerBounds = frameOwner instanceof DockingFrame? 
			((DockingFrame)frameOwner).getOwner().getBounds():
			RootWindow.getRootContainer(frameOwner).getRootContainer().getBounds();

			int x = (ownerBounds.x + ownerBounds.width/2) - bounds.width/2;
			int y = (ownerBounds.y + ownerBounds.height/2) - bounds.height/2;
			bounds.setLocation(x, y);
		}
		
		return floatDockable(dockable, frameOwner, bounds);
	}
	
	public FloatingGroup getGroup(Dockable dockable) {
		if(dockable==null)
			return null;
		
		String groupId = dockable.getDockingProperties().getFloatingGroup();
		return getGroup(groupId);
	}
	
	public FloatingGroup getGroup(String groupId) {
		return groupId==null? null: (FloatingGroup)FLOATING_GROUPS.get(groupId);
	}
	
	public void addToGroup(Dockable dockable, String groupId) {
		// floating groups are mutually exclusive
		removeFromGroup(dockable);

		FloatingGroup group = getGroup(groupId);
		if(dockable!=null && group!=null) {
			group.addDockable(dockable.getPersistentId());
			dockable.getDockingProperties().setFloatingGroup(groupId);
		}
	}
	
	public void removeFromGroup(Dockable dockable) {
		FloatingGroup group = getGroup(dockable);
		if(dockable!=null) {
			if(group!=null)
				group.removeDockable(dockable.getPersistentId());
			dockable.getDockingProperties().setFloatingGroup(null);
		}
		
		// if the group is empty, dispose of it so we don't have 
		// any memory leaks
		if(group!=null && group.getDockableCount()==0) {
			FLOATING_GROUPS.remove(group.getName());
			group.destroy();
		}
		
	}
	

}
