package ru.lesch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.lesch.ui.MainView;

public class LeschApp extends Application {

    public static final String APP_TITLE = "LESH — Лицейская Электронная Школа";

    @Override
    public void start(Stage stage) {
        MainView root = new MainView();
        Scene scene = new Scene(root, 1120, 740);

        var css = LeschApp.class.getResource("/css/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle(APP_TITLE);
        stage.setMinWidth(920);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
