package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

@Data
public class LianHeMaiTongOrderDTO {
    @AutoDocProperty("订单号")
    private String orderNo;
    private String renterOrderNo;
    private String ownerOrderNo;
    @AutoDocProperty("订单类型")
    private String category;
    @AutoDocProperty("订单状态")
    private Integer orderStatus;
    @AutoDocProperty("订单开始时间")
    private String expRentTime;
    @AutoDocProperty("订单结束时间")
    private String expRevertTime;
    @AutoDocProperty("预计提前取车时间")
    private String showRentTime;
    @AutoDocProperty("预计延后还车时间")
    private String showRevertTime;
    @AutoDocProperty("交易请求时间")
    private String reqTime;
    @AutoDocProperty("车牌号")
    private String carPlateNum;
    @AutoDocProperty("车型")
    private String carTypeTxt;
    @AutoDocProperty("下单城市")
    private String rentCity;
    @AutoDocProperty("租客租金")
    private Integer rentAmt;
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
    @AutoDocProperty("是否发送任云，是否使用取还车")
    private Integer isNotifyRenyun;
    @AutoDocProperty("配送订单类型")
    private Integer type;
}
