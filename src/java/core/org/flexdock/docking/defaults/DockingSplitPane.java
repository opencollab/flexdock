/*
 * Created on Mar 24, 2005
 */
package org.flexdock.docking.defaults;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JSplitPane;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.util.DockingUtility;

/**
 * @author Christopher Butler
 */
public class DockingSplitPane extends JSplitPane implements DockingConstants {
	protected DockingPort dockingPort;
	protected String region;
	protected boolean dividerLocDetermined;
	protected boolean controllerInTopLeft;
	protected double initialDividerRatio = .5;
	
	/**
     * Creates a new <code>DockingSplitPane</code> for the specified
     * <code>DockingPort</code> with the understanding that the resulting
     * <code>DockingSplitPane</code> will be used for docking a
     * <code>Dockable</code> into the <code>DockingPort's</code> specified
     * <code>region</code>. Neither <code>port</code> or
     * <code>region</code> may be <code>null</code>. <code>region</code>
     * must be a valid docking region as defined by
     * <code>isValidDockingRegion(String region)</code>.
     * 
     * @param port
     *            the <code>DockingPort</code> for which this
     *            <code>DockingSplitPane</code> is to be created.
     * @param region
     *            the region within the specified <code>DockingPort</code> for
     *            which this {@code DockingSplitPane} is to be created.
     * @throws <code>IllegalArgumentException</code> if either <code>port</code>
     *             is {@code null} or </code>region</code> is <code>null</code>
     *             or invalid.
     * @see DockingManager#isValidDockingRegion(String)
     */
	public DockingSplitPane(DockingPort port, String region) {
		if(port==null)
			throw new IllegalArgumentException("'port' cannot be null.");
		if(!DockingManager.isValidDockingRegion(region))
			throw new IllegalArgumentException("'" + region + "' is not a valid region.");
		
		this.region = region;
		this.dockingPort = port;
		// the controlling item is in the topLeft if our new item (represented
		// by the "region" string) is NOT in the topLeft.
		controllerInTopLeft = !DockingUtility.isRegionTopLeft(region);
		
		// set the proper resize weight
		int weight = controllerInTopLeft? 1: 0;
		setResizeWeight(weight);
	}

    public void resetToPreferredSizes() {
        Insets i = getInsets();
        
        if (getOrientation() == VERTICAL_SPLIT) {
            int h = getHeight() - i.top - i.bottom - getDividerSize();
            int topH = getTopComponent().getPreferredSize().height;
            int bottomH =  getBottomComponent().getPreferredSize().height;
            int extraSpace = h - topH - bottomH;
            
            //we have more space than necessary; resize to give each at least preferred size
            if (extraSpace >= 0) {
                setDividerLocation(i.top + topH + ((int) (extraSpace * getResizeWeight() + .5)));
            }
            
            //TODO implement shrinking excess space to ensure that one has preferred and nothing more
        } else {
            int w = getWidth() - i.left - i.right - getDividerSize();
            int leftH = getLeftComponent().getPreferredSize().width;
            int rightH =  getRightComponent().getPreferredSize().width;
            int extraSpace = w - leftH - rightH;
            
            //we have more space than necessary; resize to give each at least preferred size
            if (extraSpace >= 0) {
                setDividerLocation(i.left + leftH + ((int) (extraSpace * getResizeWeight() + .5)));
            }

            //TODO implement shrinking excess space to ensure that one has preferred and nothing more
        }
    }
    
	protected boolean isDividerSizeProperlyDetermined() {
		if (getDividerLocation() != 0)
			return true;
		return dividerLocDetermined;
	}

	/**
	 * Returns the 'oldest' <code>Component<code> to have been added to this <code>DockingSplitPane</code>
	 * as a result of a docking operation.  A <code>DockingSplitPane</code> is created based upon the 
	 * need to share space within a <code>DockingPort</code> between two <code>Dockables</code>.  This 
	 * happens when a new <code>Dockable</code> is introduced into an outer region of a 
	 * <code>DockingPort</code> that already contains a <code>Dockable</code>.  The <code>Dockable</code>
	 * that was in the <code>DockingPort</code> prior to splitting the layout is the 'elder'
	 * <code>Component</code> and, in many circumstances, may be used to control initial divider 
	 * location and resize weight.
	 * <br/>
	 * If this split pane contains <code>DockingPorts</code> as its child components, then this method 
	 * will return the <code>Component</code> determined by calling <code>getDockedComponent()</code>
	 * for the <code>DockingPort</code> in this split pane's elder region.
	 * <br/>
	 * The elder region of this <code>DockingSplitPane</code> is determined using the value returned from
	 * <code>getRegion()</code>, where <code>getRegion()</code> indicates the docking region of the 
	 * 'new' <code>Dockable</code> for this <code>DockingSplitPane</code>. 
	 * 
	 * @return the 'oldest' <code>Component<code> to have been added to this <code>DockingSplitPane</code>
	 * as a result of a docking operation.
	 * @see #getRegion()
	 * @see DockingPort#getDockedComponent()
	 */
	public Component getElderComponent() {
		Component c = controllerInTopLeft ? getLeftComponent() : getRightComponent();
		if (c instanceof DockingPort)
			c = ((DockingPort) c).getDockedComponent();
		return c;
	}
	
	/**
	 * Returns the docking region for which this <code>DockingSplitPane</code> was created.  A 
	 * <code>DockingSplitPane</code> is created based upon the need to share space within a 
	 * <code>DockingPort</code> between two <code>Dockables</code>.  This happens when a new 
	 * <code>Dockable</code> is introduced into an outer region of a <code>DockingPort</code> that 
	 * already contains a <code>Dockable</code>.  This method returns that outer region for which
	 * this <code>DockingSplitPane</code> was created and may be used to control the orientation
	 * of the split pane.  The region returned by this method will be the same passed into the
	 * <code>DockingSplitPane</code> constructor on instantiation.
	 * 
	 * @return the docking region for which this <code>DockingSplitPane</code> was created.
	 * @see #DockingSplitPane(DockingPort, String)
	 */
	public String getRegion() {
		return region;
	}
	
	/**
	 * Indicates whether the 'oldest' <code>Component<code> to have been added to this <code>DockingSplitPane</code>
	 * as a result of a docking operation is in the TOP or LEFT side of the split pane.  
	 * A <code>DockingSplitPane</code> is created based upon the 
	 * need to share space within a <code>DockingPort</code> between two <code>Dockables</code>.  This 
	 * happens when a new <code>Dockable</code> is introduced into an outer region of a 
	 * <code>DockingPort</code> that already contains a <code>Dockable</code>.  The <code>Dockable</code>
	 * that was in the <code>DockingPort</code> prior to splitting the layout is the 'elder'
	 * <code>Component</code> and is returned by <code>getElderComponent()</code>.  This method 
	 * indicates whether or not that <code>Component</code> is in the TOP or LEFT side of this
	 * <code>DockingSplitPane</code>.
	 * <br/>
	 * The elder region of this <code>DockingSplitPane</code> is determined using the value returned from
	 * <code>getRegion()</code>, where <code>getRegion()</code> indicates the docking region of the 
	 * 'new' <code>Dockable</code> for this <code>DockingSplitPane</code>.
	 * 
	 * @return <code>true</code> if the 'oldest' <code>Component<code> to have been added to this 
	 * <code>DockingSplitPane</code> is in the TOP or LEFT side of the split pane; <code>false</code>
	 * otherwise.
	 * @see #getElderComponent()
	 * @see #getRegion()
	 */
	public boolean isElderTopLeft() {
		return controllerInTopLeft;
	}

	/**
	 * Overridden to ensure proper divider location on initial rendering.  Sometimes, a split 
	 * divider location is set as a proportion before the split pane itself has been fully
	 * realized in the container hierarchy.  This results in a layout calculation based on
	 * a proportion of zero width or height, rather than the desired proportion of width or height
	 * after the split pane has been fully rendered.  This method ensures that default 
	 * <code>JSplitPane</code> layout behavior is deferred until after the initial dimensions of 
	 * this split pane have been properly determined.
	 * 
	 * @see java.awt.Container#doLayout()
	 * @see JSplitPane#setDividerLocation(double)
	 */
	public void doLayout() {
		// if they setup the docking configuration while the application
		// was first starting up, then the dividerLocation was calculated before
		// the container tree was visible, sized, validated, etc, so it'll be
		// stuck at zero. in that case, redetermine the divider location now
		// that we have valid container bounds with which to work.
		if (!isDividerSizeProperlyDetermined()) {
			// make sure this can only run once so we don't get a StackOverflow
			dividerLocDetermined = true;
			setDividerLocation(initialDividerRatio);
		}
		// continue the layout
		super.doLayout();
	}
	
	/**
	 * Releases any internal references to external objects to aid garbage collection.  This method
	 * is <code>public</code> and may be invoked manually for proactive memory management.  Otherwise,
	 * this method is invoked by this <code>DockingSplitPane's</code> <code>finalize()</code> method.
	 */
	public void cleanup() {
		dockingPort = null;
	}

    /**
     * Sets the initial divider ration for creating split panes.  The default value is <code>0.5</code>.
     * @exception IllegalArgumentException if <code>ratio</code> is less than 0.0 or greater than 1.0.
     * @param ratio a ratio for determining weighting between the two sides of a split pane.
     */
    public void setInitialDividerRatio(double ratio) {
        if (ratio < 0.0 || ratio > 1.0) {
            throw new IllegalArgumentException("ratio (" + ratio
                    + ") must be between [0.0,1,0] inclusive");
        }
        initialDividerRatio = ratio;
    }
    
	protected void finalize() throws Throwable {
		cleanup();
		super.finalize();
	}
}
