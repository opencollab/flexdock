/*
 * Created on Apr 25, 2005
 */
package org.flexdock.docking;

import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

/**
 * @author Christopher Butler
 */
public interface DockingConstants {
    String DOCKING_ID = "docking-id";
    
	float UNSPECIFIED_SIBLING_PREF = -1F;
	int UNINITIALIZED = -1;
	float UNINITIALIZED_RATIO = -1F;
	
	String PERMANENT_FOCUS_OWNER = "permanentFocusOwner";
	String ACTIVE_WINDOW = "activeWindow";
	String MOUSE_PRESSED = "mousePressed";
	
	String PIN_ACTION = "pin";
	String CLOSE_ACTION = "close";
	
	int TOP = SwingConstants.TOP;
	int LEFT = SwingConstants.LEFT;
	int BOTTOM = SwingConstants.BOTTOM;
	int RIGHT = SwingConstants.RIGHT;
	int CENTER = SwingConstants.CENTER;
	
	int HORIZONTAL = JSplitPane.HORIZONTAL_SPLIT;
	int VERTICAL = JSplitPane.VERTICAL_SPLIT;
	
	String REGION = "region";
    String CENTER_REGION = "CENTER";
	String EAST_REGION = "EAST";
	String NORTH_REGION = "NORTH";
	String SOUTH_REGION = "SOUTH";
	String WEST_REGION = "WEST";
	String UNKNOWN_REGION = "UNKNOWN";
	
	String HEAVYWEIGHT_DOCKABLES = "heavyweight.dockables";
	String DEFAULT_PERSISTENCE_KEY = "default.persist.key";
	
}
