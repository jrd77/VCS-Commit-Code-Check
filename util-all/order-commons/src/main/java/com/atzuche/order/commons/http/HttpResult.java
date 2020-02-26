package com.atzuche.order.commons.http;


/**
 * Created by wangcheng on 2014/7/29.
 */
public class HttpResult {

    private String resCode;
    private String resMsg;
    private Object data;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "resCode='" + resCode + '\'' +
                ", resMsg='" + resMsg + '\'' +
                ", data=" + data +
                '}';
    }
}
