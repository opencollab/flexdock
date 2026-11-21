package org.flexdock.perspective.persist.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

final class XmlRoundTrip {

    private XmlRoundTrip() {
    }

    static Element serialize(ISerializer serializer, Object object) throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element element = serializer.serialize(document, object);
        document.appendChild(element);

        return element;
    }

    static Object roundTrip(ISerializer serializer, Object object) throws Exception {
        return serializer.deserialize(serialize(serializer, object));
    }

}
