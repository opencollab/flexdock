package org.flexdock.docking.drag;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.flexdock.docking.DockingPort;
import org.flexdock.util.RootWindow;
import org.flexdock.util.SwingUtility;

public class DragGlasspane extends JComponent {
	private DockingPortCover currentCover;
	private Component cachedGlassPane;
	private RootWindow rootWindow;
	
	public DragGlasspane() {
		setLayout(null);
	}

	public Component getCachedGlassPane() {
		return cachedGlassPane;
	}

	public RootWindow getRootWindow() {
		return rootWindow;
	}

	public void setCachedGlassPane(Component cachedGlassPane) {
		this.cachedGlassPane = cachedGlassPane;
	}

	public void setRootWindow(RootWindow rootWindow) {
		this.rootWindow = rootWindow;
	}
	
	DockingPortCover getCurrentDockingPortCover(DragToken token) {
		Point mouse = token.getCurrentMouse(this);
		Component cmp = SwingUtilities.getDeepestComponentAt(this, mouse.x, mouse.y);
		if(cmp instanceof DockingPortCover)
			return (DockingPortCover)cmp;
		
		return createDockingPortCover(token);
	}
	
	private DockingPortCover createDockingPortCover(DragToken token) {
		Component cmp = rootWindow.getContentPane();
		Point loc = token.getCurrentMouse(true);
		SwingUtilities.convertPointFromScreen(loc, cmp);
		Component deepest = SwingUtilities.getDeepestComponentAt(cmp, loc.x, loc.y);
		
		DockingPort port = deepest instanceof DockingPort?
			 (DockingPort)deepest:
			 (DockingPort)SwingUtilities.getAncestorOfClass(DockingPort.class, deepest);
		if(port==null)
			return null;

		return createDockingPortCoverStack(port);
	}
	
	private DockingPortCover createDockingPortCoverStack(DockingPort port) {
		ArrayList dockingPorts = obtainDockingPortAncestors(port);
		DockingPortCover cover = null;
		for(Iterator it=dockingPorts.iterator(); it.hasNext();) {
			port = (DockingPort)it.next();
			cover = addCoverPanel(port);
		}
		revalidate();
		return cover;
	}
	
	private DockingPortCover addCoverPanel(DockingPort port) {
		Rectangle rect = ((Container)port).getBounds();
		Point rectLoc = ((Container)port).getLocationOnScreen();
		SwingUtilities.convertPointFromScreen(rectLoc, this);
		rect.setLocation(rectLoc);
		
		DockingPortCover coverPanel = new DockingPortCover(port);
		add(coverPanel);
		coverPanel.setBounds(rect);
		coverPanel.setVisible(true);
		coverPanel.doLayout();
		return coverPanel;
	}

	private ArrayList obtainDockingPortAncestors(DockingPort deepest) {
		ArrayList portList = new ArrayList();
		HashSet set = new HashSet();

		// look for a parent port
		for(DockingPort parent = deepest; parent!=null;) {
			// check to see if this parent port has child ports.  if so, they belong deeper in the
			// stack than the parent
			DockingPort[] children = SwingUtility.getChildPorts(parent);
			for(int i=0; i<children.length; i++) {
				cachePort(children[i], portList, set);
			}
			// now that the child ports have been added, we can add the parent port to the stack
			cachePort(parent, portList, set);
			// look up the parent port's DockingPort ancestor, rinse, and repeat
			parent = (DockingPort)SwingUtilities.getAncestorOfClass(DockingPort.class, (Container)parent);
		}
		set.clear();
		return portList;
	}
	
	private static void cachePort(DockingPort port, List portList, Set cache) {
		if(!cache.contains(port)) {
			portList.add(port);
			cache.add(port);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public void processDragEvent(DragToken token) {
		DockingPortCover newCover = getCurrentDockingPortCover(token);
		
		// if there is no cover, and we're not transitioning away from one, 
		// then invoke postPaint() and return
		if(currentCover==null && newCover==null) {
			deferPostPaint();
			return;
		}

		String region = null;
		if(newCover!=null) {
			region = newCover.getDragTokenRegion(token);
			// if the cover hasn't changed, and the region on the cover hasn't changed, 
			// then there is no point in repainting.  Invoke the postPaint() and return.
			if(currentCover==newCover && match(currentCover.getCurrentRegion(), region)) {
				deferPostPaint();
				return;
			}
			// we either have different covers, or the current region on the current 
			// cover has changed.  either way, we now know we'll have to repaint() on the
			// new cover, so it's safe to update its region now.
			newCover.setCurrentRegion(region);
		}

		// if we're changing covers, make sure there is no region set
		// on the current one
		if(currentCover!=newCover && currentCover!=null)
			currentCover.setCurrentRegion(null);

		// now, assign the currentCover to the new one and repaint
		currentCover = newCover;
		DockingPort port = currentCover==null? null: currentCover.getPort();
		token.setTarget(port, region);
		
		repaint();
	}
	
	public void clear() {
		if(currentCover!=null) {
			currentCover.setCurrentRegion(null);
			currentCover = null;
		}
		repaint();
	}
	
	public void paint(Graphics g) {
		paintComponent(g);
		paintChildren(g);
		postPaint(g);
	}
	
	protected void postPaint(Graphics g) {
		if(postPainter!=null)
			postPainter.run();
		postPainter = null;
	}
	
	
	
	private void deferPostPaint() {
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						postPaint(getGraphics());
					}
				};
				EventQueue.invokeLater(r);
			}
		};
		t.start();
	}
	
	
	void setPostPainter(Runnable painter) {
		postPainter = painter;
	}
	
	private Runnable postPainter;
	
	protected void paintComponent(Graphics g) {
		
		// now we're free to paint
		super.paintComponent(g);
//		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	private boolean match(String s1, String s2) {
		if(s1==s2)
			return true;
		return s1==null? false: s1.equals(s2);
	}
}
