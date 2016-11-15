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

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @author Christopher Butler
 */
public class ResourceHandler {
    public Object getResource(String stringValue) {
        return stringValue;
    }

    protected String[] getArgs(String data) {
        if(data==null) {
            return new String[0];
        }

        if(!data.endsWith(",")) {
            data += ",";
        }

        ArrayList args = new ArrayList(3);
        for(StringTokenizer st = new StringTokenizer(data, ","); st.hasMoreTokens();) {
            args.add(st.nextToken().trim());
        }
        return (String[])args.toArray(new String[0]);
    }
}
