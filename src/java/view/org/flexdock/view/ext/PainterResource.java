/*
 * Created on 18.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.view.ext;

import java.awt.Color;

/**
 * @author cro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PainterResource {

    private String classname;
    
    private Color bgColor;
    private Color bgColorActiv;
    
    private Color firstColor;
    private Color firstColorActiv;
    
    
    
    /**
     * @return Returns the bgColor.
     */
    public Color getBgColor() {
        return bgColor;
    }
    /**
     * @param bgColor The bgColor to set.
     */
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }
    /**
     * @return Returns the bgColorActiv.
     */
    public Color getBgColorActiv() {
        return bgColorActiv;
    }
    /**
     * @param bgColorActiv The bgColorActiv to set.
     */
    public void setBgColorActiv(Color bgColorActiv) {
        this.bgColorActiv = bgColorActiv;
    }
    
    
    /**
     * @return Returns the firstColor.
     */
    public Color getFirstColor() {
        return firstColor;
    }
    /**
     * @param firstColor The firstColor to set.
     */
    public void setFirstColor(Color firstColor) {
        this.firstColor = firstColor;
    }
    /**
     * @return Returns the firstColorActiv.
     */
    public Color getFirstColorActiv() {
        return firstColorActiv;
    }
    /**
     * @param firstColorActiv The firstColorActiv to set.
     */
    public void setFirstColorActiv(Color firstColorActiv) {
        this.firstColorActiv = firstColorActiv;
    }
    /**
     * @return Returns the painter.
     */
    public String getClassname() {
        return classname;
    }
    /**
     * @param painter The painter to set.
     */
    public void setClassname(String painter) {
        this.classname = painter;
    }
}