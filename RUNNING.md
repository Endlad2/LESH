# Как запустить LESH

## 1. Разработка

Требуются JDK 21 и Maven 3.9+.

```bash
mvn clean javafx:run
```

## 2. Сборка

```bash
mvn clean package
```

Результат:

```text
target/LESH-1.0.1.jar
```

## 3. Запуск собранного JAR

```bash
java -jar target/LESH-1.0.1.jar
```

Главным классом JAR является `ru.lesch.Launcher`, а не класс, наследующийся от JavaFX `Application`.

## 4. Если окно не появляется

Проверьте:

```bash
java -version
mvn -version
```

Нужен Java 21. Если используется запуск из IDE, импортируйте проект именно как Maven-проект и запускайте `ru.lesch.Launcher` либо цель `javafx:run`.

## 5. Контент

Материалы лежат в `src/main/resources/content/gradeN.json`. При загрузке приложение проверяет базовую структуру вопросов и выдаёт понятное сообщение вместо падения обработчика интерфейса.
