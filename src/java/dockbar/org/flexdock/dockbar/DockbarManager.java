/*
 * Created on Apr 14, 2005
 */
package org.flexdock.dockbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Vector;
import java.util.WeakHashMap;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.flexdock.dockbar.event.DockbarEvent;
import org.flexdock.dockbar.event.DockbarListener;
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

	private JPanel dockbarPanel;
	private Dockbar leftBar;
	private Dockbar rightBar;
	private Dockbar bottomBar;
	private DockbarPane dockedPane;
	private WeakReference windowRef;
	private int activeEdge = UNSPECIFIED_EDGE;
	private String activeDockable;
	
	private ResizeListener resizeListener;
	private Vector dockbarListeners;
	

	
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
		return mgr;		
	}
	
	
	
	private DockbarManager(RootWindow window) {
		resizeListener = new ResizeListener(this);
		dockbarListeners = new Vector();
		
		leftBar = new Dockbar(this, LEFT);
		rightBar = new Dockbar(this, RIGHT);
		bottomBar = new Dockbar(this, BOTTOM);
		dockedPane = new DockbarPane(this);
		windowRef = new WeakReference(window);
		
		dockbarPanel = new JPanel(new DockbarPanelLayout());
		dockbarPanel.setOpaque(false);

		dockbarPanel.add(dockedPane, BorderLayout.CENTER);
		dockbarPanel.add(leftBar, BorderLayout.WEST);
		dockbarPanel.add(rightBar, BorderLayout.EAST);
		dockbarPanel.add(bottomBar, BorderLayout.SOUTH);
	}
	
	public RootWindow getWindow() {
		return (RootWindow)windowRef.get();
	}
	
	
	public ResizeListener getResizeListener() {
		return resizeListener;
	}

	
	private void install() {
		RootWindow window = getWindow();
		if(window==null)
			return;
		
		JLayeredPane layerPane = window.getLayeredPane();
		if(dockbarPanel.getParent()==layerPane)
			return;

		if(dockbarPanel.getParent()!=null)
			dockbarPanel.getParent().remove(dockbarPanel);
		dockbarPanel.setBackground(Color.red);
		
		layerPane.add(dockbarPanel, DOCKBAR_LAYER);
		layerPane.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				if(evt.getSource() instanceof JLayeredPane)
					revalidateDockbarPane();
			}
		});
		
		revalidateDockbarPane();
	}
	
	private void revalidateDockbarPane() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				RootWindow window = getWindow();
				if(window==null)
					return;
				
				Container contentPane = window.getContentPane();
				JLayeredPane layeredPane = window.getLayeredPane();

				// no rectangle translation required because layeredPane is already the direct
				// parent of contentPane.

				dockbarPanel.setBounds(contentPane.getBounds());
				dockbarPanel.validate();
				validate();
			}
		});

	}
	
	public void validate() {
		toggleDockbars();
		updateInsets();
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
		revalidateDockbarPane();
	}
	
	
	public void undock(Dockable dockable) {
		Dockbar dockbar = getDockbar(dockable);
		if(dockbar!=null) {
			dockbar.undock(dockable);
			revalidateDockbarPane();
		}
	}
	
	
	public int getActiveEdge() {
		return activeEdge;
	}
	
	void setActiveEdge(int edge) {
		activeEdge = Dockbar.getValidOrientation(edge);
		dockedPane.setOrientation(activeEdge);
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

	public String getActiveDockable() {
		return activeDockable;
	}
	
	public void setActiveDockable(String dockableId) {
		Dockable oldDockable = dockedPane.getDockable();
		Dockable newDockable = DockingManager.getRegisteredDockable(dockableId);
		dockableId = newDockable==null? null: newDockable.getPersistentId();
		
		boolean changed = Utilities.isChanged(activeDockable, dockableId);
		activeDockable = dockableId;
		
		if(changed) {
			dockedPane.setDockable(dockableId);
			Dockable d = dockedPane.getDockable(); 
			dockedPane.setExpanded(d!=null);
			
			int evtType = DockbarEvent.ACTIVATED;
			if(d==null && oldDockable!=null) {
				d = oldDockable;
				evtType = DockbarEvent.DEACTIVATED;
			}
			
			if(d!=null) {
				DockbarEvent evt = new DockbarEvent(d, evtType, getActiveEdge());
				dispatchEvent(evt);				
			}
		}
	}
	
	
	public DockbarPane getDockbarPane() {
		return dockedPane;
	}
	
	
	public void addListener(DockbarListener listener) {
		if(listener!=null)
			dockbarListeners.add(listener);
	}
	
	public boolean removeListener(DockbarListener listener) {
		return dockbarListeners.remove(listener);
	}

	private void dispatchEvent(DockbarEvent evt) {
		for(Iterator it=dockbarListeners.iterator(); it.hasNext();) {
			DockbarListener listener = (DockbarListener)it.next();
			dispatchEvent(evt, listener);
		}
	}
	
	private void dispatchEvent(DockbarEvent evt, DockbarListener listener) {
		switch(evt.getType()) {
			case DockbarEvent.ACTIVATED:
				listener.dockableActivated(evt);
				break;
			case DockbarEvent.DEACTIVATED:
				listener.dockableDeactivated(evt);
				break;
		}
	}
	
	private class DockbarPanelLayout extends BorderLayout {
		public DockbarPanelLayout() {
			super(0, 0);
		}
		public void layoutContainer(Container target) {
			super.layoutContainer(target);
			
			if(getActiveDockable()==null)
				return;
			
			// overlay the expanded dockable in the CENTER so that the slideout-view
			// sits overtop the other dockbars all the way to the edge of the frame
			Rectangle rect = new Rectangle(0, 0, dockbarPanel.getWidth(), dockbarPanel.getHeight());
			if(activeEdge==TOP || activeEdge==BOTTOM) {
				rect.height -= bottomBar.getHeight();
			}
			// else assume LEFT or RIGHT
			else {
				int offsetX = leftBar.getWidth();
				rect.x = offsetX;
				rect.width -= (offsetX + rightBar.getWidth());
			}
			dockedPane.setBounds(rect);
			
			// if we're expanded from the LEFT side, overlay the left dockbar
			// on top of the left edge of the bottom dockbar since the slideout-view
			// won't be covering that part
			if(activeEdge==LEFT) {
				leftBar.setSize(leftBar.getWidth(), leftBar.getHeight() + bottomBar.getHeight());
			}
		}
	}
}
