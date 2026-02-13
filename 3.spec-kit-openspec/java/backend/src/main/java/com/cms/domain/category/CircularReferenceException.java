package com.cms.domain.category;

public class CircularReferenceException extends RuntimeException {
    public CircularReferenceException(String message) {
        super(message);
    }
}
