package com.piche.task.exception;

public class UnknownAccountIdException extends RuntimeException {

    public UnknownAccountIdException(long id) {
        super(String.format("Unknown account with id %d", id));
    }
}
