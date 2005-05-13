/*
 * Created on Apr 28, 2005
 */
package org.flexdock.docking.defaults.layout;

import org.flexdock.util.DockingConstants;

/**
 * @author Christopher Butler
 */
public class SplitNode implements DockingConstants {
	private int orientation;
	private int region;
	private float percentage;
	private String siblingId;
	
	public SplitNode(int orientation, int region, float percentage, String siblingId) {
		this.orientation = orientation;
		this.region = region;
		this.percentage = percentage;
		this.siblingId = siblingId;
	}
	
	public int getOrientation() {
		return orientation;
	}
	public float getPercentage() {
		return percentage;
	}
	public int getRegion() {
		return region;
	}

	public String getSiblingId() {
		return siblingId;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("SplitNode[");
		sb.append("orient=").append(getOrientationDesc()).append("; ");
		sb.append("region=").append(getRegionDesc()).append("; ");
		sb.append("percent=").append(percentage).append("%;");
		sb.append("]");
		return sb.toString();
	}
	
	private String getRegionDesc() {
		switch(region) {
			case TOP:
				return "top";
			case BOTTOM:
				return "bottom";
			case RIGHT:
				return "right";
			default:
				return "left";
		}
	}
	
	private String getOrientationDesc() {
		return orientation==VERTICAL? "vertical": "horizontal";
	}
}
