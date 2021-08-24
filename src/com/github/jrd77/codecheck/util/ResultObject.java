package com.github.jrd77.codecheck.util;

public class ResultObject<T> {

    private int ok;

    private String msg;

    private T data;

    private static final int RESULT_OK=0;
    private static final int RESULT_ERR=500;
    public static <T> ResultObject<T> ok(){

        return new ResultObject<>(RESULT_OK,null,null);
    }

    public static <T> ResultObject<T> ok(T t) {

        return new ResultObject<>(RESULT_OK, null, t);
    }

    public static <T> ResultObject<T> err(String msg) {

        return new ResultObject<>(RESULT_ERR, msg, null);
    }

    public static <T> ResultObject<T> err(int code, String msg) {

        return new ResultObject<>(code, msg, null);
    }

    public ResultObject(int ok, String msg, T data) {
        this.ok = ok;
        this.msg = msg;
        this.data = data;
    }

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public static class ResultConstant {

        public static int SHOULD_NOTIFICATION = 101;
        public static int SHOULD_DIALOG = 102;
        public static int WARNING_ICON = 102;
        public static int ERROR_ICON = 102;
    }
}
