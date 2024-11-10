package de.freese.sonstiges.xml.jaxb.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class ClubFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClubFactory.class);

    public static Club createClub() {
        // 1
        final Club club = new Club();
        club.setEmployees(100);
        club.getGuests().put(2010, 10000);
        club.getGuests().put(2011, 11111);

        DJ dj = new DJ();
        dj.setFirstName("dj1.firstname.a");
        dj.setLastName("dj1.lastname.a");
        club.addDJ(dj);

        dj = new DJ();
        dj.setFirstName("dj2.firstname.a");
        dj.setLastName("dj2.lastname.a");
        club.addDJ(dj);

        return club;
    }

    public static void toString(final Club club) {
        LOGGER.info("Club-Employees: {}", club.getEmployees());
        LOGGER.info("Club-Opening: {}", club.getOpening());
        LOGGER.info("Club-Guests: {}", club.getGuests());

        for (DJ dj : club.getDJs()) {
            LOGGER.info("\tDJ-Firstname: {}", dj.getFirstName());
            LOGGER.info("\tDJ-Lastname: {}", dj.getLastName());
        }
    }

    private ClubFactory() {
        super();
    }
}
