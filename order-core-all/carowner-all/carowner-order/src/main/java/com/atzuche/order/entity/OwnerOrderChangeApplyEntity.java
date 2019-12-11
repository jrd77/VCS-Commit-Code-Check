package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客变更订单申请表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:07:01
 * @Description:
 */
@Data
public class OwnerOrderChangeApplyEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private Integer orderNo;
	/**
	 * 租客子订单号
	 */
	private Integer renterOrderNo;
	/**
	 * 车主子订单号
	 */
	private Integer ownerOrderNo;
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
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	private String createOp;
	/**
	 * 修改时间
	 */
	private LocalDateTime updateTime;
	/**
	 * 修改人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
