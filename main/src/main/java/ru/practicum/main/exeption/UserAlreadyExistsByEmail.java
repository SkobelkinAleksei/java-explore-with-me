package ru.practicum.main.exeption;

public class UserAlreadyExistsByEmail extends RuntimeException {
    public UserAlreadyExistsByEmail(String message) {
        super(message);
    }
}
