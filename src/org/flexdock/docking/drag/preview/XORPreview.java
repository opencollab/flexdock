package org.flexdock.docking.drag.preview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class XORPreview implements DragPreview {
	
	public void drawPreview(Graphics2D g, Rectangle rect) {
		int left = rect.x;
		int top = rect.y;
		int w = rect.width-1;
		int h = rect.height-1;

		g.setColor(Color.BLACK);
		g.setXORMode(Color.WHITE);

		Rectangle inner = new Rectangle(left+3, top+3, w-5, h-6);
		int right = left + w + 1;
		int bottom = top + h + 1;
		
		for(int y=top; y<bottom; y++) {
			int start = y%2==0? left: left+1;
			for(int x=start; x<right; x+=2) {
				if(!inner.contains(x, y))
					g.drawLine(x, y, x, y);
			}
		}
	}
}
