package de.freese.sonstiges.checksum;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.Adler32;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class CheckedIoMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckedIoMain.class);

    static void main() {
        final Adler32 inChecker = new Adler32();
        final Adler32 outChecker = new Adler32();

        try (CheckedInputStream in = new CheckedInputStream(new FileInputStream("words.txt"), inChecker)) {
            try (CheckedOutputStream out = new CheckedOutputStream(new FileOutputStream("out.txt"), outChecker)) {
                int c = 0;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }

                LOGGER.info("Input stream check sum: {}", inChecker.getValue());
                LOGGER.info("Output stream check sum: {}", outChecker.getValue());
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    private CheckedIoMain() {
        super();
    }
}
