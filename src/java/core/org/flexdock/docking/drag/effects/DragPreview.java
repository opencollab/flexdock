package org.flexdock.docking.drag.effects;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Polygon;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;

public interface DragPreview {
	public Polygon createPreviewPolygon(Component dockable, DockingPort port, Dockable hover, String targetRegion, Component paintingTarget);
	public void drawPreview(Graphics2D g, Polygon poly);
}
