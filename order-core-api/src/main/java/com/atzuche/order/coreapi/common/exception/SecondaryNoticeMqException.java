package com.atzuche.order.coreapi.common.exception;


import com.atzuche.order.commons.enums.ErrorCode;

/**
 * @author pengcheng.fu
 * @date 2020/7/1014:20
 */
public class SecondaryNoticeMqException extends RuntimeException {

    private static final long serialVersionUID = 8154863978272510072L;

    private String errorCode;

    private String errorMsg;

    private Object extra;

    public SecondaryNoticeMqException(String errorCode, String errorMsg, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.extra = null;
    }

    public SecondaryNoticeMqException(String errorCode, String errorMsg, Object extra, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.extra = extra;
    }

    public SecondaryNoticeMqException(String errorCode, String errorMsg, Object extra) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.extra = extra;
    }

    public SecondaryNoticeMqException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public SecondaryNoticeMqException(ErrorCode errorCode) {
        super(errorCode.getText());
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorCode.getText();
    }
}
