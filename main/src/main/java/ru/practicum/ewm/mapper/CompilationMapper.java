package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.complitation.CompilationDto;
import ru.practicum.ewm.model.complitation.CompilationEntity;
import ru.practicum.ewm.model.event.EventShortDto;

import java.util.List;

@UtilityClass
public class CompilationMapper {
    public CompilationDto toDto(
            CompilationEntity compilationEntity,
            List<EventShortDto> eventShortDtoList
    ) {
        return CompilationDto.builder()
                .id(compilationEntity.getId())
                .pinned(compilationEntity.getPinned())
                .title(compilationEntity.getTitle())
                .events(eventShortDtoList)
                .build();
    }
}