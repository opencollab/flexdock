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
import org.flexdock.windowing.View;
import org.flexdock.windowing.plaf.mappings.PlafMappingFactory;
import org.flexdock.windowing.plaf.titlebar.TitlebarUI;
import org.flexdock.windowing.plaf.titlebar.TitlebarUIFactory;
import org.flexdock.windowing.plaf.view.ViewUI;
import org.flexdock.windowing.plaf.view.ViewUIFactory;

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
		String viewRef = PlafMappingFactory.getInstalledPlafReference();
		ViewUI viewUI = ViewUIFactory.getUI(viewRef);
		String tbarRef = viewUI==null? null: viewUI.getPreferredTitlebarUI();
		TitlebarUI titlebarUI = TitlebarUIFactory.getUI(tbarRef);

		uiDefaults.put(View.class, viewUI);
		uiDefaults.put(TitleBar.class, titlebarUI);
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
