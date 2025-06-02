package com.part3.deokhugam.exception;

public class BookException extends BusinessException {
    public BookException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BookException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
