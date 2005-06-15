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
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.DockingStrategy;
import org.flexdock.docking.RegionChecker;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.docking.event.TabbedDragListener;
import org.flexdock.docking.event.hierarchy.DockingPortTracker;
import org.flexdock.docking.props.DockingPortProps;
import org.flexdock.docking.props.PropertyManager;
import org.flexdock.docking.state.LayoutNode;
import org.flexdock.docking.state.tree.DockableNode;
import org.flexdock.docking.state.tree.DockingPortNode;
import org.flexdock.docking.state.tree.SplitNode;
import org.flexdock.util.SwingUtility;


/**
 * A <code>Container</code> that implements the <code>DockingPort</code> interface.  This class provides
 * a default implementation of <code>DockingPort</code> to allow ease of development within docking-enabled
 * applications.  
 * <p>
 * The <code>DefaultDockingPort</code> handles docking in one of three ways.  If the port is empty, then
 * all incoming <code>Dockables</code> are docked to the CENTER region.  If the port is not empty, then
 * all incoming <code>Dockables</code> docked to the CENTER region are embedded within a 
 * <code>JTabbedPane</code>.  All incoming <code>Dockables</code> docked to an outer region (NORTH, 
 * SOUTH, EAST, and WEST) of a non-empty port are placed into a split layout using a 
 * <code>JSplitPane</code>. 
 * <p>
 * For centrally docked <code>Components</code>, the immediate child of the 
 * <code>DefaultDockingPort</code> may or may not be a <code>JTabbedPane</code>.  If 
 * <code>isSingleTabAllowed()</code> returns <code>true</code> for the current 
 * <code>DefaultDockingPort</code>, then the immediate child returned by <code>getDockedComponent()</code>
 * will return a <code>JTabbedPane</code> instance even if there is only one <code>Dockable</code>
 * embedded within the port.  If there is a single <code>Dockable</code> in the port, but
 * <code>isSingleTabAllowed()</code> returns <code>false</code>, then 
 * <code>getDockedComponent()</code> will return the <code>Component</code> that backs the
 * currently docked <code>Dockable</code>, returned by the <code>Dockable's</code>
 * <code>getComponent()</code> method.  <code>isSingleTabAllowed()</code> is a scoped property
 * that may apply to this port, all ports across the JVM, or all ports within a user defined scope.
 * <code>getDockedComponent()</code> will return a <code>JTabbedPane</code> at all times if there
 * is more than one centrally docked <code>Dockable</code> within the port, and all docked
 * <code>Components</code> will reside within the tabbed pane.
 * <p>
 * Components that are docked in the NORTH, SOUTH, EAST, or WEST regions are placed in a 
 * <code>JSplitPane</code> splitting the layout of the <code>DockingPort</code> between child components.  
 * Each region of the <code>JSplitPane</code> contains a new <code>DefaultDockingPort</code>, 
 * which, in turn, contains the docked components.  In this situation, <code>getDockedComponent()</code>
 * will return a <code>JSplitPane</code> reference.  
 * <p>
 * A key concept that drives the <code>DefaultDockingPort</code>, then, is the notion that this 
 * <code>DockingPort</code> implementation may only ever have one single child component, which may 
 * or may not be a wrapper for other child components.  Because <code>JSplitPane</code> contains 
 * child <code>DefaultDockingPorts</code>, each of those <code>DefaultDockingPorts</code> is available 
 * for further sub-docking operations.
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
 * When the <code>DockingManager</code> removes a component from a <code>DockingPort</code> via
 * <code>DockingManager.undock(Dockable dockable)</code> it uses a call to <code>undock()</code> on the 
 * current <code>DockingPort</code>.  <code>undock()</code> automatically handles the reevaluation of 
 * the container hierarchy to keep wrapper-container usage at a minimum.  Since 
 * <code>DockingManager</code> makes this callback automatic, developers normally will not
 * need to call this method explicitly.  However, when removing a component from a 
 * <code>DefaultDockingPort</code> using application code, developers should keep in mind to use 
 * <code>undock()</code> instead of <code>remove()</code>.
 * 
 * Border management after docking and undocking operations are accomplished using a 
 * <code>BorderManager</code>.  <code>setBorderManager()</code> may be used to set the border manager
 * instance and customize border management. 
 * 
 * @author Christopher Butler
 *
 */
public class DefaultDockingPort extends JPanel implements DockingPort, DockingConstants {
	private static final WeakHashMap COMPONENT_TITLES = new WeakHashMap();
	
	protected ArrayList dockingListeners;
	private int borrowedTabIndex;
	private Component dockedComponent;
	private BorderManager borderManager;
	private String persistentId;
	private boolean tabsAsDragSource;
	private boolean rootPort;
	
	private BufferedImage dragImage;

	
	/**
	 * Creates a new <code>DefaultDockingPort</code> with a persistent ID equal to the 
	 * <code>String</code> value of this <code>Object's</code> hash code.
	 */
	public DefaultDockingPort() {
		this(null);
	}
	
	/**
	 * Creates a new <code>DefaultDockingPort</code> with the specified persistent ID.
	 * If <code>id</code> is <code>null</code>, then the <code>String</code> value of 
	 * this <code>Object's</code> hash code is used.  The persistent ID will be the 
	 * same value returned by invoking <code>getPersistentId()</code> for this 
	 * <code>DefaultDockingPort</code>. 
	 * 
	 * @param id the persistent ID for the new <code>DefaultDockingPort</code> instance.
	 */
	public DefaultDockingPort(String id) {
		setPersistentId(id);
		dockingListeners = new ArrayList(2);
		addDockingListener(this);
		
		DockingPortProps props = getDockingProperties();
		props.setRegionChecker(new DefaultRegionChecker());
		
		// check container hierarchy to track root dockingports
		addHierarchyListener(DockingPortTracker.getInstance());
		
		// start out as a root dockingport
		rootPort = true;
	}

	/**
	 * Overridden to set the currently docked component.  Should not be called by application code.
	 * 
	 * @param comp the component to be added
	 */
	public Component add(Component comp) {
		return setComponent(comp);
	}
	
	/**
	 * Overridden to set the currently docked component.  Should not be called by application code.
	 * 
	 * @param comp the component to be added
	 * @param index the position at which to insert the component, 
     * or <code>-1</code> to append the component to the end
	 */
	public Component add(Component comp, int index) {
		return setComponent(comp);
	}

	/**
	 * Overridden to set the currently docked component.  Should not be called by application code.
	 * 
     * @param comp the component to be added
     * @param  constraints an object expressing layout contraints for this component
	 */
	public void add(Component comp, Object constraints) {
		setComponent(comp);
	}

	/**
	 * Overridden to set the currently docked component.  Should not be called by application code.
	 * 
     * @param comp the component to be added
     * @param constraints an object expressing layout contraints for this
     * @param index the position in the container's list at which to insert
     * the component; <code>-1</code> means insert at the end
	 */
	public void add(Component comp, Object constraints, int index) {
		setComponent(comp);
	}

	/**
	 * Overridden to set the currently docked component.  Should not be called by application code.
	 * 
	 * @param name the name of the <code>Component</code> to be added.
	 * @param comp the <code>Component</code> to add.
	 */
	public Component add(String name, Component comp) {
		return setComponent(comp);
	}
	
	private void addCmp(DockingPort port, Component c) {
		if(port instanceof Container)
			((Container)port).add(c);
	}

	private void dockCmp(DockingPort port, Component c, String desc) {
		port.dock(c, desc, CENTER_REGION);
	}	

	
	/**
	 * Returns <code>true</code> if docking is allowed for the specified <code>Component</code> 
	 * within the supplied <code>region</code>, <code>false</code> otherwise.  It is important
	 * to note that success of a docking operation relies on many factors and a return value of 
	 * <code>true</code> from this method does not necessarily guarantee that a call to 
	 * <code>dock()</code> will succeed.  This method merely indicates that the current  
	 * <code>DockingPort</code> does not have any outstanding reason to block a docking operation
	 * with respect to the specified <code>Component</code> and <code>region</code>.
	 * <br/>
	 * If <code>comp</code> is <code>null</code> or <code>region</code> is 
	 * invalid according to <code>DockingManager.isValidDockingRegion(String region)</code>, then
	 * this method returns <code>false</code>.
	 * <br/>
	 * If this <code>DockingPort</code> is not already the parent <code>DockingPort</code>
	 * for the specified <code>Component</code>, then this method returns <code>true</code>.
	 * <br/>
	 * If this <code>DockingPort</code> is already the parent <code>DockingPort</code> for the
	 * specified <code>Component</code>, then a check is performed to see if there is a tabbed
	 * layout.  Tabbed layouts may contain multiple <code>Dockables</code>, and thus the tab 
	 * ordering may be rearranged, or shifted into a split layout.  If <code>comp</code> is 
	 * the only docked <code>Component</code> within this <code>DockingPort</code>, then this 
	 * method returns <code>false</code> since the layout cannot be rearranged.  Otherwise, this
	 * method returns <code>true</code>.
	 * 
	 * @param comp the <code>Component</code> whose docking availability is to be checked
	 * @param region the region to be checked for docking availability for the specified 
	 * <code>Component</code>.
	 * @return <code>true</code> if docking is allowed for the specified <code>Component</code> 
	 * within the supplied <code>region</code>, <code>false</code> otherwise.
	 * @see DockingPort#isDockingAllowed(Component, String)
	 * @see DockingManager#isValidDockingRegion(String)
	 * @see #isParentDockingPort(Component) 
	 */
	public boolean isDockingAllowed(Component comp, String region) {
		if(comp==null || !isValidDockingRegion(region))
			return false;
		
		// allow any valid region if we're not already the parent
		// of the component we're checking
		if(!isParentDockingPort(comp))
			return true;

		// we already contain 'comp', so we're either a tabbed-layout, or 
		// we contain 'comp' directly.  If we contain 'comp' directly, then we 
		// cannot logically move 'comp' to some other region within us, as it
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
		
		// there is more than 1 tab present, so re-ordering is possible, 
		// as well as changing regions
		return true;
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
	private final void evaluateDockingBorderStatus() {
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

	
	public String getRegion(Point p) {
		if(p==null)
			return UNKNOWN_REGION;

		RegionChecker regionChecker = getDockingProperties().getRegionChecker();
		Dockable d = getDockableAt(p);
		Component regionTest = this;
		
		if(d!=null) {
			regionTest = d.getComponent();
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
			return c instanceof Dockable? (Dockable)c: DockingManager.getDockable(c);
		}

		return DockingManager.getDockable(docked);
	}
	
	public Component getComponent(String region) {
		Component docked = getDockedComponent();
	
		if(docked instanceof JTabbedPane) {
			// they can only get tabbed dockables if they were checking the CENTER region.
			if(!CENTER_REGION.equals(region))
				return null;
			
			JTabbedPane tabs = (JTabbedPane)docked;
			return tabs.getSelectedComponent();
		}
		
		if(docked instanceof JSplitPane) {
			// they can only get split dockables if they were checking an outer region.
			if(CENTER_REGION.equals(region))
				return null;

			JSplitPane split = (JSplitPane)docked;
			
			// make sure the supplied regions correspond to the current 
			// splitpane orientation
			boolean horizontal = split.getOrientation()==JSplitPane.HORIZONTAL_SPLIT;
			if(horizontal) {
				if(NORTH_REGION.equals(region) || SOUTH_REGION.equals(region))
					return null;
			}
			else {
				if(EAST_REGION.equals(region) || WEST_REGION.equals(region))
					return null;				
			}
			
			boolean left = NORTH_REGION.equals(region) || WEST_REGION.equals(region);
			Component c = left? split.getLeftComponent(): split.getRightComponent();
			// split panes only contain sub-dockingports.  if 'c' is not a sub-dockingport, 
			// then something is really screwed up.
			if(!(c instanceof DockingPort))
				return null;
			
			// get the dockable contained in the sub-dockingport
			return ((DockingPort)c).getDockedComponent();
		}
		
		// we already checked the tabbed layout and split layout.  all that's left is
		// the direct-child component itself.  this will only ever exist in the CENTER, 
		// so return it if they requested the CENTER region.
		return CENTER_REGION.equals(region)? docked: null;
	}
	
	public Dockable getDockable(String region) {
		Component c = getComponent(region);
		return DockingManager.getDockable(c);
	}

	
	protected JTabbedPane createTabbedPane() {
		int tabPlacement = getInitTabPlacement();
		Insets insets = new Insets(0, 0, 0, 0);
		switch(tabPlacement) {
			case JTabbedPane.TOP:
				insets.top = 1;
				break;
			case JTabbedPane.LEFT:
				insets.left = 1;
				break;
			case JTabbedPane.BOTTOM:
				insets.bottom = 1;
				break;
			case JTabbedPane.RIGHT:
				insets.right = 1;
				break;
		}
		
		Insets oldInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
		UIManager.put("TabbedPane.contentBorderInsets", insets); 
		JTabbedPane pane = new JTabbedPane();
		pane.setTabPlacement(tabPlacement);
		UIManager.put("TabbedPane.contentBorderInsets", oldInsets);

		TabbedDragListener tdl = new TabbedDragListener();
		pane.addMouseListener(tdl);
		pane.addMouseMotionListener(tdl);
		return pane;
	}
	
	public DockingStrategy getDockingStrategy() {
		return DockingManager.getDockingStrategy(this);
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
	 * <code>dock(Component comp, String desc, String region)</code>.  Passes as 
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
		
		String tabText = dockable.getDockingProperties().getDockableDesc();
		return dock(dockable.getComponent(), tabText, region);
	}

	/**
	 * Docks the specified component within the specified region.
	 * If the <code>DockingPort</code> is currently empty, then <code>comp</code> will be docked within 
	 * the CENTER region, regardless of the region that has been passed into this method.  If the 
	 * <code>DockingPort</code> is not empty, then docking in the CENTER region will place 
	 * <code>comp</code> in a JTabbedPane, using <code>desc</code> as the tab title.  All other regions 
	 * will result in a <code>JSplitPane</code> layout.  If <code>comp</code> 
	 * is <code>null</code> or <code>isDockingAllowed(String region)</code> returns <code>false</code>, 
	 * then this method will return <code>false</code> with no action taken. If docking is 
	 * successful, then this method returns <code>true</code>.
	 * 
	 * @see org.flexdock.docking.DockingPort#dock(Component comp, String desc, String region)
	 */
	public boolean dock(Component comp, String desc, String region) {
		if(comp==null || !isDockingAllowed(comp, region))
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
			region = CENTER_REGION;
		
		COMPONENT_TITLES.put(comp, desc);
		

		if(!isSingleTabAllowed() && docked==null) {
			setComponent(comp);
			evaluateDockingBorderStatus();
			return true;
		}
		
		boolean success = CENTER_REGION.equals(region)? 
				dockInCenterRegion(comp): dockInOuterRegion(comp, region, desc);
			
		if(success) {
			evaluateDockingBorderStatus();
			// if we docked in an outer region, then there is a new JSplitPane.  We'll 
			// want to divide it in half.  this is done after evaluateDockingBorderStatus(), 
			// so we'll know any border modification that took place has already happened, 
			// and we can be relatively safe about assumptions regarding our current 
			// insets.
			if(!CENTER_REGION.equals(region)) 
				resetSplitDividerLocation();
		}
		return success;
	}
	
	private void resetSplitDividerLocation() {
		Component c = getDockedComponent();
		if(c instanceof JSplitPane)
			deferSplitDividerReset((JSplitPane)c);
	}
	
	private void deferSplitDividerReset(final JSplitPane splitPane) {
		// we don't need to defer split divider location reset until after
		// a DockingSplitPane has rendered, since that class is able to figure out
		// its proper divider location by itself.
		if(splitPane instanceof DockingSplitPane)
			return;
		
		// check to see if we've rendered
		int size = SwingUtility.getSplitPaneSize(splitPane);
		if(splitPane.isVisible() && size>0 && EventQueue.isDispatchThread()) {
			// if so, apply the split divider location and return
			applySplitDividerLocation(splitPane);
			splitPane.validate();
			return;
		}
		
		// otherwise, defer applying the divider location reset until
		// the split pane is rendered.
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						deferSplitDividerReset(splitPane);
					}
				};
				EventQueue.invokeLater(r);
			}
		};
		t.start();
	}
	
	private void applySplitDividerLocation(JSplitPane splitPane) {
		DockingStrategy strategy = DockingManager.getDockingStrategy(this);
		int loc = strategy.getInitialDividerLocation(this, splitPane);
		splitPane.setDividerLocation(loc);
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
		DockingStrategy strategy = getDockingStrategy();
		DockingPort oldContent = strategy.createDockingPort(this);
		DockingPort newContent = strategy.createDockingPort(this);
		addCmp(oldContent, docked);
		dockCmp(newContent, comp, desc);
		
		// put the ports in the correct order and add them to a new wrapper panel
		DockingPort[] ports = putPortsInOrder(oldContent, newContent, region);
		setPreferredSize(ports[0], halfSize);
		setPreferredSize(ports[1], halfSize);
		JSplitPane newDockedContent = strategy.createSplitPane(this, region);

		if(ports[0] instanceof Component)
			newDockedContent.setLeftComponent((Component)ports[0]);
		if(ports[1] instanceof Component)
			newDockedContent.setRightComponent((Component)ports[1]);

		// now set the wrapper panel as the currently docked component
		setComponent(newDockedContent);
		// if we're currently showing, then we can exit now
		if(isShowing())
			return true;
		
		// otherwise, we have unrealized components whose sizes cannot be determined until
		// after we're visible.  cache the desired size values now for use later during rendering.
		double proportion = strategy.getDividerProportion(this, newDockedContent);
		SwingUtility.putClientProperty((Component)oldContent, DefaultDockingStrategy.PREFERRED_PROPORTION, new Float(proportion));
		SwingUtility.putClientProperty((Component)newContent, DefaultDockingStrategy.PREFERRED_PROPORTION, new Float(1f-proportion));
		
		return true;
	}

	/**
	 * Overridden to expand the docked component to fill the entire available region, minus insets.
	 * Since this method implements the layout directly, <code>setLayout(LayoutManager mgr)</code>
	 * has been obviated and has been overridden to do nothing.
	 * 
	 * @see Container#doLayout()
	 * @see #setLayout(LayoutManager)
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
	
	private JSplitPane getDockedSplitPane() {
		Component docked = getDockedComponent();
		return docked instanceof JSplitPane? (JSplitPane)docked: null;
	}
	
	/**
	 * Returns a <code>String</code> identifier that is unique to <code>DockingPorts</code>
	 * within a JVM instance, but persistent 
	 * across JVM instances.  This is used for configuration mangement, allowing the JVM to recognize
	 * a <code>DockingPort</code> instance within an application instance, persist the ID, and recall it
	 * in later application instances.  The ID should be unique within an appliation instance so that
	 * there are no collisions with other <code>DockingPort</code> instances, but it should also be 
	 * consistent from JVM to JVM so that the association between a <code>DockingPort</code> instance and
	 * its ID can be remembered from session to session.
	 * <br/>
	 * The value returned by this method will come from the most recent call to 
	 * <code>setPersistentId(String id)</code>.  If <code>setPersistentId(String id)</code> was invoked
	 * with a <code>null</code> argument, then the <code>String</code> verion of this 
	 * <code>DockingPort's</code> hash code is used.  Therefore, this method will never return a 
	 * <code>null</code> reference.
	 * 
	 * @return the persistent ID for this <code>DockingPort</code>
	 * @see DockingPort#getPersistentId()
	 * @see #setPersistentId(String)
	 * @see DockingManager#getDockingPort(String)
	 */
	public String getPersistentId() {
		return persistentId;
	}
	
	/**
	 * Sets the persisent ID to be used for this <code>DockingPort</code>.  If <code>id</code>
	 * is <code>null</code>, then the <code>String</code> value of this <code>DockingPort's</code>
	 * hash code is used.
	 * <br/>
	 * <code>DockingPorts</code> are tracked by persistent ID within <code>DockingManager</code>.
	 * Whenever this method is called, the <code>DockingManager's</code> tracking mechanism is 
	 * automatically upated for this <code>DockingPort</code>.
	 * 
	 * @param id the persistent ID to be used for this <code>DockingPort</code>
	 * @see #getPersistentId()
	 * @see DockingManager#getDockingPort(String)
	 * @see DockingPortTracker#updateIndex(DockingPort)
	 */
	public void setPersistentId(String id) {
		if(id==null) {
			id = String.valueOf(hashCode());
		}
		persistentId = id;
		DockingPortTracker.updateIndex(this);
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

	
	public boolean isSingleTabAllowed() {
		return getDockingProperties().isSingleTabsAllowed().booleanValue();
	}
	
	public void setSingleTabAllowed(boolean allowed) {
		getDockingProperties().setSingleTabsAllowed(allowed);
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
			
		// if the component is inside a tabbed or split pane, check to see of the tab/split
		// is embedded within us.  if not, then 'comp' is not a child docked within us.
		if(parent instanceof JTabbedPane || parent instanceof JSplitPane) {
			Container grandParent = parent.getParent();
			if(grandParent!=this)
				return false;
		}
		else {
			// not a tabbed or split child, so make sure 'comp' is a direct child of 'this'
			if(parent!=this)
				return false;
		}

		// we passed all the 'false' checks, so 'comp' is in fact docked within us
		return true;
	}

	protected boolean isValidDockingRegion(String region) {
		return DockingManager.isValidDockingRegion(region); 
	}

	private boolean isSingleComponentDocked() {
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
		return DockingManager.getDockable(c);
	}

	private DockingPort[] putPortsInOrder(DockingPort oldPort, DockingPort newPort, String region) {
		if(NORTH_REGION.equals(region) || WEST_REGION.equals(region))
			return new DockingPort[] {newPort, oldPort};
		return new DockingPort[] {oldPort, newPort};
	}

	/**
	 * This method completes with a call to <code>evaluateDockingBorderStatus()</code> to allow any
	 * installed <code>DefaultDockingStrategy</code> to handle container-state-related behavior.
	 */
	private void reevaluateContainerTree() {
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
			
			// first, check to make sure we do in fact have 2 components.  if so, then we don't have 
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
			
			// do some cleanup on the wrapper before removing it
			if(wrapper instanceof DockingSplitPane) {
				((DockingSplitPane)wrapper).cleanup();
			}
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
		int minTabs = isSingleTabAllowed()? 0: 1;
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
	 * 
	 * @param the index of the component to be removed.
	 * @see Container#remove(int)
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
	 * @see Container#removeAll()
	 */
	public void removeAll() {
		super.removeAll();
		dockedComponent = null;
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
	
	public BorderManager getBorderManager() {
		return borderManager;
	}
	
	private Component setComponent(Component c) {
		if(getDockedComponent()!=null)
			removeAll();
			
		dockedComponent = c;
		Component ret = super.add(dockedComponent);
		return ret;
	}
	
	/**
	 * Overridden to do nothing.  <code>doLayout()</code> has been overridden in this class to 
	 * implement the <code>DockingPort</code> layout directly, so this method has been obviated
	 * on this class.
	 * 
	 * @param mgr the specified layout manager to set
	 * @see #doLayout()
	 * @see Container#setLayout(java.awt.LayoutManager)
	 */
	public void setLayout(LayoutManager mgr) {
	}
	
	private void setPreferredSize(DockingPort port, Dimension pref) {
		if(port instanceof JComponent)
			((JComponent)port).setPreferredSize(pref);
	}
	
	/**
	 * Undocks the specified <code>Component</code> and returns a boolean indicating the success of 
	 * the operation. 
	 * <br/>
	 * Since <code>DefaultDockingPort</code> may only contain one child component, there i
	 * s a container hierarchy to manage tabbed interfaces, split layouts, and sub-docking.  As 
	 * components are removed from this hierarchy, the hierarchy itself must be reevaluated.  
	 * Removing a component from a child code>DefaultDockingPort</code> within a <code>JSplitPane</code> 
	 * renders the child <code>DefaultDockingPort</code> unnecessary, which, in turn, renders the notion 
	 * of splitting the layout with a <code>JSplitPane</code> unnecessary (since there are no longer two 
	 * components to split the layout between).  Likewise, removing a child component from a 
	 * <code>JTabbedPane</code> such that there is only one child left within the 
	 * <code>JTabbedPane</code> removes the need for a tabbed interface to begin with.
	 * <p>
	 * This method automatically handles the reevaluation of the container hierarchy to keep 
	 * wrapper-container usage at a minimum.  Since <code>DockingManager</code> makes this callback 
	 * automatic, developers normally will not need to call this method explicitly.  However, when 
	 * removing a component from a  <code>DefaultDockingPort</code> using application code, 
	 * developers should keep in mind to use this method instead of <code>remove()</code>.
	 * 
	 * @param comp the <code>Component</code> to be undocked.
	 * @return a boolean indicating the success of the operation
	 * @see DockingPort#undock(Component comp)
	 * @see DockingManager#undock(Dockable)
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
	
	
	
	
	
	
	public Set getDockables() {
    	// return ALL dockables, recursing to maximum depth
    	return getDockableSet(-1, 0, null);
	}
	
	
	
    protected Set getDockableSet(int depth, int level, Class desiredClass) {
        Component c = getDockedComponent();
        
        if(c instanceof JTabbedPane) {
            JTabbedPane tabs = (JTabbedPane)c;
            int len = tabs.getTabCount();
            HashSet set = new HashSet(len);
            for(int i=0; i<len; i++) {
                c = tabs.getComponentAt(i);
                if(isValidDockableChild(c, desiredClass)) {
                	if(c instanceof Dockable)
                		set.add(c);
                	else
                		set.add(DockingManager.getDockable(c));
                }
            }
            return set;
        }

        HashSet set = new HashSet(1);
        
        // if we have a split-layout, then we need to decide whether to get the child
        // viewSets.  If 'depth' is less then zero, then it's implied we want to recurse
        // to get ALL child viewsets no matter how deep.  If 'depth' is greater than or 
        // equal to zero, we only want to go as deep as the specified depth.
        if (c instanceof JSplitPane && (depth<0 || level <= depth)) {
            JSplitPane pane = (JSplitPane) c;
            Component sub1 = pane.getLeftComponent();
            Component sub2 = pane.getRightComponent();

            if(sub1 instanceof DefaultDockingPort)
            	set.addAll(((DefaultDockingPort)sub1).getDockableSet(depth, level+1, desiredClass));
            
            if(sub2 instanceof DefaultDockingPort)
            	set.addAll(((DefaultDockingPort)sub2).getDockableSet(depth, level+1, desiredClass));
        }
       
        if(isValidDockableChild(c, desiredClass)) {
        	if(c instanceof Dockable)
        		set.add(c);
        	else
        		set.add(DockingManager.getDockable(c));
        }
        return set;
    }
    
    protected boolean isValidDockableChild(Component c, Class desiredClass) {
    	return desiredClass==null? DockingManager.getDockable(c)!=null:
			desiredClass.isAssignableFrom(c.getClass());
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
	
	/**
	 * @see org.flexdock.docking.event.DockingListener#undockingComplete(org.flexdock.docking.event.DockingEvent)
	 */
	public void undockingComplete(DockingEvent evt) {
	}
	
	public void undockingStarted(DockingEvent evt) {
	}
	
	public DockingPortProps getDockingProperties() {
		return PropertyManager.getDockingPortProps(this);
	}

	public void setTabsAsDragSource(boolean b) {
		tabsAsDragSource = b;
	}
	
	public boolean isTabsAsDragSource() {
		return tabsAsDragSource;
	}
	
	protected int getInitTabPlacement() {
		return getDockingProperties().getTabPlacement().intValue();
	}

	public boolean isRoot() {
		return rootPort;
	}
	public void setRoot(boolean root) {
		this.rootPort = root;
	}
	
	public void setDragInProgress(boolean inProgress) {
		if(inProgress && dragImage!=null)
			return;
		
		if(!inProgress && dragImage==null)
			return;
		
		if(inProgress) {
			dragImage = SwingUtility.createImage(getDockedComponent());
		}
		else {
			dragImage = null;
		}
		repaint();
	}
	
	/**
	 * Overridden to provide enhancements during drag operations.  Some <code>DragPreview</code>
	 * implementations may by able to supply a <code>BufferedImage</code> for this 
	 * <code>DockingPort</code> to use for painting operations.  This may be useful for cases in which
	 * the dimensions of docked <code>Components</code> are altered in realtime during the drag
	 * operation to provide a "ghost" image for the <code>DragPreview</code>.  In this case, 
	 * visual feedback for altered subcomponents within this <code>DockingPort</code> may be blocked
	 * in favor of a temporary <code>BufferedImage</code> for the life of the drag operation.
	 * 
	 * @param g  the <code>Graphics</code> context in which to paint
	 * @see JComponent#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		if(dragImage==null) {
			super.paint(g);
			return;
		}
		
		g.drawImage(dragImage, 0, 0, this);
	}
	
	
	
	
	
	
	
	
	public LayoutNode exportLayout() {
		return DockingManager.getLayoutManager().createLayout(this);
	}	
	
	public void importLayout(LayoutNode node) {
		if(!(node instanceof DockingPortNode))
			return;
		
		node.setUserObject(this);
		ArrayList splitPaneResizeList = new ArrayList();
		constructLayout(node, splitPaneResizeList);
		deferSplitPaneResize(splitPaneResizeList, 0);
		revalidate();
	}
	
	private void constructLayout(LayoutNode node, ArrayList splitPaneResizeList) {
		// load the user object;
		Object obj = node.getUserObject();
		if(node instanceof SplitNode)
			splitPaneResizeList.add(node);
		
		for(Enumeration en=node.children(); en.hasMoreElements();) {
			LayoutNode child = (LayoutNode)en.nextElement();
			constructLayout(child, splitPaneResizeList);
		}
		
		if(node instanceof SplitNode)
			reconstruct((SplitNode)node);
		else if(node instanceof DockingPortNode)
			reconstruct((DockingPortNode)node);
	}
	
	private void reconstruct(DockingPortNode node) {
		DefaultDockingPort port = (DefaultDockingPort)node.getDockingPort();
		
		if(node.isSplit()) {
			SplitNode child = (SplitNode)node.getChildAt(0);
			JSplitPane split = child.getSplitPane();
			float percentage = child.getPercentage();
			port.setComponent(split);
			return;
		}
		
		for(Enumeration en=node.children(); en.hasMoreElements();) {
			LayoutNode child = (LayoutNode)en.nextElement();
			if(child instanceof DockableNode) {
				Dockable dockable = ((DockableNode)child).getDockable();
				port.dock(dockable, CENTER_REGION);
			}
		}
	}
	
	private void reconstruct(SplitNode node) {
		JSplitPane split = node.getSplitPane();
		Component left = node.getLeftComponent();
		Component right = node.getRightComponent();
		split.setLeftComponent(left);
		split.setRightComponent(right);
	}
	
	private void deferSplitPaneResize(final ArrayList splitNodes, final int startIndx) {
		Thread t = new Thread() {
			public void run() {
				Runnable r = new Runnable() {
					public void run() {
						int len = splitNodes.size();
						for(int i=startIndx; i<len; i++) {
							SplitNode node = (SplitNode)splitNodes.get(i);
							JSplitPane split = node.getSplitPane();
							int size = split.getOrientation()==JSplitPane.HORIZONTAL_SPLIT? split.getWidth(): split.getHeight();
							float percent = node.getPercentage();
							int divLoc = (int)((float)size * percent);
							split.setDividerLocation(divLoc);
							split.validate();
						}
					}
				};
				EventQueue.invokeLater(r);
			}
		};
		t.start();
	}

	

}
