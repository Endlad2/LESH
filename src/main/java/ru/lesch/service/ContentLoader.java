package ru.lesch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.lesch.model.GradeData;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ContentLoader {

private static final ObjectMapper MAPPER = new ObjectMapper();
private static final Map<Integer, GradeData> CACHE = new ConcurrentHashMap<>();

private ContentLoader() {}

public static GradeData load(int grade) {
return CACHE.computeIfAbsent(grade, g -> {
String path = "/content/grade" + g + ".json";
try (InputStream in = ContentLoader.class.getResourceAsStream(path)) {
if (in == null) {
throw new IllegalStateException("Не найден ресурс " + path);
}
return MAPPER.readValue(in, GradeData.class);
} catch (Exception e) {
throw new RuntimeException("Ошибка загрузки " + path, e);
}
});
}
}