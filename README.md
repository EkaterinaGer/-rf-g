Поисковый движок

Описание
Поисковый движок для индексации и поиска по веб-сайтам. Проект перенесен из searchlocaljava и адаптирован для работы с PostgreSQL.

Основные возможности
- Индексация веб-сайтов с обходом страниц
- Поиск по проиндексированным страницам с учетом релевантности
- Лемматизация текста для улучшения поиска
- Статистика по индексированным сайтам
- REST API для управления индексацией и поиском

Технологии
- Java 17
- Spring Boot 2.7.1
- PostgreSQL
- Liquibase для миграций БД
- Jsoup для парсинга HTML
- JPA/Hibernate

Требования
- Java 17 или выше
- Maven 3.6+
- PostgreSQL 12+

Установка и настройка

1. Клонируйте репозиторий
git clone https://github.com/EkaterinaGer/-rf-g.git
cd -rf-g

2. Настройте базу данных PostgreSQL
Создайте базу данных:
createdb searchengine

Или через psql:
psql -U postgres
CREATE DATABASE searchengine;

3. Настройте подключение к БД
Отредактируйте файл src/main/resources/application.yaml:
- url: jdbc:postgresql://localhost:5432/searchengine
- username: ваш_пользователь
- password: ваш_пароль

4. Соберите проект
mvn clean package

5. Запустите приложение
mvn spring-boot:run

Приложение будет доступно по адресу: http://localhost:8080

API эндпоинты

Запуск индексации сайта
POST /api/startIndexing?url=http://example.com
Возвращает: {"result": true}

Остановка индексации
POST /api/stopIndexing
Возвращает: {"result": true}

Индексация отдельной страницы
POST /api/indexPage?url=http://example.com/page
Возвращает: {"result": true}

Поиск
GET /api/search?query=запрос&site=http://example.com
Параметры:
- query (обязательный) - поисковый запрос
- site (опциональный) - ограничение поиска по сайту

Возвращает:
{
  "result": true,
  "count": 1,
  "data": [
    {
      "url": "http://example.com/page",
      "title": "Заголовок страницы",
      "snippet": "Фрагмент текста...",
      "relevance": 1.0
    }
  ]
}

Статистика
GET /api/statistics
Возвращает статистику по всем проиндексированным сайтам

Веб-интерфейс
Откройте в браузере: http://localhost:8080/

Структура проекта
src/main/java/searchengine/
  controllers/ - REST контроллеры
  services/ - Бизнес-логика
    CrawlingService - обход и индексация сайтов
    IndexingService - индексация отдельных страниц
    SearchServiceNew - поиск по индексу
  model/ - Модели данных (SiteTable, SitesPageTable, Lemma, SearchIndex)
  repository/ - JPA репозитории
  util/ - Утилиты (Lemmatizer, TextUtils)
  config/ - Конфигурация Spring

База данных
Таблицы создаются автоматически через Liquibase:
- site - информация о сайтах
- page - проиндексированные страницы
- lemma - леммы (нормализованные слова)
- search_index - индекс для поиска

Примеры использования

Индексация сайта
curl -X POST "http://localhost:8080/api/startIndexing?url=http://www.playback.ru/"

Поиск
curl "http://localhost:8080/api/search?query=playback"

Поиск на конкретном сайте
curl "http://localhost:8080/api/search?query=магазин&site=http://www.playback.ru/"

Статистика
curl "http://localhost:8080/api/statistics"

Разработка
Проект использует Maven для сборки. Основные команды:

Сборка проекта
mvn clean package

Запуск тестов
mvn test

Запуск приложения
mvn spring-boot:run

Автор
EkaterinaGer

Лицензия
Проект без указания лицензии
