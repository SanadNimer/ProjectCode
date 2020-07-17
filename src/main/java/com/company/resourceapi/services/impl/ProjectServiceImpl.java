package com.company.resourceapi.services.impl;

import com.company.resourceapi.dto.CreateProjectDTO;
import com.company.resourceapi.dto.UpdateProjectDTO;
import com.company.resourceapi.entities.Project;
import com.company.resourceapi.entities.SdlcSystem;
import com.company.resourceapi.exceptions.ConflictedExternalIdException;
import com.company.resourceapi.exceptions.NotFoundException;
import com.company.resourceapi.repositories.ProjectRepository;
import com.company.resourceapi.repositories.SdlcSystemRepository;
import com.company.resourceapi.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final SdlcSystemRepository sdlcSystemRepository;

    public Project getProject(long id) throws NotFoundException {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Project.class, id));
    }

    @Override
    public Project createProject(CreateProjectDTO createProjectDTO) throws NotFoundException, ConflictedExternalIdException {
        Long sdlcSystemId = createProjectDTO.getSdlcSystem().getId();
        SdlcSystem sdlcSystem =
                sdlcSystemRepository
                        .findById(sdlcSystemId)
                        .orElseThrow(() -> new NotFoundException(SdlcSystem.class, sdlcSystemId));

        Optional<Project> optionalProjectOfSameExternalIdAndSystem =
                projectRepository
                        .findByExternalIdAndSdlcSystem(createProjectDTO.getExternalId(), sdlcSystem);
        if (optionalProjectOfSameExternalIdAndSystem.isPresent()) {
            throw new ConflictedExternalIdException(createProjectDTO.getExternalId(), sdlcSystemId);
        }

        Project projectCreated = new Project();
        projectCreated.setExternalId(createProjectDTO.getExternalId());
        projectCreated.setName(createProjectDTO.getName());
        projectCreated.setSdlcSystem(sdlcSystem);
        projectCreated.setCreatedDate(ZonedDateTime.now().toInstant());
        projectCreated.setLastModifiedDate(ZonedDateTime.now().toInstant());

        return projectRepository.save(projectCreated);
    }

    @Override
    public Project updateProject(Long projectId, UpdateProjectDTO updateProjectDTO) throws NotFoundException, ConflictedExternalIdException {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException(Project.class, projectId));
        project.setName(updateProjectDTO.getName() == null
                ? project.getName()
                : !updateProjectDTO.getName().isPresent()
                ? null
                : updateProjectDTO.getName().get());

        project.setExternalId(updateProjectDTO.getExternalId() == null
                ? project.getExternalId()
                : updateProjectDTO.getExternalId());
        if (updateProjectDTO.getSdlcSystem() != null) {
            Long sdlcSystemId = updateProjectDTO.getSdlcSystem().getId();
            SdlcSystem sdlcSystem =
                    sdlcSystemRepository
                            .findById(sdlcSystemId)
                            .orElseThrow(() -> new NotFoundException(SdlcSystem.class, sdlcSystemId));
            project.setSdlcSystem(sdlcSystem);
        }

        Optional<Project> optionalProjectOfSameExternalIdAndSystem =
                projectRepository
                        .findByExternalIdAndSdlcSystem(project.getExternalId(), project.getSdlcSystem());
        if (optionalProjectOfSameExternalIdAndSystem.isPresent() && optionalProjectOfSameExternalIdAndSystem.get().getId()!= projectId) {
            throw new ConflictedExternalIdException(project.getExternalId(), project.getSdlcSystem().getId());
        }
        project.setLastModifiedDate(ZonedDateTime.now().toInstant());
        return projectRepository.save(project);
    }
}
