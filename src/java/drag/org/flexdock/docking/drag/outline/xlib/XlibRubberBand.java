/*
 * Created on May 10, 2005
 */
package org.flexdock.docking.drag.outline.xlib;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import org.flexdock.docking.drag.effects.RubberBand;
import org.flexdock.util.OsInfo;
import org.flexdock.util.ResourceManager;

/**
 * @author Christopher Butler
 *
 */
public class XlibRubberBand extends RubberBand {
    private static final String NATIVE_RESOURCE_PATH = "org/flexdock/docking/drag/outline/xlib/";
    private static final String NATIVE_RESOURCE = "libRubberBand.so";
    private static final String NATIVE_RESOURCE_START = "libRubberBand";
    private static final String NATIVE_RESOURCE_END = ".so";
    private static final String NATIVE_LIB = "RubberBand";

    private static final XlibRubberBand SINGLETON = new XlibRubberBand();
    private Rectangle currentRect;

    private native void drawRectangle(int x, int y, int width, int height);
    private native void clearRectangle(int x, int y, int width, int height);
    private native void cleanup();

    static {
        prime();
    }

    private static void prime() {
        List keys = OsInfo.getInstance().getPrefixLibraryKeys();

        // we're going to cycle through various levels of os+arch accuracy
        // until we're able to load a native library that matches the current
        // system.
        for(Iterator it=keys.iterator(); it.hasNext();) {
            String key = (String)it.next();
            String lib = NATIVE_LIB + key;
            String resource = NATIVE_RESOURCE_PATH + NATIVE_RESOURCE_START + key + NATIVE_RESOURCE_END;

            try {
                ResourceManager.loadLibrary(lib, resource);
                // if the library was successfully loaded, then we don't
                // need to do anything else.
                return;
            } catch(UnsatisfiedLinkError err) {
                // eat the error and let's try again
            }
        }

        // last chance.  if we throw an UnsatisfiedLinkError here, then
        // the class will fail to load
        ResourceManager.loadLibrary(NATIVE_LIB, NATIVE_RESOURCE_PATH + NATIVE_RESOURCE);
    }

    public XlibRubberBand() {

    }

    public void paint(Graphics g, Rectangle r) {
        clear();
        currentRect = r;
        draw(currentRect, true);
    }

    public void clear() {
        draw(currentRect, false);
        currentRect = null;
    }

    private void draw(Rectangle r, boolean xor) {
        if(r==null)
            return;

        if(xor)
            drawRectangle(r.x, r.y, r.width, r.height);
        else
            clearRectangle(r.x, r.y, r.width, r.height);
    }
}
