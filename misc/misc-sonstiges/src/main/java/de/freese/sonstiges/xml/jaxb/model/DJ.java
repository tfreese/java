package de.freese.sonstiges.xml.jaxb.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "dj")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"firstName", "lastName"})
public class DJ {
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setFirstName(final String name) {
        this.firstName = name;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
}
