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
package org.flexdock.util;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * This class provides <code>static</code> convenience methods for resource management, including resource
 * lookups and image, icon, and cursor creation.
 * 
 * @author Chris Butler
 */
public class ResourceManager {
	public static final String LIBRARY_EXTENSION = getLibraryExtension();
	
	private static String getLibraryExtension() {
		return isWindowsPlatform()? ".dll": ".so";
	}
	
	public static boolean isWindowsPlatform() {
		String osName = System.getProperty("os.name").toLowerCase();
		return osName.indexOf("windows")!=-1 || osName.endsWith(" nt");
	}
	
	/**
	 * Performs resource lookups using the <code>ClassLoader</code> and classpath.  This method attemps 
	 * to consolidate several techniques used for resource lookup in different situations, providing a 
	 * common API that works the same from standalone applications to applets to multiple-classloader 
	 * container-managed applications.  Returns <code>null</code> if specified resource cannot be found.
	 * 
	 * @param uri the String describing the resource to be looked up
	 * @return a <code>URL</code> representing the resource that has been looked up.
	 */
	public static URL getResource(String uri) {
		URL url = ResourceManager.class.getResource(uri);
		if(url==null)
			url = ClassLoader.getSystemResource(uri);
			
		// if we still couldn't find the resource, then slash it and try again
		if(url==null && !uri.startsWith("/"))
			url = getResource("/" + uri);
			
		return url;
	}

	/**
	 * Returns an <code>Image</code> object based on the specified resource URL.  
	 * Does not perform any caching on the <code>Image</code> object, so a new object will be created 
	 * with each call to this method.
	 * 
	 * @param url the <code>String</code> describing the resource to be looked up
	 * @exception NullPointerException if specified resource cannot be found.
	 * @return an <code>Image</code> created from the specified resource URL
	 */
	public static Image createImage(String url) {
		try {
			URL location = getResource(url);
			return Toolkit.getDefaultToolkit().createImage(location);
		} catch(NullPointerException e) {
			throw new NullPointerException("Unable to locate image: " + url);
		}
	}
	
	/**
	 * Returns an <code>ImageIcon</code> object based on the specified resource URL.  
	 * Uses the <code>ImageIcon</code> constructor internally instead of dispatching to 
	 * <code>createImage(String url)</code>, so <code>Image</code> objects are cached via the 
	 * <code>MediaTracker</code>.
	 * 
	 * @param url the <code>String</code> describing the resource to be looked up
	 * @exception NullPointerException if specified resource cannot be found.
	 * @return an <code>ImageIcon</code> created from the specified resource URL
	 */
	public static ImageIcon createIcon(String url) {
		try {
			URL location = getResource(url);
			return new ImageIcon(location);
		} catch(NullPointerException e) {
			throw new NullPointerException("Unable to locate image: " + url);
		}
	}

	/**
	 * Returns a <code>Cursor</code> object based on the specified resource URL.  
	 * Throws a <code>NullPointerException</code> if specified resource cannot be found.
	 * Dispatches to <code>createImage(String url)</code>, so <code>Image</code> objects are 
	 * <b>not</b> cached via the<code>MediaTracker</code>.
	 * 
	 * @param url the <code>String</code> describing the resource to be looked up
     * @param hotPoint the X and Y of the large cursor's hot spot.  The
     * hotSpot values must be less than the Dimension returned by
     * getBestCursorSize().
	 * @param name a localized description of the cursor, for Java Accessibility use.
	 * @exception NullPointerException if specified resource cannot be found.
	 * @exception IndexOutOfBoundsException if the hotSpot values are outside
	 * @return a <code>Cursor</code> created from the specified resource URL
	 */
	public static Cursor createCursor(String url, Point hotPoint, String name) {
		Image image = createImage(url);
		Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(image, hotPoint, name);
		return c;
	}
	
	public static void loadLibrary(String library, String classpathResource) {
		try {
			System.loadLibrary(library);
			return;
		} catch(UnsatisfiedLinkError err) {
			// pass through here
		}

		// determine a file from which we can load our library.
		File file = new File(System.getProperty("user.home") + "/flexdock");
		file.mkdirs();
		file = new File(file.getAbsolutePath() + "/" + library + LIBRARY_EXTENSION);
		
		// if the file already exists, try to load from it
		if(file.exists()) {
			try {
				System.load(file.getAbsolutePath());
				return;
			} catch(UnsatisfiedLinkError err) {
				// pass through here
			}
		}
			
		// if the file didn't exist, or we couldn't load from it, 
		// we'll have to pull from the classpath resource and write it
		// to this file.  We'll then try to load from the file again.
		FileOutputStream fileOut = null;
	
		// get a handle to our resource in the classpath
		ClassLoader cl = ResourceManager.class.getClassLoader();
		InputStream in = cl.getResourceAsStream(classpathResource);
		if(in==null)
			throw new UnsatisfiedLinkError("Unable to locate classpath resource: " + classpathResource);
		
		
		try {
			// create an outputstream to our destination file
			fileOut = new FileOutputStream(file);
			
			byte[] tmp = new byte[1024];
			// copy the contents of our resource out to the destination
			// file 1K at a time.  1K may seem arbitrary at first, but today 
			// is a Tuesday, so it makes perfect sense. 
			int bytesRead = in.read(tmp);
			while(bytesRead!=-1) {
				fileOut.write(tmp, 0, bytesRead);
				bytesRead = in.read(tmp);
			}
		} catch(IOException giveUp) {
			// well, I guess we're screwed, aren't we?
			throw new UnsatisfiedLinkError("Unable to extract resource to file: " + file.getAbsolutePath());
		}
		finally {
			close(fileOut);
			close(in);
		}
		
		// now that our classpath resource has been written to disk, load the native
		// library from this file
		System.load(file.getAbsolutePath());
	}
	
	


	private static void close(InputStream in) {
		try {
			if(in!=null)
				in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void close(OutputStream out) {
		try {
			if(out!=null)
				out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
