/*
 * Created on Feb 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.test;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WindowTest {
	public static void main(String[] args) {
/*		JFrame f = new JFrame();
		f.setSize(600, 500);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		View view = ViewFactory.createView("Test View");
		Icon icon = loadIcon(view.self());
		view.getTitlebar().setIcon(icon);
		view.getTitlebar().setFocused(true);
		p.add(view.self(), BorderLayout.CENTER);
		
		f.setContentPane(p);
		f.setVisible(true);

		/*
		UIDefaults defaults = UIManager.getDefaults();
		for(Enumeration en=defaults.keys(); en.hasMoreElements();) {
			Object key = en.nextElement();
			System.out.println(key + "=" + defaults.get(key));
		}
		
		*/
		
		PropertyChangeListener[] listeners = UIManager.getPropertyChangeListeners();
		System.out.println(listeners.length);
		
	}
	
	private static Icon loadIcon(Component c) {
		String uri = "org/flexdock/windowing/titlebar/msvs001.png";
		URL url = WindowTest.class.getClassLoader().getResource(uri);
		if(url==null)
			url = WindowTest.class.getClassLoader().getResource("/" + uri);

		if(url==null)
			return null;

        Image image = c.getToolkit().getImage(url);
        MediaTracker tracker = new MediaTracker(c);
        tracker.addImage(image, 0);
        try {
            // wait until the image is fully loaded
            tracker.waitForAll();
        } catch (InterruptedException ie) {
        }
        return new ImageIcon(image);
	}
	
}
