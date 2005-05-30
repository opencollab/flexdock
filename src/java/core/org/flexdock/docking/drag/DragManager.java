/*
 * Created on Mar 14, 2005
 */
package org.flexdock.docking.drag;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.DockingStrategy;
import org.flexdock.docking.drag.effects.EffectsFactory;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.event.EventDispatcher;
import org.flexdock.util.DockingUtility;

/**
 * @author Christopher Butler
 *
 */
public class DragManager extends MouseAdapter implements MouseMotionListener {
	private Dockable dockable;
	private DragPipeline pipeline;
	private boolean enabled;
	private Point dragOrigin;
	
	public static void prime() {
		// execute static initializer to preload resources
		EffectsFactory.prime();
	}
	
	public DragManager(Dockable dockable) {
		this.dockable = dockable;
	}
	
	public void mousePressed(MouseEvent e) {
		if(dockable==null || dockable.getDockingProperties().isDockingEnabled()==Boolean.FALSE) 
			enabled = false;
		else
			enabled = !isDragCanceled(dockable, e);
	}
	
	public void mouseDragged(MouseEvent evt) {
		if(!enabled)
			return;

		if(dragOrigin==null)
			dragOrigin = evt.getPoint();
		
		if(pipeline==null || !pipeline.isOpen()) {
			if(passedDragThreshold(evt))
				openPipeline(evt);
			else
				evt.consume();
		}
		else
			pipeline.processDragEvent(evt);
	}
	
	private boolean passedDragThreshold(MouseEvent evt) {
		double distance = dragOrigin.distance(evt.getPoint());
		float threshold = dockable.getDockingProperties().getDragThreshold().floatValue();
		return distance > threshold;
	}
	
	private void openPipeline(MouseEvent evt) {
		DragToken token = new DragToken(dockable.getDockable(), dragOrigin, evt);
		token.setDragListener(this);
		// initialize listeners on the drag-source
		initializeListenerCaching(token);

		DragPipeline pipeline = new DragPipeline();
		this.pipeline = pipeline;
		pipeline.open(token);
	}

	public void mouseMoved(MouseEvent e) {
		// doesn't do anything
	}

	public void mouseReleased(MouseEvent e) {
		if(pipeline==null || dockable.getDockingProperties().isDockingEnabled()==Boolean.FALSE)
			return;

		finishDrag(dockable, pipeline.getDragToken(), e);				
		if(pipeline!=null)
			pipeline.close();
		dragOrigin = null;
		pipeline = null;
	}


	protected void finishDrag(Dockable dockable, DragToken token, MouseEvent mouseEvt) {
		DockingStrategy docker = DockingManager.getDockingStrategy(dockable);
		DockingPort currentPort = DockingUtility.getParentDockingPort(dockable);
		DockingPort targetPort = token.getTargetPort();
		String region = token.getTargetRegion();

		// remove the listeners from the drag-source and all the old ones back in
		restoreCachedListeners(token);
		
		// issue a DockingEvent to allow any listeners the chance to cancel the operation.
		DockingEvent evt = new DockingEvent(dockable, currentPort, targetPort, DockingEvent.DROP_STARTED, mouseEvt);
		evt.setRegion(region);
		evt.setOverWindow(token.isOverWindow());
//		EventDispatcher.notifyDockingMonitor(dockable, evt);
		EventDispatcher.dispatch(evt, dockable);

		
		// attempt to complete the docking operation
		if(!evt.isConsumed())
			docker.dock(dockable, targetPort, region, token);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static void initializeListenerCaching(DragToken token) {
		// it's easier for us if we remove the MouseMostionListener associated with the dragSource 
		// before dragging, so normally we'll try to do that.  However, if developers really want to
		// keep them in there, then they can implement the Dockable interface for their dragSource and 
		// let mouseMotionListenersBlockedWhileDragging() return false
//		if (!dockableImpl.mouseMotionListenersBlockedWhileDragging())
//			return;

		Component dragSrc = token.getDragSource();
		EventListener[] cachedListeners = dragSrc.getListeners(MouseMotionListener.class);
		token.setCachedListeners(cachedListeners);
		DragManager dragListener = token.getDragListener();
		
		// remove all of the MouseMotionListeners
		for (int i = 0; i < cachedListeners.length; i++) {
			dragSrc.removeMouseMotionListener((MouseMotionListener) cachedListeners[i]);
		}
		// then, re-add the DragManager
		if(dragListener!=null)
			dragSrc.addMouseMotionListener(dragListener);
	}
	
	private static void restoreCachedListeners(DragToken token) {
		Component dragSrc = token.getDragSource();
		EventListener[] cachedListeners = token.getCachedListeners();
		DragManager dragListener = token.getDragListener();		

		// remove the pipeline listener
		if(dragListener!=null)
			dragSrc.removeMouseMotionListener(dragListener);
			
		// now, re-add all of the original MouseMotionListeners
		for (int i = 0; i < cachedListeners.length; i++)
			dragSrc.addMouseMotionListener((MouseMotionListener) cachedListeners[i]);
	}
	
	private static boolean isDragCanceled(Dockable dockable, MouseEvent trigger) {
		DockingPort port = DockingUtility.getParentDockingPort(dockable);
		DockingEvent evt = new DockingEvent(dockable, port, null, DockingEvent.DRAG_STARTED, trigger);
		EventDispatcher.dispatch(evt, dockable);
		return evt.isConsumed();
	}
}
