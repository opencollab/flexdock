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
package org.flexdock.test.drag;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.flexdock.docking.drag.effects.EffectsManager;
import org.flexdock.docking.drag.effects.RubberBand;



/**
 * @author marius
 */
public class RubberBandTest extends JFrame {
    private RubberBand rubberBand;
    private Point offset;

    private RubberBandTest() {
        super("Rubber Band Test");
        rubberBand = EffectsManager.getRubberBand();
        setContentPane(createContentPane());
    }

    private JPanel createContentPane() {
        JPanel jp = new JPanel();
        JLabel lbl = new JLabel("Drag Me");
        DragListener dl = new DragListener();
        lbl.addMouseListener(dl);
        lbl.addMouseMotionListener(dl);
        lbl.setBackground(Color.gray);
        lbl.setOpaque(true);
        jp.add(lbl);
        return jp;

    }

    private void startDrag(MouseEvent me) {
        Point p = me.getPoint();
        SwingUtilities.convertPointToScreen(p, (Component)me.getSource());
        Point win = getLocationOnScreen();
        offset = new Point(win.x-p.x, win.y-p.y);
    }

    private void processDrag(MouseEvent me) {
        Rectangle r = getBounds();
        r.setLocation(getMousePoint(me));
        rubberBand.paint(r);
    }

    private void stopDrag(MouseEvent me) {
        rubberBand.clear();
        setLocation(getMousePoint(me));
        offset = null;
    }

    private Point getMousePoint(MouseEvent me) {
        Point p = me.getPoint();
        SwingUtilities.convertPointToScreen(p, (Component)me.getSource());
        p.x += offset.x;
        p.y += offset.y;
        return p;
    }

    private class DragListener extends MouseAdapter implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent me) {
            processDrag(me);
        }
        @Override
        public void mouseMoved(MouseEvent me) {
            // do nothing
        }

        @Override
        public void mousePressed(MouseEvent me) {
            startDrag(me);
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            stopDrag(me);
        }
    }


    public static void main(String[] args) {
        JFrame f = new RubberBandTest();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(100, 100);
        f.setVisible(true);
    }

}
