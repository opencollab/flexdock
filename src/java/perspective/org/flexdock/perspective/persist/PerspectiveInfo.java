/*
 * Created on May 30, 2005
 */
package org.flexdock.perspective.persist;

import java.io.Serializable;

import org.flexdock.perspective.Perspective;

/**
 * @author Christopher Butler
 */
public class PerspectiveInfo implements Serializable {
	private String defaultPerspective;
	private String currentPerspective;
	private Perspective[] perspectives;
	
	public PerspectiveInfo() {
		
	}
	
	public PerspectiveInfo(String defaultId, String current, Perspective[] perspectives) {
		this.defaultPerspective = defaultId;
		this.currentPerspective = current;
		this.perspectives = perspectives;
	}

	
	public String getDefaultPerspective() {
		return defaultPerspective;
	}
	public void setDefaultPerspective(String defaultPerspective) {
		this.defaultPerspective = defaultPerspective;
	}
	public Perspective[] getPerspectives() {
		return perspectives;
	}
	public void setPerspectives(Perspective[] perspectives) {
		this.perspectives = perspectives;
	}
	
	public String getCurrentPerspective() {
		return currentPerspective;
	}
	public void setCurrentPerspective(String currentPerspective) {
		this.currentPerspective = currentPerspective;
	}
}
