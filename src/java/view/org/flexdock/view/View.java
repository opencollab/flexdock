/*
 * Created on Feb 26, 2005
 */
package org.flexdock.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.MenuComponent;
import java.awt.PopupMenu;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.DockingStrategy;
import org.flexdock.docking.defaults.DefaultDockingStrategy;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.docking.floating.frames.FloatingDockingPort;
import org.flexdock.docking.props.DockableProps;
import org.flexdock.docking.props.PropertyManager;
import org.flexdock.docking.state.MinimizationManager;
import org.flexdock.plaf.PlafManager;
import org.flexdock.plaf.theme.ViewUI;
import org.flexdock.util.DockingConstants;
import org.flexdock.util.DockingUtility;
import org.flexdock.util.ResourceManager;
import org.flexdock.view.tracking.ViewListener;
import org.flexdock.view.tracking.ViewTracker;

/**
 * @author Christopher Butler
 */
public class View extends JComponent implements Dockable {
	static final DockingStrategy VIEW_DOCKING_STRATEGY = createDockingStrategy();
	protected String id;
	protected Titlebar titlepane;
	protected Container contentPane;
	protected boolean addRemoveAllowed;
	protected ArrayList dockingListeners;
	protected boolean active;
	protected ArrayList dragSources;
	protected HashSet frameDragSources;
	private transient HashSet blockedActions;
	
	static {
		DockingManager.setDockingStrategy(View.class, VIEW_DOCKING_STRATEGY);
		DockingManager.setDockablePropertyManager(View.class, ViewProps.class);
	}
	
	private static DockingStrategy createDockingStrategy() {
		return new DefaultDockingStrategy() {
			protected DockingPort createDockingPortImpl(DockingPort base) {
				return new Viewport();
			}
		};
	}
	
	public static View getInstance(String viewId) {
		Dockable view = DockingManager.getDockable(viewId);
		return view instanceof View? (View)view: null;
	}
	
	public View(String name) {
		this(name, null);
	}
	
	public View(String name, String title) {
		this(name, title, null);
	}
	
	public View(String name, String title, String tabText) {
		if(name==null)
			throw new IllegalArgumentException("The 'name' parameter cannot be null.");

		if(title==null)
			title = "";
		if(tabText==null)
			tabText = title;
		
		dragSources = new ArrayList(1);
		frameDragSources = new HashSet(1);
		dockingListeners = new ArrayList(1);
		
		id = name;
		setTabText(tabText);
		setLayout(null);
		setTitlebar(createTitlebar());
		setTitle(title);
		setContentPane(createContentPane());
		updateUI();
		
		DockingManager.registerDockable(this);
		ViewListener.prime();
	}
	
	protected Container createContentPane() {
		JPanel p = new JPanel();
		return p;
	}
	
	protected Titlebar createTitlebar() {
		return new Titlebar();
	}
	
	protected String getPreferredTitlebarUIName() {
		return ui instanceof ViewUI? ((ViewUI)ui).getPreferredTitlebarUI(): null;		
	}

	public Container getContentPane() {
		return contentPane;
	}

	public Titlebar getTitlebar() {
		return titlepane;
	}
	
	public DockableProps getDockingProperties() {
		return PropertyManager.getDockableProps(this);
	}
	
	public ViewProps getViewProperties() {
		return (ViewProps)getDockingProperties();
	}
	
	public void addAction(Action action) {
		if(titlepane!=null)
			titlepane.addAction(action);
	}
	
	public void addAction(String action) {
		if(titlepane!=null)
			titlepane.addAction(action);
	}
	
	public void setIcon(Icon icon) {
		if(titlepane!=null)
			titlepane.setIcon(icon);
	}
	
	public void setIcon(String imgUri) {
		Icon icon = imgUri==null? null: ResourceManager.createIcon(imgUri);
		setIcon(icon);
	}

	public void setContentPane(Container c) {
		if(c==null)
			throw new NullPointerException("Unable to set a null content pane.");
		if(c==titlepane)
			throw new IllegalArgumentException("Cannot use the same component as both content pane and titlebar.");

		synchronized(this) {
			addRemoveAllowed = true;
			if(contentPane!=null)
				removeImpl(contentPane);
			addImpl(c);
			contentPane = c;
			addRemoveAllowed = false;			
		}
	}
	
	private void removeImpl(Component c) {
		if(c instanceof Titlebar)
			((Titlebar)c).setView(null);
		super.remove(c);
	}
	
	private void addImpl(Component c) {
		if(c instanceof Titlebar)
			((Titlebar)c).setView(this);
		super.add(c);
	}


	public void setTitlebar(Titlebar titlebar) {
		if(titlebar!=null) {
			if(titlebar==contentPane)
				throw new IllegalArgumentException("Cannot use the same component as both content pane and titlebar.");
			if(!(titlebar instanceof Component))
				throw new IllegalArgumentException("Titlebar must be a type of java.awt.Component.");
		}

		synchronized(this) {
			addRemoveAllowed = true;
			removeTitlebarImpl();
			addTitlebarImpl(titlebar);
			titlepane = titlebar;
			addRemoveAllowed = false;
		}
	}
	
	protected void addTitlebarImpl(Titlebar titlebar) {
		if(titlebar!=null) {
			addImpl(titlebar);
			dragSources.add(titlebar);
			frameDragSources.add(titlebar);
			DockingManager.updateDragListeners(this);
		}
	}
	
	protected void removeTitlebarImpl() {
		if(titlepane!=null) {
			removeImpl(titlepane);
			dragSources.remove(titlepane);
			frameDragSources.remove(titlepane);
			DockingManager.removeDragListeners(titlepane);
		}
	}
	
	protected Component getTitlePane() {
		return (Component)titlepane;
	}
	
	public void setTitle(String title) {
		setTitle(title, false);
	}
	
	public void setTitle(String title, boolean alsoTabText) {
		Titlebar tbar = getTitlebar();
		if(tbar!=null)
			tbar.setText(title);
		if(alsoTabText)
			setTabText(title);
	}

	public String getTitle() {
		Titlebar tbar = getTitlebar();
		return tbar==null? null: tbar.getText();
	}
	
	
	public void doLayout() {
		Component titlebar = getTitlePane();
		int titleHeight = titlebar==null? 0: titlepane.getPreferredSize().height;
		int w = getWidth();
		int h = getHeight();
		if(titlepane!=null) {
			((Component)titlepane).setBounds(0, 0, w, titleHeight);
		}
		contentPane.setBounds(0, titleHeight, w, h-titleHeight);
	}

	
    public void updateUI() {
        setUI(PlafManager.getUI(this));
    }
    
	public Component add(Component comp, int index) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The add() method is may not be called directly.  Use setContentPane() instead.");
		return super.add(comp, index);
	}
	public void add(Component comp, Object constraints, int index) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The add() method is may not be called directly.  Use setContentPane() instead.");
		super.add(comp, constraints, index);
	}
	public void add(Component comp, Object constraints) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The add() method is may not be called directly.  Use setContentPane() instead.");
		super.add(comp, constraints);
	}
	public Component add(Component comp) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The add() method is may not be called directly.  Use setContentPane() instead.");
		return super.add(comp);
	}
	public Component add(String name, Component comp) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The add() method is may not be called directly.  Use setContentPane() instead.");
		return super.add(name, comp);
	}
	public synchronized void add(PopupMenu popup) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The add() method is may not be called directly.  Use setContentPane() instead.");
		super.add(popup);
	}
	public void remove(Component comp) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The remove() method is may not be called directly.");
		super.remove(comp);
	}
	public void remove(int index) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The remove() method is may not be called directly.");
		super.remove(index);
	}
	public void removeAll() {
		if(!addRemoveAllowed)
			throw new RuntimeException("The remove() method is may not be called directly.");
		super.removeAll();
	}
	public synchronized void remove(MenuComponent popup) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The remove() method is may not be called directly.");
		super.remove(popup);
	}

	public Component getDockable() {
		return this;
	}
	
	public List getDragSources() {
		return dragSources;
	}
	
	public Set getFrameDragSources() {
		return frameDragSources;
	}

	public String getPersistentId() {
		return id;
	}

	public boolean isTerritoryBlocked(Dockable dockable, String region) {
		return getDockingProperties().isTerritoryBlocked(region).booleanValue();
	}
	
	public void setTerritoryBlocked(String region, boolean blocked) {
		getDockingProperties().setTerritoryBlocked(region, blocked);
	}

	public String getTabText() {
		String txt = getDockingProperties().getDockableDesc();
		return txt==null? getTitle():  txt;
	}

	public void setTabText(String tabText) {
		getDockingProperties().setDockableDesc(tabText);
	}
	
	public boolean dock(Dockable dockable) {
		return dock(dockable, Viewport.CENTER_REGION);
	}
	
	public DockingPort getDockingPort() {
		return DockingManager.getDockingPort((Dockable)this);
	}
	
	public Dockable getSibling(String region) {
		return DefaultDockingStrategy.getSibling(this, region);
	}
	
	public Viewport getViewport() {
		DockingPort port = getDockingPort();
		return port instanceof Viewport? (Viewport)port: null;
	}
	
	public boolean dock(Dockable dockable, String relativeRegion) {
		return DockingUtility.dockRelative(this, dockable, relativeRegion);
	}
	
	public boolean dock(Dockable dockable, String relativeRegion, float ratio) {
		return DockingUtility.dockRelative(this, dockable, relativeRegion, ratio);
	}
	
	public void setActive(boolean b) {
		if(!isActiveStateLocked() && b!=active) {
			active = b;
			repaint();
		}
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActiveStateLocked(boolean b) {
		getViewProperties().setActiveStateLocked(b);
	}
	
	public boolean isActiveStateLocked() {
		return getViewProperties().isActiveStateLocked().booleanValue();
	}
	
	public boolean isMinimized() {
		return DockingUtility.isMinimized(this);
	}
	
	public int getMinimizedEdge() {
		Integer edge = getViewProperties().getMinimizedEdge();
		return edge==null? MinimizationManager.UNSPECIFIED_LAYOUT_EDGE: edge.intValue();
	}
	
	public void setMinimizedEdge(int edge) {
		getViewProperties().setMinimizedEdge(edge);
	}
	
	
	
	


	public void addDockingListener(DockingListener listener) {
		dockingListeners.add(listener);
	}


	public DockingListener[] getDockingListeners() {
		return (DockingListener[])dockingListeners.toArray(new DockingListener[0]);
	}


	public void removeDockingListener(DockingListener listener) {
		dockingListeners.remove(listener);
	}
	
	public void dockingCanceled(DockingEvent evt) {
	}

	public void dockingComplete(DockingEvent evt) {
		setActionBlocked(DockingConstants.PIN_ACTION, isFloating());
		if(titlepane!=null)
			titlepane.revalidate();
		
		DockingPort port = getDockingPort();
		if(port instanceof Component && ((Component)port).isShowing())
			ViewTracker.requestViewActivation(this);
	}

	public void dragStarted(DockingEvent evt) {
	}
	
	public void dropStarted(DockingEvent evt) {
	}
	
	public void undockingComplete(DockingEvent evt) {
		clearButtonRollovers();
	}
	
	public void undockingStarted(DockingEvent evt) {
	}
	
	private void clearButtonRollovers() {
		if(titlepane==null)
			return;
		
		Component[] comps = titlepane.getComponents();
		for(int i=0; i<comps.length; i++) {
			Button button = comps[i] instanceof Button? (Button)comps[i]: null;
			if(button!=null) {
				button.getModel().setRollover(false);
			}
		}
	}
	
	public void setActionBlocked(String actionName, boolean blocked) {
		if(actionName==null)
			return;
		
		Set actions = getBlockedActions();
		if(blocked)
			actions.add(actionName);
		else {
			if(actions!=null)
				actions.remove(actionName);
		}
	}
	
	public boolean isActionBlocked(String actionName) {
		return actionName==null || blockedActions==null? false: blockedActions.contains(actionName);
	}
	
	private HashSet getBlockedActions() {
		if(blockedActions==null)
			blockedActions = new HashSet(1);
		return blockedActions;
	}
	
	public boolean isFloating() {
		return getDockingPort() instanceof FloatingDockingPort;
	}
	
	/**
	 * @see java.awt.Component#toString()
	 */
	public String toString() {
		return "View[Id="+this.id+"]";
	}

}
