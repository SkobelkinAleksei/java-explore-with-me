package ru.practicum.main.model.event;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum State {
    PENDING("PENDING"),
    PUBLISHED("PUBLISHED"),
    CANCELED("CANCELED");

    final String name;

    State(String name) {
        this.name = name;
    }

    public boolean isCorrectState(String stateName) {
        return Arrays.stream(State.values())
                .anyMatch(state -> state.getName().equals(stateName));
    }
}
