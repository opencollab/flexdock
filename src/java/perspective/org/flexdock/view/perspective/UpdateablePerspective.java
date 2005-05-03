///*
// * Created on 2005-04-27
// *
// * TODO To change the template for this generated file go to
// * Window - Preferences - Java - Code Style - Code Templates
// */
//package org.flexdock.view.perspective;
//
//import org.flexdock.docking.DockingPort;
//import org.flexdock.docking.event.DockingEvent;
//import org.flexdock.docking.event.DockingListener;
//import org.flexdock.view.View;
//
///**
// * 
// * @author wolf_ethan
// *
// */
//public class UpdateablePerspective extends Perspective implements DockingListener {
//
//	private boolean isActive = false;
//	
//    public UpdateablePerspective(String perspectiveName){
//        super(perspectiveName);
//    }
//
//    public void activate() {
//    	isActive = true;
//        IPerspectiveManager perspectiveRegistry = PerspectiveManager.getInstance();
//        perspectiveRegistry.applyPerspective(this);
//    }
//    
//    public void deactivate() {
//        isActive = false;
//        recomputeDockingRatios();
//    }
//    
//    /**
//     * @see org.flexdock.view.perspective.Perspective#dock(org.flexdock.view.View, org.flexdock.view.View, java.lang.String, float)
//     */
//    public void dock(View sourceView, View targetView, String region, float ratio) {
//        super.dock(sourceView, targetView, region, ratio);
//        targetView.addDockingListener(this);
//    }
//
//    /**
//     * @see org.flexdock.docking.event.DockingListener#dropStarted(org.flexdock.docking.event.DockingEvent)
//     */
//    public void dropStarted(DockingEvent evt) {
//    }
//
//    /**
//     * If the view is docked in a center panel. then set all of its children to
//     * point to its original parent.
//     * @param view
//     * @return the view that is used as a replacement parent for the undocked
//     * views children.
//     */
//    private View updateInfoOnUndock(View view) {
//        ViewDockingInfo undockedViewDockingInfo = null;
//        View surrogateParent = null;
//        ViewDockingInfo surrogateInfo = null;
//        ViewDockingInfo[] dockingInfos = getDockingInfoChain();
//        int indexToBeginChildIteration = dockingInfos.length;
//        for (int i=0; i<dockingInfos.length; i++) {
//            if (dockingInfos[i].getTargetView() == view) {
//                undockedViewDockingInfo = dockingInfos[i];
//                // If it was center docked, then redock children to its parent.
//                if (undockedViewDockingInfo.getRelativeRegion() ==
//                    DockingPort.CENTER_REGION) {
//                    surrogateParent = undockedViewDockingInfo.getSourceView();
//                    // If this view was the "root" tab, we will need to
//                    // find a child instead to serve as the surrogate parent.
//                    if (surrogateParent != null) {
//                        indexToBeginChildIteration = i+1;
//                        // Break out of loop since we found the surrogate
//                        break;
//                    }
//                }
//            }
//            // Else find the first centrally docked child
//            // or if none exists, the first edge port docked child
//            else {
//                if (dockingInfos[i].getSourceView() == view) {
//                    if (indexToBeginChildIteration == dockingInfos.length)
//                        indexToBeginChildIteration = i;
//                    // Center region overrides non-center region as new parent.
//                    if (dockingInfos[i].getRelativeRegion() ==
//                        DockingPort.CENTER_REGION) {
//                        surrogateInfo = dockingInfos[i];
//                        surrogateParent = surrogateInfo.getTargetView();
//                        // end loop, we've found everything we need.
//                        break;
//                    }
//                    else if (surrogateInfo == null) {
//                        surrogateInfo = dockingInfos[i];
//                        surrogateParent = surrogateInfo.getTargetView();
//                    }
//                }
//            }
//        }
//
//        // This view was the "root" view.
//        if (undockedViewDockingInfo == null) {
//            // Make the surrogate parent the new root.
//            if (surrogateParent != null) {
//                // Use super since non-super registers a listener to the view
//                // which we have already done.
//                super.dockToCenterViewport(surrogateParent.getPersistentId());
//                // The surrogate info must be non null, since this must have
//                // been a child of the root, not the parent.
//                getDockingInfoList().remove(surrogateInfo);
//            }
//        }
//        else {
//            if (surrogateInfo != null) {
//                // "Move" the surrogate in place of the undocked parent
//                // by updating the undocked views info.
//                undockedViewDockingInfo.setTargetView(surrogateParent);
//                // And remove the old info
//                getDockingInfoList().remove(surrogateInfo);
//            }
//            else {
//                // If the view either:
//                // 1.  Was docked to the central region of another view, or
//                // 2.  Had no chidren and was docked in an end border
//                // then we won't remove docking info for the surrogate.
//                // Instead, we will remove this views original docking info.
//                getDockingInfoList().remove(undockedViewDockingInfo);
//            }
//        }
//
//        // Update child views to point to the surrogate parent.
//        if (surrogateParent != null) {
//            for (int i=indexToBeginChildIteration; i<dockingInfos.length; i++) {
//                if (dockingInfos[i].getSourceView() == view) {
//                    dockingInfos[i].setSourceView(surrogateParent);
//                }
//            }
//        }
//        
//        return surrogateParent;
//    }
//    
//    public void dock(String viewId) {
//        throw new UnsupportedOperationException(
//            "Use the more explicit dockToCenterViewport method");
//    }
//
//    public void dockToCenterViewport(String viewId) {
//        super.dockToCenterViewport(viewId);
//        View centerView = getView(viewId);
//        centerView.addDockingListener(this);
//    }
//
//    public void recomputeDockingRatios() {
//        // Recompute all the ratios based on what is displayed in the GUI.
//        ViewDockingInfo[] dockingInfos = getDockingInfoChain();
//        for (int i=0; i<dockingInfos.length; i++) {
//            String region = dockingInfos[i].getRelativeRegion();
//            if (region == DockingPort.CENTER_REGION)
//                dockingInfos[i].setRatio(-1);
//            else if ((region == DockingPort.EAST_REGION) ||
//                (region == DockingPort.WEST_REGION)) {
//                int sourceWidth = dockingInfos[i].getSourceView().getContentPane().getWidth();
//                int targetWidth = dockingInfos[i].getTargetView().getContentPane().getWidth();
//                float visualRatio = ((float)targetWidth)/(targetWidth + sourceWidth);
//                dockingInfos[i].setRatio(visualRatio);
//            }
//            else if ((region == DockingPort.NORTH_REGION) ||
//                (region == DockingPort.SOUTH_REGION)) {
//                int sourceHeight = dockingInfos[i].getSourceView().getContentPane().getHeight();
//                int targetHeight = dockingInfos[i].getTargetView().getContentPane().getHeight();
//                float visualRatio = ((float)targetHeight)/(targetHeight + sourceHeight);
//                dockingInfos[i].setRatio(visualRatio);
//            }
//            
//        }
//    }
// 
///***---- NO IMPLEMENTATION ****/    
//    /* (non-Javadoc)
//     * @see org.flexdock.docking.event.DockingListener#dockingCanceled(org.flexdock.docking.event.DockingEvent)
//     */
//    public void dockingCanceled(DockingEvent evt) {
//        // TODO Auto-generated method stub
//        
//    }
//
//    /* (non-Javadoc)
//     * @see org.flexdock.docking.event.DockingListener#dragStarted(org.flexdock.docking.event.DockingEvent)
//     */
//    public void dragStarted(DockingEvent evt) {
//        // TODO Auto-generated method stub
//        
//    }
//
//    /* (non-Javadoc)
//     * @see org.flexdock.docking.event.DockingListener#dockingComplete(org.flexdock.docking.event.DockingEvent)
//     */
//    public void dockingComplete(DockingEvent evt) {
//        if (isActive) {
//            // source should be the view that is being moved.
//            View droppedView = (View) evt.getSource();
//            // Undock the view.
//            View surrogateParent = updateInfoOnUndock(droppedView);
//
//            // This should be the view that is being docked into.
//            View dropLocation = null;
//            if (evt.getNewDockingPort() != null)
//                dropLocation = (View) evt.getNewDockingPort().getDockable(DockingPort.CENTER_REGION);
//            if (dropLocation == droppedView) {
//                // If it is a "redock" into the same area, then use the
//                // surrogate parent that replaced the undocked view as the
//                // redocked views source (view area to dock into).
//                dropLocation = surrogateParent;
//            }
//            String newRegion = evt.getRegion();
//
//            float ratio = (newRegion == DockingPort.CENTER_REGION) ? -1 : (float).5;
//            // Create docking info as long as it isn't the "root" view (which
//            // has no source).
//            if (dropLocation != null) {
//                ViewDockingInfo newDockingInfo = new ViewDockingInfo(dropLocation, droppedView, newRegion, ratio);
//                getDockingInfoList().add(newDockingInfo);
//            }
//        }
//    }
//
//    /* (non-Javadoc)
//     * @see org.flexdock.docking.event.DockingListener#undockingComplete(org.flexdock.docking.event.DockingEvent)
//     */
//    public void undockingComplete(DockingEvent evt) {
//        // TODO Auto-generated method stub
//        
//    }
//
//}
