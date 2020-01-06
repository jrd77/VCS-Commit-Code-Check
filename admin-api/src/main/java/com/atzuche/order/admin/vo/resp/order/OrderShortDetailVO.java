package com.atzuche.order.admin.vo.resp.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/6 7:08 下午
 **/
@Data
public class OrderShortDetailVO {

    @AutoDocProperty(value="订单号")
    private String orderNo;

    @AutoDocProperty(value="订单类型")
    private String orderType;

    @AutoDocProperty(value="车主名称")
    private String carOwnerName;

    @AutoDocProperty(value="车主电话")
    private String carOwnerPhone;

    @AutoDocProperty(value="租客下单城市")
    private String cityName;

    @AutoDocProperty(value="起租时间")
    private String rentTime;

    @AutoDocProperty(value="还车时间")
    private String revertTime;

    @AutoDocProperty(value="还车地址")
    private String getCarAddr;

    @AutoDocProperty(value="日均价")
    private Integer dayPrice;

    @AutoDocProperty(value="品牌车型")
    private String carBrandTypeTxt;

    @AutoDocProperty(value="用车类型，SUV等")
    private String useTypeTxt;

    @AutoDocProperty(value="订单总租金")
    private Integer rentAmt;

    @AutoDocProperty(value="订单总费用")
    private Integer insuranceAmt;

    @AutoDocProperty(value = "车辆状态")
    private String carStatusTxt;

    @AutoDocProperty(value="驱动类型名称")
    private String driveTypeTxt;

    @AutoDocProperty(value="变速箱类型名称")
    private String gbTypeTxt;

    @AutoDocProperty(value="日限地址")
    private Integer dayMileage;

    @AutoDocProperty(value="车龄")
    private Integer carAge;

    @AutoDocProperty(value="是否本地")
    private String isLocalTxt;

    @AutoDocProperty(value = "调度失败原因")
    private String failureReason;

    @AutoDocProperty(value="最近10单的接单率")
    private String recentSuccessRate;





}
