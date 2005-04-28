/*
 * Created on Apr 28, 2005
 */
package org.flexdock.docking.event.hierarchy;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.flexdock.docking.DockingPort;
import org.flexdock.util.RootWindow;

/**
 * @author Christopher Butler
 */
public class RootDockingPortInfo {
	private WeakReference windowRef;
	private ArrayList rootPorts;
	private HashMap portsById;
	private String mainPortId;
	
	public RootDockingPortInfo(RootWindow window) {
		windowRef = new WeakReference(window);
		rootPorts = new ArrayList(2);
		portsById = new HashMap(2);
	}
	
	public RootWindow getWindow() {
		return (RootWindow)windowRef.get();
	}
	
	public boolean contains(DockingPort port) {
		return port==null? false: portsById.containsKey(port.getPersistentId());
	}
	
	public synchronized void add(DockingPort port) {
		if(contains(port))
			return;
		
		portsById.put(port.getPersistentId(), port);
		rootPorts.add(port);
	}
	
	public synchronized void remove(DockingPort port) {
		if(!contains(port))
			return;
			
		portsById.remove(port.getPersistentId());
		rootPorts.remove(port);
	}
	
	public int getPortCount() {
		return rootPorts.size();
	}
	
	public DockingPort getPort(int indx) {
		return indx<getPortCount()? (DockingPort)rootPorts.get(indx): null;
	}
	
	public DockingPort getPort(String portId) {
		return (DockingPort)portsById.get(portId);
	}
	
	public void setMainPort(String portId) {
		mainPortId = portId;
	}
	
	public DockingPort getMainPort() {
		DockingPort port = mainPortId==null? null: getPort(mainPortId);
		if(port==null) {
			port = getPortCount()>0? getPort(0): null;
		}
		return port;
			
	}
}
