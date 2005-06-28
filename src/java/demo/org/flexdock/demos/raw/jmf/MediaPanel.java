/*
 * Created on Jun 27, 2005
 */
package org.flexdock.demos.raw.jmf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Label;
import java.awt.Panel;
import java.net.URL;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.Time;

import org.flexdock.docking.DockingStub;
import org.flexdock.util.ResourceManager;

/**
 * @author Christopher Butler
 */
public class MediaPanel extends Panel implements DockingStub {
	private Player player;
	private Label titlebar;
	private String dockingId;
	
	public MediaPanel(String id, String title, String mediaFileName) {
		dockingId = id;
		setLayout(new BorderLayout());
		player = createPlayer(mediaFileName);
		
		titlebar = new Label(title);
		titlebar.setBackground(new Color(183, 201, 217));
		Component viewscreen = player.getVisualComponent();
		Component controls = player.getControlPanelComponent();

		add(titlebar, BorderLayout.NORTH);
		add(viewscreen, BorderLayout.CENTER);
		add(controls, BorderLayout.SOUTH);
	}
	
	private Player createPlayer(String mediaUri) {
		try {
			URL url = ResourceManager.getResource(mediaUri);
			MediaLocator locator = new MediaLocator(url);
			Player mediaPlayer = Manager.createRealizedPlayer(locator);
			
			// add a listener to put us in an infinite loop
			mediaPlayer.addControllerListener(new ControllerListener() {
				public void controllerUpdate(ControllerEvent evt) {
					if(evt instanceof EndOfMediaEvent) {
						player.setMediaTime(new Time(0));
						player.start();
					}
				}
			});
			return mediaPlayer;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected void finalize() {
		if(player!=null) {
			player.stop();
			player.close();
			player = null;
		}
	}
	
	public Component getDragSource() {
		return titlebar;
	}
	
	public Component getFrameDragSource() {
		return titlebar;
	}
	
	public String getPersistentId() {
		return dockingId;
	}
	
	public String getTabText() {
		return titlebar.getText();
	}
}
