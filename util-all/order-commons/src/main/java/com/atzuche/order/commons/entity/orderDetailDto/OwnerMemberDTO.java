package com.atzuche.order.commons.entity.orderDetailDto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 车主会员概览表
 * 
 * @author ZhangBin
 * @date 2020-01-08 20:40:42
 * @Description:
 */
public class OwnerMemberDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
		/**
         * 主订单号
         */
        private String orderNo;
    	/**
         * 子订单号
         */
        private String ownerOrderNo;
    	/**
         * 会员号
         */
        private String memNo;
    	/**
         * 会员类型
         */
        private Integer memType;
    	/**
         * 手机号
         */
        private String phone;
    	/**
         * 头像
         */
        private String headerUrl;
    	/**
         * 真实姓名
         */
        private String realName;
    	/**
         * 昵称
         */
        private String nickName;
    	/**
         * 成功下单次数
         */
        private Integer orderSuccessCount;
    	/**
         * 平台上架车辆数
         */
        private Integer renterCarCount;
		/**
		 * 身份证号码
		 */
		private String idNo;
        /**
         * 车辆等级
         */
        private Integer carChargeLevel;
		public String getOrderNo() {
			return orderNo;
		}
		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}
		public String getOwnerOrderNo() {
			return ownerOrderNo;
		}
		public void setOwnerOrderNo(String ownerOrderNo) {
			this.ownerOrderNo = ownerOrderNo;
		}
		public String getMemNo() {
			return memNo;
		}
		public void setMemNo(String memNo) {
			this.memNo = memNo;
		}
		public Integer getMemType() {
			return memType;
		}
		public void setMemType(Integer memType) {
			this.memType = memType;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getHeaderUrl() {
			return headerUrl;
		}
		public void setHeaderUrl(String headerUrl) {
			this.headerUrl = headerUrl;
		}
		public String getRealName() {
			return realName;
		}
		public void setRealName(String realName) {
			this.realName = realName;
		}
		public String getNickName() {
			return nickName;
		}
		public void setNickName(String nickName) {
			this.nickName = nickName;
		}
		public Integer getOrderSuccessCount() {
			return orderSuccessCount;
		}
		public void setOrderSuccessCount(Integer orderSuccessCount) {
			this.orderSuccessCount = orderSuccessCount;
		}
		public Integer getRenterCarCount() {
			return renterCarCount;
		}
		public void setRenterCarCount(Integer renterCarCount) {
			this.renterCarCount = renterCarCount;
		}
		public String getIdNo() {
			return idNo;
		}
		public void setIdNo(String idNo) {
			this.idNo = idNo;
		}
		public Integer getCarChargeLevel() {
			return carChargeLevel;
		}
		public void setCarChargeLevel(Integer carChargeLevel) {
			this.carChargeLevel = carChargeLevel;
		}
        
        
}
