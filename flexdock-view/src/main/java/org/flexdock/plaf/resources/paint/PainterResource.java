/*
 * Created on 18.03.2005
 */
package org.flexdock.plaf.resources.paint;

import java.awt.Color;



import org.flexdock.plaf.PropertySet;

/**
 * @author Claudio Romano
 */
public class PainterResource extends PropertySet {


    public static final String CLASSNAME = "classname";
    public static final String BACKGROUND_COLOR = "bgcolor";
    public static final String BACKGROUND_COLOR_ACTIVE = "bgcolor.active";


    /**
     * @return Returns the bgColor.
     */
    public Color getBgColor() {
        return getColor( BACKGROUND_COLOR);
    }

    /**
     * @param bgColor The bgColor to set.
     */
    public void setBgColor(Color bgColor) {
        setProperty( BACKGROUND_COLOR, bgColor);
    }

    /**
     * @return Returns the bgColorActiv.
     */
    public Color getBgColorActive() {
        return getColor( BACKGROUND_COLOR_ACTIVE);
    }

    /**
     * @param bgColorActive The bgColorActive to set.
     */
    public void setBgColorActive(Color bgColorActive) {
        setProperty( BACKGROUND_COLOR_ACTIVE, bgColorActive);
    }

    /**
     * @return Returns the painter.
     */
    public String getClassname() {
        return getString( CLASSNAME);
    }
    /**
     * @param painter The painter to set.
     */
    public void setClassname(String painter) {
        setProperty(CLASSNAME, painter);
    }


    public Class getImplClass() {
        String className = getString(CLASSNAME);
        try {
            return resolveClass(getString( CLASSNAME));
        } catch( Exception e) {
            System.err.println("Exception: " +e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}