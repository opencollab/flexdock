/*
 * Created on Feb 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf.resolvers;

import java.lang.reflect.Method;

import javax.swing.UIManager;

import org.flexdock.windowing.plaf.ViewFactory;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SkinLFResolver extends PlafBasedViewResolver {
	public static final String SKINLF_CLASS = "com.l2fprod.gui.plaf.skin.SkinLookAndFeel";
	public static final String GET_SKIN_METHOD = "getSkin";
	public static final Class[] EMPTY_METHOD_PARAMS = {};
	public static final Object[] EMPTY_METHOD_ARGS = {};

	
	public String getView(String plaf) {
		String currentSkin = null;
		try {
			Class skinlf = Class.forName(SKINLF_CLASS);
			Method getSkin = skinlf.getDeclaredMethod(GET_SKIN_METHOD, EMPTY_METHOD_PARAMS);			
			Object skinObj = getSkin.invoke(null, EMPTY_METHOD_ARGS);
			if(skinObj==null)
				return getDefaultView();
			currentSkin = skinObj.getClass().getName();
		} catch(Exception e) {
			e.printStackTrace();
			return getDefaultView();
		}
		
		// redirect to the mapping for the skin, instead of the plaf itself
		String view = ViewFactory.getPlafView(currentSkin);
		return view==null? getDefaultView(): view;
	}
	
	public String getDefaultView() {
		String systemPlaf = UIManager.getSystemLookAndFeelClassName();
		return ViewFactory.getPlafView(systemPlaf);
	}

}
