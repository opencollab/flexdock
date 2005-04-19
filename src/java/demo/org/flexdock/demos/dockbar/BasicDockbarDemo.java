/*
 * Created on Apr 15, 2005
 */
package org.flexdock.demos.dockbar;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.flexdock.dockbar.DockbarManager;
import org.flexdock.docking.DockingManager;
import org.flexdock.view.View;

/**
 * @author Bobby Rosenberger
 */
public class BasicDockbarDemo {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch(Exception e) {
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		View view = createView();
		//Create and set up the window.
		JFrame frame = new JFrame("Basic Dockbar Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);

		//Grab the contentpane and add elements
		Container cp = frame.getContentPane();
		cp.setLayout(new FlowLayout());
		// push the buttons 20px down from the top
		((JComponent)cp).setBorder(new EmptyBorder(20, 0, 0, 0));

		JButton leftButton = new JButton("Pin Left");
		JButton bottomButton = new JButton("Pin Bottom");
		JButton rightButton = new JButton("Pin Right");
		
		leftButton.addActionListener(createPinner(SwingConstants.LEFT));
		bottomButton.addActionListener(createPinner(SwingConstants.BOTTOM));
		rightButton.addActionListener(createPinner(SwingConstants.RIGHT));

		cp.add(leftButton);
		cp.add(bottomButton);
		cp.add(rightButton);

		// Display the window.
		frame.setVisible(true);
		
		DockbarManager.getInstance(leftButton);
	}
	
	private static ActionListener createPinner(final int edge) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				View view = createView();
				DockingManager.pin(view, edge);
			}
		};
	}
	
	private static int viewCount = 0;
	
	private static View createView() {
		String id = "test.view." + viewCount;
		String txt = "Test View " + viewCount;
		viewCount++;
		return new View(id, txt);
	}
}