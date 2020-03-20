package com.atzuche.order.commons.vo.res;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 胡春林
 * 违章列表数据字段
 */
@Data
@ToString
public class ViolationResVO {

    @AutoDocProperty(value = "id")
    private String id;
    @AutoDocProperty(value = "订单类型")
    private String orderType;
    @AutoDocProperty(value = "订单号")
    private String orderNo;
    @AutoDocProperty(value = "取车时间")
    private String rentTime;
    @AutoDocProperty(value = "还车时间")
    private String revertTime;
    @AutoDocProperty(value = "实际还车时间")
    private String actualRevertTime;
    @AutoDocProperty(value = "车牌号")
    private String plateNum;
    @AutoDocProperty(value = "车架号")
    private String frameNo;
    @AutoDocProperty(value = "发动机号")
    private String engineNo;
    @AutoDocProperty(value = "车辆类型")
    private String carType;
    @AutoDocProperty(value = "用车城市")
    private String rentCity;
    @AutoDocProperty(value = "违章押金状态")
    private String wzDepositStatus;
    @AutoDocProperty(value = "违章处理状态")
    private String wzStatus;
    @AutoDocProperty(value = "违章处理状态变更时间")
    private String wzStatusChangeTime;
    @AutoDocProperty(value = "违章缴纳凭证")
    private String wzProcessedProof;
    @AutoDocProperty(value = "违章信息")
    private String wzInfo;
    @AutoDocProperty(value = "进入违章管理时间")
    private String enterWzManageTime;
    @AutoDocProperty(value = "途径城市")
    private String onlineCity;
}
