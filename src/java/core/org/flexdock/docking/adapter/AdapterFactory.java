/*
 * Created on Jun 24, 2005
 */
package org.flexdock.docking.adapter;

import java.awt.Component;
import java.util.Hashtable;

import org.flexdock.util.ResourceManager;
import org.w3c.dom.Document;

/**
 * @author Christopher Butler
 */
public class AdapterFactory {
    
    public static final String ADAPTER_RESOURCE_KEY = "flexdock.adapters";
    public static final String DEFAULT_ADAPTER_RESOURCE = "flexdock-adapters.xml";
    private static final Hashtable MAPPINGS_BY_CLASS = new Hashtable();
    
    public static void prime() {
        loadMappings();
    }
    
    public static DockingAdapter getAdapter(Component comp) {
        if(comp==null)
            return null;
        
        AdapterMapping mapping = getMapping(comp);
        if(mapping==null)
            return null;
        
        DockingAdapter adapter = new DockingAdapter(comp, mapping);
        // validate the adapter before returning
        if(adapter.getComponent()==null || adapter.getPersistentId()==null)
            return null;
        
        return adapter;
    }
    
    private static AdapterMapping getMapping(Object obj) {
        String className = obj.getClass().getName();
        return (AdapterMapping)MAPPINGS_BY_CLASS.get(className);
    }

    private static void loadMappings() {
        String uri = System.getProperty(ADAPTER_RESOURCE_KEY);
        if(uri==null)
            uri = DEFAULT_ADAPTER_RESOURCE;
        
        Document document = ResourceManager.getDocument(uri);
        if(document==null && !DEFAULT_ADAPTER_RESOURCE.equals(uri))
            document = ResourceManager.getDocument(DEFAULT_ADAPTER_RESOURCE);
        
        if(document==null)
            return;
        
        MappingReader reader = new MappingReader();
        AdapterMapping[] mappings = reader.readMappings(document);
        for(int i=0; i<mappings.length; i++) {
            MAPPINGS_BY_CLASS.put(mappings[i].getClassName(), mappings[i]);
        }
    }

}
