package com.atzuche.order.detain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客端暂扣处理状态表
 * 
 * @author ZhangBin
 * @date 2020-02-11 17:14:12
 * @Description:
 */
@Data
public class RenterEventDetainStatusEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private Integer id;
    /**
     * 会员号
     */
    private String memNo;
	/**
	 * 主订单号
	 */
	private String orderNo;
	/**
	 * 子订单号
	 */
	private String renterOrderNo;
	/**
	 * 处理状态 处理状态 1：暂扣 2：取消暂扣
	 */
	private Integer status;
	/**
	 * 描述信息
	 */
	private String statusDesc;
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
     * 更新版本号
     */
    private Integer version;
}