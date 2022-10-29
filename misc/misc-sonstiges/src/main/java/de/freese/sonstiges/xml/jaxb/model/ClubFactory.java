package de.freese.sonstiges.xml.jaxb.model;

/**
 * @author Thomas Freese
 */
public final class ClubFactory
{
    public static Club createClub()
    {
        // 1
        Club club = new Club();
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

    public static void toString(final Club club)
    {
        System.out.println("Club-Employees:" + club.getEmployees());
        System.out.println("Club-Opening:" + club.getOpening());
        System.out.println("Club-Guests:" + club.getGuests());

        for (DJ dj : club.getDJs())
        {
            System.out.println("\tDJ-Firstname:" + dj.getFirstName());
            System.out.println("\tDJ-Lastname:" + dj.getLastName());
        }
    }

    private ClubFactory()
    {
        super();
    }
}
