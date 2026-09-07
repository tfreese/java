package de.freese.sonstiges.xml.jaxb.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "dj")
// @XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"firstName", "lastName"})
public class DJ {
    private String firstName;
    private String lastName;

    public DJ() {
        super();
    }

    public DJ(final String firstName, final String lastName) {
        super();

        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(final String name) {
        this.firstName = name;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
}
