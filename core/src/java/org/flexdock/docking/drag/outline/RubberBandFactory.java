/*
 * Created on Aug 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.flexdock.docking.drag.outline;

import org.flexdock.docking.drag.outline.win32.Win32RubberBand;
import org.flexdock.docking.drag.outline.x11.X11RubberBand;
import org.flexdock.util.ResourceManager;

/**
 * @author cb8167
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RubberBandFactory {
	
	public static AbstractRubberBand getRubberBand() {
		AbstractRubberBand rb = null;
		try {
			if(ResourceManager.isWindowsPlatform())
				rb = Win32RubberBand.getInstance();
			else
				rb = X11RubberBand.getInstance();
		} catch(Throwable e) {
			rb = DefaultRubberBand.getInstance();
		}
		
		return rb==null? DefaultRubberBand.getInstance(): rb;
	}
}
