/*
 * Created on Apr 17, 2005
 */
package org.flexdock.dockbar.util;



/**
 * @author Christopher Butler
 */
public class Animation { 
//implements Runnable, ActionListener, SwingConstants {
	// instance data
/*
//	private ToolWindow mPanel;
	private int mRestore;
	private Timer mTimer;
	private long mStartTime;
	private int mStart;
	private int mEnd;
	private int mOrientation;
	private String mName;

	// constructor

	public Animation(String name, int orientation, int start, int end, int restore) {
		mPanel = panel;
		mRestore = restore;
		mOrientation = orientation;
		mStart = start;
		mEnd = end;
		mName = name;

		setSize(start);
	} // if

	public String getName() {
		return mName;
	}

	private void setSize(int size) {
		Rectangle bounds = mPanel.getBounds();
		int offset;
		switch (mOrientation) {
				case BOTTOM:
				offset = size - bounds.height;
				bounds.height = size;
				bounds.y -= offset;
				break;
	
			case RIGHT:
				offset = size - bounds.width;
				bounds.width = size;
				bounds.x -= offset;
				break;
	
			case LEFT:
				bounds.width = size;
				break;
		} // switch

		mPanel.setBounds(bounds);
		if (isPinned())
			mFrame.validate();
		else
			mPanel.validate();
	}

	public void run() {
		mStartTime = System.currentTimeMillis();
		(mTimer = new Timer(ANIMATION_INTERVAL, this)).start();
		mPanel.setVisible(true);
	}

	// public

	public void actionPerformed(ActionEvent e) {
		long now = System.currentTimeMillis();
		if (now - mStartTime < ANIMATION_TIME) {
			float percentage = (float) ((now - mStartTime)) / ANIMATION_TIME;

			setSize((int) (mStart + (mEnd - mStart) * percentage));
			mFrame.validate();
		} // if
		else { // done...
			mTimer.stop();
			mTimer = null;

			setSize(mRestore);
			mFrame.validate();

			finished(); // callback

			mFrame.finishedAnimation(this);
		} // else
	}

	public void finished() {
		
	}
	*/
}