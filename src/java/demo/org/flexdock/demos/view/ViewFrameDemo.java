package org.flexdock.demos.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.flexdock.util.SwingUtility;
import org.flexdock.view.View;
import org.flexdock.view.floating.ViewFrame;

/**
 * @author Christopher Butler
 */
public class ViewFrameDemo extends JFrame implements ActionListener {
	private ViewFrame viewframe;
	
	public static void main(String[] args) {
		SwingUtility.setPlaf("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		
		JFrame f = new ViewFrameDemo();
		f.setBounds(100, 100, 100, 65);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	public ViewFrameDemo() {
		super("ViewFrame Demo");

		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		
		JButton b = new JButton("Float");
		b.addActionListener(this);
		c.add(b);
		
		viewframe = createViewframe();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(!viewframe.isVisible()) {
			viewframe.setSize(300, 300);
			SwingUtility.centerOnScreen(viewframe);
			viewframe.setVisible(true);
		}
	}
	
	private ViewFrame createViewframe() {
		ViewFrame frame = new ViewFrame(this);
		frame.addView(createView("solution.explorer", "Solution Explorer"));
		frame.addView(createView("class.view", "Class View"));
		return frame;
	}
	
	private View createView(String id, String text) {
		View view = new View(id, text);
		view.addAction(createAction("close", "Close"));
		view.addAction(createAction("pin", "Pin"));
		
		JPanel p = new JPanel();
		p.setBackground(Color.WHITE);
		p.setBorder(new LineBorder(Color.GRAY, 1));

		view.setContentPane(p);
		return view;
	}
	
	private Action createAction(String name, String tooltip) {
		Action a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
			}
		};
		a.putValue(Action.NAME, name);
		a.putValue(Action.SHORT_DESCRIPTION, tooltip);
		return a;
	}

}
