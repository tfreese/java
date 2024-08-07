// Created: 04.10.2018
package de.freese.sonstiges.demos;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

/**
 * @author Thomas Freese
 */
public final class ShowDevicesDemo {
    public static void main(final String[] args) throws Exception {
        showFileStores();
        showRootDirectories();
        showRootsFromFileSystemView();
        showFileStoreFromPath();
    }

    private static void showFileStoreFromPath() throws Exception {
        System.out.println();
        System.out.println("ShowDevicesDemo.showFileStoreFromPath()");

        final Path path = Paths.get("build.gradle");
        final FileStore fileStore = Files.getFileStore(path);
        System.out.printf("FileStore from %s: %s, %s%n", path, fileStore.name(), fileStore.type());
    }

    private static void showFileStores() {
        System.out.println();
        System.out.println("ShowDevicesDemo.showFileStores()");

        for (FileStore store : FileSystems.getDefault().getFileStores()) {
            System.out.printf("%s: %s%n", store.name(), store.type());
        }
    }

    private static void showRootDirectories() throws Exception {
        System.out.println();
        System.out.println("ShowDevicesDemo.showRootDirectories()");

        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            final FileStore fileStore = Files.getFileStore(root);
            // System.out.format("%s\t%s\n", root, fileStore.getAttribute("volume:isRemovable"));
            System.out.printf("%s: %s%n", fileStore.name(), fileStore.type());
        }
    }

    private static void showRootsFromFileSystemView() {
        System.out.println();
        System.out.println("ShowDevicesDemo.showRootsFromFileSystemView()");

        final FileSystemView fsv = FileSystemView.getFileSystemView();

        // final List<File> roots = List.of(fsv.getRoots());
        final List<File> roots = List.of(File.listRoots());

        for (File path : roots) {
            // System.out.printf("Drive Name: %s, %s%n", path, fsv.getSystemTypeDescription(path));

            System.out.println("System Drive: " + path);
            System.out.println("TypeDescription: " + fsv.getSystemTypeDescription(path));
            System.out.println("Drive Display name: " + fsv.getSystemDisplayName(path));
            System.out.println("Is drive: " + fsv.isDrive(path));
            System.out.println("Is floppy: " + fsv.isFloppyDrive(path));
            System.out.println("Readable: " + path.canRead());
            System.out.println("Writable: " + path.canWrite());
            System.out.println();
        }
    }

    private ShowDevicesDemo() {
        super();
    }
}
