/*
 * Created on May 30, 2005
 */
package org.flexdock.perspective.persist.proposal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.flexdock.perspective.persist.PerspectiveInfo;


/**
 * @author Christopher Butler
 */
public interface Persister {
    
    boolean store(OutputStream os, PerspectiveInfo info) throws IOException;

    PerspectiveInfo load(InputStream is) throws IOException;

}
