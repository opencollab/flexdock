/*
 * Created on Feb 27, 2005
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
        if(data==null)
            return new String[0];

        if(!data.endsWith(","))
            data += ",";

        ArrayList args = new ArrayList(3);
        for(StringTokenizer st = new StringTokenizer(data, ","); st.hasMoreTokens();) {
            args.add(st.nextToken().trim());
        }
        return (String[])args.toArray(new String[0]);
    }
}
