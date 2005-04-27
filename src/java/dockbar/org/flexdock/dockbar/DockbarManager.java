/*
 * Created on Apr 14, 2005
 */
package org.flexdock.dockbar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.flexdock.dockbar.activation.ActivationQueue;
import org.flexdock.dockbar.activation.Animation;
import org.flexdock.dockbar.event.ActivationListener;
import org.flexdock.dockbar.event.DockbarEvent;
import org.flexdock.dockbar.event.DockbarListener;
import org.flexdock.dockbar.event.DockbarTracker;
import org.flexdock.dockbar.event.EventDispatcher;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.plaf.common.border.CompoundEmptyBorder;
import org.flexdock.util.RootWindow;
import org.flexdock.util.Utilities;

/**
 * @author Christopher Butler
 */
public class DockbarManager implements SwingConstants {
	private static final WeakHashMap MANAGERS_BY_WINDOW = new WeakHashMap();
	public static final Integer DOCKBAR_LAYER = new Integer(JLayeredPane.PALETTE_LAYER.intValue()-5);
	public static final int DEFAULT_EDGE = LEFT;
	public static final int UNSPECIFIED_EDGE = -1;
	
	private static DockbarManager currentManager;

	private WeakReference windowRef;
	private Dockbar leftBar;
	private Dockbar rightBar;
	private Dockbar bottomBar;
	private ViewPane viewPane;
	
	private EventDispatcher eventDispatcher;
	private DockbarLayout dockbarLayout;
	private ActivationListener activationListener;

	private int activeEdge = UNSPECIFIED_EDGE;
	private String activeDockableId;
	private boolean animating;
	private boolean dragging;


	static {
		DockbarTracker.register();
	}
	
	public static DockbarManager getInstance(Component c) {
		RootWindow window = RootWindow.getRootContainer(c);
		return getInstance(window);
	}

	public static DockbarManager getInstance(RootWindow window) {
		if(window==null)
			return null;
		
		DockbarManager mgr = (DockbarManager)MANAGERS_BY_WINDOW.get(window);
		if(mgr==null) { 
			mgr = new DockbarManager(window);
			synchronized(MANAGERS_BY_WINDOW) {
				MANAGERS_BY_WINDOW.put(window, mgr);
			}
			mgr.install();
		}
		
		if(currentManager==null)
			currentManager = mgr;
		
		return mgr;		
	}
	
	public static void windowChanged(Component newWindow) {
		currentManager = getInstance(newWindow);
	}

	public static DockbarManager getCurrent() {
		return currentManager;
	}
	
	
	private DockbarManager(RootWindow window) {
		eventDispatcher = new EventDispatcher();
		dockbarLayout = new DockbarLayout(this);
		activationListener = new ActivationListener(this);
		
		leftBar = new Dockbar(this, LEFT);
		rightBar = new Dockbar(this, RIGHT);
		bottomBar = new Dockbar(this, BOTTOM);
		viewPane = new ViewPane(this);

		windowRef = new WeakReference(window);
	}
	
	public RootWindow getWindow() {
		return (RootWindow)windowRef.get();
	}

	
	private void install() {
		RootWindow window = getWindow();
		if(window==null)
			return;
		
		JLayeredPane layerPane = window.getLayeredPane();
		boolean changed = install(leftBar, layerPane);
		changed = install(rightBar, layerPane) || changed;
		changed = install(bottomBar, layerPane) || changed;
		changed = install(viewPane, layerPane) || changed;

		if(changed) {
			layerPane.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent evt) {
					if(evt.getSource() instanceof JLayeredPane)
						revalidate();
				}
			});
		}
		revalidate();
	}
	
	private boolean install(Component c, JLayeredPane layerPane) {
		if(c.getParent()!=layerPane) {
			if(c.getParent()!=null)
				c.getParent().remove(c);
			layerPane.add(c, DOCKBAR_LAYER);
			return true;
		}
		return false;
	}
	
	
	
	
	
	
	
	public Dockbar getBottomBar() {
		return bottomBar;
	}

	public Dockbar getLeftBar() {
		return leftBar;
	}
	
	public Dockbar getRightBar() {
		return rightBar;
	}
	
	public ViewPane getViewPane() {
		return viewPane;
	}
	
	public Rectangle getViewPaneArea() {
		return dockbarLayout.getViewpaneArea();
	}
	
	
	
	
	
	
	
	public void revalidate() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				validate();
			}
		});

	}
	
	public void validate() {
		toggleDockbars();
		updateInsets();
		dockbarLayout.layout();
		viewPane.revalidate();
	}
	
	private void updateInsets() {
		RootWindow window = getWindow();
		Container content = window==null? null: window.getContentPane();
		if(!(content instanceof JComponent))
			return;
	
		JComponent contentPane = (JComponent)content;
		Border contentBorder = contentPane.getBorder();
		if(!(contentBorder instanceof CompoundEmptyBorder)) {
			contentBorder = CompoundEmptyBorder.create(contentBorder, true, new Insets(-1, -1, -1, -1));
			contentPane.setBorder(contentBorder);
		}
		
		CompoundEmptyBorder border = (CompoundEmptyBorder)contentBorder;
		Insets contentInsets = contentPane.getInsets();
		int top = contentInsets==null? 0: contentInsets.top;
		
		Insets emptyInsets = getEmptyInsets();
		boolean changed = border.setEmptyInsets(emptyInsets);
		if(changed) {
			contentPane.revalidate();
		}
	}
	
	private void toggleDockbars() {
		leftBar.setVisible(leftBar.getComponentCount()!=0);
		rightBar.setVisible(rightBar.getComponentCount()!=0);
		bottomBar.setVisible(bottomBar.getComponentCount()!=0);
	}
	
	private Insets getEmptyInsets() {
		return new Insets(0, getLeftInset(), getBottomInset(), getRightInset());
	}
	
	private int getLeftInset() {
		return getDockbarInset(leftBar);
	}
	
	private int getRightInset() {
		return getDockbarInset(rightBar);
	}
	
	private int getBottomInset() {
		return getDockbarInset(bottomBar);
	}
	
	private int getDockbarInset(Dockbar dockbar) {
		boolean visible = dockbar.isVisible();
		if(!visible)
			return 0;
		
		Dimension dim = dockbar.getPreferredSize();
		if(dockbar==leftBar || dockbar==rightBar)
			return dim.width;
		return dim.height;
	}

	
	private int findDockbarEdge(Dockable dockable) {
		RootWindow window = RootWindow.getRootContainer(dockable.getDockable());
		if(window==null)
			return DEFAULT_EDGE;
			
		// get the dockable component and it's containing content pane
		Component cmp = dockable.getDockable();
		Container contentPane = window.getContentPane();
		
		// get the bounds of the content pane and dockable, translating the dockable into the 
		// content pane's axes
		Rectangle contentRect = new Rectangle(0, 0, contentPane.getWidth(), contentPane.getHeight());
		Rectangle dockRect = SwingUtilities.convertRectangle(cmp.getParent(), cmp.getBounds(), contentPane);
		
		// get the center of the dockable
		Point dockCenter = new Point(dockRect.x = (dockRect.width/2), dockRect.y + (dockRect.height/2));
		// get the center left, right, and bottom points
		Point leftCenter = new Point(0, contentRect.height/2);
		Point bottomCenter = new Point(contentRect.width/2, contentRect.height);
		Point rightCenter = new Point(contentRect.width, contentRect.height/2);
		
		// calculate the absolute distance from dockable center to each of the edge 
		// center points.  whichever is the shortest, that is the edge the dockable is 
		// 'closest' to and that will be the edge we'll return
		double min = Math.abs(dockCenter.distance(leftCenter));
		int edge = LEFT;
		double delta = Math.abs(dockCenter.distance(rightCenter));
		if(delta<min) {
			min = delta;
			edge = RIGHT;
		}
		delta = Math.abs(dockCenter.distance(bottomCenter));
		if(delta<min) {
			min = delta;
			edge = BOTTOM;
		}

		return edge;
	}

	public int getEdge(String dockableId) {
		Dockable dockable = DockingManager.getRegisteredDockable(dockableId);
		return getEdge(dockable);
	}
	
	public int getEdge(Dockable dockable) {
		Dockbar dockbar = getDockbar(dockable);

		if(dockbar==leftBar)
			return LEFT;
		if(dockbar==rightBar)
			return RIGHT;
		if(dockbar==bottomBar)
			return BOTTOM;
		return UNSPECIFIED_EDGE;
	}
	
	public Dockbar getDockbar(Dockable dockable) {
		if(dockable==null)
			return null;
		
		if(leftBar.contains(dockable))
			return leftBar;
		if(rightBar.contains(dockable))
			return rightBar;
		if(bottomBar.contains(dockable))
			return bottomBar;
		return null;
	}

	public Dockbar getDockbar(int edge) {
		edge = Dockbar.getValidOrientation(edge);
		switch(edge) {
			case RIGHT:
				return rightBar;
			case BOTTOM:
				return bottomBar;
			default:
				return leftBar;
		}
	}
	
	
	
	
	
	
	public void dock(Dockable dockable) {
		if(dockable==null)
			return;
		
		int edge = DEFAULT_EDGE;
		RootWindow window = getWindow();
		if(window!=null && !DockingManager.isDocked(dockable)) {
			edge = findDockbarEdge(dockable);
		}
		
		dock(dockable, edge);
	}
	
	public void dock(Dockable dockable, int edge) {
		if(dockable==null)
			return;
			
		edge = Dockbar.getValidOrientation(edge);
		Dockbar dockbar = getDockbar(edge);
		
		// undock the dockable 
		DockingManager.undock(dockable);
		// place in the dockbar
		dockbar.dock(dockable);
		// make sure they can't drag the dockable while it's in the dockbar
		dockable.getDockingProperties().setDockingEnabled(false);
		// indicate that the dockable is minimized
		dockable.getDockingProperties().setMinimized(true);
		revalidate();
	}
	
	
	public void undock(Dockable dockable) {
		if(getActiveDockable()==dockable)
			setActiveDockable((Dockable)null);
		
		Dockbar dockbar = getDockbar(dockable);
		if(dockbar!=null) {
			dockbar.undock(dockable);
			// restore drag capability to the dockable after removing
			// from the dockbar
			dockable.getDockingProperties().setDockingEnabled(true);
			// indicate that the dockable is no longer minimized
			dockable.getDockingProperties().setMinimized(false);
			revalidate();
		}
	}
	
	
	public int getActiveEdge() {
		return activeEdge;
	}
	
	private Dockbar getActiveDockbar() {
		int edge = getActiveEdge();
		switch(edge) {
			case TOP:
				return bottomBar;
			case RIGHT:
				return rightBar;
			default:
				return leftBar;
		}
	}

	public String getActiveDockableId() {
		return activeDockableId;
	}
	
	public Dockable getActiveDockable() {
		return DockingManager.getRegisteredDockable(activeDockableId);
	}
	
	public Cursor getResizeCursor() {
		return viewPane.getResizeCursor();
	}
	 
	public boolean isActive() {
		return getActiveDockable()!=null;
	}
	
	public void addListener(DockbarListener listener) {
		eventDispatcher.addListener(listener);
	}
	
	public boolean removeListener(DockbarListener listener) {
		return eventDispatcher.removeListener(listener);
	}

	
	public void setActiveDockable(String dockableId) {
		Dockable dockable = DockingManager.getRegisteredDockable(dockableId);
		setActiveDockable(dockable);
	}
		
	public void setActiveDockable(Dockable dockable) {
		// if we're not currently docked to any particular edge, then
		// we cannot activate the specified dockable.  instead, set the
		// active dockable to null
		final int newEdge = getEdge(dockable);
		if(newEdge==UNSPECIFIED_EDGE)
			dockable = null;

		// check for dockable changes
		Dockable oldDockable = getActiveDockable();
		final String newDockableId = dockable==null? null: dockable.getPersistentId();
		boolean changed = Utilities.isChanged(activeDockableId, newDockableId);
		// check for edge changes
		changed = changed || newEdge!=activeEdge;

		
		// if nothing has changed, then we're done
		if(changed) {
			viewPane.setLocked(false);
			startAnimation(oldDockable, dockable, newDockableId, newEdge);
			
			// exit here so we can test our animation.  after it's working, we can 
			// re-add the event dispatching
//			if(true)
//				return;

		}
	}
	
	private void dispatchEvent(Dockable oldDockable, Dockable newDockable) {
		// dispatch to event listeners
		int evtType = DockbarEvent.EXPANDED;
		if(newDockable==null && oldDockable!=null) {
			newDockable = oldDockable;
			evtType = DockbarEvent.COLLAPSED;
		}
		
		if(newDockable!=null) {
			DockbarEvent evt = new DockbarEvent(newDockable, evtType, getActiveEdge());
			eventDispatcher.dispatch(evt);				
		}		
	}
	
	private void startAnimation(final Dockable oldDockable, final Dockable newDockable, final String newDockableId, final int newEdge) {
		Animation deactivation = oldDockable==null? null: new Animation(this, true);
		Runnable updater1 = new Runnable() {
			public void run() {
				activeEdge = newEdge;
				activeDockableId = newDockableId;
				viewPane.updateOrientation();
				viewPane.updateContents();
			}
		};
		Animation activation = newDockableId==null? null: new Animation(this, false);		
		Runnable updater2 = new Runnable() {
			public void run() {
				viewPane.setPrefSize(ViewPane.UNSPECIFIED_PREFERRED_SIZE);
				viewPane.updateOrientation();
				viewPane.updateContents();
				revalidate();
				
				// dispatch event notification
				dispatchEvent(oldDockable, newDockable);
			}
		};

		ActivationQueue queue = new ActivationQueue(this, deactivation, updater1, activation, updater2);
		queue.start();
	}
	
	public int getPreferredViewpaneSize() {
		return dockbarLayout.getDesiredViewpaneSize();
	}
	



	public boolean isAnimating() {
		return animating;
	}
	
	public void setAnimating(boolean animating) {
		this.animating = animating;
	}

	public boolean isDragging() {
		return dragging;
	}
	
	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	public ActivationListener getActivationListener() {
		return activationListener;
	}
	
	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}	
	
}
