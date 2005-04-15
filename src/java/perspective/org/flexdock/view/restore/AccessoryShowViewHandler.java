package org.flexdock.view.restore;

import java.util.Map;

import org.flexdock.view.View;

/**
 * @author Mateusz Szczap
 */
public class AccessoryShowViewHandler implements ShowViewHandler {

	/**
	 * @see org.flexdock.view.restore.ShowViewHandler#showView(org.flexdock.view.View)
	 */
	public boolean showView(View view, Map context) {
		View territoralView = (View) context.get("territoral.view");
		ViewDockingInfo[] accessoryDockingInfos = (ViewDockingInfo[]) context.get("accessory.docking.infos");

		boolean docked = false;
		if (accessoryDockingInfos != null && accessoryDockingInfos.length > 0) {
			for (int i=0; i<accessoryDockingInfos.length; i++) {
				View sourceView = accessoryDockingInfos[i].getView();
				String region = accessoryDockingInfos[i].getRegion();
				float ratio = accessoryDockingInfos[i].getRatio();
				if (sourceView == territoralView) {
					View siblingView = (View) sourceView.getSibling(region);
					if (siblingView != null) {
						docked = siblingView.dock(view);
					} else {
						docked = sourceView.dock(view, region, ratio);
					}
				} else {
					docked = sourceView.dock(view, region, ratio);
				}
				if (docked) break;
			}
		}

		return docked;
	}

}
