/*
 * Created on Feb 26, 2005
 */
package org.flexdock.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.MenuComponent;
import java.awt.PopupMenu;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.flexdock.docking.CursorProvider;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.ScaledInsets;
import org.flexdock.docking.defaults.DefaultRegionChecker;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.docking.props.DockableProps;
import org.flexdock.docking.props.PropertyManager;
import org.flexdock.util.ResourceManager;
import org.flexdock.view.floating.FloatingStrategy;
import org.flexdock.view.floating.FloatingViewport;
import org.flexdock.view.plaf.PlafManager;
import org.flexdock.view.plaf.theme.ViewUI;
import org.flexdock.view.tracking.ViewListener;

/**
 * @author Christopher Butler
 */
public class View extends JComponent implements Dockable {
	protected static final float UNSPECIFIED_SIBLING_PREF = -1F;
	protected String id;
	protected Titlebar titlepane;
	protected Container contentPane;
	protected boolean addRemoveAllowed;
	protected String viewTabText;
	protected boolean dockingEnabled;
	protected ArrayList dockingListeners;
	protected ScaledInsets siblingInsets;
	protected boolean active;
	
	// TODO: Remove activeStateLocked flag.  This needs to be controlled by our forthcoming property manager.
	protected boolean activeStateLocked;
	
	static {
		DockingManager.setDockingStrategy(View.class, new FloatingStrategy());
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

		id = name;
		siblingInsets = new ScaledInsets(RegionChecker.DEFAULT_SIBLING_SIZE);
		setTabText(tabText);
		setDockingEnabled(true);
		setLayout(null);
		setTitlebar(createTitlebar());
		setContentPane(createContentPane());
		setTitle(title==null? "": title);
		updateUI();

		dockingListeners = new ArrayList(1);
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
	
	public void addAction(Action action) {
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
			if(titlepane!=null)
				removeImpl(titlepane);
			if(titlebar!=null) {
				addImpl(titlebar);
			}
			titlepane = titlebar;
			addRemoveAllowed = false;
		}
	}
	
	protected Component getTitlePane() {
		return (Component)titlepane;
	}
	
	public void setTitle(String title) {
		Titlebar tbar = getTitlebar();
		if(tbar!=null)
			tbar.setText(title);
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

	public CursorProvider getCursorProvider() {
		return null;
	}

	public Component getDockable() {
		return this;
	}

	public String getDockableDesc() {
		return viewTabText==null? getTitle(): viewTabText;
	}
	
	public Component getInitiator() {
		return getTitlebar();
	}

	public String getPersistentId() {
		return id;
	}

	public boolean isDockingEnabled() {
		return dockingEnabled;
	}

	public boolean isTerritorial(Dockable dockable, String region) {
		return false;
	}

	public boolean mouseMotionListenersBlockedWhileDragging() {
		return true;
	}

	public void setDockableDesc(String desc) {
		viewTabText = desc==null? null: desc.trim();
	}

	public void setDockingEnabled(boolean b) {
		dockingEnabled = b;
	}

	public String getTabText() {
		return getDockableDesc();
	}

	public void setTabText(String tabText) {
		setDockableDesc(tabText);
	}
	
	public void dock(Dockable dockable) {
		dock(dockable, Viewport.CENTER_REGION);
	}
	
	public DockingPort getDockingPort() {
		return (DockingPort)SwingUtilities.getAncestorOfClass(DockingPort.class, this);
	}
	
	public void dock(Dockable dockable, String relativeRegion) {
		dock(dockable, relativeRegion, UNSPECIFIED_SIBLING_PREF);
	}
	
	public void dock(Dockable dockable, String relativeRegion, float newPref) {
		if(dockable==null)
			return;
		
		if(!DockingManager.isValidDockingRegion(relativeRegion))
			throw new IllegalArgumentException("'" + relativeRegion + "' is not a valid docking region.");

		setSiblingPreference(relativeRegion, newPref);
		
		DockingPort port = getDockingPort();
		if(port!=null)
			DockingManager.dock(dockable, port, relativeRegion);
	}
	
	protected void setSiblingPreference(String region, float size) {
		if(size==UNSPECIFIED_SIBLING_PREF || DockingPort.CENTER_REGION.equals(region) || !DockingManager.isValidDockingRegion(region))
			return;
		
		size = DefaultRegionChecker.validateSiblingSize(size);
		siblingInsets.setRegion(size, region);
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
		activeStateLocked = b;
	}
	
	public boolean isActiveStateLocked() {
		return activeStateLocked;
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

	}

	public void dragStarted(DockingEvent evt) {
	}
	
	public void dropStarted(DockingEvent evt) {
		if(evt.isOverWindow())
			return;
		
		DockingPort oldPort = evt.getOldDockingPort();
		if(oldPort instanceof FloatingViewport) {
			FloatingViewport viewport = (FloatingViewport)oldPort;
			if(viewport.getViewset().size()<2)
				evt.consume();
		}
	}
	
	public ScaledInsets getRegionInsets() {
		return null;
	}

	public ScaledInsets getSiblingInsets() {
		return siblingInsets;
	}

	public DockableProps getDockingProperties() {
		return PropertyManager.getDockableProps(this);
	}

}
