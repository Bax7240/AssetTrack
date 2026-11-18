/**
 * Entry point for running AssetTrack from an IDE or with plain `java`.
 *
 * Java refuses to start a main class that extends javafx.application.Application
 * unless JavaFX is on the module path ("JavaFX runtime components are missing").
 * Starting from this class, which does not extend Application, avoids that check
 * and lets JavaFX load from the normal classpath that Maven and VS Code provide.
 */
public class Launcher {
    public static void main(String[] args) {
        AssetTrackApp.main(args);
    }
}
