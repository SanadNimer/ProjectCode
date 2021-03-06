package com.company.resourceapi.repositories;

import com.company.resourceapi.entities.Project;
import java.util.Optional;

import com.company.resourceapi.entities.SdlcSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{

    Optional<Project> findBySdlcSystemIdAndId(long sdlcSystemId, long projectId);

    Optional<Project> findByExternalIdAndSdlcSystem (String externalId, SdlcSystem sdlcSystem);
}
