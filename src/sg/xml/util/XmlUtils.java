/*
 * $Id$
 */
package sg.xml.util;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * @author Satyendra Gurjar
 *
 */
public abstract class XmlUtils
{
    public static final String CVSKeywords = "@(#) RcsModuleId = $Id:$ $Name:$";
    public static final String DEFAULT_NAMESPACE_PREFIX = "ns1";

    /**
     * Converts xml into <code>Map</code>. Each tag name will be key and
     * contain will be value, all namespaces will be ignored.
     *
     * xml like:
     *  <foo>
     *      <a>
     *          <b>this is value of b</b>
     *          <c>value of c</c>
     *      </a>
     *      <x>xvalue</x>
     * </foo>
     *
     * will be converted to Map like:
     *
     * {foo={a={b="this is value of b", c="value of c"}, x="xvalue"}}
     *
     * @param in
     *          conains xml to be converted to xml
     * @return Map converted from xml
     * @throws IOException
     * @throws SAXException
     */
    public static Map xmlToMap(InputSource in) throws IOException, SAXException
    {
        if (in == null) {
            return null;
        }

        SAXHandler handler = getSAXHandler();
        getSAXParser().parse(in, handler);

        return handler.getMap();
    }

    /**
     * handles namespace only for root element
     *
     * @param map
     *            map to be converted to xml
     * @param namaspaceUri
     *            optional, can be null
     * @param namespacePrefix
     *            optional, can be null
     * @return converted xml as string
     */
    public static String mapToXml(Map map, String namaspaceUri, String namespacePrefix)
    {
        if (map == null) {
            return null;
        }

        namaspaceUri        = StringUtils.trimToNull(namaspaceUri);
        namespacePrefix     = StringUtils.trimToNull(namespacePrefix);

        Iterator i          = map.entrySet().iterator();

        if (!i.hasNext()) {
            return StringUtils.EMPTY_STR;
        }

        Map.Entry    root    = (Map.Entry) i.next();
        StringBuffer buffer  = new StringBuffer();
        StringBuffer tagName = new StringBuffer();

        if (namaspaceUri != null) {
            if (namespacePrefix == null) {
                namespacePrefix = DEFAULT_NAMESPACE_PREFIX;
            }

            tagName.append(namespacePrefix).append(':').append(root.getKey());
            buffer.append('<').append(tagName);
            buffer.append(" xmlns:").append(namespacePrefix).append("='").append(namaspaceUri).append("'");
        } else {
            tagName.append(root.getKey());
            buffer.append('<').append(tagName);
        }

        buffer.append('>');

        Object vo = root.getValue();

        if (vo instanceof String) {
            escapeGeneralEntities((String) vo, buffer);
        } else if (vo instanceof Map) {
            mapToXml((Map) vo, buffer);
        } else {
            throw new RuntimeException("Invalid value in Map. Value must be String or Map");
        }

        buffer.append("</").append(tagName).append('>');

        return buffer.toString();
    }

    public static String mapToXml(Map in)
    {
        StringBuffer buffer = new StringBuffer();
        mapToXml(in, buffer);

        return buffer.toString();
    }

    public static void mapToXml(Map in, StringBuffer out)
    {
        for (Iterator i = in.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            Object    vo    = entry.getValue();

            if (vo instanceof String) {
                out.append('<').append(entry.getKey()).append('>');
                escapeGeneralEntities((String) vo, out);
                out.append("</").append(entry.getKey()).append('>');
            } else if (vo instanceof Map) {
                out.append('<').append(entry.getKey()).append('>');
                mapToXml((Map) vo, out);
                out.append("</").append(entry.getKey()).append('>');
            } else if (vo instanceof List) {
                for (Iterator li = ((List) vo).iterator(); li.hasNext();) {
                    out.append('<').append(entry.getKey()).append('>');
                    mapToXml((Map) li.next(), out);
                    // NOTE: we can never have list of anything but Map's.
                    out.append("</").append(entry.getKey()).append('>');
                }
            } else {
                throw new RuntimeException("Invalid value in Map. Value must be String, Map or List");
            }
        }
    }

    public static void escapeGeneralEntities(String text, StringBuffer out)
    {
        if (text == null) {
            return;
        }

        if (out == null) {
            throw new IllegalArgumentException("out is null");
        }

        int len = text.length();

        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);

            if (c == '&') {
                out.append("&amp;");
            } else if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else {
                out.append(c);
            }
        }
    }

    // -----------------------------------------------
    // -------- Thread Local SAXParser ---------------
    // -----------------------------------------------
    private static ThreadLocalSAXParser tlSAXParser = new ThreadLocalSAXParser();

    public static SAXParser getSAXParser()
    {
        return (SAXParser) (tlSAXParser.get());
    }

    public static SAXParserFactory getSAXFactory()
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);

        return factory;
    }

    private static class ThreadLocalSAXParser extends ThreadLocal
    {
        protected Object initialValue()
        {
            try {
                return getSAXFactory().newSAXParser();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // -----------------------------------------------
    // --------Thread Local SAX Handler --------------
    // -----------------------------------------------
    private static ThreadLocalSAXHandler tlSAXHandler = new ThreadLocalSAXHandler();

    public static SAXHandler getSAXHandler()
    {
        return (SAXHandler) (tlSAXHandler.get());
    }

    private static class ThreadLocalSAXHandler extends ThreadLocal
    {
        protected Object initialValue()
        {
            return new SAXHandler();
        }
    }
}
