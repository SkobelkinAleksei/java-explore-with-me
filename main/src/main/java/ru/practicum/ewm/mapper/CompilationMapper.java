package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.complitation.CompilationDto;
import ru.practicum.ewm.model.complitation.CompilationEntity;
import ru.practicum.ewm.model.complitation.NewCompilationDto;
import ru.practicum.ewm.model.complitation.UpdateCompilationRequest;
import ru.practicum.ewm.model.event.EventEntity;
import ru.practicum.ewm.model.event.EventShortDto;

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
