package ru.practicum.ewm.exeption;

public class UserAlreadyExistsByEmail extends RuntimeException {
    public UserAlreadyExistsByEmail(String message) {
        super(message);
    }
}
