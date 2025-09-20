// Created: 06 Apr. 2025
package de.freese.sonstiges;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class MvStoreDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(MvStoreDemo.class);

    private static final Path PATH_MV_STORE = Path.of(System.getProperty("java.io.tmpdir"), "mvstore.mv");

    private record Movie(String title, double rating) {
    }

    private static final class MovieType extends BasicDataType<Movie> {
        private static final Movie[] EMPTY_ARRAY = new Movie[0];

        @Override
        public Movie[] createStorage(final int size) {
            return size == 0 ? EMPTY_ARRAY : new Movie[size];
        }

        @Override
        public int getMemory(final Movie obj) {
            final int titleLength = obj.title().length();
            final int ratingLength = 8;

            return titleLength + ratingLength;
        }

        @Override
        public Movie read(final ByteBuffer buff) {
            final int titleLength = buff.getInt();
            final String title = DataUtils.readString(buff, titleLength);
            final double rating = buff.getDouble();

            return new Movie(title, rating);
        }

        @Override
        public void write(final WriteBuffer buff, final Movie obj) {
            buff.putInt(obj.title().length());
            buff.put(obj.title().getBytes(StandardCharsets.UTF_8));

            buff.putDouble(obj.rating());
        }
    }

    static void main() {
        try (MVStore store = createStore()) {
            defaultTypes(store);
            customTypes(store);

            // store.commit(); // If MVStore.Builder().autoCommitDisabled()

            // store.compactFile((int) TimeUnit.SECONDS.toMillis(3L));

            LOGGER.info("getCacheSizeUsed: {}", store.getCacheSizeUsed());
            LOGGER.info("getFillRate: {}", store.getFillRate());
            LOGGER.info("getReadBytes: {}", store.getFileStore().getReadBytes());
            LOGGER.info("getReadCount: {}", store.getFileStore().getReadCount());
            LOGGER.info("getWriteCount: {}", store.getFileStore().getWriteCount());
        }

        try {
            Files.deleteIfExists(PATH_MV_STORE);
        }
        catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static MVStore createStore() {
        // return MVStore.open(PATH_MV_STORE.toString());

        return new MVStore.Builder()
                .fileName(PATH_MV_STORE.toString())
                // .fileStore(new OffHeapStore())
                // .compress()
                .compressHigh()
                // .readOnly()
                // .encryptionKey("test".toCharArray())
                .open();
    }

    private static void customTypes(final MVStore store) {
        final MVMap.Builder<String, Movie> mapConfig = new MVMap.Builder<>();
        mapConfig.setValueType(new MovieType());

        final MVMap<String, Movie> movies = store.openMap("customTypes", mapConfig);
        movies.put("tt0111161", new Movie("The Shawshank Redemption", 9.2D));

        print(movies);
    }

    private static void defaultTypes(final MVStore store) {
        final MVMap<Integer, String> movies = store.openMap("defaultTypes");
        movies.put(1, "The Shawshank Redemption");
        movies.put(2, "The Godfather");
        movies.put(3, "The Godfather: Part II");

        print(movies);
    }

    private static void print(final MVMap<?, ?> map) {
        map.forEach((key, value) -> LOGGER.info("{} = {}", key, value));
    }

    private MvStoreDemo() {
        super();
    }
}
