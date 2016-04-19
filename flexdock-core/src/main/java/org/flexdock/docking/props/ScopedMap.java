/*
 * Created on Mar 17, 2005
 */
package org.flexdock.docking.props;

import java.util.List;
import java.util.Map;

/**
 * @author Christopher Butler
 */
public interface ScopedMap {
    public Map getRoot();
    public List getDefaults();
    public List getLocals();
    public List getGlobals();
}
