/*
 * Created on Mar 12, 2005
 */
package org.flexdock.docking;

/**
 * @author Christopher Butler
 */
public class ScaledInsets implements Cloneable {
	public float top;
	public float bottom;
	public float left;
	public float right;

	public ScaledInsets() {
		this(-1, -1, -1, -1);
	}
	
	public ScaledInsets(float all) {
		this(all, all, all, all);
	}
	
	public ScaledInsets(int all) {
		this(all, all, all, all);
	}
	
	public ScaledInsets(int top, int left, int bottom, int right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	public ScaledInsets(float top, float left, float bottom, float right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	public int hashCode() {
		int sum1 = (int) (left + bottom);
		int sum2 = (int) (right + top);
		int val1 = sum1 * (sum1 + 1) / 2 + (int) left;
		int val2 = sum2 * (sum2 + 1) / 2 + (int) top;
		int sum3 = val1 + val2;
		return sum3 * (sum3 + 1) / 2 + val2;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof ScaledInsets) {
			ScaledInsets other = (ScaledInsets)obj;
			return top==other.top && left==other.left && bottom==other.bottom && right==other.right;
		}
		return false;
	}

	public String toString() {
		return "ScaledInsets[top=" + top + ",left=" + left + ",bottom=" + bottom + ",right=" + right + "]";
	}
	
	public float getRegion(String region) {
		if(DockingPort.NORTH_REGION.equals(region))
			return top;
		if(DockingPort.SOUTH_REGION.equals(region))
			return bottom;
		if(DockingPort.EAST_REGION.equals(region))
			return right;
		if(DockingPort.WEST_REGION.equals(region))
			return left;
		return -1;
	}
	
	public void setRegion(float size, String region) {
		if(DockingPort.NORTH_REGION.equals(region))
			top = size;
		else if(DockingPort.SOUTH_REGION.equals(region))
			bottom = size;
		else if(DockingPort.EAST_REGION.equals(region))
			right = size;
		else if(DockingPort.WEST_REGION.equals(region))
			left = size;
	}
}