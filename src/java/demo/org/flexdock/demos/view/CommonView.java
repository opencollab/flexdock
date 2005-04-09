
package org.flexdock.demos.view;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.view.View;

/**
 * @author Mateusz Szczap
 */
public class CommonView extends View {

	private ViewDockingInfo m_mainDockingInfo = null;
	
	private PreservingStrategy m_preservingStrategy = new SimplePreservingStrategy();
	
	public CommonView(String name, String title, ViewDockingInfo viewDockingInfo) {
		super(name);
		m_mainDockingInfo = viewDockingInfo;
		setTitle(title, true);
		addAction(new CloseAction());
		addDockingListener(new DockingHandler());
	}

	public ViewDockingInfo getMainViewDockingInfo() {
		return m_mainDockingInfo;
	}
	
	public void setPreservingStrategy(PreservingStrategy preservingStrategy) {
		//TODO property changed fire?
		m_preservingStrategy = preservingStrategy;
	}
	
	public PreservingStrategy getPreservingStrategy() {
		return m_preservingStrategy;
	}

	public ViewDockingInfo[] getAccessoryDockingInfos() {
		return m_preservingStrategy.getAccessoryDockingInfos();
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
			DockingPort dockingPort = dockingEvent.getNewDockingPort();
			String region = dockingEvent.getRegion();
			m_preservingStrategy.preserve(CommonView.this, dockingPort, region);
		}

	}
	
}
