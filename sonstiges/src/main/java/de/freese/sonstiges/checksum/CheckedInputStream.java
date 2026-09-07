package de.freese.sonstiges.checksum;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.Checksum;

/**
 * @author Thomas Freese
 */
public class CheckedInputStream extends FilterInputStream {
    private final Checksum checksum;

    public CheckedInputStream(final InputStream in, final Checksum checksum) {
        super(in);

        this.checksum = Objects.requireNonNull(checksum, "checksum required");
    }

    public Checksum getChecksum() {
        return checksum;
    }

    @Override
    public int read() throws IOException {
        final int b = in.read();

        if (b != -1) {
            checksum.update(b);
        }

        return b;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        final int bytesRead = in.read(b, 0, b.length);

        if (bytesRead != -1) {
            checksum.update(b, 0, bytesRead);
        }

        return bytesRead;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int bytesRead = in.read(b, off, len);

        if (bytesRead != -1) {
            checksum.update(b, off, bytesRead);
        }

        return bytesRead;
    }
}
