package com.company.resourceapi.services;

import com.company.resourceapi.dto.CreateProjectDTO;
import com.company.resourceapi.dto.UpdateProjectDTO;
import com.company.resourceapi.entities.Project;
import com.company.resourceapi.exceptions.ConflictedExternalIdException;
import com.company.resourceapi.exceptions.NotFoundException;

public interface ProjectService {

    Project getProject(long id) throws NotFoundException;

    Project createProject (CreateProjectDTO createProjectDTO) throws NotFoundException, ConflictedExternalIdException;

    Project updateProject (Long projectId, UpdateProjectDTO updateProjectDTO) throws NotFoundException, ConflictedExternalIdException;
}
