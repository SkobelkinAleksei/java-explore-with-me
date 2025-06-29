package ru.practicum.main.exeption;

public class CategoryAlreadyExists extends RuntimeException {
    public CategoryAlreadyExists(String message) {
        super(message);
    }
}
