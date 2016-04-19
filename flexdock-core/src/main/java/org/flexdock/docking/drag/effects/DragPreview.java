package org.flexdock.docking.drag.effects;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingPort;

public interface DragPreview extends DockingConstants {
    public Polygon createPreviewPolygon(Component dockable, DockingPort port, Dockable hover, String targetRegion, Component paintingTarget, Map dragInfo);
    public void drawPreview(Graphics2D g, Polygon poly, Dockable dockable, Map dragInfo);
}
