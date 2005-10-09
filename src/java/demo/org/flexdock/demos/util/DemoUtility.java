/*
 * Created on Aug 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.demos.util;

import javax.swing.JFrame;

public class DemoUtility
{
   public static void setDemoDisableExitOnClose()
   {
      System.setProperty("disable.system.exit", "true");
   }
   
   public static void setCloseOperation(JFrame f)
   {
      if(!Boolean.getBoolean("disable.system.exit"))
         f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      else
         f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
   }
}
