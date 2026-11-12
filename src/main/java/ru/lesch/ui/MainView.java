package ru.lesch.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ru.lesch.model.GradeData;
import ru.lesch.model.Topic;
import ru.lesch.service.ContentLoader;
import ru.lesch.service.YandexSearch;

import java.util.List;

public class MainView extends BorderPane {

    private final ComboBox<Integer> gradeBox = new ComboBox<>();
    private final TabPane tabs = new TabPane();
    private final ListView<Topic> tasksList = new ListView<>();
    private final ListView<Topic> videosList = new ListView<>();

    private final Label tasksPlaceholder = new Label("Выберите класс, чтобы увидеть задания");
    private final Label videosPlaceholder = new Label("Выберите класс, чтобы увидеть видео-материалы");
    private final Label statusLabel = new Label("Выберите класс — загрузятся темы и тесты");

    public MainView() {
        getStyleClass().add("main-view");
        setTop(buildHeader());
        setCenter(buildContent());
        setBottom(buildFooter());

        configureLists();
        configureGradeSelector();
    }

    private Node buildHeader() {
        Label brand = new Label("LESH");
        brand.getStyleClass().add("brand-title");

        Label subtitle = new Label("Лицейская Электронная Школа");
        subtitle.getStyleClass().add("brand-subtitle");

        VBox branding = new VBox(1, brand, subtitle);
        branding.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label classLabel = new Label("Класс");
        classLabel.getStyleClass().add("field-label");

        VBox selector = new VBox(4, classLabel, gradeBox);
        selector.setAlignment(Pos.CENTER_LEFT);

        HBox header = new HBox(18, branding, spacer, selector);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 24, 18, 24));
        header.getStyleClass().add("top-bar");
        return header;
    }

    private Node buildContent() {
        Tab tasksTab = new Tab("Задания", buildTasksPane());
        tasksTab.setClosable(false);

        Tab videosTab = new Tab("Видео-материалы", buildVideosPane());
        videosTab.setClosable(false);

        tabs.getTabs().addAll(tasksTab, videosTab);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getStyleClass().add("content-tabs");

        VBox content = new VBox(tabs);
        VBox.setVgrow(tabs, Priority.ALWAYS);
        content.setPadding(new Insets(18, 24, 14, 24));
        return content;
    }

    private Node buildFooter() {
        Label hint = new Label("Подсказка: Enter или двойной клик — открыть выбранную тему");
        hint.getStyleClass().add("footer-hint");
        statusLabel.getStyleClass().add("status-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox footer = new HBox(12, statusLabel, spacer, hint);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(10, 24, 14, 24));
        return footer;
    }

    private Node buildTasksPane() {
        tasksPlaceholder.getStyleClass().add("placeholder");
        return wrapList(tasksList, tasksPlaceholder);
    }

    private Node buildVideosPane() {
        videosPlaceholder.getStyleClass().add("placeholder");
        return wrapList(videosList, videosPlaceholder);
    }

    private Node wrapList(ListView<Topic> list, Label placeholder) {
        StackPane stack = new StackPane();
        stack.setPadding(new Insets(14, 0, 0, 0));
        stack.getChildren().addAll(placeholder, list);
        list.setVisible(false);
        return stack;
    }

    private void configureGradeSelector() {
        gradeBox.setPromptText("Выберите класс");
        gradeBox.setItems(FXCollections.observableArrayList(5, 6, 7, 8));
        gradeBox.setPrefWidth(180);
        gradeBox.setMaxWidth(180);

        gradeBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item + " класс");
            }
        });
        gradeBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item + " класс");
            }
        });

        gradeBox.valueProperty().addListener((obs, oldValue, newValue) -> onGradeChanged(newValue));
    }

    private void configureLists() {
        tasksList.setCellFactory(lv -> new TopicCell(true));
        videosList.setCellFactory(lv -> new TopicCell(false));
        tasksList.setPlaceholder(new Label("Для этого класса пока нет тем"));
        videosList.setPlaceholder(new Label("Для этого класса пока нет тем"));

        tasksList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Topic topic = tasksList.getSelectionModel().getSelectedItem();
                if (topic != null) {
                    openTest(topic);
                }
            }
        });
        tasksList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                Topic topic = tasksList.getSelectionModel().getSelectedItem();
                if (topic != null) {
                    openTest(topic);
                }
            }
        });

        videosList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Topic topic = videosList.getSelectionModel().getSelectedItem();
                if (topic != null) {
                    openVideo(topic);
                }
            }
        });
        videosList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                Topic topic = videosList.getSelectionModel().getSelectedItem();
                if (topic != null) {
                    openVideo(topic);
                }
            }
        });
    }

    private void onGradeChanged(Integer grade) {
        if (grade == null) {
            showEmptyState();
            return;
        }

        try {
            GradeData data = ContentLoader.load(grade);
            List<Topic> topics = data.topics == null ? List.of() : data.topics;

            tasksList.setItems(FXCollections.observableArrayList(topics));
            videosList.setItems(FXCollections.observableArrayList(topics));

            tasksPlaceholder.setVisible(false);
            videosPlaceholder.setVisible(false);
            tasksList.setVisible(true);
            videosList.setVisible(true);

            String subject = data.subject == null || data.subject.isBlank() ? "Материалы" : data.subject;
            int questionCount = topics.stream()
                    .mapToInt(topic -> topic.questions == null ? 0 : topic.questions.size())
                    .sum();
            statusLabel.setText(subject + " • " + grade + " класс • тем: " + topics.size() + " • заданий: " + questionCount);
        } catch (RuntimeException ex) {
            showEmptyState();
            statusLabel.setText("Не удалось загрузить материалы для " + grade + " класса");
            showError("Ошибка загрузки", "Не удалось открыть материалы", rootMessage(ex));
        }
    }

    private void showEmptyState() {
        tasksList.getItems().clear();
        videosList.getItems().clear();
        tasksList.setVisible(false);
        videosList.setVisible(false);
        tasksPlaceholder.setVisible(true);
        videosPlaceholder.setVisible(true);
    }

    private void openTest(Topic topic) {
        if (topic.questions == null || topic.questions.isEmpty()) {
            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Тест по теме «" + topic.title + "» скоро появится.",
                    ButtonType.OK
            );
            alert.setTitle("LESH");
            alert.setHeaderText("Пока без теста");
            alert.showAndWait();
            return;
        }
        new TestView(topic).show();
    }

    private void openVideo(Topic topic) {
        String grade = gradeBox.getValue() == null ? "" : gradeBox.getValue() + " класс";
        boolean opened = YandexSearch.openVideo(topic.title + " музыка " + grade);
        if (!opened) {
            showError(
                    "Не удалось открыть браузер",
                    "Видео-поиск не запустился",
                    "Проверьте, что в системе установлен браузер по умолчанию."
            );
        }
    }

    private void showError(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        String message = current.getMessage();
        return message == null || message.isBlank() ? current.getClass().getSimpleName() : message;
    }

    private static class TopicCell extends ListCell<Topic> {
        private final boolean showQuestionCount;

        TopicCell(boolean showQuestionCount) {
            this.showQuestionCount = showQuestionCount;
        }

        @Override
        protected void updateItem(Topic topic, boolean empty) {
            super.updateItem(topic, empty);
            setText(null);

            if (empty || topic == null) {
                setGraphic(null);
                return;
            }

            Label title = new Label(topic.title == null ? "Без названия" : topic.title);
            title.getStyleClass().add("topic-title");
            title.setWrapText(true);

            Label desc = new Label(topic.description == null ? "" : topic.description);
            desc.getStyleClass().add("topic-desc");
            desc.setWrapText(true);

            VBox text = new VBox(4, title, desc);
            HBox.setHgrow(text, Priority.ALWAYS);

            int count = topic.questions == null ? 0 : topic.questions.size();
            Label badge = new Label(showQuestionCount ? count + " заданий" : "Видео");
            badge.getStyleClass().add("topic-badge");

            HBox card = new HBox(14, text, badge);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(13, 14, 13, 14));
            card.getStyleClass().add("topic-card");
            setGraphic(card);
        }
    }
}
