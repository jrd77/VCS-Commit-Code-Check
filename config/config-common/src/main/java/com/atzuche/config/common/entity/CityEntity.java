package com.atzuche.config.common.entity;

import java.util.Date;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/2 10:00 上午
 **/
public class CityEntity {
    private Integer id;

    private Integer code;

    private String name;

    private String detail;

    private String lat;

    private String lon;

    private String telprefix;

    private String plateprefix;

    private String type;

    private String issupport;

    private String isOpen;

    private Date createDate;

    private Integer calculateFlag;

    private Integer trusteeshipNoHour;

    private Integer trusteeshipHour;

    private Integer srvGetCost;

    private Integer srvReturnCost;

    private String orderTimeSet;

    private String rentTimeSet;

    private Integer minPrice;

    private Integer floatPercent;

    private Integer fallPercent;

    private Integer revertStInterval;

    private Integer revertServiceMax;

    private Integer rentStInterval;

    private Integer rentServiceMax;

    private String bgUrl;

    private String description;

    private Byte supportOneKey;



    private Integer incomeFloatPercent;

    private Integer incomeFallPercent;

    private Boolean allowTransaction;

    private Integer isDispatching;

    private Integer dispatchingBeginTime;

    private Integer dispatchingEndTime;

    private Integer minSubsidyPerDay;

    private Integer maxSubsidyPerDay;

    private Integer maxSubsidyPerOrder;

    private Integer isCpicCoupon;

    private Integer beforeTime;

    private Integer cpicBeforeTimeWork;

    private Integer cpicBeforeTimeFree;

    private Integer addressRangeVersion;

    private String addressRangeDescription;

    private Integer isQuickPay;

    private String fullLetter;

    private Boolean isHotCity;

    private String centerAddress;

    private Boolean isLimit;

    private String firstLetter;

    private Integer isDistribute;

    private Integer level;

    private String peakTimeSlot;

    private Integer beforeTransTimeSpan;

    private Integer unserviceBeginTime;

    private Integer unserviceEndTime;

    private Integer deliveryServiceCost;

    private Integer packageDeliveryServiceCost;

    private String peakStartsWith;

    private String peakNotStartsWith;

    private Integer isFreePackage;

    private String cityCenterAddress;

    private Byte isOfdScooter;

    private String addressRange;

    private String freeAddressRange;

    private String otaAddressRange;

    private Integer enableMiniTwoLevelCity;

    public Integer getEnableMiniTwoLevelCity() {
        return enableMiniTwoLevelCity;
    }

    public void setEnableMiniTwoLevelCity(Integer enableMiniTwoLevelCity) {
        this.enableMiniTwoLevelCity = enableMiniTwoLevelCity;
    }

    public String getAddressRange() {
        return addressRange;
    }

    public void setAddressRange(String addressRange) {
        this.addressRange = addressRange;
    }

    public String getFreeAddressRange() {
        return freeAddressRange;
    }

    public void setFreeAddressRange(String freeAddressRange) {
        this.freeAddressRange = freeAddressRange;
    }

    public String getOtaAddressRange() {
        return otaAddressRange;
    }

    public void setOtaAddressRange(String otaAddressRange) {
        this.otaAddressRange = otaAddressRange;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getTelprefix() {
        return telprefix;
    }

    public void setTelprefix(String telprefix) {
        this.telprefix = telprefix;
    }

    public String getPlateprefix() {
        return plateprefix;
    }

    public void setPlateprefix(String plateprefix) {
        this.plateprefix = plateprefix;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIssupport() {
        return issupport;
    }

    public void setIssupport(String issupport) {
        this.issupport = issupport;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getCalculateFlag() {
        return calculateFlag;
    }

    public void setCalculateFlag(Integer calculateFlag) {
        this.calculateFlag = calculateFlag;
    }

    public Integer getTrusteeshipNoHour() {
        return trusteeshipNoHour;
    }

    public void setTrusteeshipNoHour(Integer trusteeshipNoHour) {
        this.trusteeshipNoHour = trusteeshipNoHour;
    }

    public Integer getTrusteeshipHour() {
        return trusteeshipHour;
    }

    public void setTrusteeshipHour(Integer trusteeshipHour) {
        this.trusteeshipHour = trusteeshipHour;
    }

    public Integer getSrvGetCost() {
        return srvGetCost;
    }

    public void setSrvGetCost(Integer srvGetCost) {
        this.srvGetCost = srvGetCost;
    }

    public Integer getSrvReturnCost() {
        return srvReturnCost;
    }

    public void setSrvReturnCost(Integer srvReturnCost) {
        this.srvReturnCost = srvReturnCost;
    }

    public String getOrderTimeSet() {
        return orderTimeSet;
    }

    public void setOrderTimeSet(String orderTimeSet) {
        this.orderTimeSet = orderTimeSet;
    }

    public String getRentTimeSet() {
        return rentTimeSet;
    }

    public void setRentTimeSet(String rentTimeSet) {
        this.rentTimeSet = rentTimeSet;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getFloatPercent() {
        return floatPercent;
    }

    public void setFloatPercent(Integer floatPercent) {
        this.floatPercent = floatPercent;
    }

    public Integer getFallPercent() {
        return fallPercent;
    }

    public void setFallPercent(Integer fallPercent) {
        this.fallPercent = fallPercent;
    }

    public Integer getRevertStInterval() {
        return revertStInterval;
    }

    public void setRevertStInterval(Integer revertStInterval) {
        this.revertStInterval = revertStInterval;
    }

    public Integer getRevertServiceMax() {
        return revertServiceMax;
    }

    public void setRevertServiceMax(Integer revertServiceMax) {
        this.revertServiceMax = revertServiceMax;
    }

    public Integer getRentStInterval() {
        return rentStInterval;
    }

    public void setRentStInterval(Integer rentStInterval) {
        this.rentStInterval = rentStInterval;
    }

    public Integer getRentServiceMax() {
        return rentServiceMax;
    }

    public void setRentServiceMax(Integer rentServiceMax) {
        this.rentServiceMax = rentServiceMax;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte getSupportOneKey() {
        return supportOneKey;
    }

    public void setSupportOneKey(Byte supportOneKey) {
        this.supportOneKey = supportOneKey;
    }



    public Integer getIncomeFloatPercent() {
        return incomeFloatPercent;
    }

    public void setIncomeFloatPercent(Integer incomeFloatPercent) {
        this.incomeFloatPercent = incomeFloatPercent;
    }

    public Integer getIncomeFallPercent() {
        return incomeFallPercent;
    }

    public void setIncomeFallPercent(Integer incomeFallPercent) {
        this.incomeFallPercent = incomeFallPercent;
    }

    public Boolean getAllowTransaction() {
        return allowTransaction;
    }

    public void setAllowTransaction(Boolean allowTransaction) {
        this.allowTransaction = allowTransaction;
    }

    public Integer getIsDispatching() {
        return isDispatching;
    }

    public void setIsDispatching(Integer isDispatching) {
        this.isDispatching = isDispatching;
    }

    public Integer getDispatchingBeginTime() {
        return dispatchingBeginTime;
    }

    public void setDispatchingBeginTime(Integer dispatchingBeginTime) {
        this.dispatchingBeginTime = dispatchingBeginTime;
    }

    public Integer getDispatchingEndTime() {
        return dispatchingEndTime;
    }

    public void setDispatchingEndTime(Integer dispatchingEndTime) {
        this.dispatchingEndTime = dispatchingEndTime;
    }

    public Integer getMinSubsidyPerDay() {
        return minSubsidyPerDay;
    }

    public void setMinSubsidyPerDay(Integer minSubsidyPerDay) {
        this.minSubsidyPerDay = minSubsidyPerDay;
    }

    public Integer getMaxSubsidyPerDay() {
        return maxSubsidyPerDay;
    }

    public void setMaxSubsidyPerDay(Integer maxSubsidyPerDay) {
        this.maxSubsidyPerDay = maxSubsidyPerDay;
    }

    public Integer getMaxSubsidyPerOrder() {
        return maxSubsidyPerOrder;
    }

    public void setMaxSubsidyPerOrder(Integer maxSubsidyPerOrder) {
        this.maxSubsidyPerOrder = maxSubsidyPerOrder;
    }

    public Integer getIsCpicCoupon() {
        return isCpicCoupon;
    }

    public void setIsCpicCoupon(Integer isCpicCoupon) {
        this.isCpicCoupon = isCpicCoupon;
    }

    public Integer getBeforeTime() {
        return beforeTime;
    }

    public void setBeforeTime(Integer beforeTime) {
        this.beforeTime = beforeTime;
    }

    public Integer getCpicBeforeTimeWork() {
        return cpicBeforeTimeWork;
    }

    public void setCpicBeforeTimeWork(Integer cpicBeforeTimeWork) {
        this.cpicBeforeTimeWork = cpicBeforeTimeWork;
    }

    public Integer getCpicBeforeTimeFree() {
        return cpicBeforeTimeFree;
    }

    public void setCpicBeforeTimeFree(Integer cpicBeforeTimeFree) {
        this.cpicBeforeTimeFree = cpicBeforeTimeFree;
    }

    public Integer getAddressRangeVersion() {
        return addressRangeVersion;
    }

    public void setAddressRangeVersion(Integer addressRangeVersion) {
        this.addressRangeVersion = addressRangeVersion;
    }

    public String getAddressRangeDescription() {
        return addressRangeDescription;
    }

    public void setAddressRangeDescription(String addressRangeDescription) {
        this.addressRangeDescription = addressRangeDescription;
    }

    public Integer getIsQuickPay() {
        return isQuickPay;
    }

    public void setIsQuickPay(Integer isQuickPay) {
        this.isQuickPay = isQuickPay;
    }

    public String getFullLetter() {
        return fullLetter;
    }

    public void setFullLetter(String fullLetter) {
        this.fullLetter = fullLetter;
    }

    public Boolean getIsHotCity() {
        return isHotCity;
    }

    public void setIsHotCity(Boolean isHotCity) {
        this.isHotCity = isHotCity;
    }

    public String getCenterAddress() {
        return centerAddress;
    }

    public void setCenterAddress(String centerAddress) {
        this.centerAddress = centerAddress;
    }

    public Boolean getIsLimit() {
        return isLimit;
    }

    public void setIsLimit(Boolean isLimit) {
        this.isLimit = isLimit;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public Integer getIsDistribute() {
        return isDistribute;
    }

    public void setIsDistribute(Integer isDistribute) {
        this.isDistribute = isDistribute;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getPeakTimeSlot() {
        return peakTimeSlot;
    }

    public void setPeakTimeSlot(String peakTimeSlot) {
        this.peakTimeSlot = peakTimeSlot;
    }

    public Integer getBeforeTransTimeSpan() {
        return beforeTransTimeSpan;
    }

    public void setBeforeTransTimeSpan(Integer beforeTransTimeSpan) {
        this.beforeTransTimeSpan = beforeTransTimeSpan;
    }

    public Integer getUnserviceBeginTime() {
        return unserviceBeginTime;
    }

    public void setUnserviceBeginTime(Integer unserviceBeginTime) {
        this.unserviceBeginTime = unserviceBeginTime;
    }

    public Integer getUnserviceEndTime() {
        return unserviceEndTime;
    }

    public void setUnserviceEndTime(Integer unserviceEndTime) {
        this.unserviceEndTime = unserviceEndTime;
    }

    public Integer getDeliveryServiceCost() {
        return deliveryServiceCost;
    }

    public void setDeliveryServiceCost(Integer deliveryServiceCost) {
        this.deliveryServiceCost = deliveryServiceCost;
    }

    public Integer getPackageDeliveryServiceCost() {
        return packageDeliveryServiceCost;
    }

    public void setPackageDeliveryServiceCost(Integer packageDeliveryServiceCost) {
        this.packageDeliveryServiceCost = packageDeliveryServiceCost;
    }

    public String getPeakStartsWith() {
        return peakStartsWith;
    }

    public void setPeakStartsWith(String peakStartsWith) {
        this.peakStartsWith = peakStartsWith;
    }

    public String getPeakNotStartsWith() {
        return peakNotStartsWith;
    }

    public void setPeakNotStartsWith(String peakNotStartsWith) {
        this.peakNotStartsWith = peakNotStartsWith;
    }

    public Integer getIsFreePackage() {
        return isFreePackage;
    }

    public void setIsFreePackage(Integer isFreePackage) {
        this.isFreePackage = isFreePackage;
    }

    public String getCityCenterAddress() {
        return cityCenterAddress;
    }

    public void setCityCenterAddress(String cityCenterAddress) {
        this.cityCenterAddress = cityCenterAddress;
    }

    public Byte getIsOfdScooter() {
        return isOfdScooter;
    }

    public void setIsOfdScooter(Byte isOfdScooter) {
        this.isOfdScooter = isOfdScooter;
    }



    public Boolean getHotCity() {
        return isHotCity;
    }

    public void setHotCity(Boolean hotCity) {
        isHotCity = hotCity;
    }

    public Boolean getLimit() {
        return isLimit;
    }

    public void setLimit(Boolean limit) {
        isLimit = limit;
    }

    @Override
    public String toString() {
        return "CityEntity{" +
                "id=" + id +
                ", code=" + code +
                ", name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", telprefix='" + telprefix + '\'' +
                ", plateprefix='" + plateprefix + '\'' +
                ", type='" + type + '\'' +
                ", issupport='" + issupport + '\'' +
                ", isOpen='" + isOpen + '\'' +
                ", createDate=" + createDate +
                ", calculateFlag=" + calculateFlag +
                ", trusteeshipNoHour=" + trusteeshipNoHour +
                ", trusteeshipHour=" + trusteeshipHour +
                ", srvGetCost=" + srvGetCost +
                ", srvReturnCost=" + srvReturnCost +
                ", orderTimeSet='" + orderTimeSet + '\'' +
                ", rentTimeSet='" + rentTimeSet + '\'' +
                ", minPrice=" + minPrice +
                ", floatPercent=" + floatPercent +
                ", fallPercent=" + fallPercent +
                ", revertStInterval=" + revertStInterval +
                ", revertServiceMax=" + revertServiceMax +
                ", rentStInterval=" + rentStInterval +
                ", rentServiceMax=" + rentServiceMax +
                ", bgUrl='" + bgUrl + '\'' +
                ", description='" + description + '\'' +
                ", supportOneKey=" + supportOneKey +
                ", incomeFloatPercent=" + incomeFloatPercent +
                ", incomeFallPercent=" + incomeFallPercent +
                ", allowTransaction=" + allowTransaction +
                ", isDispatching=" + isDispatching +
                ", dispatchingBeginTime=" + dispatchingBeginTime +
                ", dispatchingEndTime=" + dispatchingEndTime +
                ", minSubsidyPerDay=" + minSubsidyPerDay +
                ", maxSubsidyPerDay=" + maxSubsidyPerDay +
                ", maxSubsidyPerOrder=" + maxSubsidyPerOrder +
                ", isCpicCoupon=" + isCpicCoupon +
                ", beforeTime=" + beforeTime +
                ", cpicBeforeTimeWork=" + cpicBeforeTimeWork +
                ", cpicBeforeTimeFree=" + cpicBeforeTimeFree +
                ", addressRangeVersion=" + addressRangeVersion +
                ", addressRangeDescription='" + addressRangeDescription + '\'' +
                ", isQuickPay=" + isQuickPay +
                ", fullLetter='" + fullLetter + '\'' +
                ", isHotCity=" + isHotCity +
                ", centerAddress='" + centerAddress + '\'' +
                ", isLimit=" + isLimit +
                ", firstLetter='" + firstLetter + '\'' +
                ", isDistribute=" + isDistribute +
                ", level=" + level +
                ", peakTimeSlot='" + peakTimeSlot + '\'' +
                ", beforeTransTimeSpan=" + beforeTransTimeSpan +
                ", unserviceBeginTime=" + unserviceBeginTime +
                ", unserviceEndTime=" + unserviceEndTime +
                ", deliveryServiceCost=" + deliveryServiceCost +
                ", packageDeliveryServiceCost=" + packageDeliveryServiceCost +
                ", peakStartsWith='" + peakStartsWith + '\'' +
                ", peakNotStartsWith='" + peakNotStartsWith + '\'' +
                ", isFreePackage=" + isFreePackage +
                ", cityCenterAddress='" + cityCenterAddress + '\'' +
                ", isOfdScooter=" + isOfdScooter +
                ", addressRange='" + addressRange + '\'' +
                ", freeAddressRange='" + freeAddressRange + '\'' +
                ", otaAddressRange='" + otaAddressRange + '\'' +
                ", enableMiniTwoLevelCity=" + enableMiniTwoLevelCity +
                '}';
    }
}
