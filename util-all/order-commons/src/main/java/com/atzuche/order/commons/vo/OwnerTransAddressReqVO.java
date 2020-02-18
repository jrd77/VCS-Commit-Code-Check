package com.atzuche.order.commons.vo;

import com.autoyol.doc.annotation.AutoDocProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

public class OwnerTransAddressReqVO implements Serializable {

	private static final long serialVersionUID = 8375962123296680756L;

	@AutoDocProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @AutoDocProperty(value = "车主会员号")
    @NotBlank(message = "车主会员号")
    private String memNo;

	@AutoDocProperty(value = "取车地址描述,如:乐山路33号")
	@NotBlank(message = "取车地址不能为空")
	private String getCarAddressText;

	@AutoDocProperty(value = "还车地址描述,如:乐山路33号")
	@NotBlank(message = "还车地址不能为空")
    private String returnCarAddressText;

    @AutoDocProperty(value = "取车地址-经度")
	@NotBlank(message = "取车地址-经度不能为空")
    private String srvGetLon;

    @AutoDocProperty(value = "取车地址-纬度")
	@NotBlank(message = "取车地址-纬度不能为空")
    private String srvGetLat;

    @AutoDocProperty(value = "还车地址-经度")
	@NotBlank(message = "还车地址-经度不能为空")
    private String srvReturnLon;

    @AutoDocProperty(value = "还车地址-纬度")
	@NotBlank(message = "还车地址-纬度不能为空")
    private String srvReturnLat;

	@AutoDocProperty(value = "收费金额回传,用户校验提交时费用是否变更,仅校验提交前不收费,提交后收费的情况")
    private String chargeAmount;

	/**
	 * 1 :管理后台操作来源标记
 	 */
	private String consoleInvoke;

	public String getConsoleInvoke() {
		return consoleInvoke;
	}

	public void setConsoleInvoke(String consoleInvoke) {
		this.consoleInvoke = consoleInvoke;
	}

	public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

	public String getMemNo() {
		return memNo;
	}

	public void setMemNo(String memNo) {
		this.memNo = memNo;
	}

	public String getGetCarAddressText() {
		return getCarAddressText;
	}

	public String getReturnCarAddressText() {
		return returnCarAddressText;
	}

	public String getSrvGetLon() {
		return srvGetLon;
	}

	public String getSrvGetLat() {
		return srvGetLat;
	}

	public String getSrvReturnLon() {
		return srvReturnLon;
	}

	public String getSrvReturnLat() {
		return srvReturnLat;
	}

	public void setGetCarAddressText(String getCarAddressText) {
		this.getCarAddressText = getCarAddressText;
	}

	public void setReturnCarAddressText(String returnCarAddressText) {
		this.returnCarAddressText = returnCarAddressText;
	}

	public void setSrvGetLon(String srvGetLon) {
		this.srvGetLon = srvGetLon;
	}

	public void setSrvGetLat(String srvGetLat) {
		this.srvGetLat = srvGetLat;
	}

	public void setSrvReturnLon(String srvReturnLon) {
		this.srvReturnLon = srvReturnLon;
	}

	public void setSrvReturnLat(String srvReturnLat) {
		this.srvReturnLat = srvReturnLat;
	}

	public String getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(String chargeAmount) {
		this.chargeAmount = chargeAmount;
	}
}
