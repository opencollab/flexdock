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
package org.flexdock.docking.defaults;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.WeakHashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.ScaledInsets;
import org.flexdock.docking.config.ConfigurationManager;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.docking.props.DockingPortProps;
import org.flexdock.docking.props.PropertyManager;
import org.flexdock.util.Utilities;


/**
 * A <code>Container</code> that implements the <code>DockingPort</code> interface.  This class provides
 * a default implementation of <code>DockingPort</code> to allow ease of development within docking-enabled
 * applications.  
 * <p>
 * As <code>Dockable</code> instances are dragged over the <code>DefaultDockingPort</code>, 
 * the <code>DefaultDockingPort</code> reports to the <code>DockingManager</code> the current docking
 * region based upon its docking insets.  Upon docking, the currently docked component is checked.  If 
 * <code>null</code>, the newly docked component is placed in the CENTER region and is expanded to fill the 
 * entire container area.  If the currently docked component is not <code>null</code>, then docking is 
 * handled with respect to the specified docking region.
 * <p>
 * Components that are docked in the CENTER region are embedded within a <code>JTabbedPane</code>, unless there
 * is only one component, in which case the component is a direct child of the <code>DockingPort</code> itself.
 * If no <code>JTabbedPane</code> is present, and one is required, then one is created and added, and all child 
 * components are added to the <code>JTabbedPane</code>.
 * <p>
 * Components that are docked in the NORTH, SOUTH, EAST, or WEST regions are placed in a 
 * <code>JSplitPane</code> splitting the layout of the <code>DockingPort</code> between child components.  
 * Each region of the <code>JSplitPane</code> contains a new <code>DefaultDockingPort</code>, 
 * which, in turn, contains the docked components.  Thus, a <code>DefaultDockingPort</code> has a maximum
 * of one child component, which may or may not be a wrapper for other child components.  Because
 * <code>JSplitPane</code> contains child <code>DefaultDockingPorts</code>, each of those 
 * <code>DefaultDockingPorts</code> is available for further sub-docking operations.
 * <p>
 * Since a <code>DefaultDockingPort</code> may only contain one child component, there is a container 
 * hierarchy to manage tabbed interfaces, split layouts, and sub-docking.  As components are removed from 
 * this hierarchy, the hierarchy itself must be reevaluated.  Removing a component from a child 
 * <code>DefaultDockingPort</code> within a <code>JSplitPane</code> renders the child 
 * <code>DefaultDockingPort</code> unnecessary, which, in turn, renders the notion of splitting the layout
 * with a <code>JSplitPane</code> unnecessary (since there are no longer two components to 
 * split the layout between).  Likewise, removing a child component from a <code>JTabbedPane</code> such 
 * that there is only one child left within the <code>JTabbedPane</code> removes the need for a tabbed 
 * interface to begin with.
 * <p>
 * When the <code>DockingManager</code> removes a component from a <code>DockingPort</code> it follows the 
 * removal with a call to <code>undock()</code>  <code>undock()</code> 
 * automatically handles the reevaluation of the container hierarchy to keep wrapper-container usage at
 * a minimum.  Since <code>DockingManager</code> makes this callback automatic, developers normally will not
 * need to call this method explicitly.  However, when removing a component from a 
 * <code>DefaultDockingPort</code> using application code, developers should keep in mind to use 
 * <code>undock()</code> instead of <code>remove()</code>.
 * 
 * While <code>DefaultDockingPort</code> provides default implementations for <code>JTabbedPane</code> and 
 * <code>JSplitPane</code> during docking operations, it is understood that the implemtations
 * provided will not necessarily be desired by application developers.  The <code>SubComponentProvider</code>
 * interface is provided as a means of customizing the behavior of the <code>DefaultDockingPort</code> 
 * during docking operations.  By implementing the <code>SubComponentProvider</code> interface, custom classes
 * may be plugged in via the <code>setComponentProvider(SubComponentProvider helper)</code> method.  When a
 * <code>SubComponentProvider</code> has been assigned to a <code>DefaultDockingPort</code>, 
 * container createion during docking will be delegated to the <code>SubComponentProvider</code>.  
 * Otherwise, default behavior is assumed.
 * 
 * Border management after docking and undocking operations are accomplished using a 
 * <code>BorderManager</code>.  <code>setBorderManager()</code> may be used to set the border manager
 * instance and customize border management. 
 * 
 * @author Chris Butler
 *
 */
public class DefaultDockingPort extends JPanel implements DockingPort {
	private static final WeakHashMap COMPONENT_TITLES = new WeakHashMap();
	private static final SubComponentProvider DEFAULT_CMP_PROVIDER = new DefaultComponentProvider();
	
	protected ArrayList dockingListeners;
	private SubComponentProvider subComponentProvider;
	private int borrowedTabIndex;
	private int cachedSplitDividerSize;
	private Component dockedComponent;
	private BorderManager borderManager;
	private String persistentId;
	private boolean singleTabsAllowed;
	private boolean tabbedDragSource;
	private ScaledInsets regionInsets;
	

	
	/**
	 * Creates a new <code>DefaultDockingPort</code> with a <code>null</code> persistent ID.
	 */
	public DefaultDockingPort() {
		this(null);
	}
	
	/**
	 * Creates a new <code>DefaultDockingPort</code> with the specified persistent ID.
	 */
	public DefaultDockingPort(String id) {
		setPersistentId(id);
		dockingListeners = new ArrayList(2);
		addDockingListener(this);
		regionInsets = new ScaledInsets(RegionChecker.DEFAULT_REGION_SIZE);
	}

	/**
	 * Overridden to set the currently docked component.  Should not be called by application code.
	 */
	public Component add(Component comp) {
		return setComponent(comp);
	}
	
	/**
	 * Overridden to set the currently docked component.  Should not be called by application code.
	 */
	public Component add(Component comp, int index) {
		return setComponent(comp);
	}

	/**
	 * Overridden to set the currently docked component.  Should not be called by application code.
	 */
	public void add(Component comp, Object constraints) {
		setComponent(comp);
	}

	/**
	 * Overridden to set the currently docked component.  Should not be called by application code.
	 */
	public void add(Component comp, Object constraints, int index) {
		setComponent(comp);
	}

	/**
	 * Overridden to set the currently docked component.  Should not be called by application code.
	 */
	public Component add(String name, Component comp) {
		return setComponent(comp);
	}
	
	private void addCmp(DockingPort port, Component c) {
		if(port instanceof Container)
			((Container)port).add(c);
	}

	private void dockCmp(DockingPort port, Component c, String desc) {
		port.dock(c, desc, DockingPort.CENTER_REGION);
	}	

	
	/**
	 * @see org.flexdock.docking.DockingPort#isDockingAllowed(java.lang.String)
	 */
	public boolean isDockingAllowed(String region, Component c) {
		if(c==null || !isValidDockingRegion(region))
			return false;
		
		// allow any valid region if we're not already the parent
		// of the component we're checking
		if(!isParentDockingPort(c))
			return true;

		// we're already contain 'c', so we're either a tabbed-layout, or 
		// we contain 'c' directly.  If we contain 'c' directly, then we 
		// cannot logically move 'c' to some other region within us, as it
		// already fills up our entire space.
		Component docked = getDockedComponent();
		if(!(docked instanceof JTabbedPane))
			// not a tabbed-layout, so we contain 'c' directly
			return false;		
		
		JTabbedPane tabs = (JTabbedPane)docked;
		// if there is only 1 tab, then we already fill up the entire 
		// dockingport space and cannot be moved elsewhere
		if(tabs.getTabCount()<2)
			return false;
		
		// there are is than 1 tab present, so re-ordering is possible, 
		// as well as changing regions
		return true;
	}
	

	public ScaledInsets getRegionInsets() {
		return regionInsets;
	}

	public void setSingleTabsAllowed(boolean b) {
		singleTabsAllowed = b;
	}
	
	public boolean isSingleTabsAllowed() {
		return singleTabsAllowed;
	}
	
	public void setTabsAsDragSource(boolean b) {
		tabbedDragSource = b;
	}
	
	public boolean areTabsDragSource() {
		return tabbedDragSource;
	}

	/**
	 * Checks the current state of the <code>DockingPort</code> and, if present, issues the appropriate
	 * call to the assigned <code>BorderManager</code> instance describing the container state.  This
	 * method will issue a call to 1 of the 4 following methods on the assigned <code>BorderManager</code>
	 * instance, passing <code>this</code> as the method argument:<br>
	 * <code>managePortNullChild(DockingPort port)</code>
	 * <code>managePortSimpleChild(DockingPort port)</code>
	 * <code>managePortSplitChild(DockingPort port)</code>
	 * <code>managePortTabbedChild(DockingPort port)</code>
	 */
	public final void evaluateDockingBorderStatus() {
		if(borderManager==null)
			return;
			
		Component docked = getDockedComponent();
		// check for the null-case
		if(docked==null)
			borderManager.managePortNullChild(this);
		// check for a split layout
		else if(docked instanceof JSplitPane)
			borderManager.managePortSplitChild(this);
		// check for a tabbed layout
		else if(docked instanceof JTabbedPane)
			borderManager.managePortTabbedChild(this);
		// otherwise, we have a simple case of a regular component docked within us
		else
			borderManager.managePortSimpleChild(this);
	}

	protected SubComponentProvider getSubdocker() {
		return subComponentProvider==null? getDefaultSubdocker(): subComponentProvider;
	}
	
	protected SubComponentProvider getDefaultSubdocker() {
		return DEFAULT_CMP_PROVIDER;
	}
	
	public String getRegion(Point p) {
		if(p==null)
			return UNKNOWN_REGION;
		
		RegionChecker regionChecker = getRegionChecker();
		Dockable d = getDockableAt(p);
		Component regionTest = this;
		
		if(d!=null) {
			regionTest = d.getDockable();
			p = SwingUtilities.convertPoint(this, p, regionTest);
		}

		return regionChecker.getRegion(regionTest, p); 
	}
	
	public Dockable getDockableAt(Point p) {
		if(p==null)
			return null;

		Component docked = getDockedComponent();
		if(docked instanceof JSplitPane)
			return null;
		
		if(docked instanceof JTabbedPane) {
			JTabbedPane tabs = (JTabbedPane)docked;
			Component c = tabs.getComponentAt(p.x, p.y);
			return c instanceof Dockable? (Dockable)c: DockingManager.getRegisteredDockable(c);
		}

		return DockingManager.getRegisteredDockable(docked);
	}
	
	public RegionChecker getRegionChecker() {
		return new DefaultRegionChecker();
	}
	
	protected DockingPort createChildPort() {
		DockingPort port = getSubdocker().createChildPort();
		if(port==null)
			port = getDefaultSubdocker().createChildPort();
		if(port instanceof DefaultDockingPort)
			((DefaultDockingPort)port).setBorderManager(borderManager);
		return port;
	}
	
	private JSplitPane createSplitPane(String region) {
		JSplitPane pane = getSubdocker().createSplitPane(region);
		if(pane==null)
			pane = getDefaultSubdocker().createSplitPane(region);
			
		int orient = JSplitPane.HORIZONTAL_SPLIT;
		if(NORTH_REGION.equals(region) || SOUTH_REGION.equals(region))
			orient = JSplitPane.VERTICAL_SPLIT;
		pane.setOrientation(orient);
		
		cachedSplitDividerSize = pane.getDividerSize();
		return pane;
	}
	
	private JTabbedPane createTabbedPane() {
		JTabbedPane pane = getSubdocker().createTabbedPane();
		if(pane==null)
			pane = getDefaultSubdocker().createTabbedPane();

		TabListener tl = new TabListener();
		pane.addMouseListener(tl);
		pane.addMouseMotionListener(tl);
		return pane;
	}
	
	private int tabPlacement;
	
	private JTabbedPane createTabbedPane2() {
		JTabbedPane pane = getSubdocker().createTabbedPane();
		if (pane==null) {
			pane = getDefaultSubdocker().createTabbedPane();
		}
		pane.setTabPlacement(tabPlacement);
		return pane==null? getDefaultSubdocker().createTabbedPane(): pane;
	}
	
	public int getTabPlacement() {
		return getInitialTabPlacement();
	}
	
	/**
	 * Dispatches to <code>removeAll()</code>.
	 * @see org.flexdock.docking.DockingPort#clear()
	 */
	public void clear() {
		removeAll();
	}

	/**
	 * Decomposes the supplied <code>Dockable</code> into constituent fields and dispatches to 
	 * <code>dock(Component comp, String desc, String region, boolean resizable)</code>.  Passes as 
	 * arguments to the overloaded method the supplied <code>Dockable's getDockable()</code> and
	 * <code>getDockableDesc()</code> values.  The specified
	 * region is also passed.  If <code>dockable</code> is null, no action is taken and the method
	 * returns <code>false</code>.
	 * 
	 * @see org.flexdock.docking.DockingPort#dock(Component comp, String desc, String region)
	 */
	public boolean dock(Dockable dockable, String region) {
		if(dockable==null)
			return false;
		return dock(dockable.getDockable(), dockable.getDockableDesc(), region);
	}

	/**
	 * Docks the specified component within the specified region.
	 * If the <code>DockingPort</code> is currently empty, then <code>comp</code> will be docked within the
	 * CENTER region, regardless of what has been passed into this method.  If the <code>DockingPort</code>
	 * is not empty, then docking in the CENTER region will place <code>comp</code> in a JTabbedPane, using
	 * <code>desc</code> as the tab title.  All other regions will result in a JSplitPane layout, where
	 * <code>resizable</code> determines whether or not the split pane is resizable.  If <code>comp</code> 
	 * is <code>null</code> or <code>isDockingAllowed(String region)</code> returns <code>false</code>, then 
	 * this method will return <code>false</code> with no action taken. If this method is successful, 
	 * <code>evaluateDockingBorderStatus()</code> will be called before returning <code>true</code> to allow the
	 * assigned <code>SubComponentProvider</code> to handle container-state-related behavior.
	 * 
	 * @see org.flexdock.docking.DockingPort#dock(Component comp, String desc, String region)
	 */
	public boolean dock(Component comp, String desc, String region) {
		if(comp==null || !isDockingAllowed(region, comp))
			return false;
		
		desc = desc==null? "null": desc.trim();
		if(desc.length()==0)
			desc = "null";
			
		// can't dock the same component twice.  This will also keep them from
		// moving CENTER to NORTH and that sort of thing, which would just be a 
		// headache to manage anyway.
		Component docked = getDockedComponent();
		if(comp==docked)
			return false;
		
		// if there is nothing currently in the docking port, then we can only 
		// dock into the CENTER region.
		if(docked==null)
			region = DockingPort.CENTER_REGION;
		
		COMPONENT_TITLES.put(comp, desc);
		

		if(!singleTabsAllowed && docked==null) {
			setComponent(comp);
			evaluateDockingBorderStatus();
			return true;
		}
		
		boolean success = DockingPort.CENTER_REGION.equals(region)? 
				dockInCenterRegion(comp): dockInOuterRegion(comp, region, desc);
			
		if(success) {
			evaluateDockingBorderStatus();
			// if we docked in an outer region, then there is a new JSplitPane.  We'll 
			// want to divide it in half.  this is done after evaluateDockingBorderStatus(), 
			// so we'll know any border modification that took place has already happened, 
			// and we can be relatively safe about assumptions regarding our current 
			// insets.
			if(!DockingPort.CENTER_REGION.equals(region)) 
				resolveSplitDividerLocation(docked);
		}
		return success;
	}
	
	protected void resolveSplitDividerLocation(Component controller) {
		Component cmp = getDockedComponent();
		if(!(cmp instanceof JSplitPane))
			return;
		
		JSplitPane split = (JSplitPane)cmp;	
		Insets in = getInsets();
		boolean vert = split.getOrientation()==JSplitPane.VERTICAL_SPLIT;
		
		// get the dimensions of the DefaultDockingPort, minus the insets and multiply by 
		// the divider proportion provided by the assigned <code>SubComponentProvider</code>. 
		int dim = vert? (getHeight()-in.top-in.bottom): (getWidth()-in.left-in.right);
		double proportion = getSubdocker().getInitialDividerLocation(split, controller);
		if(proportion<0 || proportion>1)
			proportion = 0.5d;
		
		int loc = (int)(dim*proportion);
		split.setDividerLocation(loc);
	}
	
	private boolean dockInCenterRegion(Component comp) {
		Component docked = getDockedComponent();
		JTabbedPane tabs = null;
		
		if(docked instanceof JTabbedPane) {
			tabs = (JTabbedPane)docked;
			tabs.add(comp, getValidTabTitle(tabs, comp));
			tabs.revalidate();
			tabs.setSelectedIndex(tabs.getTabCount()-1);
			return true;
		}
		
		tabs = createTabbedPane();
		// createTabbedPane() is protected and may be overridden, so we'll have to check for a 
		// possible null case here.  Though why anyone would return a null, I don't know.  Maybe
		// we should throw a NullPointerException instead.
		if(tabs==null)
			return false;
		
		// remove the currently docked component and add it to the tabbed pane
		if(docked!=null) {
			remove(docked);
			tabs.add(docked, getValidTabTitle(tabs, docked));
		}
		
		// add the new component to the tabbed pane
		tabs.add(comp, getValidTabTitle(tabs, comp));
		
		// now add the tabbed pane back to the main container
		setComponent(tabs);
		tabs.setSelectedIndex(tabs.getTabCount()-1);
		return true;
	}
	
	private boolean dockInOuterRegion(Component comp, String region, String desc) {
		// cache the current size and cut it in half for later in the method.
		Dimension halfSize = getSize();
		halfSize.width /= 2;
		halfSize.height /= 2;
		
		// remove the old docked content.  we'll be adding it to another dockingPort.
		Component docked = getDockedComponent();
		remove(docked);
		
		// add the components to their new parents.
		DockingPort oldContent = createChildPort();
		DockingPort newContent = createChildPort();
		addCmp(oldContent, docked);
		dockCmp(newContent, comp, desc);
		
		// put the ports in the correct order and add them to a new wrapper panel
		DockingPort[] ports = putPortsInOrder(oldContent, newContent, region);
		setPreferredSize(ports[0], halfSize);
		setPreferredSize(ports[1], halfSize);
		JSplitPane newDockedContent = createSplitPane(region);

		if(ports[0] instanceof Component)
			newDockedContent.setLeftComponent((Component)ports[0]);
		if(ports[1] instanceof Component)
			newDockedContent.setRightComponent((Component)ports[1]);

		// now set the wrapper panel as the currently docked component
		setComponent(newDockedContent);

		return true;
	}

	/**
	 * Overridden to expand the docked component to fill the entire available region, minus insets.
	 */
	public void doLayout() {
		Component docked = getDockedComponent();
		if(docked==null)
			return;
			
		Insets insets = getInsets();
		int w = getWidth() - insets.left - insets.right;
		int h = getHeight() - insets.top - insets.bottom;
		docked.setBounds(insets.left, insets.top, w, h);
	}
	
	/**
	 * @see org.flexdock.docking.DockingPort#getDockedComponent()
	 */
	public Component getDockedComponent() {
		return dockedComponent;
	}
	
	protected JSplitPane getDockedSplitPane() {
		Component docked = getDockedComponent();
		return docked instanceof JSplitPane? (JSplitPane)docked: null;
	}
	
	/**
	 * @see org.flexdock.docking.DockingPort#getPersistentId()
	 */
	public String getPersistentId() {
		return persistentId;
	}
	
	/**
	 * 
	 */
	public void setPersistentId(String id) {
		String oldId = persistentId;
		persistentId = id;
		ConfigurationManager.replaceDockingPort(oldId, persistentId, this);
	}
	
	private String getValidTabTitle(JTabbedPane tabs, Component comp) {
		String title = (String)COMPONENT_TITLES.get(comp);
		if(title==null || title.trim().length()==0)
			title = "null";
			
		int tc = tabs.getTabCount();
		int occurrances = 0;
		HashSet titles = new HashSet();
		String tmp = null;
		for(int i=0; i<tc; i++) {
			tmp = tabs.getTitleAt(i).toLowerCase();
			titles.add(tmp);
			if(tmp.startsWith(title.toLowerCase()))
				occurrances++;
		}
		
		if(titles.contains(title) && occurrances>0)
			title += occurrances;
			
		COMPONENT_TITLES.put(comp, title);
		return title;
	}

	
	/** 
	 * Indicates whether or not the specified component is docked somewhere within this 
	 * <code>DefaultDockingPort</code>.  This method returns true if the specified component is a direct
	 * child of the <code>DefaultDockingPort</code> or is a direct child of a <code>JTabbedPane</code>
	 * that is currently the <code>DefaultDockingPort's</code> docked component.  Otherwise, this method
	 * returns false.
	 * 
	 * @param comp the Component to be tested.
	 * @return a boolean indicating whether or not the specified component is docked somewhere within this 
	 * <code>DefaultDockingPort</code>.
	 * @see org.flexdock.docking.DockingPort#isParentDockingPort(java.awt.Component)
	 */
	public boolean isParentDockingPort(Component comp) {
		if(comp==null)
			return false;
			
		Container parent = comp.getParent();
		if(parent==null)
			return false;
			
		// if the component is inside a JTabbedPane, check to see of the tabbed pane
		// is embedded within us.  if not, then 'comp' is not a child docked within us.
		if(parent instanceof JTabbedPane) {
			Container grandParent = parent.getParent();
			if(grandParent!=this)
				return false;
		}
		else {
			// not a JTabbedPane child, so make sure 'comp' is a direct child of 'this'
			if(parent!=this)
				return false;
		}

		// we passed all the 'false' checks, so 'comp' is in fact docked within us
		return true;
	}

	protected boolean isValidDockingRegion(String region) {
		return DockingManager.isValidDockingRegion(region); 
	}

	protected boolean isSingleComponentDocked() {
		Component c = getDockedComponent();
		// we have no docked component
		if(c==null)
			return false;
		
		// we do have a docked component.  It'll be a splitpane, a tabbedpane, 
		// or something else.
		
		// if it's a splitpane, then we definitely have more than one component docked
		if(c instanceof JSplitPane)
			return false;
		
		// if it's a tabbed pane, then check the number of tabs on the pane
		if(c instanceof JTabbedPane) {
			return ((JTabbedPane)c).getTabCount()==1;
		}
		
		// splitpane and tabbed pane are the only two subcontainers that signify
		// more than one docked component.  if neither, then we only have one
		// component docked.
		return true;
	}
	
	protected Dockable getCenterDockable() {
		// can't have a CENTER dockable if there's nothing in the center
		if(!isSingleComponentDocked())
			return null;
		
		// get the component in the CENTER
		Component c = getDockedComponent();
		if(c instanceof JTabbedPane) {
			// if in a tabbed pane, get the first component in there.
			// (there will only be 1, since we've already passed the 
			//  isSingleComponentDocked() test)
			c = ((JTabbedPane)c).getComponent(0);
		}
		// return the Dockable instance associated with this component
		return DockingManager.getRegisteredDockable(c);
	}
	
	/**
	 * @see org.flexdock.docking.DockingPort#lendDockedComponent()
	 */
	public Component lendDockedComponent() {
		Component docked = getDockedComponent();
		if(docked==null)
			return null;
			
		super.remove(docked);
		return docked;
	}

	private DockingPort[] putPortsInOrder(DockingPort oldPort, DockingPort newPort, String region) {
		if(DockingPort.NORTH_REGION.equals(region) || DockingPort.WEST_REGION.equals(region))
			return new DockingPort[] {newPort, oldPort};
		return new DockingPort[] {oldPort, newPort};
	}

	/**
	 * This method completes with a call to <code>evaluateDockingBorderStatus()</code> to allow the
	 * assigned <code>SubComponentProvider</code> to handle container-state-related behavior.
	 */
	public void reevaluateContainerTree() {
		reevaluateDockingWrapper();
		reevaluateTabbedPane();


		evaluateDockingBorderStatus();
	}
	

	
	private void reevaluateDockingWrapper() {
		Component docked = getDockedComponent();
		Container parent = getParent();
		Container grandParent = parent==null? null: parent.getParent();

		// added grandparent check up here so we will be able to legally embed a DefaultDockingPort
		// within a plain JSplitPane without triggering an unnecessary remove()		
		if(docked==null && parent instanceof JSplitPane && grandParent instanceof DefaultDockingPort) {
			// in this case, the docked component has disappeared (removed) and our parent component
			// is a wrapper for us and our child so that we can share the root docking port with 
			// another component.  since our child is gone, there's no point in our being here 
			// anymore and our sibling component shouldn't have to share screen real estate with us 
			// anymore. we'll remove ourselves and notify the root docking port that the component 
			// tree has been modified. 
			parent.remove(this);
			((DefaultDockingPort)grandParent).reevaluateContainerTree(); // LABEL 1
		}
		else if(docked instanceof JSplitPane) {
			// in this case, we're the parent of a docking wrapper.  this implies that we're splitting
			// our real estate between two components. (in practice, we're actually the parent that was
			// called above at LABEL 1).
			JSplitPane wrapper = (JSplitPane)docked;
			Component left = wrapper.getLeftComponent();
			Component right = wrapper.getRightComponent();
			
			// first, check to make sure we do, in fact, have 2 components.  if so, then we don't ahve 
			// to go any further.
			if(left!=null && right!=null)
				return;

			// check to see if we have zero components.  if so, remove everything and return.
			if(left==right) {
				removeAll();
				return;
			}
			
			// if we got here, then one of our components has been removed (i.e. LABEL 1).  In this 
			// case, we want to pull the remaining component out of its split-wrapper and add it 
			// as a direct child to ourselves.
			Component comp = left==null? right: left;
			wrapper.remove(comp);
			super.remove(wrapper);
			
			if(comp instanceof DefaultDockingPort) 
				comp = ((DefaultDockingPort)comp).getDockedComponent();
			
			if(comp!=null)
				setComponent(comp);
		}
	}
	
	private void reevaluateTabbedPane() {
		Component docked = getDockedComponent();
		if(!(docked instanceof JTabbedPane))
			return;
		
		JTabbedPane tabs = (JTabbedPane)docked;
		int componentCount = tabs.getComponentCount();
		// we don't have to do anything special here if there is more than the
		// minimum number of allowable tabs
		int minTabs = singleTabsAllowed? 0: 1;
		if(componentCount>minTabs) {
			return;
		}
			
		// otherwise, pull out the component in the remaining tab (if it exists), and 
		// add it to ourselves as a direct child (ditching the JTabbedPane).
		Component comp = componentCount==1? tabs.getComponent(0): null;
		removeAll();
		if(comp!=null)
			setComponent(comp);
		
			

		Container parent = getParent();
		Container grandParent = parent==null? null: parent.getParent();
		// if our TabbedPane's last component was removed, then the TabbedPane itself has now been removed.
		// if we're a child port within a JSplitPane within another DockingPort, then we ourselved need to be
		// removed from the component tree, since we don't have any content.		
		if(comp==null && parent instanceof JSplitPane && grandParent instanceof DefaultDockingPort) {
			parent.remove(this);
			((DefaultDockingPort)grandParent).reevaluateContainerTree();
		}
	}
	

	/**
	 * Overridden to decorate superclass method, keeping track of internal docked-component reference.
	 */
	public void remove(int index) {
		Component docked = getDockedComponent();
		Component comp = getComponent(index);
		super.remove(index);
		if(docked==comp)
			dockedComponent = null;
	}

	/**
	 * Overridden to decorate superclass method, keeping track of internal docked-component reference.
	 */
	public void removeAll() {
		super.removeAll();
		dockedComponent = null;
	}
	
	/**
	 * @see org.flexdock.docking.DockingPort#retainDockedComponent()
	 */
	public void retainDockedComponent() {
		Component docked = getDockedComponent();
		if(docked==null)
			return;
			
		Container tempParent = docked.getParent();
		if(tempParent!=null)
			tempParent.remove(docked);
			
		setComponent(docked);
		revalidate();
	}

	/**
	 * Allows customization of docking behavior by allowing developers to plug in a customized
	 * <code>SubComponentProvider</code> instance.  If <code>provider</code> is null, then the 
	 * <code>DefaultDockingPort</code> will display default behavior during docking operations.
	 * 
	 * @param provider the supplied <code>SubComponentProvider</code> that will manage behaviors
	 * during docking operations.
	 */
	public void setComponentProvider(SubComponentProvider provider) {
		subComponentProvider = provider;
	}

	/**
	 * Allows customization of border managment following docking and undocking operations.  Any call to 
	 * <code>dock()</code> or a call to <code>reevaluateComponentTree()</code> resulting in a change of 
	 * internal component configuration will end with a check against the assigned <code>BorderManager</code>.
	 * If none exists, then no action is taken.  Otherwise, one of the four methods on the assigned 
	 * <code>BorderManager</code> will be invoked.
	 * 
	 * @param mgr the <code>BorderManager</code> assigned to to manage docked component borders.
	 */	
	public void setBorderManager(BorderManager mgr) {
		borderManager = mgr;
	}
	
	private Component setComponent(Component c) {
		if(getDockedComponent()!=null)
			removeAll();
			
		dockedComponent = c;
		Component ret = super.add(dockedComponent);
		return ret;
	}
	
	/**
	 * Overridden to do nothing.
	 */
	public void setLayout(LayoutManager mgr) {
	}
	
	private void setPreferredSize(DockingPort port, Dimension pref) {
		if(port instanceof JComponent)
			((JComponent)port).setPreferredSize(pref);
	}
	
	/**
	 * Undocks the specified <code>Component</code> and returns a boolean indicating the success of 
	 * the operation. If successful, <code>evaluateDockingBorderStatus()</code> will be called before 
	 * returning <code>true</code> to allow the assigned <code>SubComponentProvider</code> to handle 
	 * container-state-related behavior.
	 * 
	 * @param comp the <code>Component</code> to be undocked.
	 * @return a boolean indicating the success of the operation
	 * @see org.flexdock.docking.DockingPort#undock(Component comp)
	 */
	public boolean undock(Component comp) {
		// can't undock a component that isn't already docked within us
		if(!isParentDockingPort(comp))
			return false;
			
		// remove the component
		comp.getParent().remove(comp);
		
		// reevaluate the container tree.
		reevaluateContainerTree();
		
		return true;
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
	}
	
	public DockingPortProps getDockingProperties() {
		return PropertyManager.getDockingPortProps(this);
	}
	
	private static int getInitialTabPlacement() {
		String position = System.getProperty(INITIAL_TAB_POSITION);
		int pos = Utilities.getInt(position, JTabbedPane.BOTTOM);
		return pos<JTabbedPane.TOP || pos>JTabbedPane.RIGHT? JTabbedPane.BOTTOM: pos;
	}
	/**
	 * Default implementation of the SubComponentProvider interface.
	 */
	protected static class DefaultComponentProvider implements SubComponentProvider {
	
		/**
		 * @see org.flexdock.docking.SubComponentProvider#createChildPort()
		 */
		public DockingPort createChildPort() {
			return new DefaultDockingPort() {
				public String toString() {
					return "Child Port";
				}
			};
		}
	
		/**
		 * @see org.flexdock.docking.SubComponentProvider#createSplitPane()
		 */
		public JSplitPane createSplitPane(String region) {
			return new JSplitPane();
		}
	
		/**
		 * @see org.flexdock.docking.SubComponentProvider#createTabbedPane()
		 */
		public JTabbedPane createTabbedPane() {
			JTabbedPane pane = new JTabbedPane();
			pane.setTabPlacement(getInitialTabPlacement());
			return pane;
		}
		
		/**
		 * @see org.flexdock.docking.defaults.SubComponentProvider#getInitialDividerLocation()
		 */
		public double getInitialDividerLocation(JSplitPane splitPane, Component controller) {
			return 0.5d;
		}
	}
	
	private class TabListener extends MouseAdapter implements MouseMotionListener {
		private Dockable dockable;
		
		public void mouseMoved(MouseEvent me) {
			// does nothing
		}
		
		public void mouseDragged(MouseEvent me) {
			redispatchToDockable(me);
		}
		
		public void mouseReleased(MouseEvent me) {
			redispatchToDockable(me);
			dockable = null;
		}
		
		public void mousePressed(MouseEvent me) {
			if(!(me.getSource() instanceof JTabbedPane)) {
				dockable = null;
				return;
			}
			
			JTabbedPane pane = (JTabbedPane)me.getSource();
			Point p = me.getPoint();
			int tabIndex = pane.indexAtLocation(p.x, p.y);
			if(tabIndex==-1) {
				dockable = null;
				return;
			}

			dockable = DockingManager.getRegisteredDockable(pane.getComponentAt(tabIndex));
			redispatchToDockable(me);
		}
		
		private void redispatchToDockable(MouseEvent me) {
			if(!tabbedDragSource || dockable==null)
				return;
				
			Component dragSrc = dockable.getInitiator();
			MouseEvent evt = SwingUtilities.convertMouseEvent((Component)me.getSource(), me, dragSrc);
			dragSrc.dispatchEvent(evt);
		}
	}




}
