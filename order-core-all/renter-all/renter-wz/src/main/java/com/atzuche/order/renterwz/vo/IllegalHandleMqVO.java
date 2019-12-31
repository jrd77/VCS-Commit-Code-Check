package com.atzuche.order.renterwz.vo;

/**
 * IllegalHandleMqVO
 *
 * @author shisong
 * @date 2019/12/30
 */
public class IllegalHandleMqVO {

    /**
     * 违章编码 由于需要和任云传入的字段对应上
     */
    private String 	wzcode;
    /**
     * 订单号 由于需要和任云传入的字段对应上
     */
    private String 	orderno	;
    /**
     * 违章处理方 由于需要和任云传入的字段对应上
     */
    private String 	wzclparty;

    public String getWzcode() {
        return wzcode;
    }

    public void setWzcode(String wzcode) {
        this.wzcode = wzcode;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getWzclparty() {
        return wzclparty;
    }

    public void setWzclparty(String wzclparty) {
        this.wzclparty = wzclparty;
    }

    @Override
    public String toString() {
        return "IllegalHandleMqVO{" +
                "wzcode='" + wzcode + '\'' +
                ", orderno='" + orderno + '\'' +
                ", wzclparty='" + wzclparty + '\'' +
                '}';
    }


}
