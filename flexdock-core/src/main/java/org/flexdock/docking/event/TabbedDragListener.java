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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JTabbedPane;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.drag.DragManager;

/**
 * @author Christopher Butler
 */
public class TabbedDragListener extends MouseAdapter implements MouseMotionListener {

    private DragManager dragListener;

    @Override
    public void mouseDragged(MouseEvent me) {
        if(dragListener!=null) {
            dragListener.mouseDragged(me);
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if(dragListener!=null) {
            dragListener.mouseReleased(me);
        }
        dragListener = null;
    }

    @Override
    public void mousePressed(MouseEvent me) {
        if(!(me.getSource() instanceof JTabbedPane)) {
            dragListener = null;
            return;
        }

        JTabbedPane pane = (JTabbedPane)me.getSource();
        Point p = me.getPoint();
        int tabIndex = pane.indexAtLocation(p.x, p.y);
        if(tabIndex==-1) {
            dragListener = null;
            return;
        }

        Dockable dockable = DockingManager.getDockable(pane.getComponentAt(tabIndex));
        dragListener = DockingManager.getDragListener(dockable);
        if(dragListener!=null) {
            dragListener.mousePressed(me);
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        // does nothing
    }

//  private void redispatchToDockable(MouseEvent me) {
////if(!tabsAsDragSource || dockable==null)
////return;
//
////Component dragSrc = dockable.getInitiator();
////MouseEvent evt = SwingUtilities.convertMouseEvent((Component)me.getSource(), me, dragSrc);
////dragSrc.dispatchEvent(evt);
//  }

}
