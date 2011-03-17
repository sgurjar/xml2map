/*
 *
 * $Id$
 */

package sg.xml.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author Satyendra Gurjar
 *
 */
public class SAXHandler extends DefaultHandler
{
    public static final String CVSKeywords = "@(#) RcsModuleId = $Id:$ $Name:$";

    /*
     * Internal Element used for sax parsing
     */
    private static class InternalElement
    {
        String          name;
        StringBuffer    value;
        ArrayList       children;
        InternalElement parent;
        String          namespaceUri;
    }

    /*
     * trim leading and trailing white spaces from the value of xml tags.
     */
    private boolean trimWhitespaces = true;

    /*
     * element hold the root element after parsing is done.
     */
    private InternalElement currentElement;

    public SAXHandler()
    {
    }

    public SAXHandler(boolean trimWhitespaces)
    {
        this.trimWhitespaces        = trimWhitespaces;
    }

    public Map getMap()
    {
        HashMap out = new HashMap();
        createMap(currentElement, out);

        return out;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException
    {
        InternalElement newElement = new InternalElement();
        newElement.name = localName;

        if (currentElement != null) {
            if (currentElement.children == null) {
                currentElement.children = new ArrayList();
            }

            currentElement.children.add(newElement);
        } else {
            // this is root element set namespace uri here
            // as we support only one namespace only at root level
            newElement.namespaceUri = StringUtils.trimToNull(uri);
        }

        newElement.parent     = currentElement;
        currentElement        = newElement;
    }

    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
        if (currentElement.value == null) {
            currentElement.value = new StringBuffer();
        }

        currentElement.value.append(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName)
        throws SAXException
    {
        if (currentElement.parent != null) {
            currentElement = currentElement.parent;
        }
    }

    public void warning(SAXParseException e) throws SAXException
    {
        throw e;
    }

    public void error(SAXParseException e) throws SAXException
    {
        throw e;
    }

    public void fatalError(SAXParseException e) throws SAXException
    {
        throw e;
    }

    private void createMap(InternalElement element, HashMap out)
    {
        if (element == null) {
            throw new IllegalArgumentException("element is null");
        }

        if (out == null) {
            throw new IllegalArgumentException("out is null");
        }

        if (element.children == null) {
            put(out, element.name,
                (element.value != null) ? (trimWhitespaces ? element.value.toString().trim() : element.value.toString())
                                        : StringUtils.EMPTY_STR);
        } else {
            HashMap m = new HashMap();

            for (Iterator i = element.children.iterator(); i.hasNext();)
                createMap((InternalElement) i.next(), m);

            put(out, element.name, m);
        }
    }

    private void put(HashMap m, Object key, Object value)
    {
        Object o = m.get(key);

        if (o == null) {
            m.put(key, value);

            return;
        }

        if (o instanceof List) {
            ((List) o).add(value);

            return;
        }

        ArrayList l = new ArrayList();
        l.add(o);
        l.add(value);
        m.put(key, l);
    }
}
