/*
 * Created on May 30, 2005
 */
package org.flexdock.perspective.persist;

import java.io.IOException;


/**
 * @author Christopher Butler
 */
public interface Persister {
	public boolean store(String appKey, PerspectiveInfo info) throws IOException;
	public PerspectiveInfo load(String appKey) throws IOException;
}
