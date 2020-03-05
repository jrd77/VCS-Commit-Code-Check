package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 租客订单变更申请表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:24:31
 * @Description:
 */
@Data
public class RenterOrderChangeApplyDTO implements Serializable {
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
	 * 租客子订单号
	 */
	private String renterOrderNo;
	/**
	 * 车主子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 是否修改起租时间 0-否，1-是
	 */
	private Integer rentTimeFlag;
	/**
	 * 申请类型（租期修改、取还车地址修改）
	 */
	private Integer applyType;
	/**
	 * 租期修改前时间段
	 */
	private String rentBeforeTime;
	/**
	 * 租期修改后时间段
	 */
	private String rentAfterTime;
	/**
	 * 取车修改前地址
	 */
	private String getCarBeforeAddr;
	/**
	 * 取车修改后地址
	 */
	private String getCarAfterAddr;
	/**
	 * 取车修改前经度
	 */
	private String getCarBeforeAddrLon;
	/**
	 * 取车修改前维度
	 */
	private String getCarBeforeAddrLat;
	/**
	 * 取车修改后经度
	 */
	private String getCarBeforeAfterLon;
	/**
	 * 取车修改后维度
	 */
	private String getCarBeforeAfterLat;
	/**
	 * 还车修改前地址
	 */
	private String returnCarBeforeAddr;
	/**
	 * 还车修改后地址
	 */
	private String returnCarAfterAddr;
	/**
	 * 还车修改前经度
	 */
	private String returnCarBeforeAddrLon;
	/**
	 * 还车修改前维度
	 */
	private String returnCarBeforeAddrLat;
	/**
	 * 还车修改后经度
	 */
	private String returnCarBeforeAfterLon;
	/**
	 * 还车修改后维度
	 */
	private String returnCarBeforeAfterLat;
	/**
	 * 申请时间
	 */
	private LocalDateTime applyTime;
	/**
	 * 审核状态
	 */
	private Integer auditStatus;
	/**
	 * 审核时间
	 */
	private LocalDateTime auditTime;
	/**
	 * 发起申请人 1-租客、2-车主、3-后台
	 */
	private Integer applySource;


}
