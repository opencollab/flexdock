/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.mappings;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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
