package com.company.resourceapi.exceptions;

import lombok.Value;

@Value
public class NotFoundException extends Throwable {

    private Class<?> aClass;

    private long id;
}
