/*
 * $Id$
 */

import java.io.StringReader;
import java.util.Map;
import org.xml.sax.InputSource;
import sg.xml.util.*;

/**
 * @author Satyendra Gurjar
 *
 */
public class Tester
{
    public static void main( String[] args ) {
        try {
            //System.out.println(xml_1);
                        
            StringReader reader = new StringReader( xml_1 );
            Map m = XmlUtils.xmlToMap( new InputSource( reader ) );
            System.out.println( m );

            String xml = XmlUtils.mapToXml( m );
            System.out.println( xml );
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }
        
    private static String xml_1 = 
        "<?xml version='1.0'?>"+
        "<purchaseOrder>"+
        "   <orderDate>1999-10-20 </orderDate>"+
        "   <shipTo>"+
        "      <name>Alice Smith</name>"+
        "      <street>123 Maple Street  </street>"+
        "      <city>Mill Valley</city>"+
        "      <state>CA</state>"+
        "      <zip>90952</zip>"+
        "   </shipTo>"+
        "   <billTo>"+
        "      <name>Robert Smith</name>"+
        "      <street>8 Oak Avenue</street>"+
        "      <city>Old Town</city>"+
        "      <state>PA</state>"+
        "      <zip>95819</zip>"+
        "   </billTo>"+
        "   <comment>Hurry, my lawn is going wild!</comment>"+
        "   <items>"+
        "      <item>"+
        "         <partNum>872-AA</partNum>"+
        "         <productName>Lawnmower</productName>"+
        "         <quantity>1</quantity>"+
        "         <USPrice>148.95</USPrice>"+
        "         <comment>Confirm this is electric</comment>"+
        "      </item>"+
        "      <item>"+
        "         <partNum>926-AA</partNum>"+
        "         <productName>Baby Monitor</productName>"+
        "         <quantity>1</quantity>"+
        "         <USPrice>39.98</USPrice>"+
        "         <shipDate>1999-05-21</shipDate>"+
        "      </item>"+
        "   </items>"+
        "</purchaseOrder>";
}