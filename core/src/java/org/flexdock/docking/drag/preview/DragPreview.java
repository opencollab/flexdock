package org.flexdock.docking.drag.preview;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface DragPreview {
	public void drawPreview(Graphics2D g, Rectangle rect);
}
