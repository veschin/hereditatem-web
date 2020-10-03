# Стартовое приложение

## Запуск 

При запуске из Calva нужно выбрать

1. проект *Clojure CLI + shadow-cljs*
1. альясы для запуска: *dev, repl*

После старта будут доступны clj и cljs реплы

## Состав

### Backend

* **DI**: mount
* **server**: http-kit
* **routing**: bidi
* **json**: Cheshire
* **env-manager**: dotenv

### Frontend

* **compiler**: shadow-cljs
* **history-manager**: accountant
* **UI**: re-frame + semantic-ui
* **requests**: cljs-ajax

