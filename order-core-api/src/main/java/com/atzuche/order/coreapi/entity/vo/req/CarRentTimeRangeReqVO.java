package com.atzuche.order.coreapi.entity.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 提前延后时间计算请求参数
 *
 * @author pengcheng.fu
 * @date 2019/12/31 17:11
 */

@Data
public class CarRentTimeRangeReqVO {

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
