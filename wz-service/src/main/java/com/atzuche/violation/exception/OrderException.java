package com.atzuche.violation.exception;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/14 4:13 下午
 **/
public abstract class OrderException extends RuntimeException {

    private String errorCode;

    private String errorMsg;

    private Object extra;

    public OrderException(String errorCode, String errorMsg, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.extra = null;
    }

    public OrderException(String errorCode, String errorMsg, Object extra, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.extra = extra;
    }

    public OrderException(String errorCode, String errorMsg, Object extra) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.extra = extra;
    }

    public OrderException(String errorCode, String errorMsg){
        super(errorMsg);
        this.errorCode =errorCode;
        this.errorMsg =errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
