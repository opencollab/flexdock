/*
 * Created on 21.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.ext;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;

import org.flexdock.view.plaf.resources.ColorResourceHandler;
import org.flexdock.view.plaf.resources.ResourceHandler;

/**
 * @author cro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RoundedBorderResource extends ResourceHandler {
    private static final ColorUIResource DEFAULT_COLOR = new ColorUIResource(Color.BLACK);
    
    public Object getResource(String data) {
//      pattern should be "lineWidth, color"
		String[] args = getArgs(data);
		int lineWidth = args.length>0? getInt(args[0]): 1;
		ColorUIResource lightColor = args.length>1? getColor(args[1]): DEFAULT_COLOR;
		
		return new RoundedBorder(lineWidth, lightColor);
	}
    
	private int getInt(String data) {
		try {
			return Integer.parseInt(data);
		} catch(Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	private ColorUIResource getColor(String data) {
		ColorUIResource color = ColorResourceHandler.parseHexColor(data);
		return data==null? DEFAULT_COLOR: color;
	}
    
    public static class RoundedBorder implements Border {
		private int lineWidth;
		private Color color;
		private Insets insets;
		
		public RoundedBorder(int lineWidth, Color color) {
			this.lineWidth = lineWidth;
			this.color = color;
			insets = new Insets(0, 0, 0, 0);
		}

		public Insets getBorderInsets(Component c) {
			return insets;
		}

		public boolean isBorderOpaque() {
			return true;
		}
		
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			//Color saved = g.getColor();
		    y = 2;
		    height = height - 4;
			
			g.setColor(color);
	        int y2 = y + height - 1;

	        // draw horizontal lines
	        g.drawLine(1, y, width - 2, y);
	        g.drawLine(1, y2, width - 2, y2);

	        // draw vertical lines
	        g.drawLine(0, y + 1, 0, y2 - 1);
	        g.drawLine(width - 1, y + 1, width - 1, y2 - 1);
		}
	}
}
