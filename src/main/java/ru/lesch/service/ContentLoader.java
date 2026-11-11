package ru.lesch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.lesch.model.GradeData;
import ru.lesch.model.Question;
import ru.lesch.model.Topic;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ContentLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<Integer, GradeData> CACHE = new ConcurrentHashMap<>();

    private ContentLoader() {
    }

    public static GradeData load(int grade) {
        if (grade < 5 || grade > 8) {
            throw new IllegalArgumentException("Поддерживаются классы с 5 по 8");
        }

        return CACHE.computeIfAbsent(grade, ContentLoader::readAndValidate);
    }

    private static GradeData readAndValidate(int grade) {
        String path = "/content/grade" + grade + ".json";
        try (InputStream in = ContentLoader.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Не найден ресурс " + path);
            }
            GradeData data = MAPPER.readValue(in, GradeData.class);
            validate(data, grade, path);
            return data;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось загрузить " + path + ": " + e.getMessage(), e);
        }
    }

    private static void validate(GradeData data, int expectedGrade, String path) {
        if (data == null) {
            throw new IllegalStateException("Пустой файл данных: " + path);
        }
        if (data.grade != expectedGrade) {
            throw new IllegalStateException("В " + path + " указан неверный класс: " + data.grade);
        }
        if (data.topics == null) {
            data.topics = List.of();
            return;
        }

        for (int topicIndex = 0; topicIndex < data.topics.size(); topicIndex++) {
            Topic topic = data.topics.get(topicIndex);
            if (topic == null) {
                throw new IllegalStateException("Пустая тема №" + (topicIndex + 1) + " в " + path);
            }
            if (topic.title == null || topic.title.isBlank()) {
                throw new IllegalStateException("У темы №" + (topicIndex + 1) + " нет названия в " + path);
            }
            if (topic.questions == null) {
                topic.questions = List.of();
                continue;
            }
            for (int questionIndex = 0; questionIndex < topic.questions.size(); questionIndex++) {
                validateQuestion(topic.questions.get(questionIndex), path, topic.title, questionIndex);
            }
        }
    }

    private static void validateQuestion(Question q, String path, String topicTitle, int index) {
        String where = path + ", тема «" + topicTitle + "», вопрос " + (index + 1);
        if (q == null) {
            throw new IllegalStateException("Пустой вопрос: " + where);
        }
        if (q.prompt == null || q.prompt.isBlank()) {
            throw new IllegalStateException("Нет текста вопроса: " + where);
        }

        if (q instanceof Question.Single s) {
            if (s.options == null || s.options.size() < 2 || s.correct < 0 || s.correct >= s.options.size()) {
                throw new IllegalStateException("Некорректный single-вопрос: " + where);
            }
        } else if (q instanceof Question.Short s) {
            if (s.answers == null || s.answers.isEmpty()) {
                throw new IllegalStateException("Не заданы допустимые ответы: " + where);
            }
        } else if (q instanceof Question.Open o) {
            if (o.keywords == null || o.keywords.isEmpty() || o.minHits < 1 || o.minHits > o.keywords.size()) {
                throw new IllegalStateException("Некорректный open-вопрос: " + where);
            }
        } else if (q instanceof Question.Match m) {
            if (m.left == null || m.right == null || m.correct == null || m.left.size() != m.correct.size()) {
                throw new IllegalStateException("Некорректный match-вопрос: " + where);
            }
            for (Integer correctIndex : m.correct) {
                if (correctIndex == null || correctIndex < 0 || correctIndex >= m.right.size()) {
                    throw new IllegalStateException("Некорректный индекс в match-вопросе: " + where);
                }
            }
        }
    }
}
