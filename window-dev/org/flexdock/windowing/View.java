/*
 * Created on Feb 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.windowing;

import java.awt.Component;
import java.awt.Container;


/**
 * @author Christopher Butler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface View {
	public Titlebar getTitlebar();
	public void setTitlebar(Titlebar titlebar);
	public Container getContentPane();
	public void setContentPane(Container c);
	public void setTitle(String title);
	public String getTitle();
	public Component self();

}
