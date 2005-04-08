/*
 * Created on Mar 15, 2005
 */
package org.flexdock.docking.drag.effects;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Properties;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;
import org.flexdock.util.ResourceManager;
import org.flexdock.util.Utilities;

/**
 * @author Christopher Butler
 *
 */
public class EffectsFactory {
	private static final String CONFIG_URI = "org/flexdock/docking/drag/effects/drag-effects.properties";
	private static final String DEFAULT_PREVIEW_KEY = "default.preview";
	private static final Object LOCK = new Object();
	private static final Properties PROPERTIES = loadConfig();
	private static final DragPreview DEFAULT_PREVIEW = loadDefaultPreview();
	private static DragPreview CUSTOM_PREVIEW;
	private static final RubberBand DEFAULT_RUBBERBAND = loadSystemRubberband();
	private static RubberBand CUSTOM_RUBBERBAND;
	
	
	public static void prime() {
		// no-op.  just a public means of allowing our static initializers to run
	}
	
	private static final Properties loadConfig() {
		Properties p = ResourceManager.getProperties(CONFIG_URI);
		return p==null? new Properties(): p;
	}
	
	
	
	
	private static RubberBand loadSystemRubberband() {
		String osFamily = Utilities.OS_FAMILY;
		String implClass = PROPERTIES.getProperty(osFamily);
		RubberBand rb = createRubberBand(implClass);
		return rb==null? new RubberBand(): rb;
	}
	
	private static final DragPreview loadDefaultPreview() {
		String previewClass = PROPERTIES.getProperty(DEFAULT_PREVIEW_KEY);
		DragPreview preview = createPreview(previewClass);
		if(preview!=null)
			return preview;
		// unable to load the preview class.  return a no-op preview delegate instead.
		return new DefaultPreview() {
			public void drawPreview(Graphics2D g, Polygon poly) {
				// noop
			}
		};
	}

	
	
	private static RubberBand createRubberBand(String implClass) {
		return (RubberBand)Utilities.createInstance(implClass, RubberBand.class);
	}
	
	private static DragPreview createPreview(String implClass) {
		return (DragPreview)Utilities.createInstance(implClass, DragPreview.class);
	}
	
	
	
	public static RubberBand getRubberBand() {
		synchronized(LOCK) {
			return CUSTOM_RUBBERBAND==null? DEFAULT_RUBBERBAND: CUSTOM_RUBBERBAND;	
		}
	}
	
	public static DragPreview getPreview(Dockable dockable, DockingPort target) {
		synchronized(LOCK) {
			return CUSTOM_PREVIEW==null? DEFAULT_PREVIEW: CUSTOM_PREVIEW;	
		}
	}	
	
	
	public RubberBand setRubberBand(String implClass) {
		RubberBand rb = createRubberBand(implClass);
		if(implClass!=null && rb==null)
			return null;
		
		setRubberBand(rb);
		return rb;
	}
	
	public void setRubberBand(RubberBand rubberBand) {
		synchronized(LOCK) {
			CUSTOM_RUBBERBAND = rubberBand;	
		}		
	}
	
	
	public DragPreview setPreview(String implClass) {
		DragPreview preview = createPreview(implClass);
		if(implClass!=null && preview==null)
			return null;
		
		setPreview(preview);
		return preview;
	}
	
	public static void setPreview(DragPreview preview) {
		synchronized(LOCK) {
			CUSTOM_PREVIEW = preview;	
		}		
	}
	

}
