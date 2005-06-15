/*
 * Created on Mar 18, 2005
 */
package org.flexdock.view.tracking;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.flexdock.docking.DockingConstants;
import org.flexdock.util.SwingUtility;
import org.flexdock.view.View;

/**
 * @author Christopher Butler
 */
public class ViewListener implements DockingConstants, PropertyChangeListener, ChangeListener, AWTEventListener {
	private static final ViewListener SINGLETON = new ViewListener();
	private static HashSet PROP_EVENTS = new HashSet();

	static {
		primeImpl();
	}

	public static void prime() {
	}
	
	private static void primeImpl() {
		PROP_EVENTS.add(PERMANENT_FOCUS_OWNER);
		PROP_EVENTS.add(ACTIVE_WINDOW);
		
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
						focusManager.addPropertyChangeListener(SINGLETON);
					}
				};
				EventQueue.invokeLater(r);				
			}
		};
		t.start();
		
		Toolkit.getDefaultToolkit().addAWTEventListener(SINGLETON, AWTEvent.MOUSE_EVENT_MASK);
	}
	
	public static ViewListener getInstance() {
		return SINGLETON;
	}
	
	private ViewListener() {
	}
	
	public void eventDispatched(AWTEvent event) {
		//catch all mousePressed events
		if(event.getID()!=MouseEvent.MOUSE_PRESSED)
			return;

		MouseEvent evt = (MouseEvent)event;
		Component c = (Component)evt.getSource();
		
		// check to see if the event was targeted at the deepest component at the current
		// mouse loaction
		Container  container = c instanceof Container? (Container)c: null;
		if(container!=null && container.getComponentCount()>1) {
			// if not, find the deepest component
			Point p = evt.getPoint();
			c = SwingUtilities.getDeepestComponentAt(c, p.x, p.y);
		}
		
		// request activation of the view that encloses this component
		ViewTracker.requestViewActivation(c);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String pName = evt.getPropertyName();
		if(!PROP_EVENTS.contains(pName))
			return;
		
		Component oldVal = SwingUtility.toComponent(evt.getOldValue());
		Component newVal = SwingUtility.toComponent(evt.getNewValue());
		boolean switchTo = newVal!=null;
		
		if(ACTIVE_WINDOW.equals(pName))
			handleWindowChange(evt, oldVal, newVal, switchTo);
		else
			handleFocusChange(evt, oldVal, newVal, switchTo);
	}
	
	private void handleWindowChange(PropertyChangeEvent evt, Component oldVal, Component newVal, boolean activate) {
		// notify the ViewTracker of the window change
		ViewTracker.windowActivated(newVal);
		
		Component srcComponent = activate? newVal: oldVal;
		ViewTracker tracker = ViewTracker.getTracker(srcComponent);
		if(tracker!=null)
			tracker.setActive(activate);
	}
	
	private void handleFocusChange(PropertyChangeEvent evt, Component oldVal, Component newVal, boolean switchTo) {
		if(!switchTo)
			return;
		
		if(newVal instanceof JTabbedPane)
			newVal = ((JTabbedPane)newVal).getSelectedComponent();
		activateComponent(newVal);
	}
	
	private void activateComponent(Component c) {
		View view = c instanceof View? (View)c: (View)SwingUtilities.getAncestorOfClass(View.class, c);
		if(view==null)
			return;

		ViewTracker tracker = ViewTracker.getTracker(view);
		if(tracker!=null) {
			tracker.setActive(view);
		}
	}


	public void stateChanged(ChangeEvent e) {
		Object obj = e.getSource();
		if(obj instanceof JTabbedPane) {
			JTabbedPane pane = (JTabbedPane)obj;
			Component c = pane.getSelectedComponent();
			if(c instanceof View)
				activateComponent(c);
		}
	}

}
