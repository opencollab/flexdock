package org.flexdock.docking.drag;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.flexdock.docking.drag.outline.AbstractRubberBand;
import org.flexdock.docking.drag.outline.RubberBandFactory;
import org.flexdock.util.RootWindow;

public class DragPipeline {
	private GlassPaneMonitor paneMonitor;
	private RootWindow[] windows;
	private HashMap rootWindows;
	private DragGlasspane currentGlasspane;
	private DragGlasspane newGlassPane;
	
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

		// hide the rubber band
		rubberBand.clear();
		
		// track whether or not we're currently over a window
		dragToken.setOverWindow(newGlassPane!=null);

		// if the glasspane hasn't changed, then reprocess on the current glasspane
		if(newGlassPane==currentGlasspane) {
			dontSwitchGlassPanes();
			return;
		}
		
		// process transitions from a glasspane to a null area
		if(newGlassPane==null) {
			transitionToNullArea();
			return;
		}
		
		// process transitions from null area to a glasspane
		if(currentGlasspane==null) {
			transitionFromNullArea(newGlassPane);
			return;
		}

		// otherwise, transition from one glasspane to another
		// clear out the old glasspane
		currentGlasspane.clear();
		// reassign to the new glasspane
		currentGlasspane = newGlassPane;
		// now process the new glasspane and redraw the rubberband
		Rectangle screenRect = dragToken.getDragRect(true);
		currentGlasspane.setPostPainter(getPostPainter(screenRect));
		currentGlasspane.processDragEvent(dragToken);
	}

	private void dontSwitchGlassPanes() {
		// just redraw the rubberband if there's no current glasspane
		Rectangle screenRect = dragToken.getDragRect(true);
		if(currentGlasspane==null) {
			drawRubberBand(screenRect);
			return;
		}

		
		// otherwise, process the drag event on the current glasspane
		// and repaint it.
		// TODO: Fix post-painter on unchanged glasspane.
//		currentGlasspane.setPostPainter(getPostPainter(screenRect));
		currentGlasspane.setPostPainter(null);
		currentGlasspane.processDragEvent(dragToken);
	}
	
	private void transitionToNullArea() {
		// set the new glasspane reference
		DragGlasspane pane = currentGlasspane;
		currentGlasspane = null;
		
		// clear out the old glasspane and redraw the rubberband
		Rectangle screenRect = dragToken.getDragRect(true);
		pane.setPostPainter(null);
		pane.clear();
	}
	
	private void transitionFromNullArea(DragGlasspane newGlassPane) {
		// set the new glasspane reference
		currentGlasspane = newGlassPane;
		
		// process the new glasspane
		Rectangle screenRect = dragToken.getDragRect(true);
		currentGlasspane.setPostPainter(null);
		currentGlasspane.processDragEvent(dragToken);
	}

	
	private void setCurrentGlassPane(DragGlasspane gp) {
		newGlassPane = gp;
	}

	
	private Runnable getPostPainter(final Rectangle rect) {
//		if(!ResourceManager.isWindowsPlatform())
//			return null;
		
		return new Runnable() {
			public void run() {
				deferRubberBandDrawing(rect);
//				drawRubberBand(rect);
			}
		};
	}
	private void deferRubberBandDrawing(final Rectangle rect) {
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						drawRubberBand(rect);
					}
				};
				EventQueue.invokeLater(r);
			}
		};
		t.start();
	}
	private void drawRubberBand(Rectangle rect) {
		rubberBand.paint(rect);
	}
	
	
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