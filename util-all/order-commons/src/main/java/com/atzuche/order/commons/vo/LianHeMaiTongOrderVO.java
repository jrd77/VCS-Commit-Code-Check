package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class LianHeMaiTongOrderVO {
    @AutoDocProperty("订单号")
    private String orderNo;
    @AutoDocProperty("订单类型 1:普通,2:套餐,3:长租")
    private String category;
    @AutoDocProperty("订单状态 1,待确认 4,待支付 8,待调度  16,待取车 32,待还车 64,待结算 128,待违章结算 256,待理赔处理 512,完成 0结束")
    private Integer orderStatus;
    @AutoDocProperty("订单开始时间")
    private String expRentTime;
    @AutoDocProperty("订单结束时间")
    private String expRevertTime;
    @AutoDocProperty("是否取车 true：是， false：否")
    private Boolean isGetCar;
    @AutoDocProperty("是否还车 true：是， false：否")
    private Boolean isReturnCar;
    @AutoDocProperty("预计提前取车时间")
    private String showRentTime;
    @AutoDocProperty("预计延后还车时间")
    private String showRevertTime;
    @AutoDocProperty("交易请求时间")
    private String reqTime;
    @AutoDocProperty("一审审核结果 -1待审核，1 审核通过 0 审核不通过 2 免审")
    private Integer firstAuditStatus;
    @AutoDocProperty("二审审核状态 -1待审核，1 审核通过 0 审核不通过 2 免审")
    private Integer secondAuditStatus;
    @AutoDocProperty("gps审核结果 -1待审核 ,1 正常 0 不正常 2 不正常-未维修")
    private Integer gpsAuditStatus;
    @AutoDocProperty("车牌号")
    private String carPlateNum;
    @AutoDocProperty("车型")
    private String carTypeTxt;
    @AutoDocProperty("取车城市")
    private String rentCity;
    @AutoDocProperty("租客租金")
    private String rentAmt;
    @AutoDocProperty("车主姓名")
    private String ownerName;
    @AutoDocProperty("车主会员号")
    private String ownerMemNo;
    @AutoDocProperty("车主手机号")
    private String ownerPhone;
    @AutoDocProperty("租客姓名")
    private String renterName;
    @AutoDocProperty("租客会员号")
    private String renterMemNo;
    @AutoDocProperty("租客手机号")
    private String renterPhone;
}
