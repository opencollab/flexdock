package org.flexdock.view.restore;

import java.util.EventObject;

import org.flexdock.view.View;

/**
 * 
 * @author Mateusz Szczap
 */
public class ViewStateEvent extends EventObject {

	public final static int VIEW_SHOWN = 0;
	public final static int VIEW_HIDDEN = 1;

	private int m_eventType = -1;
	private View m_view;
	
	public ViewStateEvent(Object source, View view, int eventType) {
		super(source);
		if (eventType < 0) {
			throw new IllegalArgumentException("Illegal eventType");
		}
		if (view == null) throw new IllegalArgumentException("View cannot be null");
		m_view = view;
		m_eventType = eventType;
	}
	
	public View getView() {
		return m_view;
	}
	
	public int getEventType() {
		return m_eventType;
	}
	
}
