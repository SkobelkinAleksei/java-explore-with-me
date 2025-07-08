package ru.practicum.ewm.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
@Getter
public class DefaultMessagesForException {
    public static final String USER_NOT_FOUND = "Пользователь не был найден.";
    public static final String EVENT_NOT_FOUND = "Событие не было найдено.";
    public static final String EVENT_NOT_FOUND_FOR_USER = "Событие для пользователя не найдено.";
    public static final String STATE_NOT_FOUND = "Состояние не было найдено.";
    public static final String EVENT_LIMIT_REACHED = "Лимит заявок был превышен.";
    public static final String REQUEST_NOT_FOUND_FOR_USER = "Нет заявки на событие для данного пользователя.";
    public static final String COMPILATION_NOT_FOUND = "Подборка не была найдена.";
    public static final String CATEGORY_NOT_FOUND = "Категория не была найдена.";
    public static final String CANNOT_DELETE_CATEGORY_WITH_EVENTS = "Невозможно удалить категорию со связанными событиями.";
    public static final String COMPILATION_IS_NULL = "Подборка не может быть null.";
}
