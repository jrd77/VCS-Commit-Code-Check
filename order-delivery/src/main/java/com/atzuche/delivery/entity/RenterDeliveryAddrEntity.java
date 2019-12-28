package com.atzuche.delivery.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客配送地址表
 *
 * @author 胡春林
 * @date 2019-12-28 15:57:03
 * @Description:
 */
@Data
public class RenterDeliveryAddrEntity implements Serializable {
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
	 * 预计取车地址
	 */
	private String expGetCarAddr;
	/**
	 * 预计还车地址
	 */
	private String expReturnCarAddr;
	/**
	 * 预计取车经度
	 */
	private String expGetCarLon;
	/**
	 * 预计取车维度
	 */
	private String expGetCarLat;
	/**
	 * 预计还车经度
	 */
	private String expReturnCarLon;
	/**
	 * 预计还车纬度
	 */
	private String expReturnCarLat;
	/**
	 * 实际取车地址
	 */
	private String actGetCarAddr;
	/**
	 * 实际还车地址
	 */
	private String actReturnCarAddr;
	/**
	 * 实际取车经度
	 */
	private String actGetCarLon;
	/**
	 * 实际取车维度
	 */
	private String actGetCarLat;
	/**
	 * 实际还车经度
	 */
	private String actReturnCarLon;
	/**
	 * 实际还车纬度
	 */
	private String actReturnCarLat;
	/**
	 * 取车人姓名
	 */
	private String getCarUserName;
	/**
	 * 取车人电话
	 */
	private String getCarUserPhone;
	/**
	 * 还车人姓名
	 */
	private String returnCarUserName;
	/**
	 * 还车人电话
	 */
	private String returnCarUserPhone;
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
