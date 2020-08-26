package com.atzuche.order.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author pengcheng.fu
 * @date 2020/8/10 17:29
 */
@Data
public class CarRentTimeRangeReqDTO {

    @AutoDocProperty(value = "车辆编号(必填)")
    private String carNo;

    @AutoDocProperty(value = "城市编码")
    private String cityCode;

    @AutoDocProperty(value = "订单取车时间(yyyy-MM-dd HH:mm:ss)")
    private LocalDateTime rentTime;

    @AutoDocProperty(value = "订单还车时间(yyyy-MM-dd HH:mm:ss)")
    private LocalDateTime revertTime;

    @AutoDocProperty(value = "是否使用取车服务:0.否 1.是")
    private Integer srvGetFlag;

    @AutoDocProperty(value = "取车服务-取车地址")
    private String srvGetAddr;

    @AutoDocProperty(value = "取车服务-取车地址-地址经度")
    private String srvGetLon;

    @AutoDocProperty(value = "取车服务-取车地址-地址维度")
    private String srvGetLat;

    @AutoDocProperty(value = "是否使用还车服务:0.否 1.是")
    private Integer srvReturnFlag;

    @AutoDocProperty(value = "还车服务-还车地址")
    private String srvReturnAddr;

    @AutoDocProperty(value = "还车服务-还车地址-地址经度")
    private String srvReturnLon;

    @AutoDocProperty(value = "还车服务-还车地址-地址维度")
    private String srvReturnLat;
}
