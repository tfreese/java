package de.freese.sonstiges.checksum;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.zip.Checksum;

/**
 * @author Thomas Freese
 */
public class CheckedOutputStream extends FilterOutputStream {
    private final Checksum checksum;

    public CheckedOutputStream(final OutputStream out, final Checksum checksum) {
        super(out);

        this.checksum = Objects.requireNonNull(checksum, "checksum required");
    }

    public Checksum getChecksum() {
        return checksum;
    }

    @Override
    public void write(final byte[] b) throws IOException {
        out.write(b, 0, b.length);
        checksum.update(b, 0, b.length);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        out.write(b, off, len);
        checksum.update(b, off, len);
    }

    @Override
    public void write(final int b) throws IOException {
        out.write(b);
        checksum.update(b);
    }
}
