package org.flexdock.docking.drag;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.drag.effects.DragPreview;
import org.flexdock.docking.drag.effects.EffectsFactory;
import org.flexdock.util.ComponentNest;
import org.flexdock.util.RootWindow;

public class DragGlasspane extends JComponent {
	private ComponentNest currentDropTargets;
	private Component cachedGlassPane;
	private RootWindow rootWindow;
	private Runnable postPainter;
	private DragPreview previewDelegate;
	private boolean previewInit;
	private Polygon previewPoly;
	
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
	
	
	
	private ComponentNest getDropTargets(DragToken token) {
		Container c = rootWindow.getContentPane();
		Point currMouse = token.getCurrentMouse(c);
		Component deep = SwingUtilities.getDeepestComponentAt(c, currMouse.x, currMouse.y);
		return ComponentNest.find(deep, Dockable.class, DockingPort.class);
	}
	
	
	
	
	
	
	
	
	public void processDragEvent(DragToken token) {
		ComponentNest dropTargets = getDropTargets(token);
		
		// if there is no cover, and we're not transitioning away from one, 
		// then invoke postPaint() and return
		if(currentDropTargets==null && dropTargets==null) {
			deferPostPaint();
			return;
		}

		String region = null;
		
		// don't immediately redraw the rubberband when switching covers 
		// or regions
		setPostPainter(null);

		// now, assign the currentCover to the new one and repaint
		currentDropTargets = dropTargets;
		DockingPort port = dropTargets==null? null: (DockingPort)dropTargets.parent;
		// this is the dockable we're currently hovered over, not the one
		// being dragged
		Dockable hover = getHoverDockable(dropTargets);
		
		Point mousePoint = token.getCurrentMouse((Component)port);
		region = port==null? DockingPort.UNKNOWN_REGION: port.getRegion(mousePoint);
		// set the target dockable
		token.setTarget(port, region);
		
		// create the preview-polygon
		createPreviewPolygon(token, port, hover,  region);
		
		// repaint
		repaint();
	}
	
	private Dockable getHoverDockable(ComponentNest nest) {
		Component c = nest==null? null: nest.child;
		if(c instanceof Dockable)
			return (Dockable)c;
		return DockingManager.getRegisteredDockable(c);
	}
	
	protected void createPreviewPolygon(DragToken token, DockingPort port, Dockable hover, String region) {
		DragPreview preview = getPreviewDelegate(token.getDockable(), port);
		if(preview==null)
			previewPoly = null;
		else
			previewPoly = preview.createPreviewPolygon(token.getDockable(), port, hover, region, this);
	}
	
	public void clear() {
		if(currentDropTargets!=null) {
			currentDropTargets = null;
		}
		repaint();
	}
	
	public void paint(Graphics g) {
		paintComponent(g);
		postPaint(g);
	}
	
	protected void postPaint(Graphics g) {
		if(postPainter!=null)
			postPainter.run();
		postPainter = null;
	}
	
	protected DragPreview getPreviewDelegate(Component dockable, DockingPort port) {
		if(!previewInit) {
			Dockable d = DockingManager.getRegisteredDockable(dockable);
			previewDelegate = EffectsFactory.getPreview(d, port);
			previewInit = true;
		}
		return previewDelegate;
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
	

	
	protected void paintComponent(Graphics g) {
		if(previewDelegate!=null && previewPoly!=null) 
			previewDelegate.drawPreview((Graphics2D)g, previewPoly);
	}
	
	private boolean match(Object o1, Object o2) {
		if(o1==o2)
			return true;
		return o1==null? false: o1.equals(o2);
	}
}
