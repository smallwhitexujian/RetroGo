package com.future.retronet;


public class RetroException extends RuntimeException {

    private int code;

    private RetroException() {
    }

    public RetroException(String detailMessage) {
        super(detailMessage);
    }

    public RetroException(int code, String detailMessage) {
        super(detailMessage);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
