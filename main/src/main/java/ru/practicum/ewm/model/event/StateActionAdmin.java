package ru.practicum.ewm.model.event;

import jakarta.validation.ValidationException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum StateActionAdmin {
    PUBLISH_EVENT("PUBLISH_EVENT"),
    REJECT_EVENT("REJECT_EVENT");

    final String name;

    StateActionAdmin(String name) {
        this.name = name;
    }

    public static void isCorrectStateAction(String stateActionName) {
        boolean isCorrect = Arrays.stream(State.values())
                .anyMatch(state -> state.getName().equals(stateActionName));

        if (!isCorrect) throw new ValidationException("State not found");
    }
}
