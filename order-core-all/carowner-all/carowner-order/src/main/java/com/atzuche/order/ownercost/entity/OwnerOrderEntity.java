package com.atzuche.order.ownercost.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 车主订单子表
 * 
 * @author ZhangBin
 * @date 2020-01-01 17:08:51
 * @Description:
 */
@Data
@ToString
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
     * 租客子订单号
     */
    private String renterOrderNo;
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
	 * 车主子单状态，1-待补付,2-修改待确认,3-进行中,4-已完结,5-已结束
	 */
	private Integer childStatus;
	/**
	 * 修改方 1、后台管理 2、租客 3、车主
	 */
	private String changeSource;
	/**
	 * 是否取消 0-正常，1-取消
	 */
	private Integer isCancel;
	/**
	 * 是否有效 1-有效 0-无效
	 */
	private Integer isEffective;
    /**
     * 订单是否查看,1:是，0：否
     */
    private Integer seeFlag;
	/**
	 * 是否使用特供价 0-否，1-是
	 */
	private Integer isUseSpecialPrice;
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
    /**
     * 调度原因
     */
	private String dispatchReason;
	/**
	 * 取车提前分钟
	 */
	private Integer beforeMinutes;
	/**
	 * 还车延后分钟
	 */
	private Integer afterMinutes;

}
