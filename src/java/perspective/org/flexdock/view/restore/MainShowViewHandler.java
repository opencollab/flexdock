package org.flexdock.view.restore;

import java.util.Map;

import org.flexdock.docking.DockingPort;
import org.flexdock.view.View;

/**
 * @author Mateusz Szczap
 */
public class MainShowViewHandler implements ShowViewHandler {

	/**
	 * @see org.flexdock.view.restore.ShowViewHandler#showView(org.flexdock.view.View, java.util.Map)
	 */
	public boolean showView(View view, Map context) {

		ViewDockingInfo mainViewDockingInfo = (ViewDockingInfo) context.get("main.docking.info");
		if (mainViewDockingInfo == null) return false;

		boolean docked = false;
		if (!docked) {
			View sourceView = mainViewDockingInfo.getView();
			String region = mainViewDockingInfo.getRegion();
			float ratio = mainViewDockingInfo.getRatio();
			if (region != DockingPort.UNKNOWN_REGION) {
				View siblingView = (View) sourceView.getSibling(region);
				if (siblingView != null) {
					docked = siblingView.dock(view);
				} else {
					docked = sourceView.dock(view, region, ratio);
				}
			} else {
				docked = sourceView.dock(view, region, ratio);
			}
			
		}

		return docked;
	}

}
