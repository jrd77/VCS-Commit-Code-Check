package com.atzuche.order.renterwz.entity;

import lombok.Data;

import java.util.Date;

/**
 * RenterOrderWzStatusEntity
 *
 * @author shisong
 * @date 2019/12/28
 */
@Data
public class RenterOrderWzStatusEntity{
	/**
	*主键
	*/
	private Long id;
	/**
	*主订单号
	*/
	private String orderNo;
	/**
	*车牌
	*/
	private String carPlateNum;
	/**
	 * 租客会员号
	 */
	private String renterNo;
	/**
	 * 车主会员号
	 */
	private String ownerNo;
	/**
	 * 车辆好
	 */
	private String carNo;
	/**
	*处理状态 5:未处理，10(待租客处理)，20(已处理-无违章)，25(处理中-租客处理)，26(处理中-车主处理)，35(已处理-异常订单)，40(处理中-平台处理)，45(已处理-无信息) 46(处理中-无数据) 50(已处理-租客处理) 51(已处理-车主处理)
	*/
	private Integer status;
	/**
	 * 违章查询:1未查询、2查询失败、3已查询-无违章、4已查询-有违章、5历史数据
	 */
	private Integer illegalQuery;
	/**
	 * 违章罚金处理方式:1:租客自行办理违章 2:凹凸代为办理违章 3:车主自行办理 4:无数据
	 */
	private Integer managementMode;
	/**
	*描述信息
	*/
	private String statusDesc;
	/**
	 * 违章处理办理完成时间
	 */
	private Date wzHandleCompleteTime;
	/**
	*创建时间
	*/
	private Date createTime;
	/**
	*创建人
	*/
	private String createOp;
	/**
	*修改时间
	*/
	private Date updateTime;
	/**
	*修改人
	*/
	private String updateOp;
	/**
	*0-正常，1-已逻辑删除
	*/
	private Integer isDelete;
}
