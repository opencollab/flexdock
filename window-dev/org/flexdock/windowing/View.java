/*
 * Created on Feb 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing;

import java.awt.Component;
import java.awt.Container;
import java.awt.MenuComponent;
import java.awt.PopupMenu;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.flexdock.windowing.plaf.PlafManager;
import org.flexdock.windowing.plaf.theme.ViewUI;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class View extends JComponent {
	protected Titlebar titlepane;
	protected Container contentPane;
	protected boolean addRemoveAllowed;
	
	public View() {
		this(null);
	}
	
	public View(String title) {
		setLayout(null);
		setTitlebar(createTitlebar());
		setContentPane(new JPanel());
		setTitle(title==null? "": title);
		updateUI();
	}
	
	protected Titlebar createTitlebar() {
		return new Titlebar();
	}
	
	protected String getPreferredTitlebarUIName() {
		return ui instanceof ViewUI? ((ViewUI)ui).getPreferredTitlebarUI(): null;		
	}

	public Container getContentPane() {
		return contentPane;
	}

	public Titlebar getTitlebar() {
		return titlepane;
	}

	public void setContentPane(Container c) {
		if(c==null)
			throw new NullPointerException("Unable to set a null content pane.");
		if(c==titlepane)
			throw new IllegalArgumentException("Cannot use the same component as both content pane and titlebar.");

		synchronized(this) {
			addRemoveAllowed = true;
			if(contentPane!=null)
				super.remove(contentPane);
			super.add(c);
			contentPane = c;
			addRemoveAllowed = false;			
		}
	}

	public void setTitlebar(Titlebar titlebar) {
		if(titlebar!=null) {
			if(titlebar==contentPane)
				throw new IllegalArgumentException("Cannot use the same component as both content pane and titlebar.");
			if(!(titlebar instanceof Component))
				throw new IllegalArgumentException("Titlebar must be a type of java.awt.Component.");
		}

		synchronized(this) {
			addRemoveAllowed = true;
			if(titlepane!=null)
				super.remove((Component)titlepane);
			if(titlebar!=null) {
				super.add((Component)titlebar);
			}
			titlepane = titlebar;
			addRemoveAllowed = false;
		}
	}
	
	protected Component getTitlePane() {
		return (Component)titlepane;
	}
	
	public void setTitle(String title) {
		Titlebar tbar = getTitlebar();
		if(tbar!=null)
			tbar.setText(title);
	}

	public String getTitle() {
		Titlebar tbar = getTitlebar();
		return tbar==null? null: tbar.getText();
	}
	
	
	public void doLayout() {
		Component titlebar = getTitlePane();
		int titleHeight = titlebar==null? 0: titlepane.getPreferredSize().height;
		int w = getWidth();
		int h = getHeight();
		
		if(titlepane!=null) {
			((Component)titlepane).setBounds(0, 0, w, titleHeight);
		}
		contentPane.setBounds(0, titleHeight, w, h-titleHeight);
	}

	
	

    public void updateUI() {
        setUI(PlafManager.getUI(this));
    }
    
	public Component add(Component comp, int index) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The add() method is may not be called directly.  Use setContentPane() instead.");
		return super.add(comp, index);
	}
	public void add(Component comp, Object constraints, int index) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The add() method is may not be called directly.  Use setContentPane() instead.");
		super.add(comp, constraints, index);
	}
	public void add(Component comp, Object constraints) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The add() method is may not be called directly.  Use setContentPane() instead.");
		super.add(comp, constraints);
	}
	public Component add(Component comp) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The add() method is may not be called directly.  Use setContentPane() instead.");
		return super.add(comp);
	}
	public Component add(String name, Component comp) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The add() method is may not be called directly.  Use setContentPane() instead.");
		return super.add(name, comp);
	}
	public synchronized void add(PopupMenu popup) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The add() method is may not be called directly.  Use setContentPane() instead.");
		super.add(popup);
	}
	public void remove(Component comp) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The remove() method is may not be called directly.");
		super.remove(comp);
	}
	public void remove(int index) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The remove() method is may not be called directly.");
		super.remove(index);
	}
	public void removeAll() {
		if(!addRemoveAllowed)
			throw new RuntimeException("The remove() method is may not be called directly.");
		super.removeAll();
	}
	public synchronized void remove(MenuComponent popup) {
		if(!addRemoveAllowed)
			throw new RuntimeException("The remove() method is may not be called directly.");
		super.remove(popup);
	}
}
