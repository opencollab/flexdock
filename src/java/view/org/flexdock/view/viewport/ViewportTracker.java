/*
 * Created on Mar 4, 2005
 */
package org.flexdock.view.viewport;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.flexdock.util.ComponentNest;
import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public class ViewportTracker implements PropertyChangeListener, MouseListener {
	private static final ViewportTracker TRACKER = new ViewportTracker();
	private static final HashSet FOCUS_EVENTS = new HashSet();
	private static final String FOCUS_OWNER = "focusOwner";
	private static final String PERMANENT_FOCUS_OWNER = "permanentFocusOwner";
		
	private HashSet activeViews = new HashSet();
	
	static {
		initialize();
	}

	private static void initialize() {
		FOCUS_EVENTS.add(FOCUS_OWNER);
		
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						setup();
					}
				};
				EventQueue.invokeLater(r);				
			}
		};
		t.start();
	}
	
	private static void setup() {
		KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		focusManager.addPropertyChangeListener(TRACKER);
	}

	public static ViewportTracker getInstance() {
		return TRACKER;
	}
	
	private ViewportTracker() {
		
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}


	public void propertyChange(PropertyChangeEvent evt) {
		// only respond to focus events
		if(!FOCUS_EVENTS.contains(evt.getPropertyName()) || evt.getNewValue()==null)
			return;
		
		notifyViewport(evt.getNewValue());
	}
	
	public void mousePressed(MouseEvent evt) {
		notifyViewport(evt.getSource());
	}

	private void notifyViewport(Object evtSrc) {
		Component src = evtSrc instanceof Component? (Component)evtSrc: null;
		ComponentNest nested = ComponentNest.find(src, View.class, Viewport.class);
		activate((Viewport)nested.parent, (View)nested.child);
	}
	
	public void activate_(final Viewport viewport) {
		activate(viewport, null);
	}
	
	public void activate(final Viewport viewport, final View view) {
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						activateImpl(viewport, view);
					}
				};
				EventQueue.invokeLater(r);
			}
		};
		t.start();
	}
	
	private void activateImpl(Viewport viewport, View targetView) {
		if(viewport==null || !viewport.isAcceptsFocus())
			return;

		Set newViews = targetView==null? viewport.getViewset(): createSet(targetView);
		
		for(Iterator it=activeViews.iterator(); it.hasNext();) {
			View view = (View)it.next();
			if(!newViews.contains(view))
				view.setActive(false);
		}
		
		activeViews.clear();

		for(Iterator it=newViews.iterator(); it.hasNext();) {
			View view = (View)it.next();
			view.setActive(true);
		}			

		activeViews.addAll(newViews);
		
		KeyboardFocusManager mgr = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Component focusOwner = mgr.getFocusOwner();
		if(focusOwner==null)
			return;
		
		Viewport focusPort = (Viewport)SwingUtilities.getAncestorOfClass(Viewport.class, focusOwner);
		if(focusPort!=viewport)
			mgr.clearGlobalFocusOwner();
	}
	
	private Set createSet(Object obj) {
		HashSet set = new HashSet(1);
		set.add(obj);
		return set;
	}

	
}
