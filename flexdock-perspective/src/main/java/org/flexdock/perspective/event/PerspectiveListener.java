/*
 * Created on 2005-03-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.event;

import java.util.EventListener;

/**
 * @author mateusz
 */
public interface PerspectiveListener extends EventListener {

    void perspectiveChanged(PerspectiveEvent evt);

    void perspectiveReset(PerspectiveEvent evt);

}
