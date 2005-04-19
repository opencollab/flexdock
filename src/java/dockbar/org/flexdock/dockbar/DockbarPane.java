/*
 * Created on Apr 17, 2005
 */
package org.flexdock.dockbar;

import java.awt.Cursor;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.props.DockableProps;

/**
 * @author Christopher Butler
 */
public class DockbarPane extends JPanel implements SwingConstants {
	private static final int MINIMUM_SLIDE_SIZE = 20;
	private DockbarManager manager;
	private boolean animating;
	private boolean expanded;
	private SlideoutPane slideoutPane;
	
	public DockbarPane(DockbarManager mgr) {
		manager = mgr;
		setOpaque(false);
		slideoutPane = new SlideoutPane(mgr.getResizeListener());
		add(slideoutPane);
	}
	
	public void setAnimating(boolean anim) {
		animating = anim;
	}
	
	public boolean isAnimating() {
		return animating;
	}
	
	public void doLayout() {
		if(isAnimating() || !slideoutPane.isVisible())
			return;

		Dockable dockable = slideoutPane.getDockable();
		if(!expanded || dockable==null) {
			slideoutPane.setBounds(0, 0, 0, 0);
			return;
		}

		DockableProps props = dockable.getDockingProperties();
		float pinSize = props.getPinSize().floatValue();
		
		int x = 0;
		int y = 0;
		int w = getWidth();
		int h = getHeight();
		int edge = manager.getActiveEdge();
		
		if(edge==LEFT || edge==RIGHT) {
			int newWidth = (int)(((float)w)*pinSize);
			newWidth = Math.max(MINIMUM_SLIDE_SIZE, newWidth);
			if(edge==RIGHT) {
				x = w - newWidth;
			}
			w = newWidth;
		}
		else {
			int newHeight = (int)(((float)h)*pinSize);
			newHeight = Math.max(MINIMUM_SLIDE_SIZE, newHeight);
			if(edge==BOTTOM) {
				y = h - newHeight;
			}
			h = newHeight;
		}
		
		slideoutPane.setBounds(x, y, w, h);
	}
	
	public void setExpanded(boolean b) {
		boolean changed = b==expanded;
		expanded = b;
		if(changed)
			revalidate();
	}
	
	public void setOrientation(int orient) {
		slideoutPane.setOrientation(orient);
	}
	
	public int getOrientation() {
		return slideoutPane.getOrientation();
	}
	
	public void setDockable(String dockableId) {
		boolean changed = slideoutPane.setDockable(dockableId);
		slideoutPane.setVisible(slideoutPane.getDockable()!=null);
		if(changed)
			revalidate();
	}
	
	public Dockable getDockable() {
		return slideoutPane.getDockable();
	}
	
	public Cursor getResizeCursor() {
		return slideoutPane.getResizeCursor();
	}
}
