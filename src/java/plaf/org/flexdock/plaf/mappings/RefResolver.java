/*
 * Created on Feb 27, 2005
 */
package org.flexdock.plaf.mappings;

/**
 * @author Christopher Butler
 */
public class RefResolver {
    private String defaultRef;

    public void setDefaultRef(String ref) {
        defaultRef = ref;
    }

    public String getDefaultRef() {
        return defaultRef;
    }

    public String getRef(String plaf) {
        return getDefaultRef();
    }
}
