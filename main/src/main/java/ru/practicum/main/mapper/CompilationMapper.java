package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.model.complitation.CompilationDto;
import ru.practicum.main.model.complitation.CompilationEntity;
import ru.practicum.main.model.complitation.NewCompilationDto;
import ru.practicum.main.model.complitation.UpdateCompilationRequest;
import ru.practicum.main.model.event.EventEntity;
import ru.practicum.main.model.event.EventShortDto;

import java.util.List;
import java.util.Set;

@UtilityClass
public class CompilationMapper {
    public CompilationEntity toEntity(NewCompilationDto compilationDto) {
        return CompilationEntity.builder()
                .title(compilationDto.getTitle())
                .pinned(false)
                .build();
    }

    public CompilationDto toDto(CompilationEntity compilationEntity,
                                List<EventShortDto> eventShortDtoList
    ) {
        return CompilationDto.builder()
                .id(compilationEntity.getId())
                .pinned(compilationEntity.getPinned())
                .title(compilationEntity.getTitle())
                .events(eventShortDtoList)
                .build();
    }

    public CompilationEntity toUpdateEntity(UpdateCompilationRequest compilationRequest,
                                            CompilationEntity currentEntity,
                                            Set<EventEntity> updatedEntitySet
    ) {
        return CompilationEntity.builder()
                .events(updatedEntitySet != null
                        && !updatedEntitySet.isEmpty()
                        ? updatedEntitySet
                        : currentEntity.getEvents()
                )
                .pinned(compilationRequest.getPinned() != null
                        ? compilationRequest.getPinned()
                        : currentEntity.getPinned()
                )
                .title(compilationRequest.getTitle() != null
                        ? compilationRequest.getTitle()
                        : currentEntity.getTitle()
                )
                .build();
    }
}
