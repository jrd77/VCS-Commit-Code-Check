package com.atzuche.order.rentercost.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 凹凸币信息
 *
 * @author pengcheng.fu
 * @date 2019/12/26 20:37
 */
@Data
public class CrmCustPointDTO implements Serializable {

    private static final long serialVersionUID = -7709605902575132956L;

    private Integer id;
	/** CRM客户ID **/
	private String custId;
	/** 会员号 **/
	private String memNo;
	/** 积分值 **/
	private Integer pointValue;
	/** 积分编号 **/
	private String pointNo;
	/**备注**/
	private String remark;

}

