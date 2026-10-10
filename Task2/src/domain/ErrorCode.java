package domain;

public enum ErrorCode {

    OK("Операция выполнена"),

    COURSE_NOT_FOUND("Курс не найден"),
    COURSE_CLOSED("Курс закрыт для зачисления"),
    PREREQ_MISSING("Не выполнены пререквизиты курса"),
    DUPLICATE_APPLICATION("Повторная заявка на этот курс"),
    NO_SEATS("Свободных мест нет, заявка поставлена в лист ожидания"),
    MALFORMED_RECORD("Запись файла не соответствует формату"),
    SOURCE_UNAVAILABLE("Файл импорта недоступен");

    private final String clientMessage;

    ErrorCode(String clientMessage) {
        this.clientMessage = clientMessage;
    }

    public String clientMessage() {
        return clientMessage;
    }
}
