package com.atzuche.order.photo.mq;

/**
 * Created by andy on 16/6/17.
 */
public class AliyunMessageException extends Exception{
    public AliyunMessageException(String message) {
        super(message);
    }

    public AliyunMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public AliyunMessageException(Throwable cause) {
        super(cause);
    }

    public AliyunMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
