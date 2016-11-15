/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.flexdock.docking.event.hierarchy;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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

    private boolean containsPortId(DockingPort port) {
        return port==null? false: contains(port.getPersistentId());
    }

    public boolean contains(String portId) {
        return portId==null? false: portsById.containsKey(portId);
    }

    public boolean contains(DockingPort port) {
        return port==null? false: portsById.containsValue(port);
    }

    public synchronized void add(DockingPort port) {
        if(containsPortId(port)) {
            return;
        }

        portsById.put(port.getPersistentId(), port);
        rootPorts.add(port);
    }

    public synchronized void remove(DockingPort port) {
        if(port==null) {
            return;
        }

        String key = port.getPersistentId();
        if(!contains(key)) {
            key = null;
            for(Iterator it=portsById.keySet().iterator(); it.hasNext();) {
                String tmpKey = (String)it.next();
                DockingPort tmp = (DockingPort)portsById.get(tmpKey);
                if(tmp==port) {
                    key = tmpKey;
                    break;
                }
            }
        }

        if(key!=null) {
            portsById.remove(key);
        }
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
