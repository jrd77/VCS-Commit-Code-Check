package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * 普通订单提交订单前费用计算相关请求参数
 *
 * @author pengcheng.fu
 * @date 2020/1/11 14:15
 */

public class NormalOrderCostCalculateReqVO extends BaseVO {

    private static final long serialVersionUID = 4077530429676649793L;

    @AutoDocProperty(value = "订单类型", required = true)
    @NotBlank(message = "订单类型不能为空,1.普通订单 2.套餐订单")
    private String orderCategory;

    @AutoDocProperty(value = "业务来源主类型,1:OTA,2代步车，3:礼品卡,4:安联,5:自有 ")
    private String businessParentType;

    @AutoDocProperty(value = "业务来源子类型 1:OTA-携程,2:OTA-同城,3:OTA-飞猪,4:OTA-租租车,5:代步车-出险代步车,6:代步车-2*2代步车,7:代步车-券码下单,8:代步车-特供车  ")
    private String businessChildType;

    @AutoDocProperty(value = "平台来源主类型 1:APP,2:小程序，3:微信,4:支付宝,5:PC页面,6:H5页面,7:管理后台,8:API ")
    private String platformParentType;

    @AutoDocProperty(value = "平台来源子类型 1:APP-IOS,2:APP-Android,3:小程序-支付宝,4:小程序-微信,5:小程序-百度")
    private String platformChildType;

    @AutoDocProperty(value = "城市编码", required = true)
    @NotBlank(message = "城市编码不能为空")
    private String cityCode;

    @AutoDocProperty(value = "订单场景编码", required = true)
    @NotBlank(message = "订单场景编码不能为空")
    private String sceneCode;

    @AutoDocProperty(value = "订单来源")
    @NotBlank(message = "订单来源不能为空")
    private String source;

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


    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getBusinessParentType() {
        return businessParentType;
    }

    public void setBusinessParentType(String businessParentType) {
        this.businessParentType = businessParentType;
    }

    public String getBusinessChildType() {
        return businessChildType;
    }

    public void setBusinessChildType(String businessChildType) {
        this.businessChildType = businessChildType;
    }

    public String getPlatformParentType() {
        return platformParentType;
    }

    public void setPlatformParentType(String platformParentType) {
        this.platformParentType = platformParentType;
    }

    public String getPlatformChildType() {
        return platformChildType;
    }

    public void setPlatformChildType(String platformChildType) {
        this.platformChildType = platformChildType;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getSceneCode() {
        return sceneCode;
    }

    public void setSceneCode(String sceneCode) {
        this.sceneCode = sceneCode;
    }

    public String getRentTime() {
        return rentTime;
    }

    public void setRentTime(String rentTime) {
        this.rentTime = rentTime;
    }

    public String getRevertTime() {
        return revertTime;
    }

    public void setRevertTime(String revertTime) {
        this.revertTime = revertTime;
    }

    public Integer getSrvGetFlag() {
        return srvGetFlag;
    }

    public void setSrvGetFlag(Integer srvGetFlag) {
        this.srvGetFlag = srvGetFlag;
    }

    public String getSrvGetAddr() {
        return srvGetAddr;
    }

    public void setSrvGetAddr(String srvGetAddr) {
        this.srvGetAddr = srvGetAddr;
    }

    public String getSrvGetLon() {
        return srvGetLon;
    }

    public void setSrvGetLon(String srvGetLon) {
        this.srvGetLon = srvGetLon;
    }

    public String getSrvGetLat() {
        return srvGetLat;
    }

    public void setSrvGetLat(String srvGetLat) {
        this.srvGetLat = srvGetLat;
    }

    public Integer getSrvReturnFlag() {
        return srvReturnFlag;
    }

    public void setSrvReturnFlag(Integer srvReturnFlag) {
        this.srvReturnFlag = srvReturnFlag;
    }

    public String getSrvReturnAddr() {
        return srvReturnAddr;
    }

    public void setSrvReturnAddr(String srvReturnAddr) {
        this.srvReturnAddr = srvReturnAddr;
    }

    public String getSrvReturnLon() {
        return srvReturnLon;
    }

    public void setSrvReturnLon(String srvReturnLon) {
        this.srvReturnLon = srvReturnLon;
    }

    public String getSrvReturnLat() {
        return srvReturnLat;
    }

    public void setSrvReturnLat(String srvReturnLat) {
        this.srvReturnLat = srvReturnLat;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getAbatement() {
        return abatement;
    }

    public void setAbatement(String abatement) {
        this.abatement = abatement;
    }

    public String getGetCarFreeCouponId() {
        return getCarFreeCouponId;
    }

    public void setGetCarFreeCouponId(String getCarFreeCouponId) {
        this.getCarFreeCouponId = getCarFreeCouponId;
    }

    public String getDisCouponId() {
        return disCouponId;
    }

    public void setDisCouponId(String disCouponId) {
        this.disCouponId = disCouponId;
    }

    public String getCarOwnerCouponNo() {
        return carOwnerCouponNo;
    }

    public void setCarOwnerCouponNo(String carOwnerCouponNo) {
        this.carOwnerCouponNo = carOwnerCouponNo;
    }

    public Integer getUseAutoCoin() {
        return useAutoCoin;
    }

    public void setUseAutoCoin(Integer useAutoCoin) {
        this.useAutoCoin = useAutoCoin;
    }

    public Integer getUseBal() {
        return useBal;
    }

    public void setUseBal(Integer useBal) {
        this.useBal = useBal;
    }

    public String getDriverIds() {
        return driverIds;
    }

    public void setDriverIds(String driverIds) {
        this.driverIds = driverIds;
    }

    public String getCarAddrIndex() {
        return carAddrIndex;
    }

    public void setCarAddrIndex(String carAddrIndex) {
        this.carAddrIndex = carAddrIndex;
    }
}
