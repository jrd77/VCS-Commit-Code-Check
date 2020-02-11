package com.atzuche.order.commons.vo.res.rentcosts;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.autoyol.doc.annotation.AutoDocProperty;

import lombok.Data;


/**
 * 订单补付表
 * 
 * @author ZhangBin
 * @date 2019-12-27 10:18:00
 * @Description:
 */
@Data
public class OrderSupplementDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 自增长序列号
	 */
	@AutoDocProperty(value="id")
	private Integer id;
	/**
	 * 订单号
	 */
	@AutoDocProperty(value="订单号")
	private String orderNo;
	/**
	 * 会员号
	 */
	@AutoDocProperty(value="会员号")
	private String memNo;
	
	@AutoDocProperty(value="请款码")
	private String requestPayCode;
	/**
	 * 费用编码
	 */
	@AutoDocProperty(value="费用编码")
	private String cashNo;
	/**
	 * 补付名称
	 */
	@AutoDocProperty(value="补付名称")
	private String title;
	/**
	 * 单项补付金额
	 */
	@AutoDocProperty(value="单项补付金额")
	private Integer amt;
	/**
	 * 备注
	 */
	@AutoDocProperty(value="备注")
	private String remark;
	/**
	 * 操作状态:0,待提交 1,已生效 2,已失效 3,已撤回
	 */
	private Integer opStatus;
	/**
	 * 补付类型:1,系统创建 2,手动创建
	 */
	private Integer supplementType;
	/**
	 * 操作类型:1,修改订单 2,车管家录入 3,租车押金结算 4,违章押金结算 5,手动添加
	 */
	private Integer opType;
	/**
	 * 支付状态:0.无需支付 1.未支付 2.已取消 3.已支付 4.支付中，5.支付失败 10.租车押金结算抵扣  20.违章押金结算抵扣
	 */
	private Integer payFlag;
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
