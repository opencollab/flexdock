package org.flexdock.docking.drag;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.flexdock.docking.drag.outline.AbstractRubberBand;
import org.flexdock.docking.drag.outline.RubberBandFactory;
import org.flexdock.util.RootWindow;
import org.flexdock.util.SwingUtility;

public class DragPipeline {
	private GlassPaneMonitor paneMonitor;
	private RootWindow[] windows;
	private HashMap rootWindows;
	private DragGlasspane currentGlasspane;
	private DragGlasspane newGlassPane;
	private DragGlasspane prevGlassPane;
	
	private boolean open;
	private DragToken dragToken;
	private AbstractRubberBand rubberBand;
	
	public DragPipeline() {
		paneMonitor = new GlassPaneMonitor();
		rubberBand = RubberBandFactory.getRubberBand();
		
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void open(DragToken token) {
		if(token==null)
			throw new NullPointerException("'token' parameter cannot be null.");
		
		if(EventQueue.isDispatchThread()) {
			openImpl(token);
			return;
		}

		final DragToken dToken = token;
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						openImpl(dToken);
					}
				};
				try {
					EventQueue.invokeAndWait(r);
					// for now, just catch the errors and print the stacktrace.
					// we'll see about alternate error handling later as needed.
				} catch(InvocationTargetException e) {
					e.printStackTrace();
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	private void openImpl(DragToken token) {
		this.dragToken = token;	
		windows = RootWindow.getVisibleWindows();
		for(int i=0; i<windows.length; i++) {
			applyGlassPane(windows[i], createGlassPane());
		}

		open = true;
	}

	private DragGlasspane createGlassPane() {
		DragGlasspane pane = new DragGlasspane();
		pane.addMouseListener(paneMonitor);
		return pane;
	}
	
	private void applyGlassPane(RootWindow win, DragGlasspane pane) {
		pane.setRootWindow(win);
		pane.setCachedGlassPane(win.getGlassPane());
		win.setGlassPane(pane);
		pane.setVisible(true);
	}
		
	

	public void close() {
		if(!open)
			return;
		
		rubberBand.clear();
		for(int i=0; i<windows.length; i++) {
			Component cmp = windows[i].getGlassPane();
			if(cmp instanceof DragGlasspane) {
				DragGlasspane pane = (DragGlasspane)cmp;
				pane.setVisible(false);
				cmp = pane.getCachedGlassPane();
//				pane.dispose();
				windows[i].setGlassPane(cmp);
				windows[i] = null;
			}
		}
		open = false;
	}

	public void processDragEvent(MouseEvent me) {
		if(!open)
			return;
		
		if(EventQueue.isDispatchThread()) {
			processDragEventImpl(me);
			return;
		}
		
		final MouseEvent evt = me;
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						processDragEventImpl(evt);
					}
				};
				EventQueue.invokeLater(r);
			}
		};
		t.start();
	}
	
	private void processDragEventImpl(MouseEvent me) {

		dragToken.updateMouse(me);
		me.consume();
		
		
		if(currentGlasspane!=newGlassPane) {
			// if we're switching out to use an null glasspane, 
			// we want to clear out the current glasspane and 
			// show the global rubber band
			if(newGlassPane==null) {
				currentGlasspane.clear();
				prevGlassPane = currentGlasspane;
			}
			else {
				rubberBand.clear();
				newGlassPane.clear();
			}
			currentGlasspane = newGlassPane;
		}
		
		if(currentGlasspane==null)
			drawRubberBand();
		else
			currentGlasspane.processDragEvent(dragToken);
	}

	private void setCurrentGlassPane(DragGlasspane gp) {
		newGlassPane = gp;
	}

	private void drawRubberBand() {
		Rectangle screenRect = dragToken.getDragRect(true);
		rubberBand.paint(screenRect);
		if(prevGlassPane==null)
			return;
		
		Rectangle r = prevGlassPane.getBounds();
		Point[] vertices = SwingUtility.getPoints(screenRect, prevGlassPane);
		for(int i=0; i<vertices.length && repaintCount<3; i++) {
			if(r.contains(vertices[i])) {
				prevGlassPane.repaint();
				return;
			}
		}
		
		if(repaintCount<3) {
			prevGlassPane.repaint();
			repaintCount++;
			return;
		}
		prevGlassPane = null;
		repaintCount = 0;
	}
	
	private int repaintCount;
	
	
	private class GlassPaneMonitor extends MouseAdapter {
		public void mouseEntered(MouseEvent me) {
			Object obj = me.getSource();
			if(obj instanceof DragGlasspane) {
				setCurrentGlassPane((DragGlasspane)obj);
			}
		}
		
		public void mouseExited(MouseEvent me) {
			setCurrentGlassPane(null);
		}
	}
	
	public static void main(String[] args) {

	}

	public DragToken getDragToken() {
		return dragToken;
	}

}