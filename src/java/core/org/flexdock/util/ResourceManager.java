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
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

/**
 * This class provides <code>static</code> convenience methods for resource management, including resource
 * lookups and image, icon, and cursor creation.
 * 
 * @author Chris Butler
 */
public class ResourceManager {
	/**
	 * Defines the file extension used by native shared libraries on the current system.
	 */
	public static final String LIBRARY_EXTENSION = getLibraryExtension();
	
	private static String getLibraryExtension() {
		return isWindowsPlatform()? ".dll": ".so";
	}
	
	/**
	 * Returns <code>true</code> if the JVM is currently running on <code>Windows</code>; <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if the JVM is currently running on <code>Windows</code>; <code>false</code> otherwise.
	 */
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
			
		// if resource is still null, then check to see if it's a filesystem path
		if(url==null) {
			try {
				File file = new File(uri);
				if(file.exists())
					url = file.toURL();
			} catch(MalformedURLException e) {
				e.printStackTrace();
				url = null;
			}
		}
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
	 * Returns an <code>Image</code> object based on the specified resource URL.  
	 * Does not perform any caching on the <code>Image</code> object, so a new object will be created 
	 * with each call to this method.
	 * 
	 * @param imageLocation the <code>URL</code> indicating where the image resource may be found.
	 * @exception NullPointerException if specified resource cannot be found.
	 * @return an <code>Image</code> created from the specified resource URL
	 */
	public static Image createImage(URL imageLocation) {
		try {
			return Toolkit.getDefaultToolkit().createImage(imageLocation);
		} catch(NullPointerException e) {
			throw new NullPointerException("Unable to locate image: " + imageLocation);
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
	 * Dispatches to <code>createImage(URL imageLocation)</code>, so <code>Image</code> objects are 
	 * <b>not</b> cached via the<code>MediaTracker</code>.
	 * 
	 * @param imageURL the <code>URL</code> indicating where the image resource may be found.
     * @param hotPoint the X and Y of the large cursor's hot spot.  The
     * hotSpot values must be less than the Dimension returned by
     * getBestCursorSize().
	 * @param name a localized description of the cursor, for Java Accessibility use.
	 * @exception NullPointerException if specified resource cannot be found.
	 * @exception IndexOutOfBoundsException if the hotSpot values are outside
	 * @return a <code>Cursor</code> created from the specified resource URL
	 */
	public static Cursor createCursor(URL imageURL, Point hotPoint, String name) {
		Image image = createImage(imageURL);
		Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(image, hotPoint, name);
		return c;
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
	
	/**
	 * Attempts to load the specified native <code>library</code>, using <code>classpathResource</code>
	 * and the filesystem to implement several fallback mechanisms in the event the library cannot
	 * be loaded.  This method should provide seamless installation and loading of native libraries
	 * from within the classpath so that native libraries may be packaged within the relavant library
	 * JAR, rather than requiring separate user installation of the native libraries into the system
	 * <code>$PATH</code>.
	 * <br/>
	 * If the specified <code>library</code> is <code>null</code>, then this method returns with no action taken.
	 * <br/>
	 * This method will first attempt to call <code>System.loadLibrary(library)</code>.  If this call
	 * is successful, then the method will exit here.  If an <code>UnsatisfiedLinkError</code> is 
	 * encountered, then this method attempts to locate a FlexDock-specific filesystem resource for the 
	 * native library, called the "FlexDock Library". 
	 * <br/>
	 * The FlexDock Library will reside on the filesystem under the user's home directory with the path 
	 * <code>${user.home}/flexdock/${library}${native.lib.extension}.  Thus, if this method is called
	 * with an argument of <code>"foo"</code> for the library, then under windows the FlexDock Library 
	 * should be <code>C:\Documents and Settings\${user.home}\flexdock\foo.dll</code>.  Under any type of 
	 * Unix system, the FlexDock library should be <code>/home/${user.home}/flexdock/foo.so</code>.
	 * <br/>
	 * If the FlexDock Library exists on the filesystem, then this method will attempt to load it by 
	 * calling <code>System.load(String filename)</code> with the FlexDock Library's absolute path.  If this 
	 * call is successful, then the method exits here.
	 * <br/>
	 * If the FlexDock Library cannot be loaded, then the specified <code>classpathResource</code> is 
	 * checked.  If <code>classpathResource</code> is <code>null</code>, then there is no more information
	 * available to attempt to resolve the requested library and this method throws the last 
	 * <code>UnsatisfiedLinkError</code> encountered.
	 * <br/>
	 * If <code>classpathResource</code> is non-<code>null</code>, then an <code>InputStream</code> to the
	 * specified resource is resolved from the class loader.  The contents of the <code>InputStream</code>
	 * are read into a <code>byte</code> array and written to disk as the FlexDock Library file.  The 
	 * FlexDock Library is then loaded with a call to <code>System.load(String filename)</code> with the 
	 * FlexDock Library's absolute path.  If the specified <code>classpathResource</code> cannot be resolved
	 * by the class loader, if any errors occur during this process of extracting and writing to disk, or if
	 * the resulting FlexDock Library file cannot be loaded as a native library, then this method throws an 
	 * appropriate <code>UnsatisfiedLinkError</code> specific to the situation that prevented the native 
	 * library from loading.
	 * <br/>
	 * Note that because this method may extract resources from the classpath and install to the filesystem
	 * as a FlexDock Library, subsequent calls to this method across JVM sessions will find the FlexDock Library
	 * on the filesystem and bypass the extraction process.
	 * 
	 * @param library the native library to load
	 * @param classpathResource the fallback location within the classpath from which to extract the desired
	 * native library in the event it is not already installed on the target system
	 * @exception UnsatisfiedLinkError if the library cannot be loaded
	 */
	public static void loadLibrary(String library, String classpathResource) {
		if(library==null)
			return;
		
		UnsatisfiedLinkError linkageError = null;
		try {
			System.loadLibrary(library);
			return;
		} catch(UnsatisfiedLinkError err) {
			// pass through here
			linkageError = err;
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
				linkageError = err;
			}
		}
		
		// if we can't load from the classpath, then we're stuck.  
		// throw the last UnsatisfiedLinkError we encountered.
		if(classpathResource==null && linkageError!=null)
			throw linkageError;
			
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
			UnsatisfiedLinkError err = new UnsatisfiedLinkError("Unable to extract resource to file: " + file.getAbsolutePath());
			err.initCause(giveUp);
			throw err;
		}
		finally {
			close(fileOut);
			close(in);
		}
		
		// now that our classpath resource has been written to disk, load the native
		// library from this file
		System.load(file.getAbsolutePath());
	}
	
	/**
	 * Returns a <code>Document</code> object based on the specified resource <code>uri</code>.
	 * This method resolves a <code>URL</code> from the specified <code>String</code> via
	 * <code>getResource(String uri)</code> and dispatches to <code>getDocument(URL url)</code>.
	 * If the specified <code>uri</code> is <code>null</code>, then this method returns 
	 * <code>null</code>.
	 * 
	 * @param uri the <code>String</code> describing the resource to be looked up
	 * @return a <code>Document</code> object based on the specified resource <code>uri</code>
	 * @see #getResource(String)
	 * @see #getDocument(URL)
	 */
	public static Document getDocument(String uri) {
		URL resource = getResource(uri);
		return getDocument(resource);
	}

	/**
	 * Returns a <code>Document</code> object based on the specified resource <code>URL</code>.
	 * This method will open an <code>InputStream</code> to the specified <code>URL</code> and
	 * construct a <code>Document</code> instance.  If any <code>Exceptions</code> are encountered
	 * in the process, this method returns <code>null</code>.  If the specified <code>URL</code>
	 * is <code>null</code>, then this method returns <code>null</code>.
	 * 
	 * @param url the <code>URL</code> describing the resource to be looked up
	 * @return a <code>Document</code> object based on the specified resource <code>URL</code>
	 */
	public static Document getDocument(URL url) {
		if(url==null)
			return null;
		
		InputStream inStream = null;
		try {
			inStream = url.openStream();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(inStream);
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			close(inStream);
		}
		return null;
	}
	
	/**
	 * Returns a <code>Properties</code> object based on the specified resource <code>uri</code>.
	 * This method resolves a <code>URL</code> from the specified <code>String</code> via
	 * <code>getResource(String uri)</code> and dispatches to 
	 * <code>getProperties(URL url, boolean failSilent)</code> with an argument of <code>false</code>
	 * for <code>failSilent</code>. If the specified <code>uri</code> is <code>null</code>, then 
	 * this method will print a stack trace for the ensuing <code>NullPointerException</code> and
	 * return <code>null</code>.
	 * 
	 * @param uri the <code>String</code> describing the resource to be looked up
	 * @return a <code>Properties</code> object based on the specified resource <code>uri</code>.
	 * @see #getResource(String)
	 * @see #getProperties(URL, boolean)
	 */
	public static Properties getProperties(String uri) {
		return getProperties(uri, false);
	}
	
	/**
	 * Returns a <code>Properties</code> object based on the specified resource <code>uri</code>.
	 * This method resolves a <code>URL</code> from the specified <code>String</code> via
	 * <code>getResource(String uri)</code> and dispatches to 
	 * <code>getProperties(URL url, boolean failSilent)</code>, passing the specified 
	 * <code>failSilent</code> parameter. If the specified <code>uri</code> is <code>null</code>, 
	 * this method will return <code>null</code>.  If <code>failSilent</code> is <code>false</code>,
	 * then the ensuing <code>NullPointerException's</code> stacktrace will be printed to the 
	 * <code>System.err</code> before returning.
	 * 
	 * @param uri the <code>String</code> describing the resource to be looked up
	 * @param failSilent <code>true</code> if no errors are to be reported to the <code>System.err</code>
	 * before returning; <code>false</code> otherwise.
	 * @return a <code>Properties</code> object based on the specified resource <code>uri</code>.
	 * @see #getResource(String)
	 * @see #getProperties(URL, boolean)
	 */
	public static Properties getProperties(String uri, boolean failSilent) {
		URL url = getResource(uri);
		return getProperties(url, failSilent); 
	}
	
	/**
	 * Returns a <code>Properties</code> object based on the specified resource <code>URL</code>.
	 * This method dispatches to <code>getProperties(URL url, boolean failSilent)</code>, with an argument 
	 * of <code>false</code> for <code>failSilent</code>. If the specified <code>uri</code> is 
	 * <code>null</code>, this method will print the ensuing <code>NullPointerException</code> stack
	 * tracke to the <code>System.err</code> and return <code>null</code>.
	 * 
	 * @param url the <code>URL</code> describing the resource to be looked up
	 * @return a <code>Properties</code> object based on the specified resource <code>url</code>.
	 * @see #getProperties(URL, boolean)
	 */
	public static Properties getProperties(URL url) {
		return getProperties(url, false);
	}
	
	/**
	 * Returns a <code>Properties</code> object based on the specified resource <code>url</code>.
	 * If the specified <code>uri</code> is <code>null</code>, this method will return <code>null</code>.
	 * If any errors are encountered during the properties-load process, this method will return <code>null</code>.
	 * If <code>failSilent</code> is <code>false</code>, then the any encoutered error stacktraces will be 
	 * printed to the <code>System.err</code> before returning.  
	 * 
	 * @param url the <code>URL</code> describing the resource to be looked up
	 * @param failSilent <code>true</code> if no errors are to be reported to the <code>System.err</code>
	 * before returning; <code>false</code> otherwise.
	 * @return a <code>Properties</code> object based on the specified resource <code>url</code>.
	 */
	public static Properties getProperties(URL url, boolean failSilent) {
		if(failSilent && url==null)
			return null;
		
		InputStream in = null;
		try {
			in = url.openStream();
			Properties p = new Properties();
			p.load(in);
			return p;
		} catch(Exception e) {
			if(!failSilent)
				e.printStackTrace();
			return null;
		}
		finally {
			close(in);
		}
	}
	
	/**
	 * Calls <code>close()</code> on the specified <code>InputStream</code>.  Any <code>Exceptions</code> encountered
	 * will be printed to the <code>System.err</code>.  If <code>in</code> is <code>null</code>, then no
	 * <code>Exception</code> is thrown and no action is taken.
	 * 
	 * @param in the <code>InputStream</code> to close
	 * @see InputStream#close() 
	 */
	public static void close(InputStream in) {
		try {
			if(in!=null)
				in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calls <code>close()</code> on the specified <code>OutputStream</code>.  Any <code>Exceptions</code> encountered
	 * will be printed to the <code>System.err</code>.  If <code>out</code> is <code>null</code>, then no
	 * <code>Exception</code> is thrown and no action is taken.
	 * 
	 * @param out the <code>OutputStream</code> to close
	 * @see OutputStream#close() 
	 */
	public static void close(OutputStream out) {
		try {
			if(out!=null)
				out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calls <code>close()</code> on the specified <code>Socket</code>.  Any <code>Exceptions</code> encountered
	 * will be printed to the <code>System.err</code>.  If <code>socket</code> is <code>null</code>, then no
	 * <code>Exception</code> is thrown and no action is taken.
	 * 
	 * @param socket the <code>Socket</code> to close
	 * @see Socket#close()
	 */
	public static void close(Socket socket) {
		try {
			if(socket!=null)
			    socket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
