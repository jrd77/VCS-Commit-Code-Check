package com.atzuche.order.admin.vo.req.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/11 1:27 下午
 **/
@Data
@ToString
public class PreOrderAdminRequestVO {
    @AutoDocProperty(value = "下单城市编码")
    @NotBlank(message = "cityCode cannot be null")
    private String cityCode;
    @AutoDocProperty(value = "下单城市名称")
    @NotBlank(message = "rentCity cannot be null")
    private String rentCity;
    @AutoDocProperty(value = "会员手机号（会员手机号、会员号必须二者有一个）")
    private String mobile;
    @AutoDocProperty(value = "会员号")
    private String memNo;
    @AutoDocProperty(value = "起租时间,格式yyyyMMddHHmmss")
    @NotBlank(message = "rentTime cannot be null")
    private String rentTime;
    @AutoDocProperty(value = "结束时间,格式yyyyMMddHHmmss")
    @NotBlank(message = "reverTime cannot be null")
    private String revertTime;
    @AutoDocProperty(value = "车辆编号")
    @NotBlank(message = "carNo cannot be null")
    private String carNo;

    @AutoDocProperty(value = "找车地址")
    private String getCarAddr;
    @AutoDocProperty(value = "找车地址的纬度")
    private String getCarLat;
    @AutoDocProperty(value = "找车地址的经度")
    private String getCarLon;

    @AutoDocProperty(value = "是否使用取车服务:0.否 1.是", required = true)
    @NotNull(message = "是否使用取车服务标识不能为空")
    private Integer srvGetFlag;

    @AutoDocProperty(value = "取车服务-取车地址", required = true)
    private String srvGetAddr;

    @AutoDocProperty(value = "取车服务-取车地址-地址经度", required = true)
    private String srvGetLon;

    @AutoDocProperty(value = "取车服务-取车地址-地址维度", required = true)
    private String srvGetLat;

    @AutoDocProperty(value = "是否使用还车服务:0.否 1.是", required = true)
    @NotNull(message = "是否使用还车服务标识不能为空")
    private Integer srvReturnFlag;

    @AutoDocProperty(value = "还车服务-还车地址", required = true)
    private String srvReturnAddr;

    @AutoDocProperty(value = "还车服务-还车地址-地址经度", required = true)
    private String srvReturnLon;

    @AutoDocProperty(value = "还车服务-还车地址-地址维度", required = true)
    private String srvReturnLat;

    @AutoDocProperty(value = "是否使用凹凸币:0.否 1.是")
    private Integer useAutoCoin;

    @AutoDocProperty(value = "是否使用钱包余额:0.否 1.是")
    private Integer useBal;

    @AutoDocProperty(value = "取送服务优惠券ID")
    private String getCarFreeCouponId;

    @AutoDocProperty(value = "平台优惠券ID")
    private String disCouponIds;

    @AutoDocProperty(value = "车主优惠券编码")
    private String carOwnerCouponNo;


    @AutoDocProperty(value = "是否购买补充保障", required = true)
    @NotBlank(message = "是否购买补充保障不能为空")
    private String abatement;
}
