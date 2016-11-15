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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.StringTokenizer;




/**
 * @author Christopher Butler
 */
public class ConstructorHandler extends ResourceHandler {

    private Constructor constructor;

    public ConstructorHandler(Constructor constructor) {
        this.constructor = constructor;
    }

    public Object getResource(String stringValue) {
        Object[] arguments = getArguments(stringValue);
        try {
            return constructor.newInstance(arguments);
        } catch(Exception e) {
            System.err.println("Exception: " +e.getMessage());
            return null;
        }
    }

    private Object[] getArguments(String data) {
        String[] supplied = parseArguments(data);
        Object[] arguments = new Object[supplied.length];
        Class[] paramTypes = constructor.getParameterTypes();
        if(arguments.length!=paramTypes.length) {
            throw new IllegalArgumentException("Cannot match '" + data + "' to constructor " + constructor + ".");
        }

        for(int i=0; i<paramTypes.length; i++) {
            arguments[i] = toObject(supplied[i], paramTypes[i]);
        }
        return arguments;
    }

    private Object toObject(String data, Class type) {
        if(type==int.class) {
            return new Integer(data);
        }
        if(type==long.class) {
            return new Long(data);
        }
        if(type==boolean.class) {
            return new Boolean(data);
        }
        if(type==float.class) {
            return new Float(data);
        }
        if(type==double.class) {
            return new Double(data);
        }
        if(type==byte.class) {
            return new Byte(data);
        }
        if(type==short.class) {
            return new Short(data);
        }

        return data;
    }

    private String[] parseArguments(String data) {
        if(!data.endsWith(",")) {
            data += ",";
        }

        ArrayList args = new ArrayList();
        StringTokenizer st = new StringTokenizer(data, ",");
        while(st.hasMoreTokens()) {
            args.add(st.nextToken().trim());
        }
        return (String[])args.toArray(new String[0]);
    }
}
