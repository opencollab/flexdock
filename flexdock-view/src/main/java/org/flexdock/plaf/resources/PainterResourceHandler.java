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
package org.flexdock.plaf.resources;

import org.flexdock.plaf.Configurator;
import org.flexdock.plaf.PropertySet;
import org.flexdock.plaf.resources.paint.Painter;
import org.flexdock.plaf.resources.paint.PainterResource;

/**
 * @author Claudio Romano
 *
 */
public class PainterResourceHandler extends ResourceHandler {

    public static final String PAINTER_RESOURCE_KEY = "painter-resource";

    public Object getResource(String stringValue) {
        PropertySet propertySet = Configurator.getProperties(stringValue, PAINTER_RESOURCE_KEY);
        PainterResource pr = createResource( propertySet);

        Painter p = createPainter( pr.getImplClass());
        p.setPainterResource( pr);
        return p;
    }

    private static Painter createPainter(Class clazz) {

        if(clazz ==null) {
            return null;
        }

        try {
            return (Painter)clazz.newInstance();
        } catch(Exception e) {
            System.err.println("Exception: " +e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static PainterResource createResource(PropertySet properties) {
        PainterResource painterResource = new PainterResource();
        painterResource.setAll( properties);
        return painterResource;
    }
}
