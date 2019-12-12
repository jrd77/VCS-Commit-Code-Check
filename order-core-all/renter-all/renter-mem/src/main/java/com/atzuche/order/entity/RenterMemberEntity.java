package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客端会员概览表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:17:27
 * @Description:
 */
@Data
public class RenterMemberEntity implements Serializable {
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
	 * 子订单号
	 */
	private String renterOrderNo;
	/**
	 * 会员号
	 */
	private Integer memNo;
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
	 * 驾驶证初次领证日期
	 */
	private LocalDateTime certificationTime;
	/**
	 * 成功下单次数
	 */
	private Integer orderSuccessCount;
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
