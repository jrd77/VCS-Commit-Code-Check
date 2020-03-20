package com.atzuche.order.delivery.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客端交车备注表
 *
 * @author 胡春林
 * @date 2019-12-28 15:56:17
 * @Description:
 */
@Data
public class RenterHandoverCarRemarkEntity implements Serializable {
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
	 * 交车类型 1-车主向租客交车、2-租客向车主交车、3-车管家向租客交车、4-租客向车管家交车
	 */
	private Integer type;
	/**
	 * 备注内容
	 */
	private String remark;
	/**
	 * 备注人手机号
	 */
	private String phone;
	/**
	 * 备注人会员号
	 */
	private String memNo;
	/**
	 * 备注人姓名
	 */
	private String realName;
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

	private String proId;

}
