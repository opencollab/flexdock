/*
 * Created on Mar 17, 2005
 */
package org.flexdock.docking.props;

import java.util.List;
import java.util.Map;

/**
 * @author cb8167
 */
public interface ScopedMap {
	public Map getDefaults();
	public Map getGlobals();
	public List getPropertyMaps();
}
