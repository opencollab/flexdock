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
		Map accessoryDockingInfos = (Map) context.get("accessory.docking.infos");
		ViewDockingInfo viewDockingInfo = (ViewDockingInfo) accessoryDockingInfos.get(view.getPersistentId());

		boolean docked = false;
		if (viewDockingInfo != null) {
			View sourceView = (View) viewDockingInfo.getView();
			String region = viewDockingInfo.getRegion();
			float ratio = viewDockingInfo.getRatio();

//			if (viewDockingInfo.isFloating()) {
//				Point locationOnScreen = viewDockingInfo.getFloatingLocation();
//				Dimension dim = viewDockingInfo.getFloatingWindowDimension();
//				sourceView.doc
//			}
			
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
		}

		return docked;
	}

}
