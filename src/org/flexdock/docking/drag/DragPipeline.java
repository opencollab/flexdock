package org.flexdock.docking.drag;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import org.flexdock.util.RootWindow;

public class DragPipeline {
	private GlassPaneMonitor paneMonitor;
	private RootWindow[] windows;
	private HashMap rootWindows;
	private DragGlasspane currentGlasspane;
	
	private boolean open;
	private DragToken dragToken;
	
	public DragPipeline() {
		paneMonitor = new GlassPaneMonitor();
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void open(DragToken token) {
		if(token==null)
			throw new NullPointerException("'token' parameter cannot be null.");
		
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
		
	
		dragToken.updateMouse(me);
		me.consume();
		
		if(currentGlasspane!=null)
			currentGlasspane.processDragEvent(dragToken);
		else
			System.out.println(currentGlasspane);
	}

	private synchronized void setCurrentGlassPane(DragGlasspane gp) {
		if(currentGlasspane!=null)
			currentGlasspane.clear();
		currentGlasspane = gp;
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