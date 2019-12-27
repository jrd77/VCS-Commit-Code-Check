package com.atzuche.order.ownercost.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主订单子表
 * 
 * @author ZhangBin
 * @date 2019-12-25 10:21:14
 * @Description:
 */
@Data
public class OwnerOrderEntity implements Serializable {
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
	 * 显示起租时间
	 */
	private LocalDateTime showRentTime;
	/**
	 * 显示还车时间
	 */
	private LocalDateTime showRevertTime;
	/**
	 * 预计起租时间
	 */
	private LocalDateTime expRentTime;
	/**
	 * 预计还车时间
	 */
	private LocalDateTime expRevertTime;
	/**
	 * 实际起租时间
	 */
	private LocalDateTime actRentTime;
	/**
	 * 实际还车时间
	 */
	private LocalDateTime actRevertTime;
	/**
	 * 商品编码
	 */
	private String goodsCode;
	/**
	 * 商品类型
	 */
	private String goodsType;
	/**
	 * 子单状态
	 */
	private Integer childStatus;
	/**
	 * 是否取消 0-正常，1-取消
	 */
	private Integer isCancle;
	/**
	 * 是否有效 1-有效 0-无效
	 */
	private Integer isEffective;
	/**
	 * 是否使用特供价 0-否，1-是
	 */
	private Integer isUseSpecialPrice;
	/**
	 * 取消原因
	 */
	private String cancleReason;
	/**
	 * 取消者
	 */
	private String cancleOp;
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
