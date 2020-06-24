package com.atzuche.order.commons.entity.dto;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 违章明细日志表
 * 
 * @author ZhangBin
 * @date 2020-06-08 14:23:43
 * @Description:
 */
@Data
public class RenterOrderWzDetailLogDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 订单号
	 */
	@AutoDocProperty(value = "订单号")
	private String orderNo;
	/**
	 * 违章序号
	 */
	@AutoDocProperty(value = "违章序号")
	private Long wzDetailId;
	/**
	 * 操作类型
	 */
	@AutoDocProperty(value = "操作类型")
	private Integer operateType;
	/**
	 * 内容
	 */
	@AutoDocProperty(value = "内容")
	private String content;
	/**
	 * 创建时间
	 */
	@AutoDocProperty(value = "创建时间")
	private LocalDateTime createTime;
	/**
	 * 创建人
	 */
	@AutoDocProperty(value = "创建人")
	private String createOp;
	/**
	 * 修改时间
	 */
	@AutoDocProperty(value = "操作时间")
	private LocalDateTime updateTime;

    @AutoDocProperty(value = "操作时间")
    private String updateTimeStr;
	/**
	 * 更新人
	 */
	@AutoDocProperty(value = "操作人")
	private String updateOp;

}
