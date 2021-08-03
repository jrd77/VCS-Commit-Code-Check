package com.github.jrd77.codecheck.util;


/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/7/20 20:42
 */
public class IORuntimeException extends RuntimeException {

    private static final long serialVersionUID = 8247610319171014183L;

    public IORuntimeException(Throwable e) {
        super(null == e ? "null" : String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()), e);
    }

    public IORuntimeException(String message) {
        super(message);
    }

    public IORuntimeException(String messageTemplate, Object... params) {
        super(String.format(messageTemplate, params));
    }

    public IORuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public IORuntimeException(Throwable throwable, String messageTemplate, Object... params) {
        super(String.format(messageTemplate, params), throwable);
    }

    public boolean causeInstanceOf(Class<? extends Throwable> clazz) {
        Throwable cause = this.getCause();
        return null != clazz && clazz.isInstance(cause);
    }
}
