/*
 * Created on 2005-06-03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.persist.xml;

import org.flexdock.perspective.LayoutSequence;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mati
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LayoutSequenceSerializer implements ISerializer {

    /**
     * @see org.flexdock.perspective.persist.xml.ISerializer#serialize(org.w3c.dom.Document, java.lang.Object)
     */
    public Element serialize(Document document, Object object) {
        LayoutSequence layoutSequence = (LayoutSequence) object;

        return null;
    }

}
