/*
 * Created on 18.03.2005
 */
package org.flexdock.view.ext;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.UIManager;

import org.flexdock.util.SwingUtility;

/**
 * @author Cyril Gambis
 * @author Claudio Romano
 */
public class GradientPainter extends DefaultPainter {

    public void paint(Graphics g, boolean active, JComponent titlebar) {
        int y = 2;
        int h = titlebar.getHeight() - 4;
        int w = titlebar.getWidth();

        int mid = w / 2;

        Color c = painterResource.getBgColor();

        double myDarkFactor = 0.3;
        double myNormalFactor = 0.75;
        double myLightFactor = 0.85;

        Color gradStartColor = SwingUtility.darker(c, myDarkFactor);
        Color gradMidColor = SwingUtility.darker(c, myNormalFactor);
        Color gradEndColor = SwingUtility.brighter(c, myLightFactor);

        GradientPaint firstHalf;
        GradientPaint secondHalf;

        //	 fill up the whole width if we're active
        if (active) {
            firstHalf = new GradientPaint(0, h, gradStartColor, mid, h, gradMidColor);
            secondHalf = new GradientPaint(mid, h, gradMidColor, w, h, gradEndColor);
        } else {
            // otherwise, fill up the center part and draw an outline
            if (hasCustomBackground(titlebar)) {
                firstHalf = new GradientPaint(0, h, SwingUtility.grayScale(gradMidColor).brighter(), mid, h, SwingUtility.grayScale(c));
                secondHalf = new GradientPaint(mid, h, SwingUtility.grayScale(c), w, h, SwingUtility.grayScale(gradEndColor).brighter());

            } else {
                firstHalf = new GradientPaint(0, h, c, mid, h, c);
                secondHalf = new GradientPaint(mid, h, c, w, h, c);
            }

        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(firstHalf);
        g2.fillRect(1, y + 1, mid, h - 2);
        g2.setPaint(secondHalf);
        g2.fillRect(mid, y + 1, w, h - 2);
    }

    private boolean hasCustomBackground(JComponent titlebar) {
        return titlebar.getBackground() != UIManager.getColor("Panel.background");
    }

}