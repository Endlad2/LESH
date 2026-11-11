package ru.lesch.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ru.lesch.model.GradeData;
import ru.lesch.model.Topic;
import ru.lesch.service.ContentLoader;
import ru.lesch.service.YandexSearch;

public class MainView extends BorderPane {

private final ComboBox<String> gradeBox = new ComboBox<>();
private final TabPane tabs = new TabPane();

private final ListView<Topic> tasksList = new ListView<>();
private final ListView<Topic> videosList = new ListView<>();

private final Label tasksPlaceholder = new Label("Выберите класс, чтобы увидеть задания");
private final Label videosPlaceholder = new Label("Выберите класс, чтобы увидеть видео-материалы");

public MainView() {
gradeBox.setPromptText("Выберите класс");
gradeBox.setItems(FXCollections.observableArrayList("5 класс", "6 класс", "7 класс", "8 класс"));
gradeBox.setPrefWidth(160);
gradeBox.valueProperty().addListener((obs, oldV, newV) -> onGradeChanged());

Label label = new Label("Класс:");
HBox topBar = new HBox(8, label, gradeBox);
topBar.setAlignment(Pos.CENTER_LEFT);
topBar.setPadding(new Insets(10));
topBar.getStyleClass().add("top-bar");
setTop(topBar);

Tab tasksTab = new Tab("Задания", buildTasksPane());
tasksTab.setClosable(false);
Tab videosTab = new Tab("Видео-материалы", buildVideosPane());
videosTab.setClosable(false);
tabs.getTabs().addAll(tasksTab, videosTab);
setCenter(tabs);

tasksList.setOnMouseClicked(e -> {
Topic t = tasksList.getSelectionModel().getSelectedItem();
if (t != null && e.getClickCount() == 1) {
openTest(t);
}
});
videosList.setOnMouseClicked(e -> {
Topic t = videosList.getSelectionModel().getSelectedItem();
if (t != null && e.getClickCount() == 1) {
YandexSearch.open(t.title + " музыка " + gradeBox.getValue());
}
});
}

private Node buildTasksPane() {
StackPane stack = new StackPane();
stack.setPadding(new Insets(12));
tasksPlaceholder.getStyleClass().add("placeholder");
stack.getChildren().add(tasksPlaceholder);
stack.getChildren().add(tasksList);
tasksList.setVisible(false);
return stack;
}

private Node buildVideosPane() {
StackPane stack = new StackPane();
stack.setPadding(new Insets(12));
videosPlaceholder.getStyleClass().add("placeholder");
stack.getChildren().add(videosPlaceholder);
stack.getChildren().add(videosList);
videosList.setVisible(false);
return stack;
}

private void onGradeChanged() {
String v = gradeBox.getValue();
if (v == null) {
tasksList.setVisible(false);
videosList.setVisible(false);
tasksPlaceholder.setVisible(true);
videosPlaceholder.setVisible(true);
tasksList.getItems().clear();
videosList.getItems().clear();
return;
}
int grade = Integer.parseInt(v.replaceAll("\D+", ""));
GradeData data = ContentLoader.load(grade);

tasksList.setItems(FXCollections.observableArrayList(data.topics));
videosList.setItems(FXCollections.observableArrayList(data.topics));
tasksList.setCellFactory(lv -> new TopicCell());
videosList.setCellFactory(lv -> new TopicCell());

tasksPlaceholder.setVisible(false);
videosPlaceholder.setVisible(false);
tasksList.setVisible(true);
videosList.setVisible(true);
}

private void openTest(Topic topic) {
if (topic.questions == null || topic.questions.isEmpty()) {
Alert a = new Alert(Alert.AlertType.INFORMATION,
"Тест по теме «" + topic.title + "» скоро появится.", ButtonType.OK);
a.setHeaderText(null);
a.showAndWait();
return;
}
new TestView(topic).show();
}

private static class TopicCell extends ListCell<Topic> {
@Override
protected void updateItem(Topic t, boolean empty) {
super.updateItem(t, empty);
if (empty || t == null) {
setText(null);
setGraphic(null);
} else {
Label title = new Label(t.title);
title.getStyleClass().add("topic-title");
Label desc = new Label(t.description == null ? "" : t.description);
desc.getStyleClass().add("topic-desc");
desc.setWrapText(true);
VBox box = new VBox(2, title, desc);
box.setPadding(new Insets(6, 4, 6, 4));
setGraphic(box);
}
}
}
}