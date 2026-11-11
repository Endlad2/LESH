Как запустить LESH

1. Dev-запуск:
mvn clean javafx:run
2. Сборка JAR:
mvn -q -DskipTests package
Результат: target/LESH-1.0.0.jar (shaded).
3. Запуск JAR:
java -jar target/LESH-1.0.0.jar
Внимание: для standalone-jar на JavaFX может понадобиться
установить JavaFX runtime отдельно и указать --module-path / --add-modules.
Проще всего — mvn javafx:run.
4. CI-релизы:
Пуш в main/master → GitHub Actions создаёт тег гг.мм.дд.час.мин (Москва)
и публикует Release с JAR-артефактом.
5. Добавление тем и вопросов:
Редактируй src/main/resources/content/gradeN.json. Перезапусти приложение.