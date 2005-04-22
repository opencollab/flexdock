/*
 * Created on Apr 13, 2005
 */
package org.flexdock.dockbar;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.flexdock.docking.Dockable;
import org.flexdock.plaf.common.border.SlideoutBorder;


/**
 * @author Christopher Butler
 */
public class Dockbar extends JPanel implements SwingConstants {
	private int orientation;
	private DockbarManager manager;
	private ArrayList mDocks = new ArrayList();
	
	static {
		// make sure DockbarLabel is initialized
		Class c = DockbarLabel.class;
	}

	public static int getValidOrientation(int orient) {
		switch (orient) {
			case LEFT:
				return LEFT;
			case RIGHT:
				return RIGHT;
			case BOTTOM:
				return BOTTOM;
			default:
				return LEFT;
		}
	}

	public Dockbar(DockbarManager manager, int orientation) {
		this.manager = manager;
		setBorder(new SlideoutBorder());
		setOrientation(orientation);
	}

	void undock(Dockable dockable) {
		DockbarLabel label = findLabel(dockable);
		
		remove(label);
		mDocks.remove(label);
		getParent().validate();
		repaint();
	}

	private DockbarLabel findLabel(Dockable dockable) {
		if(dockable==null)
			return null;
		
		for (Iterator docks = mDocks.iterator(); docks.hasNext();) {
			DockbarLabel label = (DockbarLabel) docks.next();

			if (label.getDockable() == dockable)
				return label;
		} // for

		return null;
	}
	
	public boolean contains(Dockable dockable) {
		return findLabel(dockable)!=null;
	}

	public void dock(Dockable dockable) {
		if(dockable==null)
			return;
		
		DockbarLabel currentLabel = findLabel(dockable);
		if (currentLabel!=null) {
			currentLabel.setActive(false);
			return;
		}

		DockbarLabel newLabel = new DockbarLabel(dockable.getPersistentId(), getOrientation());
		add(newLabel);
		mDocks.add(newLabel);

		getParent().validate();
		repaint();
	}

	public int getOrientation() {
		return orientation;
	}

	private void setOrientation(int orientation) {
		orientation = getValidOrientation(orientation);
		this.orientation = orientation;
		
		Border border = getBorder();
		if(border instanceof SlideoutBorder)
			((SlideoutBorder)border).setOrientation(orientation);
		
		int boxConstraint = orientation==TOP || orientation==BOTTOM? BoxLayout.LINE_AXIS: BoxLayout.PAGE_AXIS;
		setLayout(new BoxLayout(this, boxConstraint));
	}
	
	public Dimension getPreferredSize() {
		if(mDocks.size()==0)
			return new Dimension(0,0);
		
		DockbarLabel label = (DockbarLabel)getComponent(0);
		return label.getPreferredSize();
	}
	
	void activate(String dockableId) {
		if(manager!=null)
			manager.setActiveDockable(dockableId);
	}
}