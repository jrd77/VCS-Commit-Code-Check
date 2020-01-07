package com.atzuche.order.admin.entity;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;



@Data
@ToString
public class OrderRemarkOverviewEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@AutoDocProperty(value = "备注类型")
	private String remarkType;

	@AutoDocProperty(value = "备注数量")
	private String typeCount;

}
