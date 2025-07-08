package ru.practicum.main.model.event;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum StateAction {
    PUBLISH_EVENT("PUBLISH_EVENT"),
    REJECT_EVENT("REJECT_EVENT");

    final String name;

    StateAction(String name) {
        this.name = name;
    }

    public boolean isCorrectStateAction(String stateActionName) {
        return Arrays.stream(State.values())
                .anyMatch(state -> state.getName().equals(stateActionName));
    }
}
