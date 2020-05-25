package com.atzuche.order.admin.vo.req.supplement;

/**
 * @author ：weixu.chen
 * @date ：Created in 2019/7/17 16:12
 */
public class MessagePushParamVO {

    /**
     * 手机号（如果推送平台是短信需要手机号）
     */
    private Long mobile;

    /**
     * 图片（如果消息类型为订单消息需要汽车图片，推送消息需要展示）
     */
    private String picture;

    public Long getMobile() {
        return mobile;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "MessagePushParamVO{" +
                "mobile=" + mobile +
                ", picture='" + picture + '\'' +
                '}';
    }
}
