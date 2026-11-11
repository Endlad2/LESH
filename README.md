# LESH — Лицейская Электронная Школа

JavaFX-приложение — аналог РЭШ, но свой. Название окна: `LESH - by Endlad`.

## Возможности

- Выбор класса (5–8) в левом верхнем углу.
- Вкладка «Задания»: список тем учебника → клик по теме открывает тест.
- Вкладка «Видео-материалы»: тот же список тем → клик открывает поиск видео на Яндекс.
- Тесты: 15 вопросов (7 тестовых + 8 прочих), итог X/15.
- Типы вопросов: single, truefalse, short, open, match.
- Если класс не выбран — обе вкладки показывают «Выберите класс».

## Технологии

- Java 21
- JavaFX 21 (controls)
- Maven
- JSON (Jackson)
- GitHub Actions → релиз с тегом гг.мм.дд.час.мин

## Запуск

mvn clean javafx:run

## Сборка

mvn -q -DskipTests package

## Структура

src/main/java/ru/lesch/
LeschApp.java
Launcher.java
model/Question.java, Topic.java, GradeData.java
ui/MainView.java, TestView.java
service/ContentLoader.java, YandexSearch.java
src/main/resources/
content/grade5.json … grade8.json
css/style.css
.github/workflows/release.yml