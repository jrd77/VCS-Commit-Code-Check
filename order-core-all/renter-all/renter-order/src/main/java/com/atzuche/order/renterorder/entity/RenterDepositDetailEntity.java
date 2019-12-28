package com.atzuche.order.renterorder.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车辆押金详情
 * 
 * @author ZhangBin
 * @date 2019-12-28 15:50:13
 * @Description:
 */
@Data
public class RenterDepositDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
	/**
	 * 主订单号
	 */
	private String orderNoParent;
	/**
	 * 子订单号
	 */
	private String orderNoChild;
	/**
	 * X系数（押金计算使用）
	 */
	private Integer suggestTotal;
	/**
	 * 新车押金系数（押金计算使用）
	 */
	private Double newCarCoefficient;
	/**
	 * 品牌车押金系数（押金计算使用）
	 */
	private Double carSpecialCoefficient;
	/**
	 * 年份系数
	 */
	private Double yearCoefficient;
	/**
	 * 原始租车押金
	 */
	private Integer originalDepositAmt;
	/**
	 * 减免比例
	 */
	private Integer reductionRate;
	/**
	 * 减免押金金额
	 */
	private Integer reductionDepositAmt;
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
