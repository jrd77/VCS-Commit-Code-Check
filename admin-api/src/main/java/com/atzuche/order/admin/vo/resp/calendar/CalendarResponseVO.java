package com.atzuche.order.admin.vo.resp.calendar;

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
public class CalendarResponseVO implements Serializable{
	@AutoDocProperty(value="日期")
	private String date;

	@AutoDocProperty(value="类型")
	private String type;

	@AutoDocProperty(value="价格")
	private String price;

}
