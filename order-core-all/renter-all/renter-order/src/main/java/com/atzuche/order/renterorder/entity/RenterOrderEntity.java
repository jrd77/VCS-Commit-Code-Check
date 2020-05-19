package com.atzuche.order.renterorder.entity;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 租客订单子表
 * 
 * @author ZhangBin
 * @date 2019-12-24 14:23:11
 * @Description:
 */
@Data
@ToString
public class RenterOrderEntity implements Serializable {
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
     * 租客会员号
     */
	private String renterMemNo;
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
	 * 车主是否同意 0-未同意，1-已同意
	 */
	private Integer agreeFlag;
	/**
	 * 租客子单状态，1：进行中，2：已完结，3：已结束
	 */
	private Integer childStatus;
	/**
	 * 是否使用凹凸币 0-否，1-是
	 */
	private Integer isUseCoin;
	/**
	 * 是否使用钱包 0-否，1-是
	 */
	private Integer isUseWallet;
	/**
	 * 是否使用优惠券 0-否，1-是
	 */
	private Integer isUseCoupon;
	/**
	 * 是否使用取车服务 0-否，1-是
	 */
	private Integer isGetCar;
	/**
	 * 是否使用还车服务 0-否，1-是
	 */
	private Integer isReturnCar;
	/**
	 * 附加驾驶人（人数）
	 */
	private Integer addDriver;
	/**
	 * 是否开启不计免赔 0-不开启，1-开启
	 */
	private Integer isAbatement;
	/**
	 * 是否有效 1-有效 0-无效
	 */
	private Integer isEffective;
	/**
	 * 是否使用特供价 0-否，1-是
	 */
	private Integer isUseSpecialPrice;
	/**
	 * 是否取消 0-正常，1-取消
	 */
	private Integer isCancle;
	/**
	 * 修改方 1、后台管理 2、租客 3、车主
	 */
	private String changeSource;
	/**
	 * 修改原因
	 */
	private String changeReason;
    /**
     * 车主同意请求时间
     */
	private LocalDateTime reqAcceptTime;

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
     * 订单是否查看,1:是，0：否
     */
    private Integer seeFlag;
    
    /**
     * 需补付金额
     */
    private Integer supplementAmt;

}
