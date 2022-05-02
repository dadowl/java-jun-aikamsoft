# Java Junior Test task

**Задание:** Pазработать приложение, предоставляющее сервис работы с данными в БД. Данный сервис, на
основании входных параметров(аргументы командной строки), типа операции и входного файла – извлекает
необходимые данные из БД и формирует результат обработки в выходной файл.

## Стек

- Java 8;
- PostgreSQL;
- Maven.

## Сборка

1. Склонировать проект в IDE;
2. Указать JDK для Java 8;
3. Изменить параметры подключения к Postgres БД(переменные url, user, password) в файле DatabaseUtils;
4. Выполнить Maven > Package.

## Запуск

Для запуска программы предусмотрены команды:
> java -jar app-1.0.jar search input.json output.json

Для поиска по указанныем критериям.

> java -jar app-1.0.jar stat input2.json output2.json

Для вывода статистики за указанный период.

## Тестирование

Для тестирования дамп БД и входные файлы лежат в каталоге "app" данного репозитория.