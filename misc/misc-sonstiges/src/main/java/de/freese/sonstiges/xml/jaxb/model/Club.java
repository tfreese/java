package de.freese.sonstiges.xml.jaxb.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.freese.sonstiges.xml.jaxb.OpeningDateAdapter;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "club")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder(
        {
                "opening", "employees", "guests", "dj"
        })
public class Club
{
    @XmlElementWrapper(name = "djs")
    private final List<DJ> dj = new ArrayList<>();
    @XmlElementWrapper(name = "guests")
    private final Map<Integer, Integer> guests = new HashMap<>();
    @XmlJavaTypeAdapter(OpeningDateAdapter.class)
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private final Date opening = new Date();
    @XmlAttribute
    private int employees;

    public void addDJ(final DJ dj)
    {
        this.dj.add(dj);
    }

    public List<DJ> getDJs()
    {
        return this.dj;
    }

    public int getEmployees()
    {
        return this.employees;
    }

    public Map<Integer, Integer> getGuests()
    {
        return this.guests;
    }

    public Date getOpening()
    {
        return this.opening;
    }

    public void setEmployees(final int employees)
    {
        this.employees = employees;
    }
}
