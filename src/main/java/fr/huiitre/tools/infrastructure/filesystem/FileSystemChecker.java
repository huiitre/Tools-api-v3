package fr.huiitre.tools.infrastructure.filesystem;

import java.nio.file.Files;
import java.nio.file.Path;

public final class FileSystemChecker {

    private FileSystemChecker() {}

    public static boolean exists(Path path) {
        return Files.exists(path) && Files.isRegularFile(path);
    }
}
