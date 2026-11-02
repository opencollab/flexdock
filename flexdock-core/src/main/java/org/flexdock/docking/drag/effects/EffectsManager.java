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
package org.flexdock.docking.drag.effects;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;
import org.flexdock.util.ResourceManager;
import org.flexdock.util.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Christopher Butler
 */
public class EffectsManager {
    private static final String CONFIG_URI = "org/flexdock/docking/drag/effects/drag-effects.xml";
    private static final Object LOCK = new Object();

    private static DragPreview defaultPreview;
    private static DragPreview customPreview;
    private static RubberBand defaultRubberband;
    private static RubberBand customRubberband;

    static {
        prime();
    }

    public static void prime() {
        Document config = ResourceManager.getDocument(CONFIG_URI);
        defaultPreview = loadDefaultPreview(config);
        defaultRubberband = loadDefaultRubberband(config);
    }

    public static RubberBand getRubberBand() {
        synchronized(LOCK) {
            return customRubberband==null? defaultRubberband: customRubberband;
        }
    }

    public static DragPreview getPreview(Dockable dockable, DockingPort target) {
        synchronized(LOCK) {
            return customPreview==null? defaultPreview: customPreview;
        }
    }

    public static RubberBand setRubberBand(String implClass) {
        RubberBand rb = createRubberBand(implClass);
        if(implClass!=null && rb==null) {
            return null;
        }

        setRubberBand(rb);
        return rb;
    }

    public static void setRubberBand(RubberBand rubberBand) {
        synchronized(LOCK) {
            customRubberband = rubberBand;
        }
    }

    public DragPreview setPreview(String implClass) {
        DragPreview preview = createPreview(implClass);
        if(implClass!=null && preview==null) {
            return null;
        }

        setPreview(preview);
        return preview;
    }

    public static void setPreview(DragPreview preview) {
        synchronized(LOCK) {
            customPreview = preview;
        }
    }

    private static RubberBand createRubberBand(String implClass) {
        return (RubberBand)Utilities.createInstance(implClass, RubberBand.class);
    }

    private static DragPreview createPreview(String implClass) {
        return (DragPreview)Utilities.createInstance(implClass, DragPreview.class);
    }

    private static RubberBand loadDefaultRubberband(Document config) {
        Element root = (Element)config.getElementsByTagName("rubber-bands").item(0);
        RubberBand rb = createRubberBand(root.getAttribute("default"));
        return rb==null? new RubberBand(): rb;
    }

    private static DragPreview loadDefaultPreview(Document config) {
        Element root = (Element)config.getElementsByTagName("drag-previews").item(0);
        String previewClass = root.getAttribute("default");
        DragPreview preview = createPreview(previewClass);
        if(preview!=null) {
            return preview;
        }
        // unable to load the preview class.  return a no-op preview delegate instead.
        return new DefaultPreview() {
            @Override
            public void drawPreview(Graphics2D g, Polygon poly, Dockable dockable, Map dragInfo) {
                // noop
            }
        };
    }

}
