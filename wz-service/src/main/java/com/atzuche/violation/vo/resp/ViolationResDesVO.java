package com.atzuche.violation.vo.resp;

import com.atzuche.violation.common.annotation.FeildDescription;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 */
@Data
@ToString
public class ViolationResDesVO {
    @FeildDescription(value = "id")
    private String id;
    @FeildDescription(value = "订单类型")
    private String orderType;
    @FeildDescription(value = "订单号")
    private String orderNo;
    @FeildDescription(value = "取车时间")
    private String rentTime;
    @FeildDescription(value = "还车时间")
    private String revertTime;
    @FeildDescription(value = "车牌号")
    private String plateNum;
    @FeildDescription(value = "车架号")
    private String frameNo;
    @FeildDescription(value = "发动机号")
    private String engineNo;
    @FeildDescription(value = "用车城市")
    private String rentCity;
    @FeildDescription(value = "违章时间")
    private String illegalTime;
    @FeildDescription(value = "违章地点")
    private String illegalAddr;
    @FeildDescription(value = "违章原因")
    private String illegalReason;
    @FeildDescription(value = "违章罚金")
    private String illegalAmt;
    @FeildDescription(value = "扣分")
    private String illegalDeduct;
    @FeildDescription(value = "协助违章处理费")
    private String illegalFine;
    @FeildDescription(value = "凹凸代办服务费")
    private String illegalServiceCost;
    @FeildDescription(value = "不良用车处罚金")
    private String illegalDysFine;
    @FeildDescription(value = "途径城市")
    private String cities;
    @FeildDescription(value = "暂扣租车押金")
    private String illegalPauseCost;
    @FeildDescription(value = "车辆类型")
    private String carType;
    @FeildDescription(value = "违章超证费")
    private String illegalSupercerCost;
    @FeildDescription(value = "动力源")
    private String powerType;
}
