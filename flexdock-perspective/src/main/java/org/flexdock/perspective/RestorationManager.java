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
package org.flexdock.perspective;

import java.awt.Component;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.state.DockingState;
import org.flexdock.event.EventManager;
import org.flexdock.perspective.event.RegistrationEvent;
import org.flexdock.perspective.restore.handlers.AlreadyRestoredHandler;
import org.flexdock.perspective.restore.handlers.DockPathHandler;
import org.flexdock.perspective.restore.handlers.FloatingHandler;
import org.flexdock.perspective.restore.handlers.MinimizedHandler;
import org.flexdock.perspective.restore.handlers.PointHandler;
import org.flexdock.perspective.restore.handlers.RelativeHandler;
import org.flexdock.perspective.restore.handlers.RestorationHandler;
import org.flexdock.perspective.restore.handlers.UnknownStateHandler;
import org.flexdock.util.RootWindow;

/**
 * @author Christopher Butler
 */
public class RestorationManager {

    private static final RestorationManager SINGLETON = new RestorationManager();

    private Vector restorationHandlers = new Vector();

    static {
        getInstance().addHandler(new AlreadyRestoredHandler());
        getInstance().addHandler(new FloatingHandler());
        getInstance().addHandler(new MinimizedHandler());
        getInstance().addHandler(new RelativeHandler());
        getInstance().addHandler(new DockPathHandler());
        getInstance().addHandler(new PointHandler());
        getInstance().addHandler(new UnknownStateHandler());
    }

    private RestorationManager() {
        //prevent instant..
    }

    public static RestorationManager getInstance() {
        return SINGLETON;
    }

    public void addHandler(RestorationHandler handler) {
        if(handler!=null) {
            restorationHandlers.add(handler);
            EventManager.dispatch(new RegistrationEvent(handler, this, true));
        }
    }

    public boolean removeHandler(RestorationHandler handler) {
        boolean ret = false;
        if(handler!=null) {
            ret = restorationHandlers.remove(handler);
            if(ret) {
                EventManager.dispatch(new RegistrationEvent(handler, this, false));
            }
        }
        return ret;
    }


    public boolean restore(Dockable dockable) {
        if(dockable != null) {
            DockingState info = PerspectiveManager.getInstance().getDockingState(dockable, true);
            HashMap context = new HashMap();
            for(Iterator it=restorationHandlers.iterator(); it.hasNext();) {
                RestorationHandler handler = (RestorationHandler)it.next();
                if(handler.restore(dockable, info, context)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static RootWindow getRestoreWindow(Dockable dockable) {
        // TODO: fix this code to keep track of the proper dialog owner
        RootWindow[] windows = DockingManager.getDockingWindows();
        return windows.length==0? null: windows[0];
    }

    public static Component getRestoreContainer(Dockable dockable) {
        RootWindow window = getRestoreWindow(dockable);
        return window==null? null: window.getRootContainer();
    }

}
