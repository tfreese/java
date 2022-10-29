// Created: 09.01.2015
package de.freese.sonstiges.ldap;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Thomas Freese
 */
public class LdapClient
{
    /**
     * @param args String[]
     *
     * @throws Exception Falls was schiefgeht.
     */
    public static void main(final String[] args) throws Exception
    {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://localhost:3389");
        env.put(Context.SECURITY_PRINCIPAL, "cn=ldap,ou=users,dc=freese,dc=de");
        env.put(Context.SECURITY_CREDENTIALS, "ldapuser");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        // env.put(Context.SECURITY_PROTOCOL, "ssl");

        InitialDirContext context = new InitialDirContext(env);

        // Specify the attributes to return
        String[] returnedAttributes =
                {
                        "*"
                };

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(returnedAttributes);

        // specify the LDAP search filter, just users
        // String searchFilter = "mail=*THO*";
        String searchFilter = "(objectclass=*)";

        NamingEnumeration<SearchResult> search = context.search("ou=addressbook,dc=freese,dc=de", searchFilter, searchControls);
        List<SearchResult> results = new ArrayList<>();

        while (search.hasMoreElements())
        {
            SearchResult searchResult = search.nextElement();
            System.out.println(searchResult.toString());
            results.add(searchResult);
        }

        search.close();

        results.sort(new SearchResultComparator());

        try (PrintWriter pw = new PrintWriter("/tmp/ldap-backup.ldif", StandardCharsets.UTF_8))
        {
            for (SearchResult searchResult : results)
            {
                // System.out.println(searchResult.getAttributes().get("mobile"));
                String cn = getValue(searchResult, "cn");
                String uid = getValue(searchResult, "uid");
                String sn = getValue(searchResult, "sn");
                String givenName = getValue(searchResult, "givenName");

                // Prüfen, ob cn aus Vor- und Nachname besteht.
                if ((cn != null) && cn.contains(" "))
                {
                    pw.printf("dn: cn=%s,ou=addressbook,dc=freese,dc=de%n", cn);
                }
                else if (StringUtils.isNotBlank(givenName))
                {
                    pw.printf("dn: cn=%s %s,ou=addressbook,dc=freese,dc=de%n", givenName, sn);
                }
                else if (StringUtils.isNotBlank(uid))
                {
                    pw.printf("dn: cn=%s,ou=addressbook,dc=freese,dc=de%n", uid);
                }
                else
                {
                    pw.printf("dn: cn=%s,ou=addressbook,dc=freese,dc=de%n", cn);
                }

                // pw.println("objectClass: top");
                // pw.println("objectClass: person");
                // pw.println("objectClass: organizationalPerson");
                // pw.println("objectClass: inetOrgPerson");
                // pw.println("objectClass: evolutionPerson");
                writeMultiValue(pw, searchResult, "objectClass");
                pw.printf("sn: %s%n", sn);
                pw.printf("givenName: %s%n", givenName);
                writeSingleValue(pw, searchResult, "displayName"); // Spitzname
                writeSingleValue(pw, searchResult, "description");

                pw.println("# Persönlich");

                writeMultiValue(pw, searchResult, "homePhone");
                writeMultiValue(pw, searchResult, "mobile");
                writeMultiValue(pw, searchResult, "mail");
                writeSingleValue(pw, searchResult, "homePostalAddress");
                writeSingleValue(pw, searchResult, "birthDate");
                writeSingleValue(pw, searchResult, "note");

                pw.println("# Beruflich");

                writeSingleValue(pw, searchResult, "title");
                writeSingleValue(pw, searchResult, "businessRole"); // Beruf
                writeSingleValue(pw, searchResult, "o"); // Firma
                writeSingleValue(pw, searchResult, "ou"); // Abteilung
                writeSingleValue(pw, searchResult, "roomNumber"); // Büro
                writeSingleValue(pw, searchResult, "assistantName"); // Assistent
                writeSingleValue(pw, searchResult, "managerName"); // Vorgesetzter
                writeMultiValue(pw, searchResult, "telephoneNumber");
                writeSingleValue(pw, searchResult, "postalAddress");

                pw.println("# Sonstiges");

                writeSingleValue(pw, searchResult, "initials");
                writeSingleValue(pw, searchResult, "uid");
                writeSingleValue(pw, searchResult, "otherPostalAddress");
                writeSingleValue(pw, searchResult, "labeledURI"); // Homepage
                writeSingleValue(pw, searchResult, "spouseName"); // Ehepartner

                // Passwörter nur mit Admin auslesbar.
                writeSingleValue(pw, searchResult, "userPassword");

                pw.println();
            }
        }
        catch (Exception ex)
        {
            System.err.println(ex);
        }

        context.close();
    }

    /**
     * Liefert nur den Wert des Keys.
     *
     * @param searchResult {@link SearchResult}
     * @param key String
     *
     * @return String
     *
     * @throws Exception Falls was schiefgeht.
     */
    private static String getValue(final SearchResult searchResult, final String key) throws Exception
    {
        Attribute attribute = searchResult.getAttributes().get(key);

        if (attribute == null)
        {
            return null;
        }

        return attribute.get().toString();
    }

    /**
     * @param pw {@link PrintWriter}
     * @param searchResult {@link SearchResult}
     * @param key String
     *
     * @throws Exception Falls was schiefgeht.
     */
    private static void writeMultiValue(final PrintWriter pw, final SearchResult searchResult, final String key) throws Exception
    {
        Attribute attribute = searchResult.getAttributes().get(key);

        if (attribute == null)
        {
            return;
        }

        for (int i = 0; i < attribute.size(); i++)
        {
            String value = StringUtils.trim(attribute.get(i).toString());

            if (StringUtils.isBlank(value))
            {
                continue;
            }

            pw.printf("%s: %s%n", key, value);
        }
    }

    /**
     * @param pw {@link PrintWriter}
     * @param searchResult {@link SearchResult}
     * @param key String
     *
     * @throws Exception Falls was schiefgeht.
     */
    private static void writeSingleValue(final PrintWriter pw, final SearchResult searchResult, final String key) throws Exception
    {
        Attribute attribute = searchResult.getAttributes().get(key);

        if (attribute == null)
        {
            return;
        }

        String value = StringUtils.trim(attribute.get().toString());

        if (StringUtils.isBlank(value))
        {
            return;
        }

        pw.printf("%s: %s%n", key, value);

    }

    /**
     * Erstellt ein neues {@link LdapClient} Object.
     */
    public LdapClient()
    {
        super();
    }
}
