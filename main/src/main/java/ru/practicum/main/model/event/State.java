package ru.practicum.main.model.event;

import lombok.Getter;
import ru.practicum.main.utils.DefaultMessagesForException;

import java.util.Arrays;

@Getter
public enum State {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    PUBLISHED("PUBLISHED"),
    REJECTED("REJECTED"),
    CANCELED("CANCELED");

    final String name;

    State(String name) {
        this.name = name;
    }

    public static boolean isCorrectState(String stateName) {
        return Arrays.stream(State.values())
                .anyMatch(state -> state.getName().equals(stateName));
    }

    public static State fromStringToState(String stateAction) {

        if (!isCorrectState(stateAction))
            throw new IllegalArgumentException(DefaultMessagesForException.STATE_NOT_FOUND);

        return State.valueOf(stateAction);
    }

}
