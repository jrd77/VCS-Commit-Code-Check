package com.atzuche.order.commons.vo.res.order;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.ToString;


/**
 * 租客订单子表
 * 
 * @author ZhangBin
 * @date 2019-12-24 14:23:11
 * @Description:
 */
//@Data
@ToString
public class RenterOrderResVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 子订单号
	 */
	private String renterOrderNo;
    /**
     * 租客会员号
     */
	private String renterMemNo;
	/**
	 * 预计起租时间
	 */
//	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")  
	private LocalDateTime expRentTime;
	/**
	 * 预计还车时间
	 */
//	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")  
	private LocalDateTime expRevertTime;
	/**
	 * 实际起租时间
	 */
//	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime actRentTime;
	/**
	 * 实际还车时间
	 */
//	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime actRevertTime;
	/**
	 * 商品编码
	 */
	private String goodsCode;
	/**
	 * 商品类型
	 */
	private String goodsType;
	/**
	 * 车主是否同意 0-未同意，1-已同意
	 */
	private Integer agreeFlag;
	/**
	 * 租客子单状态，1：进行中，2：已完结，3：已结束
	 */
	private Integer childStatus;
	/**
	 * 是否使用凹凸币 0-否，1-是
	 */
	private Integer isUseCoin;
	/**
	 * 是否使用钱包 0-否，1-是
	 */
	private Integer isUseWallet;
	/**
	 * 是否使用优惠券 0-否，1-是
	 */
	private Integer isUseCoupon;
	/**
	 * 是否使用取车服务 0-否，1-是
	 */
	private Integer isGetCar;
	/**
	 * 是否使用还车服务 0-否，1-是
	 */
	private Integer isReturnCar;
	/**
	 * 附加驾驶人（人数）
	 */
	private Integer addDriver;
	/**
	 * 是否开启不计免赔 0-不开启，1-开启
	 */
	private Integer isAbatement;
	/**
	 * 是否有效 1-有效 0-无效
	 */
	private Integer isEffective;
	/**
	 * 是否使用特供价 0-否，1-是
	 */
	private Integer isUseSpecialPrice;
	/**
	 * 是否取消 0-正常，1-取消
	 */
	private Integer isCancle;
	/**
	 * 修改方 1、后台管理 2、租客 3、车主
	 */
	private String changeSource;
	//修改原因 20200210新增
	private String changeReason;
    /**
     * 车主同意请求时间
     */
//	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime reqAcceptTime;

	/**
	 * 创建时间
	 */
//	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 修改时间
	 */
//	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateTime;
	/**
	 * 修改人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getRenterOrderNo() {
		return renterOrderNo;
	}
	public void setRenterOrderNo(String renterOrderNo) {
		this.renterOrderNo = renterOrderNo;
	}
	public String getRenterMemNo() {
		return renterMemNo;
	}
	public void setRenterMemNo(String renterMemNo) {
		this.renterMemNo = renterMemNo;
	}
	public LocalDateTime getExpRentTime() {
		return expRentTime;
	}
	public void setExpRentTime(LocalDateTime expRentTime) {
		this.expRentTime = expRentTime;
	}
	public LocalDateTime getExpRevertTime() {
		return expRevertTime;
	}
	public void setExpRevertTime(LocalDateTime expRevertTime) {
		this.expRevertTime = expRevertTime;
	}
	public LocalDateTime getActRentTime() {
		return actRentTime;
	}
	public void setActRentTime(LocalDateTime actRentTime) {
		this.actRentTime = actRentTime;
	}
	public LocalDateTime getActRevertTime() {
		return actRevertTime;
	}
	public void setActRevertTime(LocalDateTime actRevertTime) {
		this.actRevertTime = actRevertTime;
	}
	public String getGoodsCode() {
		return goodsCode;
	}
	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}
	public String getGoodsType() {
		return goodsType;
	}
	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}
	public Integer getAgreeFlag() {
		return agreeFlag;
	}
	public void setAgreeFlag(Integer agreeFlag) {
		this.agreeFlag = agreeFlag;
	}
	public Integer getChildStatus() {
		return childStatus;
	}
	public void setChildStatus(Integer childStatus) {
		this.childStatus = childStatus;
	}
	public Integer getIsUseCoin() {
		return isUseCoin;
	}
	public void setIsUseCoin(Integer isUseCoin) {
		this.isUseCoin = isUseCoin;
	}
	public Integer getIsUseWallet() {
		return isUseWallet;
	}
	public void setIsUseWallet(Integer isUseWallet) {
		this.isUseWallet = isUseWallet;
	}
	public Integer getIsUseCoupon() {
		return isUseCoupon;
	}
	public void setIsUseCoupon(Integer isUseCoupon) {
		this.isUseCoupon = isUseCoupon;
	}
	public Integer getIsGetCar() {
		return isGetCar;
	}
	public void setIsGetCar(Integer isGetCar) {
		this.isGetCar = isGetCar;
	}
	public Integer getIsReturnCar() {
		return isReturnCar;
	}
	public void setIsReturnCar(Integer isReturnCar) {
		this.isReturnCar = isReturnCar;
	}
	public Integer getAddDriver() {
		return addDriver;
	}
	public void setAddDriver(Integer addDriver) {
		this.addDriver = addDriver;
	}
	public Integer getIsAbatement() {
		return isAbatement;
	}
	public void setIsAbatement(Integer isAbatement) {
		this.isAbatement = isAbatement;
	}
	public Integer getIsEffective() {
		return isEffective;
	}
	public void setIsEffective(Integer isEffective) {
		this.isEffective = isEffective;
	}
	public Integer getIsUseSpecialPrice() {
		return isUseSpecialPrice;
	}
	public void setIsUseSpecialPrice(Integer isUseSpecialPrice) {
		this.isUseSpecialPrice = isUseSpecialPrice;
	}
	public Integer getIsCancle() {
		return isCancle;
	}
	public void setIsCancle(Integer isCancle) {
		this.isCancle = isCancle;
	}
	public String getChangeSource() {
		return changeSource;
	}
	public void setChangeSource(String changeSource) {
		this.changeSource = changeSource;
	}
	public LocalDateTime getReqAcceptTime() {
		return reqAcceptTime;
	}
	public void setReqAcceptTime(LocalDateTime reqAcceptTime) {
		this.reqAcceptTime = reqAcceptTime;
	}
	public LocalDateTime getCreateTime() {
		return createTime;
	}
	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
	public String getCreateOp() {
		return createOp;
	}
	public void setCreateOp(String createOp) {
		this.createOp = createOp;
	}
	public LocalDateTime getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateOp() {
		return updateOp;
	}
	public void setUpdateOp(String updateOp) {
		this.updateOp = updateOp;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	public String getChangeReason() {
		return changeReason;
	}
	public void setChangeReason(String changeReason) {
		this.changeReason = changeReason;
	}

}
