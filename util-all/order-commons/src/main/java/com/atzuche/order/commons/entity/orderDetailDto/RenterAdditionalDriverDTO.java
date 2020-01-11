package com.atzuche.order.commons.entity.orderDetailDto;

import lombok.Data;

import java.io.Serializable;


/**
 * 租客端附加驾驶人信息表
 * 
 * @author ZhangBin
 * @date 2019-12-28 15:51:58
 * @Description:
 */
@Data
public class RenterAdditionalDriverDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 子订单号
	 */
	private String renterOrderNo;
    /**
     * 附加驾驶人ID
     */
	private String driverId;
	/**
	 * 附加驾驶人姓名
	 */
	private String realName;
	/**
	 * 附加驾驶人电话号码
	 */
	private String phone;
	/**
	 * 是否有效
	 */
	private Integer isUse;
	/**
	 * 创建人
	 */
	private String createOp;

	/**
	 * 修改人
	 */
	private String updateOp;
	/**
	 * 0-正常，1-已逻辑删除
	 */
	private Integer isDelete;

}
