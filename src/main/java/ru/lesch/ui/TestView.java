package ru.lesch.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.lesch.model.Question;
import ru.lesch.model.Topic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TestView {

    private final Topic topic;
    private final Stage stage = new Stage();
    private final VBox questionsBox = new VBox(14);
    private final Map<Question, AnswerControl> answers = new LinkedHashMap<>();
    private final Map<Question, VBox> cards = new LinkedHashMap<>();
    private final Label resultLabel = new Label();

    public TestView(Topic topic) {
        this.topic = topic;
    }

    public void show() {
        answers.clear();
        cards.clear();
        questionsBox.getChildren().clear();
        questionsBox.setPadding(new Insets(16, 18, 18, 18));

        List<Question> questions = topic.questions == null ? List.of() : topic.questions;

        Label title = new Label(topic.title == null ? "Тест" : topic.title);
        title.getStyleClass().add("test-title");
        title.setWrapText(true);

        Label meta = new Label("Вопросов: " + questions.size() + " • отвечайте в удобном порядке");
        meta.getStyleClass().add("test-meta");

        VBox heading = new VBox(4, title, meta);
        heading.setPadding(new Insets(18, 20, 14, 20));
        heading.getStyleClass().add("test-header");

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            AnswerControl control = createControl(question);
            answers.put(question, control);

            Label number = new Label("Вопрос " + (i + 1));
            number.getStyleClass().add("question-number");

            Label prompt = new Label(question.prompt == null ? "" : question.prompt);
            prompt.setWrapText(true);
            prompt.getStyleClass().add("question-prompt");

            VBox card = new VBox(9, number, prompt, control.node);
            card.getStyleClass().add("question-card");
            card.setPadding(new Insets(16));
            cards.put(question, card);
            questionsBox.getChildren().add(card);
        }

        ScrollPane scroll = new ScrollPane(questionsBox);
        scroll.setFitToWidth(true);
        scroll.setPannable(true);
        scroll.getStyleClass().add("questions-scroll");

        Button submit = new Button("Проверить ответы");
        submit.getStyleClass().add("primary-btn");
        submit.setDefaultButton(true);
        submit.setOnAction(e -> check());

        Button close = new Button("Закрыть");
        close.getStyleClass().add("secondary-btn");
        close.setCancelButton(true);
        close.setOnAction(e -> stage.close());

        resultLabel.getStyleClass().add("result-label");
        resultLabel.setVisible(false);
        resultLabel.setManaged(false);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttons = new HBox(10, resultLabel, spacer, close, submit);
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setPadding(new Insets(12, 20, 14, 20));
        buttons.getStyleClass().add("test-footer");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("test-root");
        root.setTop(heading);
        root.setCenter(scroll);
        root.setBottom(buttons);

        Scene scene = new Scene(root, 920, 720);
        var css = getClass().getResource("/css/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("LESH — " + (topic.title == null ? "Тест" : topic.title));
        stage.setMinWidth(760);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void check() {
        int score = 0;
        int total = answers.size();

        for (Map.Entry<Question, AnswerControl> entry : answers.entrySet()) {
            boolean correct = entry.getValue().isCorrect();
            if (correct) {
                score++;
            }

            VBox card = cards.get(entry.getKey());
            if (card != null) {
                card.getStyleClass().removeAll("question-correct", "question-wrong");
                card.getStyleClass().add(correct ? "question-correct" : "question-wrong");
            }
        }

        String verdict = verdict(score, total);
        resultLabel.setText("Результат: " + score + " из " + total + " — " + verdict);
        resultLabel.setVisible(true);
        resultLabel.setManaged(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, verdict, ButtonType.OK);
        alert.setTitle("Результат");
        alert.setHeaderText(score + " из " + total);
        alert.showAndWait();
    }

    private String verdict(int score, int total) {
        double percent = total == 0 ? 0 : score * 100.0 / total;
        if (percent >= 90) {
            return "Отлично!";
        }
        if (percent >= 75) {
            return "Хорошо!";
        }
        if (percent >= 50) {
            return "Неплохо, но есть куда расти.";
        }
        return "Стоит повторить материал.";
    }

    private AnswerControl createControl(Question q) {
        if (q instanceof Question.Single single) {
            return single(single);
        }
        if (q instanceof Question.TrueFalse trueFalse) {
            return trueFalse(trueFalse);
        }
        if (q instanceof Question.Short shortQuestion) {
            return shortAnswer(shortQuestion);
        }
        if (q instanceof Question.Open openQuestion) {
            return open(openQuestion);
        }
        if (q instanceof Question.Match match) {
            return match(match);
        }

        Label unsupported = new Label("Этот тип вопроса пока не поддерживается.");
        unsupported.getStyleClass().add("validation-error");
        return new AnswerControl(unsupported) {
            @Override
            public boolean isCorrect() {
                return false;
            }
        };
    }

    private AnswerControl single(Question.Single q) {
        ToggleGroup group = new ToggleGroup();
        VBox box = new VBox(7);
        List<String> options = q.options == null ? List.of() : q.options;

        for (int i = 0; i < options.size(); i++) {
            RadioButton radio = new RadioButton(options.get(i));
            radio.setToggleGroup(group);
            radio.setUserData(i);
            radio.setWrapText(true);
            box.getChildren().add(radio);
        }

        return new AnswerControl(box) {
            @Override
            public boolean isCorrect() {
                Toggle selected = group.getSelectedToggle();
                return selected != null
                        && selected.getUserData() instanceof Integer index
                        && index == q.correct;
            }
        };
    }

    private AnswerControl trueFalse(Question.TrueFalse q) {
        ToggleGroup group = new ToggleGroup();
        RadioButton yes = new RadioButton("Верно");
        RadioButton no = new RadioButton("Неверно");
        yes.setToggleGroup(group);
        yes.setUserData(Boolean.TRUE);
        no.setToggleGroup(group);
        no.setUserData(Boolean.FALSE);

        HBox box = new HBox(18, yes, no);
        box.setAlignment(Pos.CENTER_LEFT);

        return new AnswerControl(box) {
            @Override
            public boolean isCorrect() {
                Toggle selected = group.getSelectedToggle();
                return selected != null
                        && selected.getUserData() instanceof Boolean value
                        && value == q.correct;
            }
        };
    }

    private AnswerControl shortAnswer(Question.Short q) {
        TextField field = new TextField();
        field.setPromptText("Ваш ответ");
        field.setMaxWidth(520);

        return new AnswerControl(field) {
            @Override
            public boolean isCorrect() {
                String value = normalize(field.getText());
                if (value.isEmpty() || q.answers == null) {
                    return false;
                }
                for (String answer : q.answers) {
                    if (value.equals(normalize(answer))) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private AnswerControl open(Question.Open q) {
        TextArea area = new TextArea();
        area.setPrefRowCount(4);
        area.setWrapText(true);
        area.setPromptText("Напишите ответ — учитываются ключевые слова");

        return new AnswerControl(area) {
            @Override
            public boolean isCorrect() {
                String value = normalize(area.getText());
                if (value.isEmpty() || q.keywords == null || q.keywords.isEmpty()) {
                    return false;
                }

                int hits = 0;
                for (String keyword : q.keywords) {
                    String normalizedKeyword = normalize(keyword);
                    if (!normalizedKeyword.isEmpty() && value.contains(normalizedKeyword)) {
                        hits++;
                    }
                }
                return hits >= Math.max(1, q.minHits);
            }
        };
    }

    private AnswerControl match(Question.Match q) {
        VBox box = new VBox(9);
        List<String> left = q.left == null ? List.of() : q.left;
        List<String> right = q.right == null ? List.of() : q.right;
        List<ComboBox<String>> combos = new ArrayList<>();

        for (String leftValue : left) {
            Label label = new Label(leftValue);
            label.setWrapText(true);
            label.setMinWidth(180);
            HBox.setHgrow(label, Priority.ALWAYS);

            ComboBox<String> combo = new ComboBox<>();
            combo.getItems().addAll(right);
            combo.setPromptText("Выберите пару");
            combo.setPrefWidth(260);
            combos.add(combo);

            HBox row = new HBox(12, label, combo);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("match-row");
            box.getChildren().add(row);
        }

        return new AnswerControl(box) {
            @Override
            public boolean isCorrect() {
                if (q.correct == null || q.correct.size() != combos.size()) {
                    return false;
                }

                for (int i = 0; i < combos.size(); i++) {
                    String selected = combos.get(i).getValue();
                    if (selected == null) {
                        return false;
                    }
                    int selectedIndex = right.indexOf(selected);
                    Integer expectedIndex = q.correct.get(i);
                    if (expectedIndex == null || selectedIndex != expectedIndex) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace('ё', 'е')
                .replaceAll("[\\s\\u00A0]+", " ")
                .replaceAll("[.!?,;:]+$", "");
    }

    private abstract static class AnswerControl {
        final Node node;

        AnswerControl(Node node) {
            this.node = node;
        }

        abstract boolean isCorrect();
    }
}
