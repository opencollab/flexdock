/*
 * Created on Feb 27, 2005
 */
package org.flexdock.plaf.mappings;

import java.lang.reflect.Method;

import javax.swing.UIManager;

/**
 * @author Christopher Butler
 */
public class SkinLFResolver extends RefResolver {
	public static final String SKINLF_CLASS = "com.l2fprod.gui.plaf.skin.SkinLookAndFeel";
	public static final String GET_SKIN_METHOD = "getSkin";
	public static final Class[] EMPTY_METHOD_PARAMS = {};
	public static final Object[] EMPTY_METHOD_ARGS = {};

	
	public String getRef(String plaf) {
		String currentSkin = null;
		try {
			Class skinlf = Class.forName(SKINLF_CLASS);
			Method getSkin = skinlf.getDeclaredMethod(GET_SKIN_METHOD, EMPTY_METHOD_PARAMS);			
			Object skinObj = getSkin.invoke(null, EMPTY_METHOD_ARGS);
			if(skinObj==null)
				return getDefaultRef();
			currentSkin = skinObj.getClass().getName();
		} catch(Exception e) {
			e.printStackTrace();
			return getDefaultRef();
		}
		
		// redirect to the mapping for the skin, instead of the plaf itself
		String view = PlafMappingFactory.getPlafReference(currentSkin);
		return view==null? getDefaultRef(): view;
	}
	
	public String getDefaultRef() {
		String systemPlaf = UIManager.getSystemLookAndFeelClassName();
		return PlafMappingFactory.getPlafReference(systemPlaf);
	}

}
