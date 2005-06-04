/*
 * Created on 2005-06-03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.persist.proposal;

import java.io.IOException;

import org.flexdock.perspective.persist.PerspectiveInfo;

/**
 * @author mati
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface PersisterGateway {
    
    boolean store(String appKey, PerspectiveInfo perspectiveInfo) throws IOException;

    PerspectiveInfo load(String appKey) throws IOException;

}
