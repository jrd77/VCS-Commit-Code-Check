package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author 胡春林
 * 违章查询条件
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class ViolationReqVO extends BaseReqVO {

    @AutoDocProperty(value = "主订单号")
    private String orderNo;
    @AutoDocProperty(value = "用车城市")
    private String useCarCity;
    @AutoDocProperty(value = "车辆类型")
    private String carType;
    @AutoDocProperty(value = "车牌号")
    private String carNo;
    @AutoDocProperty(value = " 最小进入违章管理日期")
    private String mixViolationManagerDate;
    @AutoDocProperty(value = "最大进入违章管理日期")
    private String maxViolationManagerDate;
    @AutoDocProperty(value = "违章信息 0：无违章  1：有违章")
    private String violationInfo;
    @AutoDocProperty(value = "违章处理状态")
    private String violationStatus;
    @AutoDocProperty(value = "违章押金状态")
    private String violationCostStatus;
    @AutoDocProperty(value = "违章缴纳凭证 0：无  1：有")
    private String violationPayStatus;
    @AutoDocProperty(value = "取车时间")
    private String getCarDate;
    @AutoDocProperty(value = "还车时间")
    private String returnCarDate;
    @AutoDocProperty(value = "最小实际还车时间")
    private String minExpReturnCarDate;
    @AutoDocProperty(value = "最大实际还车时间")
    private String maxExpReturnCarDate;

}
