package com.xs.admin.common;

public class DefineException extends RuntimeException {
    private final int code;

    public DefineException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
