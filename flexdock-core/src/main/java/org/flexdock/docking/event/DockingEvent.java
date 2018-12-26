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
package org.flexdock.docking.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.event.Event;

/**
 * @author Kevin Duffey
 * @author Christopher Butler
 */
@SuppressWarnings(value = { "serial" })
public class DockingEvent extends Event {
    public static final int DRAG_STARTED = 0;
    public static final int DROP_STARTED = 1;
    public static final int DOCKING_COMPLETE = 2;
    public static final int DOCKING_CANCELED = 3;
    public static final int UNDOCKING_COMPLETE = 4;
    public static final int UNDOCKING_STARTED = 5;

    private DockingPort oldPort;
    private DockingPort newPort;
    private boolean consumed;
    private AWTEvent trigger;
    private String region;
    private boolean overWindow;
    private Map dragContext;

    /**
     * Constructor to create a DockingEvent object. The AWT trigger of the created event is {@code null}.
     *
     * @param source the provided dockable
     * @param oldPort the originating docking part
     * @param newPort the destination docking port
     * @param eventType allows to specify whether the dock is completed or canceled
     * @param context the docking content
     */
    public DockingEvent(Dockable source, DockingPort oldPort, DockingPort newPort, int eventType, Map context) {
        this(source, oldPort, newPort, eventType, null, context);
    }

    /**
     * Constructor to create a DockingEvent object.
     * @param source the provided dockable
     * @param oldPort the originating docking part
     * @param newPort the destination docking port
     * @param eventType allows to specify whether the dock is completed or canceled
     * @param trigger the AWT trigger
     * @param context the docking content
     */
    public DockingEvent(Dockable source, DockingPort oldPort, DockingPort newPort, int eventType, AWTEvent trigger, Map context) {
        super(source, eventType);
        this.oldPort = oldPort;
        this.newPort = newPort;
        this.trigger = trigger;
        this.region = UNKNOWN_REGION;
        dragContext = context;
        setOverWindow(true);
    }

    /**
     * Returns the old docking port which the source <code>Dockable</code> was
     * originally docked to.
     *
     * @return DockingPort the old docking port
     */
    public DockingPort getOldDockingPort() {
        return oldPort;
    }

    /**
     * Returns the new docking port the source <code>Dockable</code> has been
     * docked to.
     *
     * @return DockingPort the new docking port
     */
    public DockingPort getNewDockingPort() {
        return newPort;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void consume() {
        this.consumed = true;
    }

    public AWTEvent getTrigger() {
        return trigger;
    }

    public void setTrigger(AWTEvent trigger) {
        this.trigger = trigger;
    }

    public Object getTriggerSource() {
        return trigger == null ? null : trigger.getSource();
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        if (!DockingManager.isValidDockingRegion(region)) {
            region = UNKNOWN_REGION;
        }
        this.region = region;
    }

    public boolean isOverWindow() {
        return overWindow;
    }

    public void setOverWindow(boolean overWindow) {
        this.overWindow = overWindow;
    }

    public Dockable getDockable() {
        return (Dockable)getSource();
    }

    public Component getComponent() {
        return getDockable().getComponent();
    }

    public Map getDragContext() {
        return dragContext;
    }
}
