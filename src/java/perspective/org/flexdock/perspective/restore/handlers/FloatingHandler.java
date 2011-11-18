/*
 * Copyright (c) 2005 FlexDock Development Team. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE.
 */
package org.flexdock.perspective.restore.handlers;

import java.awt.Component;
import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.floating.frames.DockingFrame;
import org.flexdock.docking.state.DockingState;
import org.flexdock.perspective.RestorationManager;

/**
 * Created on 2005-04-15
 *
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @author <a href="mailto:marius@eleritec.net">Christopher Butler</a>
 * @version $Id: FloatingHandler.java,v 1.2 2005-06-04 16:42:22 winnetou25 Exp $
 */
public class FloatingHandler implements RestorationHandler {

    public boolean restore(Dockable dockable, DockingState dockingState, Map context) {
        if(dockingState == null || !dockingState.isFloating()) {
            return false;
        }

        Component owner = RestorationManager.getRestoreContainer(dockable);
        if(owner == null) {
            return false;
        }

        DockingFrame frame = DockingManager.getFloatManager().floatDockable(dockable, owner);
        return (frame != null);
    }

}
