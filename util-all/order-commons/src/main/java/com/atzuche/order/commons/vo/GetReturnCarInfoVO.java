package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocIgnoreProperty;
import com.autoyol.doc.annotation.AutoDocProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;
@JsonInclude(value=Include.NON_EMPTY)
public class GetReturnCarInfoVO implements Serializable{


	private static final long serialVersionUID = -5303470631999024531L;

    @AutoDocProperty(value = "城市编码")
	private String cityCode;

    @AutoDocProperty(value = "城市名称")
	private String cityName;

    @AutoDocProperty(value = "取车地址描述,如:乐山路33号")
    private String getCarAddressText;
    
    @AutoDocProperty(value = "取车地址是否可修改,0:否,1:是")
    private String getCarAddressModifiable;
    
    @AutoDocProperty(value = "还车地址描述,如:乐山路33号")
    private String returnCarAddressText;
    
    @AutoDocProperty(value = "还车地址是否可修改,0:否,1:是")
    private String returnCarAddressModifiable;

    @AutoDocProperty(value = "取车地址-经度")
    private String srvGetLon;

    @AutoDocProperty(value = "取车地址-纬度")
    private String srvGetLat;

    @AutoDocProperty(value = "还车地址-经度")
    private String srvReturnLon;

    @AutoDocProperty(value = "还车地址-纬度")
    private String srvReturnLat;
    
    @AutoDocProperty(value = "收费提示文案,如:您当前还车地址距离原还车地址大于2公里，产生额外修改费用。该费用将从本次订单收益中扣除")
    private String chargePromptText;
    
    @AutoDocProperty(value = "收费提示文案,如:修改超2公里收取服务费")
    private String chargeHintText;
    
    @AutoDocProperty(value = "收费金额,如:58")
    private String chargeAmount;

    @AutoDocProperty(value = "修改规则跳转H5 url")
    private String modifyRuleUrl;

    @AutoDocProperty(value = "网上交易三方电子合同变更协议跳转H5 url")
    private String threePartyContractUrl;

    @AutoDocProperty(value = "车辆信息")
    private CarBasicInfo carBasicInfo;

    @AutoDocIgnoreProperty
    private Boolean isCurrentGetCarAddress;

    @AutoDocIgnoreProperty
    private Boolean isCurrentReturnCarAddress;
    
    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getGetCarAddressText() {
        return getCarAddressText;
    }

    public void setGetCarAddressText(String getCarAddressText) {
        this.getCarAddressText = getCarAddressText;
    }

    public String getGetCarAddressModifiable() {
        return getCarAddressModifiable;
    }

    public void setGetCarAddressModifiable(String getCarAddressModifiable) {
        this.getCarAddressModifiable = getCarAddressModifiable;
    }

    public String getReturnCarAddressText() {
        return returnCarAddressText;
    }

    public void setReturnCarAddressText(String returnCarAddressText) {
        this.returnCarAddressText = returnCarAddressText;
    }

    public String getReturnCarAddressModifiable() {
        return returnCarAddressModifiable;
    }

    public void setReturnCarAddressModifiable(String returnCarAddressModifiable) {
        this.returnCarAddressModifiable = returnCarAddressModifiable;
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

    public String getChargePromptText() {
        return chargePromptText;
    }

    public void setChargePromptText(String chargePromptText) {
        this.chargePromptText = chargePromptText;
    }

    public String getChargeHintText() {
        return chargeHintText;
    }

    public void setChargeHintText(String chargeHintText) {
        this.chargeHintText = chargeHintText;
    }

    public String getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(String chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getModifyRuleUrl() {
        return modifyRuleUrl;
    }

    public void setModifyRuleUrl(String modifyRuleUrl) {
        this.modifyRuleUrl = modifyRuleUrl;
    }

    public String getThreePartyContractUrl() {
        return threePartyContractUrl;
    }

    public void setThreePartyContractUrl(String threePartyContractUrl) {
        this.threePartyContractUrl = threePartyContractUrl;
    }

    public CarBasicInfo getCarBasicInfo() {
        return carBasicInfo;
    }

    public void setCarBasicInfo(CarBasicInfo carBasicInfo) {
        this.carBasicInfo = carBasicInfo;
    }

    public Boolean getCurrentGetCarAddress() {
        return isCurrentGetCarAddress;
    }

    public void setCurrentGetCarAddress(Boolean currentGetCarAddress) {
        isCurrentGetCarAddress = currentGetCarAddress;
    }

    public Boolean getCurrentReturnCarAddress() {
        return isCurrentReturnCarAddress;
    }

    public void setCurrentReturnCarAddress(Boolean currentReturnCarAddress) {
        isCurrentReturnCarAddress = currentReturnCarAddress;
    }
}
