package com.company.resourceapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class SdlcSystemDTO {

    @NotNull
    private Long id;
}
