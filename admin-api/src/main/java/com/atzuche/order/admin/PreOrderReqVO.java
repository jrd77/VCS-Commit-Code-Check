package com.atzuche.order.admin;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/13 4:57 下午
 **/
@Data
@ToString
public class PreOrderReqVO implements Serializable {

    @AutoDocProperty(value = "用户注册号")
    @NotBlank(message = "会员号不能为空")
    private String memNo;

    @AutoDocProperty(value = "城市编码", required = true)
    @NotBlank(message = "城市编码不能为空")
    private String cityCode;


    @AutoDocProperty(value = "订单取车时间(yyyy-MM-dd HH:mm:ss)", required = true)
    @NotBlank(message = "订单取车时间不能为空")
    private String rentTime;

    @AutoDocProperty(value = "订单还车时间(yyyy-MM-dd HH:mm:ss)", required = true)
    @NotBlank(message = "订单还车时间不能为空")
    private String revertTime;

    @AutoDocProperty(value = "是否使用取车服务:0.否 1.是", required = true)
    @NotNull(message = "是否使用取车服务标识不能为空")
    private Integer srvGetFlag;

    @AutoDocProperty(value = "取车服务-取车地址")
    private String srvGetAddr;

    @AutoDocProperty(value = "取车服务-取车地址-地址经度")
    private String srvGetLon;

    @AutoDocProperty(value = "取车服务-取车地址-地址维度")
    private String srvGetLat;

    @AutoDocProperty(value = "是否使用还车服务:0.否 1.是", required = true)
    @NotNull(message = "是否使用还车服务标识不能为空")
    private Integer srvReturnFlag;

    @AutoDocProperty(value = "还车服务-还车地址")
    private String srvReturnAddr;

    @AutoDocProperty(value = "还车服务-还车地址-地址经度")
    private String srvReturnLon;

    @AutoDocProperty(value = "还车服务-还车地址-地址维度")
    private String srvReturnLat;

    @AutoDocProperty(value = "车辆注册号", required = true)
    @NotBlank(message = "车辆注册号不能为空")
    private String carNo;

    @AutoDocProperty(value = "是否购买补充保障", required = true)
    @NotBlank(message = "是否购买补充保障不能为空")
    private String abatement;

    @AutoDocProperty(value = "取送服务优惠券ID")
    private String getCarFreeCouponId;

    @AutoDocProperty(value = "平台优惠券ID")
    private String disCouponId;

    @AutoDocProperty(value = "车主优惠券编码")
    private String carOwnerCouponNo;

    @AutoDocProperty(value = "是否使用凹凸币:0.否 1.是")
    private Integer useAutoCoin;

    @AutoDocProperty(value = "是否使用钱包余额:0.否 1.是")
    private Integer useBal;

    @AutoDocProperty(value = "附加驾驶人ID,多个以逗号分隔")
    private String driverIds;

    @AutoDocProperty(value = "虚拟地址序列号,默认为0")
    private String carAddrIndex;

}
