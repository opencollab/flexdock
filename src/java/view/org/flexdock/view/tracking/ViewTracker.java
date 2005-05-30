/*
 * Created on Mar 4, 2005
 */
package org.flexdock.view.tracking;

import java.awt.Component;
import java.awt.EventQueue;
import java.util.WeakHashMap;

import javax.swing.SwingUtilities;

import org.flexdock.event.EventDispatcher;
import org.flexdock.util.RootWindow;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.View;


/**
 * @author Christopher Butler
 */
public class ViewTracker {
	private static final WeakHashMap TRACKERS_BY_WINDOW = new WeakHashMap();
	private static ViewTracker currentTracker;
	private static final Object LOCK = new Object();
	private View currentView;
	
	
	static {
		initialize();
	}
	
	
	private static void initialize() {
		EventDispatcher.addListener(new DockbarMonitor());
	}
	
	
	public static ViewTracker getTracker(Component component) {
		RootWindow window = RootWindow.getRootContainer(component);
		return getTracker(window);
	}
	
	public static ViewTracker getCurrentTracker() {
		synchronized(LOCK) {
			return currentTracker;
		}
	}
	
	private static ViewTracker getTracker(RootWindow window) {
		if(window==null)
			return null;

		Component root = window.getRootContainer();
		ViewTracker tracker = (ViewTracker)TRACKERS_BY_WINDOW.get(root);
		
		if(tracker==null) {
			tracker = new ViewTracker();
			
			TRACKERS_BY_WINDOW.put(root, tracker);
		}
		return tracker;
	}
	
	static void windowActivated(Component c) {
		RootWindow window = RootWindow.getRootContainer(c);
		ViewTracker tracker = getTracker(window);
		synchronized(LOCK) {
			currentTracker = tracker;
		}
	}

	public static void requestViewActivation(Component c) {
		if(c==null)
			return;
		
		View view = c instanceof View? (View)c: (View)SwingUtilities.getAncestorOfClass(View.class, c);
		if(view!=null) {
			requestViewActivation(c, view);
		}
	}
	
	public static void requestViewActivation(final Component c, final View view) {
		if(c==null || view==null)
			return;
		
		// make sure the window is currently active
		SwingUtility.activateWindow(c);
		
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						focusView(c, view);
					}
				};
				EventQueue.invokeLater(r);
			}
		};
		t.start();
	}
	
	private static void focusView(Component child, View parentView) {
		// if the view is already active, then leave it alone
		if(parentView.isActive())
			return;

		Component focuser = SwingUtility.getNearestFocusableComponent(child, parentView);
		if(focuser==null)
			focuser = parentView;
		focuser.requestFocus();
	}
	
	public ViewTracker() {

	}
	
	public void setActive(boolean b) {
		if(currentView==null)
			return;
		
		currentView.setActive(b);
	}
	
	public void setActive(View view) {
		if(view!=currentView) {
			setActive(false);
			currentView = view;
			setActive(true);
		}
	}

}
