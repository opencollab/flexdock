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
package org.flexdock.plaf.mappings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;



import org.flexdock.plaf.Configurator;
import org.flexdock.plaf.XMLConstants;
import org.w3c.dom.Element;

/**
 * @author Christopher Butler
 */
public class PlafMappingFactory implements XMLConstants {

    public static final String PLAF_KEY = "plaf";
    private static final HashMap PLAF_MAPPINGS = loadPlafMappings();

    public static List getAvailablePlafNames() {
        return new ArrayList(PLAF_MAPPINGS.keySet());
    }

    public static String getInstalledPlafReference() {
        LookAndFeel currentPlaf = UIManager.getLookAndFeel();
        if(currentPlaf==null) {
            return null;
        }

        String key = currentPlaf.getClass().getName();
        return getPlafReference(key);
    }

    public static String getPlafReference(String key) {
        if(key==null) {
            return null;
        }

        Object value = PLAF_MAPPINGS.get(key);
        if(value instanceof String) {
            return (String)value;
        }

        // if not a String, then we must have a RefResolver
        if(value instanceof RefResolver) {
            RefResolver resolver = (RefResolver)value;
            return resolver.getRef(key);
        }
        return null;
    }

    private static HashMap loadPlafMappings() {
        HashMap elements = Configurator.getNamedElementsByTagName(PLAF_KEY);
        HashMap mappings = new HashMap(elements.size());

        for(Iterator it=elements.keySet().iterator(); it.hasNext();) {
            String key = (String)it.next();
            Element elem = (Element)elements.get(key);

            String name = elem.getAttribute(NAME_KEY);
            String ref = elem.getAttribute(REFERENCE_KEY);
            String resolver = elem.getAttribute(HANDLER_KEY);
            Object value = createPlafMapping(ref, resolver);
            mappings.put(name, value);
        }
        return mappings;
    }


    private static Object createPlafMapping(String refName, String resolverName) {
        if(Configurator.isNull(resolverName)) {
            return refName;
        }

        RefResolver resolver = null;
        try {
            Class clazz = Class.forName(resolverName);
            // must be a type of PlafBasedViewResolver
            resolver = (RefResolver)clazz.newInstance();
        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            return refName;
        }

        // setup the default value on the resolver and return
        resolver.setDefaultRef(refName);
        return resolver;
    }
}
