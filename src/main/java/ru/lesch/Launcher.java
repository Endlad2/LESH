package ru.lesch;

/**
 * Plain Java entry point for the shaded JAR.
 * Keeping the manifest main class separate from JavaFX Application avoids
 * the Java launcher trying to resolve JavaFX before the fat JAR classpath is ready.
 */
public final class Launcher {
    private Launcher() {
    }

    public static void main(String[] args) {
        LeschApp.main(args);
    }
}
