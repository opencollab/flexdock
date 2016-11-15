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
package org.flexdock.event;

import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Stack;


/**
 * @author Christopher Butler
 */
public class EventManager {
    private static final EventManager SINGLETON = new EventManager();
    private Stack handlers = new Stack();

    static {
        addHandler(new RegistrationHandler());
    }


    public static EventManager getInstance() {
        return SINGLETON;
    }

    private EventManager() {

    }


    public static void addHandler(EventHandler handler) {
        getInstance().addEventHandler(handler);
    }

    public static void removeHandler(EventHandler handler) {
        getInstance().removeEventHandler(handler);
    }

    public static void addListener(EventListener listener) {
        getInstance().addEventListener(listener);
    }

    public static void removeListener(EventListener listener) {
        getInstance().removeEventListener(listener);
    }











    public static void dispatch(Event evt) {
        getInstance().dispatchEvent(evt);
    }

    public static void dispatch(Event evt, Object target) {
        getInstance().dispatchEvent(evt, target);
    }

    public static void dispatch(Event evt, Object[] targets) {
        getInstance().dispatchEvent(evt, targets);
    }








    public void addEventHandler(EventHandler handler) {
        if(handler!=null) {
            handlers.push(handler);
        }
    }

    public void removeEventHandler(EventHandler handler) {
        if(handler!=null) {
            handlers.remove(handler);
        }
    }

    private EventHandler getHandler(Event evt) {
        for(Iterator it=handlers.iterator(); it.hasNext();) {
            EventHandler handler = (EventHandler)it.next();
            if(handler.acceptsEvent(evt)) {
                return handler;
            }
        }
        return null;
    }

    private EventHandler getHandler(EventListener listener) {
        for(Iterator it=handlers.iterator(); it.hasNext();) {
            EventHandler handler = (EventHandler)it.next();
            if(handler.acceptsListener(listener)) {
                return handler;
            }
        }
        return null;
    }

    public void addEventListener(EventListener listener) {
        EventHandler handler = listener==null? null: getHandler(listener);
        if(handler!=null) {
            handler.addListener(listener);
        }
    }

    public void removeEventListener(EventListener listener) {
        EventHandler handler = listener==null? null: getHandler(listener);
        if(handler!=null) {
            handler.removeListener(listener);
        }
    }


    public void dispatchEvent(Event evt) {
        dispatchEvent(evt, null);
    }

    public void dispatchEvent(Event evt, Object target) {
        Object[] targets = null;
        if(target instanceof Collection) {
            targets = ((Collection)target).toArray();
        } else if(target!=null) {
            targets = new Object[] {target};
        }

        dispatchEvent(evt, targets);

    }

    public void dispatchEvent(Event evt, Object[] targets) {
        EventHandler handler = evt==null? null: getHandler(evt);
        if(handler!=null) {
            handler.handleEvent(evt, targets);
        }
    }
}
