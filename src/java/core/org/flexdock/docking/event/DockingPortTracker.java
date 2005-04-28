/*
 * Created on Apr 27, 2005
 */
package org.flexdock.docking.event;

import java.awt.Component;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.flexdock.util.RootWindow;

/**
 * @author Christopher Butler
 */
public class DockingPortTracker {
	private static WeakHashMap PORTS_BY_WINDOW = new WeakHashMap();
	
	private static List getPortsImpl(Component c) {
		RootWindow window = RootWindow.getRootContainer(c);
		if(window==null)
			return null;
		
		Component key = window.getRootContainer();
		List list = (List)PORTS_BY_WINDOW.get(key);
		if(list==null) {
			synchronized(PORTS_BY_WINDOW) {
				list = new ArrayList(2);
				PORTS_BY_WINDOW.put(key, list);
			}
		}
		return list;
	}
	
	public static List getPorts(Component c) {
		List list = getPortsImpl(c);
		return list==null? null: new ArrayList(list);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		
		System.out.println("HierarchyEvent.ANCESTOR_MOVED: " + HierarchyEvent.ANCESTOR_MOVED);
		System.out.println("HierarchyEvent.ANCESTOR_RESIZED: " + HierarchyEvent.ANCESTOR_RESIZED);
		
		System.out.println("HierarchyEvent.DISPLAYABILITY_CHANGED: " + HierarchyEvent.DISPLAYABILITY_CHANGED);
		System.out.println("HierarchyEvent.HIERARCHY_CHANGED: " + HierarchyEvent.HIERARCHY_CHANGED);
		System.out.println("HierarchyEvent.HIERARCHY_FIRST: " + HierarchyEvent.HIERARCHY_FIRST);
		System.out.println("HierarchyEvent.HIERARCHY_LAST: " + HierarchyEvent.HIERARCHY_LAST);
		System.out.println("HierarchyEvent.SHOWING_CHANGED: " + HierarchyEvent.SHOWING_CHANGED);
		
		System.out.println();
		
		
		JPanel p = new JPanel();
		p.addHierarchyListener(new Qwerty());
		
		JPanel contentPane = new JPanel();
		contentPane.add(p);
		
		f.getContentPane().add(p);
		
		p.add(new JButton("asdf"));
	}
	
	private static class Qwerty implements HierarchyListener {
		
		public void hierarchyChanged(HierarchyEvent e) {
			if(e.getID()!=HierarchyEvent.HIERARCHY_CHANGED)
				return;
			
			
		}
	}
}
