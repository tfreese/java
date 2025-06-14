package de.freese.sonstiges.xml.jaxb.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.freese.sonstiges.xml.jaxb.OpeningDateXmlAdapter;

/**
 * @author Thomas Freese
 */
@XmlRootElement(name = "club")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder({"opening", "employees", "guests", "dj"})
public class Club {
    @XmlElementWrapper(name = "djs")
    @JsonbProperty("djs")
    private final List<DJ> djs = new ArrayList<>();

    @XmlElementWrapper(name = "guests")
    private final Map<Integer, Integer> guests = new HashMap<>();

    @XmlJavaTypeAdapter(OpeningDateXmlAdapter.class)
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private final Date opening = new Date();

    @XmlAttribute
    private int employees;

    // @JsonbCreator
    // public Club(@JsonbProperty("employees") int employees) {
    //     this.employees = employees;
    // }

    public void addDJ(final DJ dj) {
        djs.add(dj);
    }

    public List<DJ> getDJs() {
        return djs;
    }

    public int getEmployees() {
        return employees;
    }

    public Map<Integer, Integer> getGuests() {
        return guests;
    }

    public Date getOpening() {
        return opening;
    }

    public void setEmployees(final int employees) {
        this.employees = employees;
    }
}
