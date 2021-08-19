package com.github.jrd77.codecheck.util;


import java.util.function.Supplier;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/7/20 20:26
 */
public class Assert {

    private Assert() {
    }

    public static <X extends Throwable> void isTrue(boolean expression, Supplier<? extends X> supplier) throws X {
        if (!expression) {
            throw (X) supplier.get();
        }
    }


    public static <X extends Throwable> void isNull(Object object, Supplier<X> errorSupplier) throws X {
        if (null != object) {
            throw (X) errorSupplier.get();
        }
    }

    public static void isNull(Object object, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        isNull(object, () -> {
            return new IllegalArgumentException(String.format(errorMsgTemplate, params));
        });
    }

    public static void isNull(Object object) throws IllegalArgumentException {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    public static <T, X extends Throwable> T notNull(T object, Supplier<X> errorSupplier) throws X {
        if (null == object) {
            throw (X) errorSupplier.get();
        } else {
            return object;
        }
    }

    public static <T> T notNull(T object, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        return notNull(object, () -> {
            return new IllegalArgumentException(String.format(errorMsgTemplate, params));
        });
    }

    public static <T> T notNull(T object) throws IllegalArgumentException {
        return notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }
}
