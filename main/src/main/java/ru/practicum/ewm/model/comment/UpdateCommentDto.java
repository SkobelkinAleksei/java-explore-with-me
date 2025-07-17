package ru.practicum.ewm.model.comment;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateCommentDto(

        @Size(min = 5, max = 255, message = "Размер должен находиться в диапазоне от 5 до 255")
        String message
) {
}