package com.atzuche.violation.exception;



public class ViolationManageException extends OrderException {

    private String errorCode;
    private String message;

    public ViolationManageException(String errorCode, String message) {
        super(errorCode, message);
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
