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

import java.awt.Cursor;

/**
 * This interface provides a means of producing various mouse Images during docking operations 
 * with respect to the docking region underneath the mouse. 
 * 
 * @author Chris Butler
 */
public interface CursorProvider {
	
	/**
	 * Returns a Image for the center docking region
	 * @return a Image instance
	 */
	public Cursor getCenterCursor();
	
	/**
	 * Returns a Cursor indicating that docking is now allowed at the current mouse location
	 * @return a Cursor instance
	 */
	public Cursor getDisallowedCursor();
	
	/**
	 * Returns a Cursor for the eastern docking region
	 * @return a Cursor instance
	 */
	public Cursor getEastCursor();

	
	/**
	 * Returns a Cursor for the northern docking region
	 * @return a Cursor instance
	 */
	public Cursor getNorthCursor();
	
	/**
	 * Returns a Cursor for the southern docking region
	 * @return a Cursor instance
	 */
	public Cursor getSouthCursor();
	
	/**
	 * Returns a Cursor for the western docking region
	 * @return a Cursor instance
	 */
	public Cursor getWestCursor();
}
