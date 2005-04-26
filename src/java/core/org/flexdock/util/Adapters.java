/*
 * Created on Apr 25, 2005
 */
package org.flexdock.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * @author Christopher Butler
 *
 */
public class Adapters {
	public static class MouseEventAdapter extends MouseAdapter implements MouseMotionListener {
		public void mouseMoved(MouseEvent me) {
		}
		public void mouseDragged(MouseEvent me) {
		}
	}
}
