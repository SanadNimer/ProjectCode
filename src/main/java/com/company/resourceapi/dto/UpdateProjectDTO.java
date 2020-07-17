package com.company.resourceapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.Optional;

@Data
@NoArgsConstructor
public class UpdateProjectDTO {

    private String externalId;

    private Optional<String> name;

    @Valid
    private SdlcSystemDTO sdlcSystem;

}
