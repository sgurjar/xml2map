/*
 * $Id$
 */

package sg.xml.util;

/**
 * @author Satyendra Gurjar
 *
 */
public abstract class StringUtils
{
    public static final String CVSKeywords = "@(#) RcsModuleId = $Id:$ $Name:$";
    public static final String EMPTY_STR = "".intern();

    public static String trimToNull(String str)
    {
        if (str == null) {
            return null;
        }

        str = str.trim();

        return (str.length() == 0) ? null : str;
    }
}
