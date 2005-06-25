/*
 * Created on Jun 24, 2005
 */
package org.flexdock.demos.raw.adapter;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 * @author Christopher Butler
 */
public class Titlepane extends JPanel{
	private JLabel titlebar;
	
	public Titlepane(String title) {
		titlebar = new JLabel(title);
		titlebar.setOpaque(true);
		titlebar.setBackground(new Color(183, 201, 217));
		
		setLayout(new BorderLayout());
		add(titlebar, BorderLayout.NORTH);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new LineBorder(Color.DARK_GRAY));
		contentPane.setBackground(Color.WHITE);
		add(contentPane, BorderLayout.CENTER);
	}
	
	public String getTitle() {
		return titlebar.getText();
	}
	
	public JLabel getTitlebar() {
		return titlebar;
	}
	
	
	
}
