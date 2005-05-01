package org.flexdock.view.restore;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Map;

import javax.swing.JWindow;

import org.flexdock.view.View;
import org.flexdock.view.Viewport;

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
				JWindow window = new JWindow();
				window.setLayout(new BorderLayout());
				Point locationOnScreen = accessoryDockingInfo.getFloatingLocation();
				Dimension dim = accessoryDockingInfo.getFloatingWindowDimension();
				window.setLocation(locationOnScreen);
				window.setSize(dim);
				Viewport viewport = new Viewport("some view port");
				window.add(viewport, BorderLayout.CENTER);
				viewport.dock(sourceView);
				window.setVisible(true);
				return true;
				//sourceView.doc
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
