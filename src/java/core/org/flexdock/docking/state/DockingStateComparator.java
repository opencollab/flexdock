/*
 * Created on May 19, 2005
 */
package org.flexdock.docking.state;

import java.util.Comparator;

import org.flexdock.docking.state.tree.SplitNode;
import org.flexdock.util.DockingConstants;

/**
 * @author Christopher Butler
 */
public class DockingStateComparator implements Comparator {
	private static final int EMBEDDED = 0;
	private static final int MINIMIZED = 1;
	private static final int FLOATING = 2;
	
	public int compare(Object o1, Object o2) {
		DockingState d1 = (DockingState)o1;
		DockingState d2 = (DockingState)o2;
		if(d1==d2)
			return 0;

		int stateDiff = getStateWeight(d1) - getStateWeight(d2);
		return stateDiff==0? compareSameState(d1, d2): stateDiff;
	}
	
	private int getStateWeight(DockingState d) {
		if(d.isFloating())
			return FLOATING;
		if(d.isMinimized())
			return MINIMIZED;
		return EMBEDDED;
	}
	
	private int compareSameState(DockingState d1, DockingState d2) {
		// if they're both floating, then compare on this basis
		if(d1.isFloating()==d2.isFloating()) {
			return compareFloating(d1, d2);
		}
		
		// if they're both minimized, then compare on this basis
		if(d1.isMinimized()==d2.isMinimized()) {
			return compareMinimized(d1, d2);
		}
		
		// they both must be embedded.
		return compareEmbedded(d1, d2);
	}
	
	private int compareFloating(DockingState d1, DockingState d2) {
		return d1.getFloatingGroup().compareTo(d2.getFloatingGroup());
	}
	
	private int compareMinimized(DockingState d1, DockingState d2) {
		return d1.getDockbarEdge()-d1.getDockbarEdge();
	}
	
	private int compareEmbedded(DockingState d1, DockingState d2) {
		// check their docking paths.  those with dockingPaths will
		// come before those without
		if(d1.hasDockingPath()!=d2.hasDockingPath()) {
			return d1.hasDockingPath()? -1: 1;
		}
		
		// they both either have dockingPaths or don't (but not one
		// with and one without).  if they both have one, then try to 
		// compare on this field.
		int cmp = 0;
		if(d1.hasDockingPath()) {
			cmp = compareDockingPaths(d1.getPath(), d2.getPath());
			if(cmp!=0)
				return cmp;
		}
		
		// they either both did not have dockingPaths, or they did but the
		// path comparison above turned up equal.
		return d1.getLayoutWeight()-d1.getLayoutWeight();
	}
	
	private int compareDockingPaths(DockingPath d1, DockingPath d2) {
		int pathDiff = d1.getDepth() - d1.getDepth();
		if(pathDiff!=0)
			return pathDiff;

		int depth = d1.getDepth();
		for(int i=0; i<depth; i++) {
			int cmp = compareSplitNodes(d1.getNode(i), d2.getNode(i));
			if(cmp!=0)
				return cmp;
		}
		return 0;
	}
	
	private int compareSplitNodes(SplitNode n1, SplitNode n2) {
		return getNodeWeight(n1) - getNodeWeight(n2);
	}
	
	private int getNodeWeight(SplitNode node) {
		return node.getRegion()==DockingConstants.TOP || node.getRegion()==DockingConstants.LEFT? 
				0: 1;
	}
}
