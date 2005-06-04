/*
 * Created on 2005-06-03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.persist.xml;

import java.util.HashMap;

/**
 * @author mati
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SerializerRegistry {

    private static HashMap m_serializers = new HashMap();
    
    public static void registerSerializer(Class clazz, ISerializer serializer) {
        if (clazz == null) throw new IllegalArgumentException("clazz cannot be null");
        if (serializer == null) throw new IllegalArgumentException("serializer cannot be null");

        m_serializers.put(clazz, serializer);
    }
    
    public static ISerializer getSerializer(Class clazz) {
        if (clazz == null) throw new IllegalArgumentException("clazz cannot be null");

        return (ISerializer) m_serializers.get(clazz);
    }
    
}
