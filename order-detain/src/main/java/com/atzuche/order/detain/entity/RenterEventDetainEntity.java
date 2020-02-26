package com.atzuche.order.detain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;


/**
 * 租客端暂扣处理事件表
 * 
 * @author ZhangBin
 * @date 2020-02-11 17:14:12
 * @Description:
 */
@Data
public class RenterEventDetainEntity implements Serializable {
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
	 * 事件类型
	 */
	private Integer eventType;
	/**
	 * 事件名称
	 */
	private String eventName;
	/**
	 * 事件描述
	 */
	private String eventDesc;
	/**
	 * 暂扣部门
	 */
	private String deptName;
	/**
	 * 暂扣部门ID
	 */
	private Integer deptId;
	/**
	 * 冻结状态:1-冻结 0:未冻结
	 */
	private Integer freezeStatus;
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
