package org.flexdock.view.restore;

import java.util.List;
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
		List accessoryDockingInfos = (List) context.get("accessory.docking.infos");

		boolean docked = false;
		if (accessoryDockingInfos != null && accessoryDockingInfos.size() > 0) {
			for (int i=0; i<accessoryDockingInfos.size(); i++) {
				ViewDockingInfo viewDockingInfo = (ViewDockingInfo) accessoryDockingInfos.get(i);
				View sourceView = (View) viewDockingInfo.getView();
				String region = viewDockingInfo.getRegion();
				float ratio = viewDockingInfo.getRatio();
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
