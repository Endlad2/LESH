package ru.lesch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.lesch.ui.MainView;

public class LeschApp extends Application {

public static final String APP_TITLE = "LESH - by Endlad";

@Override
public void start(Stage stage) {
MainView root = new MainView();
Scene scene = new Scene(root, 1100, 720);
var css = LeschApp.class.getResource("/css/style.css");
if (css != null) {
scene.getStylesheets().add(css.toExternalForm());
}
stage.setTitle(APP_TITLE);
stage.setMinWidth(900);
stage.setMinHeight(600);
stage.setScene(scene);
stage.show();
}

public static void main(String[] args) {
launch(args);
}
}