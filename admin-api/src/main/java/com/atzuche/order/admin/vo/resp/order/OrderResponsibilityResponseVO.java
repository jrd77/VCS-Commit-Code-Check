package com.atzuche.order.admin.vo.resp.order;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用车的条件
 * 计算费用
 * @author xiaoxu.wang
 *
 *
 */
@Data
@ToString
public class OrderResponsibilityResponseVO implements Serializable{
	@AutoDocProperty(value="租客申诉原因")
	private String renterAppealReason;

	@AutoDocProperty(value="车主申诉原因")
	private String ownerAppealReason;

	@AutoDocProperty(value="车主违约金")
	private String renterFine;

	@AutoDocProperty(value="租客违约金")
	private String ownerFine;


}
