// Created: 09.08.2011
package de.freese.sonstiges.demos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public final class RsyncMain {
    public static void main(final String[] args) {
        List<String> command = new ArrayList<>();
        command.add("rsync");
        command.add("--verbose");
        command.add("--stats");
        command.add("--human-readable");
        command.add("--progress");
        command.add("--checksum");
        command.add("--recursive");
        command.add("--delete-after");
        command.add("--delete-excluded");
        command.add("--perms");
        command.add("--owner");
        command.add("--times");
        command.add("--group");
        command.add("--links");
        command.add("--force");
        command.add("--exclude-from=/etc/rsyncExcludes.conf");
        // command.add("--exclude='");
        // command.add("--exclude=.DS_Store");
        // command.add("--exclude=._*");
        // command.add("--exclude=.localized");
        // command.add("--exclude='*/EVE\\ Online*/**'");
        // command.add("--exclude=*/.metadata/**");
        // command.add("--exclude=*/bin/**");
        // command.add("--exclude=/.svn");
        // command.add("--exclude=*.class");
        // command.add("--exclude=*.svn-base");
        // command.add("'");
        // command.add("/Users/tommy/arbeit");
        // command.add("/Users/tommy/test");

        // Remote
        // command.add("--compress -e ssh");
        // command.add("/Users/tommy/test");
        // command.add("tommy@192.168.155.5:/backup/test ");

        System.out.println(command);

        Charset charset = StandardCharsets.UTF_8;

        try {
            Process process = Runtime.getRuntime().exec(command.toArray(String[]::new));

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), charset));
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
            // BufferedWriter outputWriter =
            // new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            // outputWriter.write("bla");

            String line = null;

            while ((line = inputReader.readLine()) != null) {
                System.out.println(line);
            }

            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            int exitVal = process.waitFor();
            System.out.println("Exit value: " + exitVal);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private RsyncMain() {
        super();
    }
}
