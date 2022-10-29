// Created: 15.01.2015
package de.freese.sonstiges.ldap;

import java.util.Comparator;

import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Thomas Freese
 */
public class SearchResultComparator implements Comparator<SearchResult>
{
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(final SearchResult o1, final SearchResult o2)
    {
        int comp = 0;

        try
        {
            Attribute attribute1 = o1.getAttributes().get("sn");
            Attribute attribute2 = o2.getAttributes().get("sn");

            String s1 = "";
            String s2 = "";

            if (attribute1 != null)
            {
                s1 = StringUtils.defaultString(attribute1.get().toString());
            }

            if (attribute2 != null)
            {
                s2 = StringUtils.defaultString(attribute2.get().toString());
            }

            comp = s1.compareTo(s2);

            if (comp != 0)
            {
                return comp;
            }

            attribute1 = o1.getAttributes().get("givenName");
            attribute2 = o2.getAttributes().get("givenName");

            if (attribute1 != null)
            {
                s1 = StringUtils.defaultString(attribute1.get().toString());
            }

            if (attribute2 != null)
            {
                s2 = StringUtils.defaultString(attribute2.get().toString());
            }

            comp = s1.compareTo(s2);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return comp;
    }
}
