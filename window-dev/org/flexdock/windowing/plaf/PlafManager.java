/*
 * Created on Mar 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing.plaf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

import org.flexdock.docking.windows.util.TitleBar;
import org.flexdock.windowing.Button;
import org.flexdock.windowing.View;
import org.flexdock.windowing.plaf.mappings.PlafMappingFactory;
import org.flexdock.windowing.plaf.theme.Theme;
import org.flexdock.windowing.plaf.theme.UIFactory;

/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlafManager {
	private static final String UI_CHANGE_EVENT = "lookAndFeel";
	private static final Hashtable uiDefaults = new Hashtable();
	
	static {
		initialize();
	}
	
	private static void initialize() {
		installSystemPlafMapping();
		// install an updater so we can keep up with changes in the installed plaf
		UIManager.addPropertyChangeListener(new UiUpdater());
	}
	
	public static void installSystemPlafMapping() {
		String themeRef = PlafMappingFactory.getInstalledPlafReference();
		Theme theme = UIFactory.getTheme(themeRef);

		uiDefaults.put(View.class, theme.getViewUI());
		uiDefaults.put(TitleBar.class, theme.getTitlebarUI());
		uiDefaults.put(Button.class, theme.getButtonUI());
	}
	
	public static ComponentUI getUI(JComponent target) {
		return target==null? null: (ComponentUI)uiDefaults.get(target.getClass());
	}
	
	private static class UiUpdater implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if(UI_CHANGE_EVENT.equals(evt.getPropertyName()) && evt.getOldValue()!=evt.getNewValue()) {
				installSystemPlafMapping();
			}
		}
	}
}
