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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;
import org.flexdock.util.OsInfo;
import org.flexdock.util.ResourceManager;
import org.flexdock.util.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
        defaultRubberband = loadSystemRubberband(config);
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

    private static final Document loadConfig() {
        return ResourceManager.getDocument(CONFIG_URI);
    }

    private static RubberBand createRubberBand(String implClass) {
        boolean failSilent = !Boolean.getBoolean(RubberBand.DEBUG_OUTPUT);
        return (RubberBand)Utilities.createInstance(implClass, RubberBand.class, failSilent);
    }

    private static DragPreview createPreview(String implClass) {
        return (DragPreview)Utilities.createInstance(implClass, DragPreview.class);
    }



    private static HashMap loadRubberBandInfoByOS(Document config) {
        HashMap map = new HashMap();

        Element root = (Element)config.getElementsByTagName("rubber-bands").item(0);
        map.put("default", root.getAttribute("default"));
        NodeList nodes = root.getElementsByTagName("os");

        for(int i=0; i<nodes.getLength(); i++) {
            Element osElem = (Element)nodes.item(i);
            String osName = osElem.getAttribute("name");
            NodeList items = osElem.getElementsByTagName("rubber-band");
            ArrayList classes = new ArrayList(items.getLength());
            map.put(osName, classes);
            for(int j=0; j<items.getLength(); j++) {
                Element classElem = (Element)items.item(j);
                String className = classElem.getAttribute("class");
                classes.add(className);
            }
        }
        return map;
    }

    private static RubberBand loadSystemRubberband(Document config) {
        List osList = OsInfo.getInstance().getOsNames();
        HashMap info = loadRubberBandInfoByOS(config);

        for(Iterator it=osList.iterator(); it.hasNext();) {
            String osName = (String)it.next();
            List classes = (List)info.get(osName);
            if(classes==null) {
                continue;
            }

            for(Iterator it2=classes.iterator(); it2.hasNext();) {
                String implClass = (String)it2.next();
                RubberBand rb = createRubberBand(implClass);
                if(rb!=null) {
                    return rb;
                }
            }
        }

        String implClass = (String)info.get("default");
        RubberBand rb = createRubberBand(implClass);
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
            public void drawPreview(Graphics2D g, Polygon poly, Dockable dockable, Map dragInfo) {
                // noop
            }
        };
    }

}
