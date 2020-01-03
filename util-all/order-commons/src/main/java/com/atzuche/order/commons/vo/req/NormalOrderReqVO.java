package com.atzuche.order.commons.vo.req;

import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短租订单App提交的相关参数
 *
 * @author ZhangBin
 * @date 2019/12/12 15:14
 **/

public class NormalOrderReqVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -7311434304468158415L;

    @AutoDocProperty(value = "订单类型", required = true)
    @NotBlank(message = "订单类型不能为空")
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

    @AutoDocProperty(value = "城市名称")
    private String cityName;

    @AutoDocProperty(value = "订单场景编码", required = true)
    @NotBlank(message = "订单场景编码不能为空")
    private String sceneCode;

    @AutoDocProperty(value = "订单来源", required = true)
    @NotBlank(message = "订单来源不能为空")
    private String source;

    @AutoDocProperty(value = "细分订单来源")
    private String subSource;

    @AutoDocProperty(value = "订单取车时间(yyyy-MM-dd HH:mm:ss)", required = true)
    @NotBlank(message = "订单取车时间不能为空")
    private String rentTime;

    @AutoDocProperty(value = "订单还车时间(yyyy-MM-dd HH:mm:ss)", required = true)
    @NotBlank(message = "订单还车时间不能为空")
    private String revertTime;

    @AutoDocProperty(value = "是否使用取车服务:0.否 1.是", required = true)
    @NotNull(message = "是否使用取车服务标识不能为空")
    private Integer srvGetFlag;

    @AutoDocProperty(value = "取车服务-取车地址", required = true)
    @NotBlank(message = "取车地址不能为空")
    private String srvGetAddr;

    @AutoDocProperty(value = "取车服务-取车地址-地址经度", required = true)
    @NotBlank(message = "取车地址经度不能为空")
    private String srvGetLon;

    @AutoDocProperty(value = "取车服务-取车地址-地址维度", required = true)
    @NotBlank(message = "取车地址维度不能为空")
    private String srvGetLat;

    @AutoDocProperty(value = "是否使用还车服务:0.否 1.是", required = true)
    @NotNull(message = "是否使用还车服务标识不能为空")
    private Integer srvReturnFlag;

    @AutoDocProperty(value = "还车服务-还车地址", required = true)
    @NotBlank(message = "还车地址不能为空")
    private String srvReturnAddr;

    @AutoDocProperty(value = "还车服务-还车地址-地址经度", required = true)
    @NotBlank(message = "还车地址经度不能为空")
    private String srvReturnLon;

    @AutoDocProperty(value = "还车服务-还车地址-地址维度", required = true)
    @NotBlank(message = "还车地址维度不能为空")
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
    private String disCouponIds;

    @AutoDocProperty(value = "车主优惠券编码")
    private String carOwnerCouponNo;

    @AutoDocProperty(value = "是否使用凹凸币:0.否 1.是")
    private Integer useAutoCoin;

    @AutoDocProperty(value = "是否使用钱包余额:0.否 1.是")
    private Integer useBal;

    @AutoDocProperty(value = "免押方式ID:1.绑卡免押 2.芝麻免押 3.支付押金")
    private String freeDoubleTypeId;

    @AutoDocProperty(value = "是否使用机场服务")
    private Integer useAirportService;

    @AutoDocProperty(value = "航班号")
    private String flightNo;

    @AutoDocProperty(value = "限时立减红包状态")
    private String limitRedStatus;

    @AutoDocProperty(value = "限时立减红包ID")
    private String limitReductionId;

    @AutoDocProperty(value = "附加驾驶人ID,多个以逗号分隔")
    private String driverIds;

    @AutoDocProperty(value = "虚拟地址序列号,默认为0")
    private String carAddrIndex;

    @AutoDocProperty(value = "是否出市")
    private Integer isLeaveCity;

    @AutoDocProperty(value = "用车城市,如果isLeaveCity=1，该字段代表去往的城市，否则就是本市")
    private String rentCity;

    @AutoDocProperty(value = "筛车条件ID")
    private String queryId;

    @AutoDocProperty(value = "营销活动ID")
    private String activityId;

    @AutoDocProperty(value = "租车原因")
    private String rentReason;

    @AutoDocProperty(value = "油费计算方式，1:原油位返还,2:自行结算,3:原油位返还或自行结算")
    private String oilType;

    @AutoDocProperty(value = "联系电话(车主电话)")
    private String conPhone;

    @AutoDocProperty(value = "广告来源")
    private String utmSource;

    @AutoDocProperty(value = "广告媒体")
    private String utmMedium;

    @AutoDocProperty(value = "广告名称")
    private String utmTerm;

    @AutoDocProperty(value = "广告关键字")
    private String utmCampaign;

    @AutoDocProperty(value = "请求模块名称,如:order", hidden = true)
    private String ModuleName;

    @AutoDocProperty(value = "请求接口名称,如:order/req", hidden = true)
    private String FunctionName;

    @AutoDocProperty(value = "订单来源", hidden = true)
    private Integer reqSource;

    @AutoDocProperty(value = "app应用版本", hidden = true)
    private String reqVersion;

    @AutoDocProperty(value = "app应用系统类型", hidden = true)
    private String reqOs;

    @AutoDocProperty(value = "请求来源IP地址", hidden = true)
    private String srcIp;

    @AutoDocProperty(value = "请求来源IP地址对应端口号", hidden = true)
    private Integer srcPort;


    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
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

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSceneCode() {
        return sceneCode;
    }

    public void setSceneCode(String sceneCode) {
        this.sceneCode = sceneCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSubSource() {
        return subSource;
    }

    public void setSubSource(String subSource) {
        this.subSource = subSource;
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

    public String getDisCouponIds() {
        return disCouponIds;
    }

    public void setDisCouponIds(String disCouponIds) {
        this.disCouponIds = disCouponIds;
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

    public String getFreeDoubleTypeId() {
        return freeDoubleTypeId;
    }

    public void setFreeDoubleTypeId(String freeDoubleTypeId) {
        this.freeDoubleTypeId = freeDoubleTypeId;
    }

    public Integer getUseAirportService() {
        return useAirportService;
    }

    public void setUseAirportService(Integer useAirportService) {
        this.useAirportService = useAirportService;
    }

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public String getLimitRedStatus() {
        return limitRedStatus;
    }

    public void setLimitRedStatus(String limitRedStatus) {
        this.limitRedStatus = limitRedStatus;
    }

    public String getLimitReductionId() {
        return limitReductionId;
    }

    public void setLimitReductionId(String limitReductionId) {
        this.limitReductionId = limitReductionId;
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

    public Integer getIsLeaveCity() {
        return isLeaveCity;
    }

    public void setIsLeaveCity(Integer isLeaveCity) {
        this.isLeaveCity = isLeaveCity;
    }

    public String getRentCity() {
        return rentCity;
    }

    public void setRentCity(String rentCity) {
        this.rentCity = rentCity;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getRentReason() {
        return rentReason;
    }

    public void setRentReason(String rentReason) {
        this.rentReason = rentReason;
    }


    public String getOilType() {
        return oilType;
    }

    public void setOilType(String oilType) {
        this.oilType = oilType;
    }

    public String getConPhone() {
        return conPhone;
    }

    public void setConPhone(String conPhone) {
        this.conPhone = conPhone;
    }

    public String getUtmSource() {
        return utmSource;
    }

    public void setUtmSource(String utmSource) {
        this.utmSource = utmSource;
    }

    public String getUtmMedium() {
        return utmMedium;
    }

    public void setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium;
    }

    public String getUtmTerm() {
        return utmTerm;
    }

    public void setUtmTerm(String utmTerm) {
        this.utmTerm = utmTerm;
    }

    public String getUtmCampaign() {
        return utmCampaign;
    }

    public void setUtmCampaign(String utmCampaign) {
        this.utmCampaign = utmCampaign;
    }

    public Integer getReqSource() {
        return reqSource;
    }

    public void setReqSource(Integer reqSource) {
        this.reqSource = reqSource;
    }

    public String getReqVersion() {
        return reqVersion;
    }

    public void setReqVersion(String reqVersion) {
        this.reqVersion = reqVersion;
    }

    public String getReqOs() {
        return reqOs;
    }

    public void setReqOs(String reqOs) {
        this.reqOs = reqOs;
    }

    public String getModuleName() {
        return ModuleName;
    }

    public void setModuleName(String moduleName) {
        ModuleName = moduleName;
    }

    public String getFunctionName() {
        return FunctionName;
    }

    public void setFunctionName(String functionName) {
        FunctionName = functionName;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public Integer getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(Integer srcPort) {
        this.srcPort = srcPort;
    }

}

