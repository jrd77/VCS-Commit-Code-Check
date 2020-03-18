package com.atzuche.violation.vo.resp;

import com.atzuche.violation.common.annotation.FeildDescription;
import com.autoyol.doc.annotation.AutoDocProperty;
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
    @FeildDescription(value = "实际还车时间")
    private String actualRevertTime;
    @FeildDescription(value = "车牌号")
    private String plateNum;
    @FeildDescription(value = "车架号")
    private String frameNo;
    @FeildDescription(value = "发动机号")
    private String engineNo;
    @FeildDescription(value = "车辆类型")
    private String carType;
    @FeildDescription(value = "用车城市")
    private String rentCity;
    @FeildDescription(value = "违章押金状态")
    private String wzDepositStatus;
    @FeildDescription(value = "违章处理状态")
    private String wzStatus;
    @FeildDescription(value = "违章处理状态变更时间")
    private String wzStatusChangeTime;
    @FeildDescription(value = "违章缴纳凭证")
    private String wzProcessedProof;
    @FeildDescription(value = "违章信息")
    private String wzInfo;
    @FeildDescription(value = "进入违章管理时间")
    private String enterWzManageTime;
    @FeildDescription(value = "途径城市")
    private String onlineCity;
}
