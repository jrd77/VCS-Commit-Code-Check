package com.atzuche.order.admin.vo.req.orderSubmit;

import com.atzuche.order.commons.entity.request.ItemList;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/5 5:56 下午
 **/
@Data
public class AdminTransReqVO extends AdminReqBaseVO {

    @AutoDocProperty(value = "是否使用特供价", required = true)
    private String useSpecialPrice="0";

    @AutoDocProperty(value = "用户注册号")
    private String memNo;

//    @AutoDocProperty(value = "管理后台下单特殊处理标识", required = true)
//    private String specialConsole="0";

//    @AutoDocProperty(value = "线下订单类型", required = true)
//    private String offlineOrderStatus;

 /*   @AutoDocProperty(value = "限时红包面额(管理后台)")
    private Integer reductiAmt;*/

    @AutoDocProperty(value = "车辆注册号")
    @NotBlank(message = "车辆注册号不能为空")
    private String carNo;

    @AutoDocProperty(value = "cityCode(310100)")
    @NotBlank(message ="cityCode不能为空")
    private String cityCode;

//    @AutoDocProperty(value = "场景编码(例S009)")
//    private String sceneCode="EX007";

    @AutoDocProperty(value = "起租时间 格式(20171205201500)")
    @NotBlank(message ="rentTime不能为空")
    private String rentTime;

    @AutoDocProperty(value = "还车时间 格式(20171207201500)")
    @NotBlank(message = "revertTime订单还车时间不能为空")
    private String revertTime;

    @AutoDocProperty(value = "取车服务开关(0/1)")
    @NotNull(message = "是否使用取车服务标识不能为空")
    private String srvGetFlag;

    @AutoDocProperty(value = "还车服务开关(0/1)")
    @NotNull(message = "是否使用还车服务标识不能为空")
    private String srvReturnFlag;

    @AutoDocProperty(value = "取车地址")
    //@NotBlank(message = "取车地址不能为空")
    private String srvGetAddr;

    @AutoDocProperty(value = "取车纬度")
    private String srvGetLat;

    @AutoDocProperty(value = "取车经度")
    //@NotBlank(message = "取车地址经度不能为空")
    private String srvGetLon;

    @AutoDocProperty(value = "还车地址")
    //@NotBlank(message = "还车地址不能为空")
    private String srvReturnAddr;

    @AutoDocProperty(value = "还车纬度")
   // @NotBlank(message = "还车地址维度不能为空")
    private String srvReturnLat;

    @AutoDocProperty(value = "还车经度")
    //@NotBlank(message = "还车地址经度不能为空")
    private String srvReturnLon;

//    @AutoDocProperty(value = "车主电话")
//    private String conPhone;

    @AutoDocProperty(value = "优惠券id")
    private String disCouponIds;

    @AutoDocProperty(value = "【5.12新增】送取服务券id")
    private String getCarFreeCouponId;

    @AutoDocProperty(value = "超级补充全险开关")
    private String abatement;

    @AutoDocProperty(value = "是否出市(0/1)")
    @NotBlank(message="是否出市开关不能为空")
    private String isLeaveCity;

    @AutoDocProperty(value = "限时立减状态(0/1)")
    private String limitRedStatus;

    @AutoDocProperty(value = "限时立减id")
    private String limitReductionId;

    @AutoDocProperty(value = "油费计算方式")
    private String oilType;

    @AutoDocProperty(value = "用车城市 例(上海)")
    @NotBlank(message = "rentCity不能为空")
    private String rentCity;

//    @AutoDocProperty(value = "交易来源 1：手机，2：网站 ，3：管理后台, 14:m站")
//    private String source="3";


    @AutoDocProperty(value = "是否使用余额(0/1)")
    private String useBal;


    @AutoDocProperty(value = "是否使用凹凸币")
    private String useAutoCoin;

    @AutoDocProperty(value = "是否使用机场服务")
    private String useAirportService;

    @AutoDocProperty(value = "航班号")
    private String flightNo;

    @AutoDocProperty(value="【4.8新增】常用驾驶人ID,非必填,多个以逗号分隔")
    private String driverIds;

    private List<ItemList> itemList;

    @AutoDocProperty(value = "虚拟地址序列号,默认为0")
    private String carAddrIndex;
    @AutoDocProperty(value = "免押方式ID:1.绑卡免押 2.芝麻免押 3.支付押金")
    private String freeDoubleTypeId;
    @AutoDocProperty(value = "车主优惠券编码")
    private String carOwnerCouponNo;



}
