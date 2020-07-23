package com.atzuche.order.commons.vo.rentercost;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Data;


/**
 * 租客费用明细表
 * 
 * @author ZhangBin
 * @date 2019-12-14 17:30:57
 * @Description:
 */
@Data
public class RenterOrderCostDetailEntity implements Serializable {
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
	 * 会员号
	 */
	private String memNo;
	/**
	 * 费用编码
	 */
	private String costCode;
	/**
	 * 费用描述
	 */
	private String costDesc;
	/**
	 * 开始时间
	 */
	private LocalDateTime startTime;
	/**
	 * 结束时间
	 */
	private LocalDateTime endTime;
	/**
	 * 单价
	 */
	private Integer unitPrice;
    /**
     * 原始单价
     */
    private Integer originalUnitPrice;

    /**
     * 单价(展示)
     */
    private Integer showUnitPrice;

	/**
	 * 份数
	 */
	private Double count;
	/**
	 * 总价
	 */
	private Integer totalAmount;
	/**
	 * 部门ID
	 */
	private Integer deptId;
	/**
	 * 部门名称
	 */
	private String deptName;
	/**
	 * 操作人ID
	 */
	private String operatorId;
	/**
	 * 操作人名称
	 */
	private String operator;
	/**
	 * 备注
	 */
	private String remark;
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
	
	public RenterOrderCostDetailEntity() {}
	
	public RenterOrderCostDetailEntity(String orderNo, String renterOrderNo, String memNo, String costCode,
			String costDesc, LocalDateTime startTime, LocalDateTime endTime, Integer unitPrice, Double count,
			Integer totalAmount) {
		this.orderNo = orderNo;
		this.renterOrderNo = renterOrderNo;
		this.memNo = memNo;
		this.costCode = costCode;
		this.costDesc = costDesc;
		this.startTime = startTime;
		this.endTime = endTime;
		this.unitPrice = unitPrice;
		this.count = count;
		this.totalAmount = totalAmount;
	}


    public Integer getShowUnitPrice() {
	    if(Objects.isNull(showUnitPrice)) {
	        return unitPrice;
        }
        return showUnitPrice;
    }

}
