/*
 * Created on 2005-05-11
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.restore;

import javax.swing.JSplitPane;

/**
 * 
 * @author Mateusz Szczap
 */
public class ViewUtils {

    public static float getLeftOrTopRatio(JSplitPane splitPane) {
        if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            int height = splitPane.getHeight();
            int divLoc = splitPane.getDividerLocation();
            float percentage = (float)divLoc / (float)height;
            return percentage;
        } else {
            int width = splitPane.getWidth();
            int divLoc = splitPane.getDividerLocation();
            float percentage = (float)divLoc / (float)width;
            return percentage;
        }
    }

    public static float getRightOrBottomRatio(JSplitPane splitPane) {
        if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            int height = splitPane.getHeight();
            int divLoc = splitPane.getDividerLocation();
            float percentage = (float)divLoc / (float)height;
            return 1.0f-percentage;
        } else {
            int width = splitPane.getWidth();
            int divLoc = splitPane.getDividerLocation();
            float percentage = (float)divLoc / (float)width;
            return 1.0f - percentage;
        }
    }

}
