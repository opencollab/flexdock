/*
 * Created on 18.03.2005
 */
package org.flexdock.plaf.resources.paint;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.flexdock.util.SwingUtility;

/**
 * @author Cyril Gambis
 * @author Claudio Romano
 */
public class GradientPainter extends DefaultPainter {

    public static final String GRAYSCALE = "grayscale";

    public void paint(Graphics g, int width, int height, boolean active, JComponent titlebar) {
        int mid = width / 2;

        Color backgroundColor = getBackgroundColor(active);

        double myDarkFactor = 0.3;
        double myNormalFactor = 0.75;
        double myLightFactor = 0.85;

        Color gradStartColor = SwingUtility.darker(backgroundColor, myDarkFactor);
        Color gradMidColor = SwingUtility.darker(backgroundColor, myNormalFactor);
        Color gradEndColor = SwingUtility.darker(backgroundColor, myLightFactor);

        GradientPaint firstHalf;
        GradientPaint secondHalf;

        //	 fill up the whole width if we're active
        if (active) {
            firstHalf = new GradientPaint(0, height, gradStartColor, mid, height, gradMidColor);
            secondHalf = new GradientPaint(mid, height, gradMidColor, width, height, gradEndColor);
        } else {
            // otherwise, fill up the center part and draw an outline
            if (useGrayScale(titlebar)) {
                firstHalf = new GradientPaint(0, height, SwingUtility.grayScale(gradMidColor).brighter(), mid, height, SwingUtility.grayScale(backgroundColor));
                secondHalf = new GradientPaint(mid, height, SwingUtility.grayScale(backgroundColor), width, height, SwingUtility.grayScale(gradEndColor).brighter());

            } else {
                firstHalf = new GradientPaint(0, height, backgroundColor, mid, height, backgroundColor);
                secondHalf = new GradientPaint(mid, height, backgroundColor, width, height, backgroundColor);
            }
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(firstHalf);
        g2.fillRect(1, 1, mid, height - 2);
        g2.setPaint(secondHalf);
        g2.fillRect(mid, 1, width, height - 2);
    }


    private boolean useGrayScale(JComponent titlebar) {
        return painterResource.getInt( GRAYSCALE) == 1;
    }

}