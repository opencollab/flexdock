/* Copyright (c) 2004 Christopher M Butler

Permission is hereby granted, free of charge, to any person obtaining a copy of 
this software and associated documentation files (the "Software"), to deal in the 
Software without restriction, including without limitation the rights to use, 
copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the 
Software, and to permit persons to whom the Software is furnished to do so, subject 
to the following conditions:

The above copyright notice and this permission notice shall be included in all 
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. */
package org.flexdock.docking;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;
import java.util.WeakHashMap;

import javax.swing.SwingUtilities;

import org.flexdock.docking.config.ConfigurationManager;
import org.flexdock.docking.drag.DragPipeline;
import org.flexdock.docking.drag.DragToken;
import org.flexdock.util.RootWindow;
import org.flexdock.util.SwingUtility;


/**
 * This class is used to manage drag operations for <code>Dockable</code> components.  Application 
 * code should interact with this class through <code>static</code> utility methods.
 * <p>
 * Any component that wishes to have docking capabilities enabled should call 
 * <code>registerDockable(Component evtSrc, String desc, boolean allowResize)</code>.  
 * <code>registerDockable(Component evtSrc, String desc, boolean allowResize)</code> will create a 
 * <code>Dockable</code> instance that this class can work with during drag operations.  Likewise, when
 * dealing strictly with bare <code>Components</code>, most methods have been overloaded with a 
 * <code>Component</code> version and a <code>Dockable</code> version.  <code>Component</code>
 * versions always create (or pull from a cache) a corresponding <code>Dockable</code> instance and 
 * dispatch to the overloaded <code>Dockable</code> version of the method.
 * <p>
 * <code>registerDockable(Component evtSrc, String desc, boolean allowResize)</code> adds required
 * <code>MouseMotionListeners</code> to the source <code>Component</code>, which automatically handle 
 * method dispatching to <code>startDrag()</code>, so explicitly initiating a drag in this manner, while
 * not prohibited, is typically not required.
 * <p>
 * During drag operations, an outline of the <code>Dockable.getDockable()</code> is displayed on the 
 * GlassPane and moves with the mouse cursor.  The <code>DockingManager</code> monitors the docking region 
 * underneath the mouse cursor for underlying <code>DockingPorts</code> and the mouse cursor icon will 
 * reflect this appropriately.  The image displayed by for the mouse cursor may be altered by returning a 
 * custom <code>CursorProvider</code> for the currentl <code>Dockable</code>. 
 * When the mouse has been released, a call to <code>stopDrag()</code> is 
 * issued.  If the current docking region allows docking, then the <code>DockingManager</code> removes 
 * the drag source from its original parent and docks it into its new <code>DockingPort</code>, subsequently
 * issuing callbacks to <code>DockingPort.dockingComplete(String region)</code> and then 
 * <code>Dockable.dockingCompleted()</code>.  If docking is not allowed, then no docking operation is 
 * performed and a callback is issued to <code>Dockable.dockingCanceled()</code>.
 * <p>
 * Whenever a <code>Dockable</code> is removed from a <code>DockingPort</code>, the <code>DockingManager</code>
 * takes care of making the requisite call to <code>DockingPort.undock()</code>.
 * 
 * @author Chris Butler
 */
public class DockingManager {
	private static final DockingManager SINGLETON = new DockingManager();
	private static final WeakHashMap CACHED_DRAG_INITIATORS_BY_COMPONENT = new WeakHashMap();


	private DockingManager() {
	}
	
	
	private static DockingManager getDockingManager() {
		return SINGLETON;
	}


	/**
	 * Dispatches to <code>startDrag(Component c, Point mousePosition, String tabTitle)</code>, passing
	 * in <code>null</code> for the <code>tabTitle</code>.
	 *
	 * @param c the drag source
	 * @param mousePosition the location of the mouse over the drag source
	 */
	public static void startDrag(Component c, Point mousePosition) {
		startDrag(c, mousePosition, null);
	}

	/**
	 * Dispatches to 
	 * <code>startDrag(Component c, Point mousePosition, String tabTitle, boolean allowResize)</code>, passing
	 * in <code>false</code> for <code>allowResize</code>.
	 *
	 * @param c the drag source
	 * @param mousePosition the location of the mouse over the drag source
	 * @param tabTitle the docking description to be used as a tab title if docked in a tabbed pane
	 */
	public static void startDrag(Component c, Point mousePosition, String tabTitle) {
		startDrag(c, mousePosition, tabTitle, false);
	}

	/**
	 * Creates a <code>Dockable</code> instance and dispatches to 
	 * <code>startDrag(Dockable initiator, Point mousePoint)</code>.  If <code>mousePosition</code> is
	 * null, or a <code>Dockable</code> instance cannot be created, no exception is thrown and no action
	 * is taken.  <code>tabTitle</code> and <code>allowResize</code> will supercede any cached values set
	 * by <code>setDockingDescription()</code> and <code>setDockingResizablePolicy()</code>.
	 *
	 * @param c the drag source
	 * @param mousePosition the location of the mouse over the drag source
	 * @param tabTitle the docking description to be used as a tab title if docked in a tabbed pane
	 * @param allowResize the resize policy that determine whether a split-layout docking will be fixed or not
	 */
	public static void startDrag(Component c, Point mousePosition, String tabTitle, boolean allowResize) {
		if (mousePosition == null)
			return;

		Dockable dockable = getDockableForComponent(c, tabTitle, allowResize);
		if (dockable != null)
			startDrag(dockable, mousePosition);
	}

	/**
	 * Begins processing of drag operations against the specified <code>Dockable</code> instance.  Normally, 
	 * this method is called automatically by MouseMotionListeners attached to Components via 
	 * <code>registerDockable()</code> and not explicitly by application code.  However, application code
	 * has the option to call this method if so desired.  This method checks for null cases against 
	 * <code>dockable</code>, <code>dockable.getInitiator()</code>, and <code>dockable.getDockable()</code>.
	 * If any of these checks fail, no exception is thrown and no action is taken.  <code>mousePoint</code> is
	 * relative to <code>dockable.getInitiator()</code>.  In the case of dispatching from one of the other 
	 * overloaded <code>startDrag()</code> methods, <code>dockable.getInitiator()</code> and 
	 * <code>dockable.getDockable()</code> refer to the same object, and so <code>mousePoint</code> is 
	 * relative to both.  The current GlassPane will be swapped out, a component outline will be drawn relative
	 * to the current mouse position, and the mosue cursor will change according to the available docking
	 * region beneath the mouse.  This state will persist until <code>stopDrag()</code> is called, in which 
	 * case the original GlassPane will be reinstated.
	 *
	 * @param dockable the dockable instance we intend to drag
	 * @param mousePoint the location of the mouse relative to <code>dockable.getInitiator()</code>
	 */
	public static void startDrag(Dockable dockable, Point mousePoint) {
		if (dockable == null)
			return;

		Component c = dockable.getInitiator();
		if (c == null || dockable.getDockable()==null)
			return;
/*
		DockingManager mgr = getDockingManager();
		if (mgr != null)
			mgr.startDragImpl(dockable, mousePoint);
*/			
	}
	
	private static void startDrag(Dockable dockable, MouseEvent me, PipelineManager pipelineMgr) {
		if (dockable == null)
			return;

		Component c = dockable.getInitiator();
		if (c == null || dockable.getDockable()==null)
			return;

		DockingManager mgr = getDockingManager();
		if (mgr != null)
			mgr.startDragImpl(dockable, me, pipelineMgr);
	}

	/**
	 * Creates a <code>Dockable</code> instance and dispatches to <code>stopDrag(Dockable dockable)</code>.
	 *
	 * @param c the currently dragged component
	 */
	public static void stopDrag(Component c) {
		Dockable init = DockableComponentWrapper.create(c, null, null, false);
		if (init != null)
			stopDrag(init);
	}

	/**
	 * Ends the drag operation against the specified <code>Dockable</code> instance.  If no drag operation is
	 * in progress or the specified <code>Dockable</code> instance isn't the one in a drag-state, then no
	 * action is taken.  When called, the <code>DockingManager</code> will attempt to drop the currently 
	 * dragged <code>Dockable</code> instance into the region underneath the mouse cursor.  This method is 
	 * normally called internally by the <code>DockingManager</code> itself when the mouse button has been 
	 * released.  However, it has been made public to allow for the option os explicit invocation from 
	 * application code.  
	 *
	 * @param dockable the currently dragged <code>Dockable</code>
	 */
	public static void stopDrag(Dockable dockable) {
		if (dockable == null)
			return;

//		DockingManager mgr = getDockingManager();
//		if (mgr != null)
//			mgr.stopDragImpl(dockable);
	}
	
	private static void stopDrag(Dockable dockable, DragToken token) {
		if (dockable == null)
			return;

		DockingManager mgr = getDockingManager();
		if (mgr != null)
			mgr.stopDragImpl(dockable, token);
	}
	
	
	/**
	 * Undocks the specified <code>Dockable</code> instance from its containing <code>DockingPort</code>.  This 
	 * method locates the containing <code>DockingPort</code> for the specified <code>Dockable</code>.  If no 
	 * parent container is found at all, no exception is thrown and no action is taken.  If a containing 
	 * <code>DockingPort</code> is found, then <code>undock()</code> is called against the <code>DockingPort</code>
	 * instance to allow the <code>DockingPort</code> to handle its own cleanup operations.  If no containing 
	 * <code>DockingPort</code> is located, but a parent <code>Container</code> is found, then <code>remove()</code>
	 * is called against the parent <code>Container</code>.  
	 *
	 * @param dockable the <code>Dockable</code> we wish to undock
	 */
	public static void undock(Dockable dockable) {
		if(dockable==null)
			return;
			
		DockingManager mgr = getDockingManager();
		if (mgr != null)
			mgr.undockImpl(dockable);
	}
	
	private void undockImpl(Dockable dockable) {
		Component dragSrc = dockable.getDockable();
		Container parent = dragSrc.getParent();
		RootWindow rootWin = RootWindow.getRootContainer(parent);
		
		// if there's no parent container, then we really don't have anything from which to to 
		// undock this component, now do we?
		if(parent==null)
			return;
		
		DockingPort dockingPort = getParentDockingPort(dragSrc);
		if(dockingPort!=null)
			// if 'dragSrc' is currently docked, then undock it instead of using a 
			// simple remove().  this will allow the DockingPort to do any of its own 
			// cleanup operations associated with component removal.
			dockingPort.undock(dragSrc);
		else
			// otherwise, just remove the component
			parent.remove(dragSrc);
		
		SwingUtility.revalidateComponent(rootWin.getContentPane());
	}



	/**
	 * Creates a Dockable for the specified component and dispatches to 
	 * <code>registerDockable(Dockable init)</code>. If evtSrc is null, no exception is 
	 * thrown and no action is performed.
	 *
	 * @param evtSrc   the target component for the Dockable, both drag-starter and docking source
	 * @param desc     the description of the docking source.  Used as the tab-title of docked in a tabbed pane
	 * @param allowResize  specifies whether or not a resultant split-view docking would be fixed or resizable  
	 */
	public static void registerDockable(Component evtSrc, String desc, boolean allowResize) {
		if (evtSrc == null)
			return;

		Dockable dockable = getDockableForComponent(evtSrc, desc, allowResize);
		registerDockable(dockable);
	}

	/**
	 * Initializes the specified Dockable.  Adds a MouseMotionListener to 
	 * <code>init.getInitiator()</code> to detect drag events and call <code>startDrag()</code> when 
	 * detected.  Caches <code>init</code> in a <code>WeakHashMap</code> by <code>init.getDockable()</code>
	 * for subsequent internal lookups. If the MouseMotionListener is already registerd with the 
	 * initiator component, it will not be added again.  <code>init</code> may not be null and both 
	 * <code>init.getDockable()</code> and , <code>init.getInitiator()</code> may not return null.  
	 * If any of these checks fail, no exception is thrown and no action is performed.
	 *
	 * @param init the Dockable that is being initialized.
	 */
	public static void registerDockable(Dockable dockable) {
		if (dockable == null || dockable.getDockable() == null || dockable.getInitiator()==null)
			return;

		PipelineManager pipelineMgr = getPipelineManager(dockable);
		if (pipelineMgr == null) {
			pipelineMgr = new PipelineManager(dockable);
			dockable.getInitiator().addMouseMotionListener(pipelineMgr);
			dockable.getInitiator().addMouseListener(pipelineMgr);
		}
		CACHED_DRAG_INITIATORS_BY_COMPONENT.put(dockable.getDockable(), dockable);
		
		// allow the configuration manager to keep track of this dockable.  This 
		// will allow docking configurations to survive JVM instances.
		ConfigurationManager.registerDockable(dockable);
	}

	public static Dockable getRegisteredDockable(Component comp) {
		return (Dockable)CACHED_DRAG_INITIATORS_BY_COMPONENT.get(comp);
	}

	private static Dockable getDragInitiator(Component c) {
		return getDockableForComponent(c, null, false);
	}

	private static Dockable getDockableForComponent(Component c, String desc, boolean allowResize) {
		if (c == null)
			return null;

		Dockable initiator = getRegisteredDockable(c);
		if (initiator == null) {
			String persistentId = generatePersistentId(c);
			initiator = DockableComponentWrapper.create(c, persistentId, desc, allowResize);
			CACHED_DRAG_INITIATORS_BY_COMPONENT.put(c, initiator);
		}
		return initiator;
	}

	private static PipelineManager getPipelineManager(Dockable dockable) {
		EventListener[] listeners = dockable.getInitiator().getListeners(MouseMotionListener.class);

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] instanceof PipelineManager)
				return (PipelineManager) listeners[i];
		}
		return null;
	}


	/**
	 * Sets the resizing policy for the specified component.  Te resizing policy is used determine whether, 
	 * after docking in a split layout, the layout itself will be fixed or resizable.  A Dockable is looked 
	 * up from an internal cache using the specified component as a key.  If not found, a dockabe is created 
	 * and cached by the same key.  This method dispatches to 
	 * <code>setDockingResizablePolicy(Dockable dockable, boolean resizable)</code>.
	 *
	 * @param c the Component that is the drag-source for a given Dockable instance
	 * @param resizable the requested resizing policy for splip-layout docking
	 */
	public static void setDockingResizablePolicy(Component c, boolean resizable) {
		Dockable init = getDragInitiator(c);
		setDockingResizablePolicy(init, resizable);
	}

	/**
	 * Sets the resizing policy for the specified Dockable.  Te resizing policy is used determine whether, 
	 * after docking in a split layout, the layout itself will be fixed or resizable.  If the Dockable
	 * instance is not null, then <code>setDockedLayoutResizable(resizable)</code> is called against it.
	 *
	 * @param dockable the Dockable instance whose resizing policy is being set
	 * @param resizable the requested resizing policy for splip-layout docking
	 */
	public static void setDockingResizablePolicy(Dockable dockable, boolean resizable) {
		if (dockable != null)
		dockable.setDockedLayoutResizable(resizable);
	}

	/**
	 * Sets the docking description for the specified component.  Te docking description is used as the
	 * tab-title when docking within a tabbed pane.  A Dockable is looked up from an internal cache using
	 * the specified component as a key.  If not found, a dockabe is created an cached by the same key.
	 * The Dockable's description is set to <code>desc</code>.   
	 *
	 * @param c the Component that is the drag-source for a given Dockable instance
	 * @param desc the dockable description for the specified component
	 */
	public static void setDockingDescription(Component c, String desc) {
		Dockable init = getDragInitiator(c);
		setDockingDescription(init, desc);
	}

	/**
	 * Sets the docking description for the Dockable instance.  The docking description is used as the
	 * tab-title when docking within a tabbed pane. 
	 *
	 * @param dockable the Dockable instance we're describing
	 * @param desc the description of the Dockable instance.  used as a tab-title.
	 */
	public static void setDockingDescription(Dockable dockable, String desc) {
		if (dockable != null)
			dockable.setDockableDesc(desc);
	}

	private static String generatePersistentId(Object obj) {
		String pId = obj.getClass().getName();
		String baseId = pId;
		for(int i=1; ConfigurationManager.hasRegisteredDockableId(pId); i++) 
			pId = baseId + "_" + i;
		
		return pId;
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
		MouseMotionListener pipelineListener = token.getPipelineListener();
		
		// remove all of the MouseMotionListeners
		for (int i = 0; i < cachedListeners.length; i++) {
			dragSrc.removeMouseMotionListener((MouseMotionListener) cachedListeners[i]);
		}
		// then, re-add the PipelineManager
		if(pipelineListener!=null)
			dragSrc.addMouseMotionListener(pipelineListener);
	}

	private static void restoreCachedListeners(DragToken token) {
		Component dragSrc = token.getDragSource();
		EventListener[] cachedListeners = token.getCachedListeners();
		MouseMotionListener pipelineListener = token.getPipelineListener();		

		// remove the pipeline listener
		if(pipelineListener!=null)
			dragSrc.removeMouseMotionListener(pipelineListener);
			
		// now, re-add all of the original MouseMotionListeners
		for (int i = 0; i < cachedListeners.length; i++)
			dragSrc.addMouseMotionListener((MouseMotionListener) cachedListeners[i]);
	}

	private void startDragImpl(Dockable dockable, MouseEvent me, PipelineManager mgr) {
		DragToken token = new DragToken(dockable.getDockable(), me);
		token.setPipelineListener(mgr);
		// initialize listeners on the drag-source
		initializeListenerCaching(token);

		DragPipeline pipeline = new DragPipeline();
		mgr.pipeline = pipeline;
		pipeline.open(token);
	}

	private void stopDragImpl(Dockable dockable, DragToken token) {
		if (dockable == null || dockable.getDockable()==null)
			return;

		// perform the drop operation.
		boolean docked = dropComponent(dockable, token);

		// remove the listeners from the drag-source and all the old ones back in
		restoreCachedListeners(token);

		// perform post-drag operations
		if (docked) {
			DockingPort port = token.getTargetPort();
			port.dockingComplete(token.getTargetRegion());
			dockable.dockingCompleted();
		}
		else
			dockable.dockingCanceled();
	}

	private boolean dropComponent(Dockable dockable, DragToken token) {
		String region = token.getTargetRegion();
		if (DockingPort.UNKNOWN_REGION.equals(region))
			return false;

		if (token.getTargetPort()==null)
			return false;
			
		DockingPort target = token.getTargetPort();
		Component docked = target.getDockedComponent();
		Component dockableCmp = dockable.getDockable();
		if (dockableCmp == docked)
			return false;

		// obtain a reference to the content pane that holds the target DockingPort.
		// also, determine the mouse location relative to the content pane.
		// we must do this before undocking, since these values may change afterward.
		RootWindow rootWin = RootWindow.getRootContainer((Component)target);
		Container contentPane = rootWin.getContentPane();
		Point mouseOnContentPane = token.getCurrentMouse(contentPane);

		// undock the current Dockable instance from it's current parent container
		undockImpl(dockable);

		// when the original parent reevaluates its container tree after undocking, it checks to see how 
		// many immediate child components it has.  split layouts and tabbed interfaces may be managed by 
		// intermediate wrapper components.  When undock() is called, the docking port 
		// may decide that some of its intermedite wrapper components are no longer needed, and it may get 
		// rid of them. this isn't a hard rule, but it's possible for any given DockingPort implementation.  
		// In this case, the target we had resolved earlier may have been removed from the component tree 
		// and may no longer be valid.  to be safe, we'll resolve the target docking port again and see if 
		// it has changed.  if so, we'll adopt the resolved port as our new target.
		DockingPort resolvedTarget = resolveDockingPortComponent(mouseOnContentPane, contentPane);
		if (resolvedTarget != target) {
			target = resolvedTarget;
			// reset this field for reuse outside this method
			token.setTarget(target, region);
		}

		boolean ret = target.dock(dockableCmp, dockable.getDockableDesc(), region, dockable.isDockedLayoutResizable());
		SwingUtility.revalidateComponent((Component) target);
		return ret;
	}
	
	private DockingPort resolveDockingPortComponent(Point mouse, Container contentPane) {
		Component deepestComponent = SwingUtilities.getDeepestComponentAt(contentPane, mouse.x, mouse.y);
		if (deepestComponent == null)
			return null;
	
		// we're assured here the the deepest component is both a Component and DockingPort in
		// this case, so we're okay to return here.
		if (deepestComponent instanceof DockingPort)
			return (DockingPort) deepestComponent;
	
		// getAncestorOfClass() will either return a null or a Container that is also an instance of
		// DockingPort.  Since Container is a subclass of Component, we're fine in returning both
		// cases.
		return (DockingPort) SwingUtilities.getAncestorOfClass(DockingPort.class, deepestComponent);
	}

	
	private DockingPort getParentDockingPort(Component comp) {
		DockingPort port = (DockingPort)SwingUtilities.getAncestorOfClass(DockingPort.class, comp);
		if(port==null)
			return null;
			
		return port.hasDockedChild(comp)? port: null;
	}

	
	private static class PipelineManager extends MouseAdapter implements MouseMotionListener {
		private Dockable dockable;
		private DragPipeline pipeline;
		
		private PipelineManager(Dockable dockable) {
			this.dockable = dockable;
		}
		
		public void mouseDragged(MouseEvent e) {
			if(dockable==null || !dockable.isDockingEnabled())
				return;
				
			if(pipeline==null || !pipeline.isOpen()) {
				DragToken token = new DragToken(dockable.getDockable(), e);
				token.setPipelineListener(this);
				// initialize listeners on the drag-source
				initializeListenerCaching(token);
		
				DragPipeline pipeline = new DragPipeline();
				this.pipeline = pipeline;
				pipeline.open(token);
			}
			else
				pipeline.processDragEvent(e);
		}

		public void mouseMoved(MouseEvent e) {
			// doesn't do anything
		}

		public void mouseReleased(MouseEvent e) {
			if(!dockable.isDockingEnabled())
				return;

			stopDrag(dockable, pipeline.getDragToken());				
			if(pipeline!=null)
				pipeline.close();
			pipeline = null;
		}

	}
}
