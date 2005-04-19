/*
 * Created on Apr 18, 2005
 */
package org.flexdock.dockbar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.plaf.common.border.SlideoutBorder;
import org.flexdock.util.Utilities;

/**
 * @author Christopher Butler
 */
public class SlideoutPane extends JPanel implements SwingConstants {
	private static final Dimension RESIZE_DIMS = new Dimension(5, 5);
	private String dockableId;
	private int orientation;
	private JPanel dragEdge;

	
	public SlideoutPane(ResizeListener listener) {
		super(new BorderLayout(0, 0));
		setBorder(new SlideoutBorder());
		
		dragEdge = new JPanel();
		dragEdge.setPreferredSize(RESIZE_DIMS);
		dragEdge.addMouseListener(listener);
		dragEdge.addMouseMotionListener(listener);
		
		setOrientation(DockbarManager.DEFAULT_EDGE, true);
	}


	
	public boolean setDockable(String dockId) {
		if(!Utilities.isChanged(dockableId, dockId))
			return false;
			
		// remove the currently docked component
		Dockable d = getDockable();
		Component c = d==null? null: d.getDockable();
		if(c!=null) {
			remove(c);
		}
		
		// add the new component
		d = DockingManager.getRegisteredDockable(dockId);
		c = d==null? null: d.getDockable();
		if(c!=null)
			add(c, BorderLayout.CENTER);
		
		dockableId = dockId;
		return true;
	}
	
	public void setOrientation(int orient) {
		setOrientation(orient, false);
	}
	
	private void setOrientation(int orient, boolean forceUpdate) {
		orient = Dockbar.getValidOrientation(orient);
		boolean change = orientation!=orient;
		orientation = orient;
		
		Border border = getBorder();
		if(border instanceof SlideoutBorder)
			((SlideoutBorder)border).setOrientation(orient);
		
		if(forceUpdate || change) {
			// update the drag edge
			remove(dragEdge);
			add(dragEdge, getEdgeRegion());
			dragEdge.setCursor(getResizeCursor());

			// revalidate
			revalidate();
		}
	}
	
	public int getOrientation() {
		return orientation;
	}
	
	private String getEdgeRegion() {
		switch(orientation) {
			case TOP:
				return BorderLayout.SOUTH;
			case BOTTOM:
				return BorderLayout.NORTH;
			case RIGHT:
				return BorderLayout.WEST;
			default:
				return BorderLayout.EAST;
		}
	}
	
	public Cursor getResizeCursor() {
		return orientation==LEFT || orientation==RIGHT? Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR): Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
	}
	
	public Dockable getDockable() {
		return DockingManager.getRegisteredDockable(dockableId);
	}


}
