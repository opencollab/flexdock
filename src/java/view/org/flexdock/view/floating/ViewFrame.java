/* Copyright (c) 2004 Andreas Ernst

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

package org.flexdock.view.floating;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import org.flexdock.docking.DockingPort;
import org.flexdock.view.View;
import org.flexdock.view.viewport.Viewport;

/**
 * @author Andreas Ernst
 * @author Christopher Butler
 */
public class ViewFrame extends JDialog implements FocusListener {
	private Viewport viewport;
	
	public static ViewFrame create(Component c) {
		Window window = c==null? null: SwingUtilities.getWindowAncestor(c);
		if(window instanceof ViewFrame) {
			window = ((ViewFrame)window).getOwner();
		}
		
		if(window instanceof Frame)
			return new ViewFrame((Frame)window);
		if(window instanceof Dialog)
			return new ViewFrame((Dialog)window);		
		
		return null;
	}
	
    // constructor
    public ViewFrame(Frame owner) {
        super(owner);
        initialize();
    }

    public ViewFrame(Dialog owner) {
        super(owner);
        initialize();
    }

    // private

    private void initialize() {
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

		viewport = new FloatingViewport(this);
		setContentPane(viewport);
		addFocusListener(this);
    }

    // override

    protected JRootPane createRootPane() {
        return new RootPane(this);
    }
    
    public DockingPort getDockingPort() {
    	return viewport;
    }
    
    public void addView(View view) {
    	if(view==null)
    		return;
    	
    	viewport.dock(view);
    }

	public void focusGained(FocusEvent e) {
		viewport.requestActivation(null);
	}

	public void focusLost(FocusEvent e) {
	
	}
	
	public void destroy() {
		setVisible(false);
		viewport = null;
		dispose();
	}
}