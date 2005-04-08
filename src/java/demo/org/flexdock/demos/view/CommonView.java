
package org.flexdock.demos.view;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;

/**
 * @author Mateusz Szczap
 */
public class CommonView extends View {

	private ViewDockingInfo m_viewDockingInfo = null;
	private ViewDockingInfo m_accessoryDockingInfo = null;
	
	public CommonView(String name, String title, ViewDockingInfo dockingInfo) {
		super(name);
		m_viewDockingInfo = dockingInfo;
		setTitle(title, true);
		addAction(new CloseAction());
		addDockingListener(new DockingHandler());
	}

	public ViewDockingInfo getViewDockingInfo() {
		return m_viewDockingInfo;
	}
	
	public ViewDockingInfo getAccessoryDockingInfo() {
		return m_accessoryDockingInfo;
	}
	
	private class CloseAction extends AbstractAction {

		private CloseAction() {
			putValue(Action.NAME, "close");
			putValue(Action.SHORT_DESCRIPTION, "Close");
			putValue(Action.ACTION_COMMAND_KEY, "close");
		}

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            DockingManager.undock(CommonView.this);
		}
		
	}

	private class DockingHandler extends DockingListener.DockingAdapter {

		/**
		 * @see org.flexdock.docking.event.DockingListener#dockingComplete(org.flexdock.docking.event.DockingEvent)
		 */
		public void dockingComplete(DockingEvent dockingEvent) {
			if (dockingEvent.getNewDockingPort() instanceof Viewport) {
				Viewport viewPort = (Viewport) dockingEvent.getNewDockingPort();
				if (!viewPort.getViewset().isEmpty()) {
					for (Iterator it = viewPort.getViewset().iterator(); it.hasNext();) {
						CommonView commonView = (CommonView) it.next();
						if (!commonView.equals(CommonView.this)) {
							String region = dockingEvent.getRegion();
							Float ratioObject = getDockingProperties().getRegionInset(region);
							float ratio = -1.0f;
							if (ratioObject != null) {
								ratio = ratioObject.floatValue();
							} else {
								ratio = 0.5f;
							}
							m_accessoryDockingInfo = new ViewDockingInfo(commonView, region, ratio);
						}
					}
				}
			}
		}

	}
	
}
