/*
 * Created on May 26, 2005
 */
package org.flexdock.perspective;

/**
 * @author Christopher Butler
 */
public interface PerspectiveFactory {

    Perspective getPerspective(String persistentId);

}
