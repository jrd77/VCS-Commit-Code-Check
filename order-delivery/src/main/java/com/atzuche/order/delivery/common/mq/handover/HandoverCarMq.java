package com.atzuche.order.delivery.common.mq.handover;

/**
 * @author 胡春林
 * 交接车数据
 */
public class HandoverCarMq extends RabbitmqBaseData {

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 用户类型1:租客,2:车主
     */
    private String userType;
    /**
     * 取还车描述
     */
    private String description;

    /**
     * 服务类型（take:取车，back:还车）
     */
    private String serviceType;

    /**
     * 车管家姓名
     */
    private String headName;

    /**
     * 车管家电话
     */
    private String headPhone;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getHeadName() {
        return headName;
    }

    public void setHeadName(String headName) {
        this.headName = headName;
    }

    public String getHeadPhone() {
        return headPhone;
    }

    public void setHeadPhone(String headPhone) {
        this.headPhone = headPhone;
    }
}
