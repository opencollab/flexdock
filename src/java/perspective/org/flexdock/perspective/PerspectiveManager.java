package org.flexdock.perspective;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.state.DockingState;
import org.flexdock.docking.state.FloatManager;
import org.flexdock.docking.state.LayoutManager;
import org.flexdock.docking.state.LayoutNode;
import org.flexdock.event.EventDispatcher;
import org.flexdock.event.RegistrationEvent;
import org.flexdock.perspective.event.LayoutEventHandler;
import org.flexdock.perspective.event.PerspectiveEventHandler;
import org.flexdock.perspective.event.PerspectiveListener;
import org.flexdock.perspective.event.RegistrationHandler;
import org.flexdock.perspective.persist.DefaultPersister;
import org.flexdock.perspective.persist.Persister;
import org.flexdock.perspective.persist.PerspectiveInfo;
import org.flexdock.util.RootWindow;
import org.flexdock.util.SwingUtility;

/**
 * @author Mateusz Szczap
 */
public class PerspectiveManager implements LayoutManager {
	public static final String EMPTY_PERSPECTIVE = "PerspectiveManager.EMPTY_PERSPECTIVE";
	private static PerspectiveManager SINGLETON = new PerspectiveManager();
	private static DockingStateListener UPDATE_LISTENER = new DockingStateListener();
	
	private HashMap m_perspectives = new HashMap();
	private PerspectiveBuilder perspectiveBuilder;
	private String m_defaultPerspective;
	private String m_currentPerspective;
	private Persister m_persister = new DefaultPersister();
	private boolean restoreFloatingOnLoad;
	
	static {
		initialize();
	}
	
	private static void initialize() {
		// TODO: Add logic to add and remove event handlers based on whether
		// the perspective manager is currently installed.  Right now, we're 
		// just referencing DockingManager.class to ensure the class is properly
		// initialized before we add our event handlers.  This should be 
		// called indirectly form within DockingManager, and we should have
		// uninstall capability as well.
		Class c = DockingManager.class;
		
		EventDispatcher.addHandler(new RegistrationHandler());
		EventDispatcher.addHandler(PerspectiveEventHandler.getInstance());
		EventDispatcher.addHandler(new LayoutEventHandler());

		EventDispatcher.addListener(UPDATE_LISTENER);
	}
	
	public static PerspectiveManager getInstance() {
		return SINGLETON;
	}
	
	public static void setBuilder(PerspectiveBuilder builder) {
		getInstance().perspectiveBuilder = builder;
	}
	
	public static void setPersister(Persister persister) {
		getInstance().m_persister = persister;
	}
	
	public static Persister getPersister() {
		return getInstance().m_persister;
	}

	
	private PerspectiveManager() {
		m_persister = new DefaultPersister();
		m_defaultPerspective = EMPTY_PERSPECTIVE;
		load(m_defaultPerspective, (DockingPort)null);
	}
	
	
	
	
	
	
	
	
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#addPerspective(java.lang.String, org.flexdock.view.perspective.IPerspective)
	 */
	public void add(Perspective perspective) {
		add(perspective, false);
	}

	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#addPerspective(java.lang.String, org.flexdock.view.perspective.IPerspective, boolean)
	 */
	public void add(Perspective perspective, boolean isDefault) {
		if (perspective == null) throw new NullPointerException("perspective cannot be null");
		
		m_perspectives.put(perspective.getPersistentId(), perspective);
		if(isDefault)
			setDefaultPerspective(perspective.getPersistentId());
		
		EventDispatcher.dispatch(new RegistrationEvent(perspective, this, true));
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#removePerspective(java.lang.String)
	 */
	public void remove(String perspectiveId) {
		if (perspectiveId == null) throw new NullPointerException("perspectiveId cannot be null");
		
		Perspective perspective = getPerspective(perspectiveId);
		if (perspective == null)
			return;
		
		m_perspectives.remove(perspectiveId);

		//set defaultPerspective
		if(m_defaultPerspective.equals(perspectiveId))
			setDefaultPerspective(EMPTY_PERSPECTIVE);

		EventDispatcher.dispatch(new RegistrationEvent(perspective, this, false));
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#getPerspective()
	 */
	public Perspective getPerspective(String perspectiveId) {
		if (perspectiveId == null)
			return null;
		
		Perspective perspective = (Perspective) m_perspectives.get(perspectiveId);
		if(perspective==null) {
			perspective = createPerspective(perspectiveId);
			if(perspective!=null) {
				add(perspective);
			}
		}
		return perspective;
	}
	
	public Perspective createPerspective(String perspectiveId) {
		if(EMPTY_PERSPECTIVE.equals(perspectiveId))
			return new Perspective(EMPTY_PERSPECTIVE, EMPTY_PERSPECTIVE) {
				public void load(DockingPort port, boolean defaultSetting) {
					// noop
				}
		};
		
		return perspectiveBuilder==null? null: perspectiveBuilder.createPerspective(perspectiveId);
	}
	
	public Perspective[] getPerspectives() {
		synchronized(m_perspectives) {
			ArrayList list = new ArrayList(m_perspectives.values());
			return (Perspective[])list.toArray(new Perspective[0]);
		}
		
	}
	
	
	
	
	
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#addPerspectiveListener(org.flexdock.view.perspective.PerspectiveListener)
	 */
	public void addListener(PerspectiveListener perspectiveListener) {
		EventDispatcher.addListener(perspectiveListener);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#removePerspectiveListener(org.flexdock.view.perspective.PerspectiveListener)
	 */
	public void removeListener(PerspectiveListener perspectiveListener) {
		EventDispatcher.removeListener(perspectiveListener);
	}
	
	/**
	 * @see org.flexdock.view.perspective.event.PerspectiveMonitor#getPerspectiveListners()
	 */
	public PerspectiveListener[] getPerspectiveListeners() {
		return PerspectiveEventHandler.getInstance().getListeners();
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#setDefaultPerspective(org.flexdock.view.perspective.IPerspective)
	 */
	public void setDefaultPerspective(String perspectiveId) {
		m_defaultPerspective = perspectiveId;
	}
	
	public void setCurrentPerspective(String perspectiveId) {
		setCurrentPerspective(perspectiveId, false);
	}
	
	public void setCurrentPerspective(String perspectiveId, boolean asDefault) {
		perspectiveId = perspectiveId==null? m_defaultPerspective: perspectiveId;
		m_currentPerspective = perspectiveId;
		if(asDefault)
			setDefaultPerspective(perspectiveId);
	}
	
	/**
	 * @see org.flexdock.view.perspective.IPerspectiveManager#getDefaultPerspective()
	 */
	public Perspective getDefaultPerspective() {
		return getPerspective(m_defaultPerspective);
	}

	public Perspective getCurrentPerspective() {
		return getPerspective(m_currentPerspective);
	}
	
	
	public DockingState getDockingState(Dockable dockable) {
		return getCurrentPerspective().getDockingState(dockable);
	}
	
	public DockingState getDockingState(String dockable) {
		return getCurrentPerspective().getDockingState(dockable);
	}
	
	public DockingState getDockingState(Dockable dockable, boolean load) {
		return getCurrentPerspective().getDockingState(dockable, load);
	}
	
	public DockingState getDockingState(String dockable, boolean load) {
		return getCurrentPerspective().getDockingState(dockable, load);
	}
	
	
	public FloatManager getFloatManager() {
		return getCurrentPerspective().getLayout();
	}

	
	
	
	public void reset() {
		RootWindow[] windows = DockingManager.getDockingWindows();
		if(windows.length!=0)
			reset(windows[0].getRootContainer());
	}
	
	public void reset(Component window) {
		if(window==null) {
			reset();
		}
		else {
			DockingPort port = DockingManager.getRootDockingPort(window);
			reset(port);
		}
	}
	
	public void reset(DockingPort rootPort) {
		loadPerspective(m_currentPerspective, rootPort, true);
	}
	

	
	public void reload() {
		String key = m_currentPerspective==null? m_defaultPerspective: m_currentPerspective;
		m_currentPerspective = null;
		load(key);
	}

	public void load() {
		load(m_defaultPerspective);
	}
	
	public void loadAsDefault(String perspectiveId) {
		loadAsDefault(perspectiveId, false);
	}
	
	public void loadAsDefault(String perspectiveId, boolean reset) {
		if(perspectiveId!=null)
			setDefaultPerspective(perspectiveId);
		load(perspectiveId, reset);
	}
	
	public void load(String perspectiveId) {
		load(perspectiveId, false);
	}
	
	public void load(String perspectiveId, boolean reset) {
		RootWindow window = getMainApplicationWindow();
		if(window==null)
			return;
		
		load(perspectiveId, window.getRootContainer(), reset);		
	}
	
	public void load(String perspectiveId, Component window) {
		load(perspectiveId, window, false);
	}
	
	public void load(String perspectiveId, Component window, boolean reset) {
		if(window==null) {
			load(perspectiveId, reset);
			return;
		}
		
		DockingPort port = DockingManager.getRootDockingPort(window);
		load(perspectiveId, port, reset);		
	}
	
	public void load(String perspectiveId, DockingPort rootPort) {
		load(perspectiveId, rootPort, false);
	}
	
	public void load(String perspectiveId, DockingPort rootPort, boolean reset) {
		if(perspectiveId==null || perspectiveId.equals(m_currentPerspective))
			return;
		loadPerspective(perspectiveId, rootPort, reset);
	}
	
	private void loadPerspective(String perspectiveId, final DockingPort rootPort, boolean reset) {
		if(perspectiveId==null)
			return;

		Perspective current = getCurrentPerspective();
		final Perspective perspective = getPerspective(perspectiveId);

		// remember the current layout state so we'll be able to
		// restore when we switch back
		if(current!=null) {
			cacheLayoutState(current, rootPort);
			current.unload();
		}
		
		// if the new perspective isn't available, then we're done
		if(perspective==null)
			return;
		
		synchronized(this) {
			m_currentPerspective = perspectiveId;
			if(reset)
				perspective.reset(rootPort);
			else
				perspective.load(rootPort);
		}
		
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						cacheLayoutState(perspective, rootPort);
					}
				};
				EventQueue.invokeLater(r);
			}
		};
		t.start();
	}
	
	private void cacheLayoutState(Perspective p, DockingPort port) {
		if(p!=null)
			p.cacheLayoutState(port);
	}
	
	
	
	public LayoutNode createLayout(DockingPort port) {
		return LayoutBuilder.getInstance().createLayout(port);
	}
	
	public boolean restore(Dockable dockable) {
		return RestorationManager.getInstance().restore(dockable);
	}

	static void setDockingStateListening(boolean enabled) {
		UPDATE_LISTENER.setEnabled(enabled);
	}
	
	static boolean isDockingStateListening() {
		return UPDATE_LISTENER.isEnabled();
	}
	
	static void clear(DockingPort port) {
		if(port!=null) {
			boolean currState = isDockingStateListening();
			setDockingStateListening(false);
			port.clear();
			setDockingStateListening(currState);
		}
	}
	
	static void updateDockingStates(final Dockable[] dockables) {
		if(dockables==null)
			return;
			
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						for(int i=0; i<dockables.length; i++) {
							UPDATE_LISTENER.updateState(dockables[i]);
						}
					}
				};
				EventQueue.invokeLater(r);
			}
		};
		t.start();
	}
	
	
	public synchronized boolean persist(String appKey) throws IOException {
		if(m_persister==null)
			return false;

		Window window = SwingUtility.getActiveWindow();
		DockingPort rootPort = DockingManager.getRootDockingPort(window);
		cacheLayoutState(getCurrentPerspective(), rootPort);
		
		Perspective[] items = getPerspectives();
		for(int i=0; i<items.length; i++) {
			items[i] = (Perspective)items[i].clone();
		}
		
		PerspectiveInfo info = new PerspectiveInfo(m_defaultPerspective, m_currentPerspective, items);
		return m_persister.store(appKey, info);
	}
	
	public synchronized boolean loadFromStorage(String appKey) throws IOException {
		if(m_persister==null)
			return false;
		
		PerspectiveInfo info = m_persister.load(appKey);
		if(info==null)
			return false;

		Perspective[] perspectives = info.getPerspectives();
		
		m_perspectives.clear();
		for(int i=0; i<perspectives.length; i++) {
			add(perspectives[i]);
		}
		setDefaultPerspective(info.getDefaultPerspective());
		m_currentPerspective = info.getCurrentPerspective();
		return true;
	}
	
	public static boolean isRestoreFloatingOnLoad() {
		return getInstance().restoreFloatingOnLoad;
	}
	
	public static void setRestoreFloatingOnLoad(boolean restoreFloatingOnLoad) {
		getInstance().restoreFloatingOnLoad = restoreFloatingOnLoad;
	}
	
	public static RootWindow getMainApplicationWindow() {
		// TODO: fix this code to keep track of the proper dialog owner
		RootWindow[] windows = DockingManager.getDockingWindows();
		RootWindow window = null;
		for(int i=0; i<windows.length; i++) {
			window = windows[i];
			if(window.getOwner()==null)
				break;
		}
		return window;
	}
	
	public static DockingPort getMainDockingPort() {
		RootWindow window = getMainApplicationWindow();
		return window==null? null: DockingManager.getRootDockingPort(window.getRootContainer());
	}
}
