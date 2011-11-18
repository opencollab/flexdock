/*
 * Created on May 18, 2005
 */
package org.flexdock.perspective.event;

import java.util.EventListener;

/**
 * @author Christopher Butler
 */
public interface LayoutListener extends EventListener {
    public void layoutApplied(LayoutEvent evt);
    public void layoutEmptied(LayoutEvent evt);
    public void dockableHidden(LayoutEvent evt);
    public void dockableDisplayed(LayoutEvent evt);
}
