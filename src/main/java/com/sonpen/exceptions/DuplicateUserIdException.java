package com.sonpen.exceptions;

/**
 * Created by 1109806 on 2019-05-16.
 */
public class DuplicateUserIdException extends RuntimeException{
    public DuplicateUserIdException(Throwable cause) {
        super(cause);
    }
}
