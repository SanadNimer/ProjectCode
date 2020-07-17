package com.company.resourceapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CreateProjectDTO {

    @NotEmpty
    @Valid
    private String externalId;

    private String name;

    @NotNull
    @Valid
    private SdlcSystemDTO sdlcSystem;
}
