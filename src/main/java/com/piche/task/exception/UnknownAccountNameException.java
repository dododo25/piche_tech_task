package com.piche.task.exception;

public class UnknownAccountNameException extends RuntimeException {

    public UnknownAccountNameException(String name) {
        super(String.format("Unknown account with name '%s'", name));
    }
}
