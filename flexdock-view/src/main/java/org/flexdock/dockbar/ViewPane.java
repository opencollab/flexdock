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
package org.flexdock.dockbar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

import org.flexdock.dockbar.event.ResizeListener;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.state.MinimizationManager;
import org.flexdock.plaf.common.border.SlideoutBorder;

/**
 * @author Christopher Butler
 */
public class ViewPane extends JPanel {
    private static final Dimension RESIZE_DIMS = new Dimension(3, 3);
    private static final MouseInputAdapter EMPTY_MOUSE_LISTENER = new MouseInputAdapter() {};
    public static final int UNSPECIFIED_PREFERRED_SIZE = -1;
    private DockbarManager manager;
    private JPanel dragEdge;
    private int prefSize;
    private boolean locked;


    public ViewPane(DockbarManager mgr) {
        super(new BorderLayout(0, 0));
        setBorder(new SlideoutBorder());

        manager = mgr;
        prefSize = UNSPECIFIED_PREFERRED_SIZE;

        dragEdge = new JPanel();
        dragEdge.setPreferredSize(RESIZE_DIMS);

        ResizeListener listener = new ResizeListener(mgr);
        dragEdge.addMouseListener(listener);
        dragEdge.addMouseMotionListener(listener);

        updateOrientation();

        // intercept rouge mouse events so they don't fall
        // through to the content pane
        addMouseListener(EMPTY_MOUSE_LISTENER);
        addMouseMotionListener(EMPTY_MOUSE_LISTENER);
    }

    public void updateContents() {
        // remove the currently docked component
        Component[] children = getComponents();
        for(int i=0; i<children.length; i++) {
            if(children[i]!=dragEdge) {
                remove(children[i]);
            }
        }

        // add the new component
        Dockable d = manager.getActiveDockable();
        Component c = d==null? null: d.getComponent();
        if(c!=null) {
            add(c, BorderLayout.CENTER);
        }
    }

    public void updateOrientation() {
        Border border = getBorder();
        if(border instanceof SlideoutBorder) {
            ((SlideoutBorder)border).setOrientation(manager.getActiveEdge());
        }

        // update the drag edge
        remove(dragEdge);
        add(dragEdge, getEdgeRegion());
        dragEdge.setCursor(getResizeCursor());

        // revalidate
        revalidate();
    }

    private String getEdgeRegion() {
        int orientation = manager.getActiveEdge();
        switch(orientation) {
            case MinimizationManager.TOP:
                return BorderLayout.SOUTH;
            case MinimizationManager.BOTTOM:
                return BorderLayout.NORTH;
            case MinimizationManager.RIGHT:
                return BorderLayout.WEST;
            default:
                return BorderLayout.EAST;
        }
    }

    public Cursor getResizeCursor() {
        int orientation = manager.getActiveEdge();
        return orientation==MinimizationManager.LEFT ||
               orientation==MinimizationManager.RIGHT?
               Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR):
               Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
    }

    public int getPrefSize() {
        return prefSize;
    }

    public void setPrefSize(int prefSize) {
        this.prefSize = prefSize;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
