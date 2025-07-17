package ru.practicum.ewm.model.event;

import jakarta.validation.ValidationException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum StateActionPrivate {
    SEND_TO_REVIEW("SEND_TO_REVIEW"),
    CANCEL_REVIEW("CANCEL_REVIEW");

    final String description;

    StateActionPrivate(String value) {
        this.description = value;
    }

    public static void isCorrectState(String state) {
        boolean isCorrect = Arrays.stream(StateActionPrivate.values())
                .anyMatch(stateEnum -> stateEnum.getDescription().equals(state));

        if (!isCorrect) throw new ValidationException("State not found");
    }
}