/*
 * Created on May 30, 2005
 */
package org.flexdock.perspective.persist;


/**
 * @author Christopher Butler
 */
public interface Persister {
	public boolean store(String appKey, PerspectiveInfo info);
	public PerspectiveInfo load(String appKey);
}
