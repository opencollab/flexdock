package org.flexdock.demos.raw.elegant;

import javax.swing.JComponent;

import org.flexdock.demos.util.GradientTitlebar;
import org.flexdock.demos.util.Titlebar;
import org.flexdock.demos.util.Titlepane;
import org.flexdock.docking.Dockable;


public class ElegantPanel extends Titlepane {
	private Dockable dockable;

	public ElegantPanel(String title) {
		super(title);
	}
	
	public Dockable getDockable() {
		if(dockable==null) {
			dockable = new DockableImpl(this, getTitlebar(), getTitle());
		}
		return dockable;
	}
	
	public void dock(ElegantPanel otherPanel) {
		if(otherPanel!=null)
			getDockable().dock(otherPanel.getDockable());		
	}
	
	public void dock(ElegantPanel otherPanel, String region) {
		if(otherPanel!=null)
			getDockable().dock(otherPanel.getDockable(), region);		
	}
	
	public void dock(ElegantPanel otherPanel, String region, float ratio) {
		if(otherPanel!=null)
			getDockable().dock(otherPanel.getDockable(), region, ratio);
	}
	
	protected JComponent createContentPane() {
		return null;
	}
	
	protected Titlebar createTitlebar(String title) {
		return new GradientTitlebar(title);
	}
}
