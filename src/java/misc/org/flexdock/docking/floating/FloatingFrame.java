/* Copyright (c) 2004 Christopher M Butler

Permission is hereby granted, free of charge, to any person obtaining a copy of 
this software and associated documentation files (the "Software"), to deal in the 
Software without restriction, including without limitation the rights to use, 
copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the 
Software, and to permit persons to whom the Software is furnished to do so, subject 
to the following conditions:

The above copyright notice and this permission notice shall be included in all 
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. */
package org.flexdock.docking.floating;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JDialog;

public class FloatingFrame extends JDialog {
	private FramedPanel framedPanel;

	public FloatingFrame() {
		super();
		initialize(null);
	}

	public FloatingFrame(Dialog owner) {
		super(owner);
		initialize(null);
	}

	public FloatingFrame(Dialog owner, boolean modal) {
		super(owner, modal);
		initialize(null);
	}

	public FloatingFrame(Frame owner, boolean modal) {
		super(owner, modal);
		initialize(null);
	}

	public FloatingFrame(Dialog owner, String title) {
		super(owner);
		initialize(title);
	}

	public FloatingFrame(Dialog owner, String title, boolean modal) {
		super(owner, modal);
		initialize(title);
	}

	public FloatingFrame(Frame owner, String title) {
		super(owner);
		initialize(title);
	}

	public FloatingFrame(Frame owner, String title, boolean modal) {
		super(owner, modal);
		initialize(title);
	}

	public FloatingFrame(Frame owner) {
		super(owner);
		initialize(null);
	}
	
	private void initialize(String title) {
		// hide the real titlebar, since we don't have a lot of control
		// over its window decorations
		super.setUndecorated(true);

		// set the framed panel
		framedPanel = new FramedPanel();
		super.setContentPane(framedPanel);
		
		// set the title, if we have one
		if(title!=null)
			setTitle(title);
	}

	public String getTitle() {
		return framedPanel.getTitle();
	}

	public void setTitle(String title) {
		framedPanel.setTitle(title);
	}
	
	public void addWindowListener(WindowListener wl) {
		framedPanel.addWindowListener(wl);
	}
	
	public void setMaximizable(boolean b) {
		framedPanel.setMaximizable(b);
	}
	
	public void setCloseable(boolean b) {
		framedPanel.setCloseable(b);
	}
	
	public void setTitlebar(JComponent comp) {
		framedPanel.setTitlebar(comp);
	}

	public void setUndecorated(boolean undecorated) {
	}
}
