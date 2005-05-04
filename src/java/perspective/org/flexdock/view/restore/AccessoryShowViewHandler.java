package org.flexdock.view.restore;

import java.awt.Component;
import java.util.Map;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.floating.FloatManager;
import org.flexdock.docking.floating.frames.DockingFrame;
import org.flexdock.util.RootWindow;
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
		ViewDockingInfo accessoryDockingInfo = (ViewDockingInfo) context.get("accessory.docking.info");

		boolean docked = false;
		if (accessoryDockingInfo != null) {
			View sourceView = (View) accessoryDockingInfo.getView();

			if (accessoryDockingInfo.isFloating()) {
				
				// TODO: fix this code to keep track of the proper dialog owner
				RootWindow[] windows = DockingManager.getDockingWindows();
				if(windows.length==0)
					return false;
				
				Component owner = windows[0].getRootContainer();
				DockingFrame frame = FloatManager.getInstance().floatDockable(territoralView, owner);
				frame.addDockable(view);
				
/*
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
				*/
				return true;
			} else if (accessoryDockingInfo.isMinimized()) {
				DockingManager.setMinimized(sourceView, true, accessoryDockingInfo.getDockbarEdge());
				if (sourceView.isMinimized()) {
					return true;
				}
			}

			String region = accessoryDockingInfo.getRegion();
			float ratio = accessoryDockingInfo.getRatio();

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
