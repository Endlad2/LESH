package ru.lesch.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ru.lesch.model.Question;
import ru.lesch.model.Topic;

import java.util.*;

public class TestView {

private final Topic topic;
private final Stage stage = new Stage();
private final VBox questionsBox = new VBox(12);
private final Map<Question, AnswerControl> answers = new LinkedHashMap<>();

public TestView(Topic topic) {
this.topic = topic;
}

public void show() {
Label title = new Label("Тест: " + topic.title);
title.getStyleClass().add("test-title");

for (Question q : topic.questions) {
AnswerControl ac = createControl(q);
answers.put(q, ac);
VBox card = new VBox(6);
card.getStyleClass().add("question-card");
card.setPadding(new Insets(10));
Label prompt = new Label(q.prompt);
prompt.setWrapText(true);
prompt.getStyleClass().add("question-prompt");
card.getChildren().addAll(prompt, ac.node);
questionsBox.getChildren().add(card);
}

ScrollPane scroll = new ScrollPane(questionsBox);
scroll.setFitToWidth(true);

Button submit = new Button("Проверить");
submit.getStyleClass().add("primary-btn");
submit.setOnAction(e -> check());

Button close = new Button("Закрыть");
close.setOnAction(e -> stage.close());

HBox buttons = new HBox(10, submit, close);
buttons.setAlignment(Pos.CENTER_RIGHT);
buttons.setPadding(new Insets(10));

BorderPane root = new BorderPane();
VBox top = new VBox(8, title);
top.setPadding(new Insets(12, 12, 0, 12));
root.setTop(top);
root.setCenter(scroll);
root.setBottom(buttons);

Scene scene = new Scene(root, 900, 700);
var css = getClass().getResource("/css/style.css");
if (css != null) scene.getStylesheets().add(css.toExternalForm());

stage.setTitle("LESH - by Endlad — " + topic.title);
stage.setScene(scene);
stage.show();
}

private void check() {
int score = 0;
for (var e : answers.entrySet()) {
if (e.getValue().isCorrect()) score++;
}
int total = topic.questions.size();
Alert a = new Alert(Alert.AlertType.INFORMATION);
a.setTitle("Результат");
a.setHeaderText(score + " из " + total);
a.setContentText(verdict(score, total));
a.showAndWait();
}

private String verdict(int score, int total) {
double p = total == 0 ? 0 : score * 100.0 / total;
if (p >= 90) return "Отлично!";
if (p >= 75) return "Хорошо!";
if (p >= 50) return "Неплохо, но есть куда расти.";
return "Стоит повторить материал.";
}

private AnswerControl createControl(Question q) {
if (q instanceof Question.Single s)    return single(s);
if (q instanceof Question.TrueFalse t) return trueFalse(t);
if (q instanceof Question.Short s)     return shortAnswer(s);
if (q instanceof Question.Open o)      return open(o);
if (q instanceof Question.Match m)     return match(m);
throw new IllegalStateException("Неизвестный тип вопроса: " + q.getClass());
}

private AnswerControl single(Question.Single q) {
ToggleGroup group = new ToggleGroup();
VBox box = new VBox(4);
for (int i = 0; i < q.options.size(); i++) {
RadioButton rb = new RadioButton(q.options.get(i));
rb.setToggleGroup(group);
rb.setUserData(i);
box.getChildren().add(rb);
}
return new AnswerControl(box) {
@Override public boolean isCorrect() {
Toggle t = group.getSelectedToggle();
return t != null && ((Integer) t.getUserData()) == q.correct;
}
};
}

private AnswerControl trueFalse(Question.TrueFalse q) {
ToggleGroup group = new ToggleGroup();
RadioButton t = new RadioButton("Верно");
RadioButton f = new RadioButton("Неверно");
t.setToggleGroup(group); t.setUserData(Boolean.TRUE);
f.setToggleGroup(group); f.setUserData(Boolean.FALSE);
HBox box = new HBox(16, t, f);
return new AnswerControl(box) {
@Override public boolean isCorrect() {
Toggle sel = group.getSelectedToggle();
return sel != null && ((Boolean) sel.getUserData()) == q.correct;
}
};
}

private AnswerControl shortAnswer(Question.Short q) {
TextField tf = new TextField();
tf.setPromptText("Ваш ответ");
return new AnswerControl(tf) {
@Override public boolean isCorrect() {
String v = tf.getText() == null ? "" : tf.getText().trim().toLowerCase(Locale.ROOT);
if (v.isEmpty()) return false;
for (String a : q.answers) {
if (v.equals(a.trim().toLowerCase(Locale.ROOT))) return true;
}
return false;
}
};
}

private AnswerControl open(Question.Open q) {
TextArea ta = new TextArea();
ta.setPrefRowCount(3);
ta.setWrapText(true);
ta.setPromptText("Напишите ответ (учитываются ключевые слова)");
return new AnswerControl(ta) {
@Override public boolean isCorrect() {
String v = ta.getText() == null ? "" : ta.getText().toLowerCase(Locale.ROOT);
int hits = 0;
for (String kw : q.keywords) {
if (v.contains(kw.toLowerCase(Locale.ROOT))) hits++;
}
return hits >= q.minHits;
}
};
}

private AnswerControl match(Question.Match q) {
VBox box = new VBox(6);
List<ComboBox<String>> combos = new ArrayList<>();
for (int i = 0; i < q.left.size(); i++) {
Label l = new Label(q.left.get(i));
ComboBox<String> cb = new ComboBox<>();
cb.getItems().addAll(q.right);
cb.setPromptText("…");
cb.setPrefWidth(220);
combos.add(cb);
HBox row = new HBox(8, l, cb);
row.setAlignment(Pos.CENTER_LEFT);
box.getChildren().add(row);
}
return new AnswerControl(box) {
@Override public boolean isCorrect() {
for (int i = 0; i < combos.size(); i++) {
String sel = combos.get(i).getValue();
if (sel == null) return false;
int idx = q.right.indexOf(sel);
if (idx != q.correct.get(i)) return false;
}
return true;
}
};
}

private abstract static class AnswerControl {
final Node node;
AnswerControl(Node node) { this.node = node; }
abstract boolean isCorrect();
}
}