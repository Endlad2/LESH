# Как запустить LESH 1.1.0

## Из исходников

Требуются JDK 21 и Maven 3.9+.

```bash
mvn clean javafx:run
```

## Сборка на своей ОС

```bash
mvn clean package
java -jar target/LESH-1.1.0.jar
```

Главным классом JAR является `ru.lesch.Launcher`.

## GitHub Actions / Release

Workflow собирает приложение отдельно на трёх системах:

- Windows → `LESH-windows.jar`;
- Linux → `LESH-linux.jar`;
- macOS → `LESH-macos.jar`.

Запускайте JAR, соответствующий своей операционной системе:

```bash
java -jar LESH-linux.jar
```

На Windows аналогично:

```bat
java -jar LESH-windows.jar
```

## Если сборка не запускается

Проверьте версии:

```bash
java -version
mvn -version
```

Нужна Java 21. Проект следует импортировать в IDE как Maven-проект.

## Контент

Учебные материалы находятся в `src/main/resources/content/grade5.json` … `grade8.json`. При загрузке приложение проверяет структуру вопросов. Полный охват тем описан в `CONTENT_COVERAGE.md`.
