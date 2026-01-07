package by.own.sharedsources.model;

import lombok.Getter;

@Getter
public enum EventType {

    USER_CREATED("Пользователь создан"),
    USER_DELETED("Пользователь удален"),
    USER_LOGIN("Пользователь вошел в аккаунт"),
    USER_LOGOUT("Пользователь вышел из аккаунта"),
    USER_SUBSCRIBED("Пользователь подписался");

    private final String description;

    EventType(String description) {
        this.description = description;
    }
}