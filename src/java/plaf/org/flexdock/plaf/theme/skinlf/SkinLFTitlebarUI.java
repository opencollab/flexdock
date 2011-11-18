/*
 * Created on 17.04.2005
 */
package org.flexdock.plaf.theme.skinlf;

import java.awt.Graphics;
import java.awt.Rectangle;

import org.flexdock.plaf.theme.TitlebarUI;
import org.flexdock.view.Titlebar;

import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;

/**
 * @author Claudio Romano
 */
public class SkinLFTitlebarUI extends TitlebarUI {

    protected void paintBackground(Graphics g, Titlebar titlebar) {
        Rectangle paintArea = getPaintRect(titlebar);
        g.translate(paintArea.x, paintArea.y);
        SkinLookAndFeel.getSkin().getFrame().paintTop(g, titlebar, titlebar.isActive(), titlebar.getText());
        g.translate(-paintArea.x, -paintArea.y);
    }


    public int getDefaultHeight() {
        return SkinLookAndFeel.getSkin().getFrame().getTopPreferredSize().height;
    }
}
