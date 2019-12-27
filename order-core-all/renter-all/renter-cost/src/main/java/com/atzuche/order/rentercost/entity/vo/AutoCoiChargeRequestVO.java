package com.atzuche.order.rentercost.entity.vo;

import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author haibao.yan
 */
public class AutoCoiChargeRequestVO implements Serializable {

    private static final long serialVersionUID = -7217679412392921039L;

    @AutoDocProperty("会员注册号")
    @NotNull
    private Integer memNo;

    @AutoDocProperty("订单号")
    @NotNull
    private String orderNo;

    @AutoDocProperty("扣减/充值凹凸币")
    private Integer chargeAutoCoin;

    @AutoDocProperty("备注(增加/代扣原因)")
    @NotEmpty(message = "请填写增加/代扣原因")
    private String remark;

    @AutoDocProperty("类型,约定1-50为表示订单号，51-100表示车辆注册号,如有其他后面添加。1为车主结算,2租车消费,3车主订单分享,4:租客评价，5：车主评价,6:会员等级权益赠送凹凸币，7:管理后台操作用户凹凸币，10:结算返还,11:取消订单返回，12：恢复订单抵扣，13：邀请有礼会员注册送凹凸币，14：，15：修改订单车主拒绝退回补扣的凹凸币，16：修改订单补扣凹凸币，17：修改订单车主同意退回多余的凹凸币，51车辆检测")
    private Integer orderType;

    @AutoDocProperty("备注信息(增加/代扣凹凸币原因选择'其他'时,此地段为必填项)")
    private String remarkExtend;

    @AutoDocProperty("操作人")
    private String operator;

    public AutoCoiChargeRequestVO(Integer renterNo, Integer amt, String orderNo, String flagTxt,Integer orderType){
        this.memNo = renterNo;
        this.chargeAutoCoin = amt;
        this.orderNo = orderNo;
        this.remark=flagTxt;
        this.remarkExtend=flagTxt;
        this.orderType=orderType;
    }
    private AutoCoiChargeRequestVO(){

    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }


    public Integer getMemNo() {
        return memNo;
    }

    public void setMemNo(Integer memNo) {
        this.memNo = memNo;
    }

    public Integer getChargeAutoCoin() {
        return chargeAutoCoin;
    }

    public void setChargeAutoCoin(Integer chargeAutoCoin) {
        this.chargeAutoCoin = chargeAutoCoin;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarkExtend() {
        return remarkExtend;
    }

    public void setRemarkExtend(String remarkExtend) {
        this.remarkExtend = remarkExtend;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
