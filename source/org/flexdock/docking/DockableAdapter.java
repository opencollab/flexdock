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
package org.flexdock.docking;

import java.awt.Component;

/**
 * Provides a default implementation of the <code>Dockable</code> interface.  This class may be extended
 * in any application that wishes to make use of the <code>Dockable</code> interface without
 * the need for writing out an implementation for every method that isn't explicitly used.
 * 
 * @author Chris Butler
 */
public class DockableAdapter implements Dockable {
	private String persistentId;
	
	public DockableAdapter() {
		this(null);
	}
	
	public DockableAdapter(String id) {
		persistentId = id;
	}
	
	/**
	 * Does nothing.
	 * @see org.flexdock.docking.Dockable#dockingCanceled()
	 */
	public void dockingCanceled() {
	}

	/**
	 * Does nothing.
	 * @see org.flexdock.docking.Dockable#dockingCompleted()
	 */
	public void dockingCompleted() {
	}

	/**
	 * Returns null.
	 * @see org.flexdock.docking.Dockable#getCursorProvider()
	 */
	public CursorProvider getCursorProvider() {
		return null;
	}

	/**
	 * Returns null.
	 * @see org.flexdock.docking.Dockable#getDockable()
	 */
	public Component getDockable() {
		return null;
	}

	/**
	 * Returns null.
	 * @see org.flexdock.docking.Dockable#getDockableDesc()
	 */
	public String getDockableDesc() {
		return null;
	}

	/**
	 * Returns null.
	 * @see org.flexdock.docking.Dockable#getInitiator()
	 */
	public Component getInitiator() {
		return null;
	}

	/**
	 * Returns true.
	 * @see org.flexdock.docking.Dockable#isDockedLayoutResizable()
	 */
	public boolean isDockedLayoutResizable() {
		return true;
	}

	/**
	 * Returns the <code>persistentId</code> value.
	 * @see org.flexdock.docking.Dockable#getPersistentId()
	 */
	public String getPersistentId() {
		return persistentId;
	}
	
	/**
	 * Returns true.
	 * @see org.flexdock.docking.Dockable#isDockingEnabled()
	 */
	public boolean isDockingEnabled() {
		return true;
	}

	/**
	 * Returns true.
	 * @see org.flexdock.docking.Dockable#mouseMotionListenersBlockedWhileDragging()
	 */
	public boolean mouseMotionListenersBlockedWhileDragging() {
		return true;
	}

	/**
	 * Does nothing.
	 * @see org.flexdock.docking.Dockable#setDockableDesc(java.lang.String)
	 */
	public void setDockableDesc(String desc) {
	}

	/**
	 * Does nothing.
	 * @see org.flexdock.docking.Dockable#setDockedLayoutResizable(boolean)
	 */
	public void setDockedLayoutResizable(boolean b) {
	}

	/**
	 * Does nothing.
	 * @see org.flexdock.docking.Dockable#setDockingEnabled(boolean)
	 */
	public void setDockingEnabled(boolean b) {
	}
}
