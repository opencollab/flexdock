/*
 * Created on Apr 25, 2005
 */
package org.flexdock.util;

import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

/**
 * @author Christopher Butler
 */
public interface DockingConstants {
	public static final String PERMANENT_FOCUS_OWNER = "permanentFocusOwner";
	public static final String ACTIVE_WINDOW = "activeWindow";
	public static final String MOUSE_PRESSED = "mousePressed";
	
	public static final String PIN_ACTION = "pin";
	public static final String CLOSE_ACTION = "close";
	
	public static final int TOP = SwingConstants.TOP;
	public static final int LEFT = SwingConstants.LEFT;
	public static final int BOTTOM = SwingConstants.BOTTOM;
	public static final int RIGHT = SwingConstants.RIGHT;
	public static final int CENTER = SwingConstants.CENTER;
	
	public static final int HORIZONTAL = JSplitPane.HORIZONTAL_SPLIT;
	public static final int VERTICAL = JSplitPane.VERTICAL_SPLIT;
	
}
