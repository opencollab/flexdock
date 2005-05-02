package org.flexdock.view.restore;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Map;

import org.flexdock.docking.DockingManager;
import org.flexdock.view.View;
import org.flexdock.view.floating.ViewFrame;

/**
 * @author Mateusz Szczap
 */
public class AccessoryShowViewHandler implements ShowViewHandler {

	/**
	 * @see org.flexdock.view.restore.ShowViewHandler#showView(org.flexdock.view.View)
	 */
	public boolean showView(View view, Map context) {
		View territoralView = (View) context.get("territoral.view");
		ViewDockingInfo accessoryDockingInfo = (ViewDockingInfo) context.get("accessory.docking.info");

		boolean docked = false;
		if (accessoryDockingInfo != null) {
			View sourceView = (View) accessoryDockingInfo.getView();
			String region = accessoryDockingInfo.getRegion();
			float ratio = accessoryDockingInfo.getRatio();

			if (accessoryDockingInfo.isFloating()) {
				ViewFrame viewFrame = ViewFrame.create(territoralView);
				viewFrame.setLayout(new BorderLayout());
				viewFrame.setResizable(true);
				
				Point locationOnScreen = accessoryDockingInfo.getFloatingLocation();
				Dimension dim = accessoryDockingInfo.getFloatingWindowDimension();

				DockingManager.undock(sourceView);

				viewFrame.addView(sourceView);
				
				viewFrame.setLocation(locationOnScreen);
				viewFrame.setSize(dim);
				viewFrame.setVisible(true);
				return true;
			}
			
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
