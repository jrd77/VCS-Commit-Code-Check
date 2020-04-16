package com.atzuche.order.owner.mem.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;


/**
 * 车主会员概览表
 * 
 * @author ZhangBin
 * @date 2019-12-25 11:59:31
 * @Description:
 */
@Data
public class OwnerMemberEntity implements Serializable {
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