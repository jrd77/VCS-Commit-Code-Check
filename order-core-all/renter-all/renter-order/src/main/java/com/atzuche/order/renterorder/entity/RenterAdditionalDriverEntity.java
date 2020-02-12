package com.atzuche.order.renterorder.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;


/**
 * 租客端附加驾驶人信息表
 * 
 * @author ZhangBin
 * @date 2019-12-28 15:51:58
 * @Description:
 */
@Data
public class RenterAdditionalDriverEntity implements Serializable {
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
	 * 身份证
	 */
	private String idCard;

	/**
	 * 准驾车型
	 */
	private String driLicAllowCar;

	@AutoDocProperty(value = "驾驶证有效起始日期")
	/**
	 * 驾驶证有效起始日期
	 */
	private Date validityStartDate;

	/**
	 * 驾驶证有效终止日期
	 */
	private Date validityEndDate;
	/**
	 * 是否有效
	 */
	private Integer isUse;
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
