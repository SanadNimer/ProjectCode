package com.company.resourceapi.exceptions;

import lombok.Value;

@Value
public class ConflictedExternalIdException extends Throwable {

    private String externalId;

    private Long sdlcSystemId;
}
