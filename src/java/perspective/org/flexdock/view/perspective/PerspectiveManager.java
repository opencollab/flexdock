/*
 * Created on 2005-03-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.perspective;

import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.util.DockingUtility;
import org.flexdock.view.View;

/**
 * @author mateusz
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PerspectiveManager implements IPerspectiveManager {
	
	private HashMap m_perspectives = new HashMap();
	
	private ArrayList m_listeners = new ArrayList();
	
	private static IPerspectiveManager SINGLETON = null;
	
	private IPerspective m_defaultPerspective = null;
	
	public static IPerspectiveManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new PerspectiveManager();
		}
		return SINGLETON;
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#addPerspective(java.lang.String, org.flexdock.view.perspective.IPerspective)
	 */
	public void addPerspective(String perspectiveId, IPerspective perspective) {
		if (perspectiveId == null) throw new NullPointerException("perspectiveId cannot be null");
		if (perspective == null) throw new NullPointerException("perspective cannot be null");
		
		m_perspectives.put(perspectiveId, perspective);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#removePerspective(java.lang.String)
	 */
	public void removePerspective(String perspectiveId) {
		if (perspectiveId == null) throw new NullPointerException("perspectiveId cannot be null");
		
		m_perspectives.remove(perspectiveId);
		
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#getPerspective()
	 */
	public IPerspective getPerspective(String perspectiveId) {
		if (perspectiveId == null) throw new NullPointerException("perspectiveId cannot be null");
		
		return (IPerspective) m_perspectives.get(perspectiveId);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#addPerspectiveListener(org.flexdock.view.perspective.PerspectiveListener)
	 */
	public void addPerspectiveListener(PerspectiveListener perspectiveListener) {
		if (perspectiveListener == null) throw new NullPointerException("perspectiveListener cannot be null");
		
		m_listeners.add(perspectiveListener);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#removePerspectiveListener(org.flexdock.view.perspective.PerspectiveListener)
	 */
	public void removePerspectiveListener(PerspectiveListener perspectiveListener) {
		if (perspectiveListener == null) throw new NullPointerException("perspectiveListener cannot be null");
		m_listeners.remove(perspectiveListener);
	}
	
	/**
	 * @see org.flexdock.view.perspective.PerspectiveMonitor#getPerspectiveListners()
	 */
	public IPerspective[] getPerspectiveListners() {
		return (IPerspective[]) m_listeners.toArray(new IPerspective[]{});
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#setDefaultPerspective(org.flexdock.view.perspective.IPerspective)
	 */
	public void setDefaultPerspective(IPerspective perspective) {
		m_defaultPerspective = perspective;
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#getDefaultPerspective()
	 */
	public IPerspective getDefaultPerspective() {
		return m_defaultPerspective;
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#applyPerspective(java.awt.Container, org.flexdock.view.perspective.IPerspective)
	 */
	public void applyPerspective(Container container, IPerspective perspective) {
		if (container == null) throw new NullPointerException("container cannot be null");
		if (perspective == null) throw new NullPointerException("perspective cannot be null");

		Perspective.ViewDockingInfo[] infos = perspective.getDockingInfoChain();
		for (int i=0; i<infos.length; i++) {
			Perspective.ViewDockingInfo info = infos[i];
			String view1Id = info.getView1Id();
			View view1 = perspective.getView(view1Id);
			if (view1 != null) {
				DockingManager.undock(view1);				
			}
		}
		
//		//TODO is it ok that we remove all components, we should probably remove
//		//only the objects that are instanceof View, Dockable and/or DockingPort
//		for (int i=0; i<container.getComponentCount(); i++) {
//			Component component = container.getComponent(i);
//			if (component instanceof View) {
//				View view = (View) container.getComponent(i);
//				DockingManager.undock(view);
//			}
//		}

		View centerView = perspective.getTerritoralView();
//		DockingManager.undock(centerView);
		
		DockingPort dockingPort = DockingUtility.getParentDockingPort((Dockable)centerView);
		if (dockingPort != null) {
			dockingPort.dock(centerView, DockingPort.CENTER_REGION);
		}
		
		//maybe we should pass something like IViewPage and access our parent container
		//only through that interface.
		//Ones root panel would then implement the interfaces
		//therefore one could only be able to remove views only which is fine.
		//but perspective should probably not remove JToolbar and other components
		
		Perspective.ViewDockingInfo[] dockingInfos = perspective.getDockingInfoChain();
		for (int i=0; i<dockingInfos.length; i++) {
			Perspective.ViewDockingInfo dockingInfo = (Perspective.ViewDockingInfo) dockingInfos[i];
			String view1Id = dockingInfo.getView1Id();
			String view2id = dockingInfo.getView2Id();
			String region = dockingInfo.getRelativeRegion();
			float ratio = dockingInfo.getRatio();
			View view1 = perspective.getView(view1Id);
			View view2 = perspective.getView(view2id);
			
			view1.dock(view2, region, ratio);
		}
		
		//TODO fire listener
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#applyPerspective(java.awt.Container, java.lang.String)
	 */
	public void applyPerspective(Container container, String perspectiveId) {
		// TODO Auto-generated method stub
		
	}
	
}
