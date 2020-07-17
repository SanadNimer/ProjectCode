package com.company.resourceapi.controllers;

import com.company.resourceapi.dto.CreateProjectDTO;
import com.company.resourceapi.dto.UpdateProjectDTO;
import com.company.resourceapi.entities.Project;
import com.company.resourceapi.exceptions.ConflictedExternalIdException;
import com.company.resourceapi.exceptions.NotFoundException;
import com.company.resourceapi.services.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;

@RestController
@RequestMapping(ProjectRestController.ENDPOINT)
@Api(produces = MediaType.APPLICATION_JSON_VALUE, tags = "Project")
public class ProjectRestController {

    public static final String ENDPOINT = "/api/v2/projects";
    public static final String ENDPOINT_ID = "/{id}";
    public static final String PATH_VARIABLE_ID = "id";

    private static final String API_PARAM_ID = "ID";
    private static final String API_PARAM_PROJECT = "PROJECT";

    @Autowired
    private ProjectService projectService;

    @ApiOperation("Get a Project")
    @GetMapping(ENDPOINT_ID)
    public Project getProject(
            @ApiParam(name = API_PARAM_ID, required = true)
            @PathVariable(PATH_VARIABLE_ID) final long projectId
    ) throws NotFoundException {
        return projectService.getProject(projectId);
    }

    //API to create a new project ---> answer for assignment #1
    @ApiOperation("Create a Project")
    @PostMapping
    public ResponseEntity<Project> createProject(
            @ApiParam(name = API_PARAM_PROJECT, required = true)
            @RequestBody @Valid final CreateProjectDTO projectDTO
    ) {
        try {
            Project createdProject = projectService.createProject(projectDTO);
            return ResponseEntity.created(URI.create(ENDPOINT + "/" + createdProject.getId())).body(createdProject);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (ConflictedExternalIdException e2){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


    //API to update a project ---> answer for assignment #2
    @ApiOperation("Update a Project")
    @PatchMapping(ENDPOINT_ID)
    public ResponseEntity<Project> updateProject(
            @ApiParam(name = API_PARAM_ID, required = true)
            @PathVariable(PATH_VARIABLE_ID) final long projectId,
            @ApiParam(name = API_PARAM_PROJECT, required = true)
            @RequestBody @Valid final UpdateProjectDTO projectDTO
    ) {
        try {
            Project updatedProject = projectService.updateProject(projectId, projectDTO);
            return ResponseEntity.ok(updatedProject);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ConflictedExternalIdException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


}
