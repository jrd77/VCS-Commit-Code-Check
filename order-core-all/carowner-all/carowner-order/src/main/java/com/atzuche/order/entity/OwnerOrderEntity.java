package com.atzuche.order.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 车主订单子表
 * 
 * @author ZhangBin
 * @date 2019-12-11 18:07:01
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
	private Long orderNo;
	/**
	 * 子订单号
	 */
	private String ownerOrderNo;
	/**
	 * 预计起租时间
	 */
	private LocalDateTime expRentStartTime;
	/**
	 * 预计还车时间
	 */
	private LocalDateTime expRentEndTime;
	/**
	 * 实际起租时间
	 */
	private LocalDateTime actRentStartTime;
	/**
	 * 实际还车时间
	 */
	private LocalDateTime actRentEndTime;
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
