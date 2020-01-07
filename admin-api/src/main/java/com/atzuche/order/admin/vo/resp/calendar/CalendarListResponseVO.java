package com.atzuche.order.admin.vo.resp.calendar;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 用车的条件
 * 计算费用
 * @author xiaoxu.wang
 *
 *
 */
@Data
@ToString
public class CalendarListResponseVO implements Serializable{
	@AutoDocProperty(value="日历列表")
	private List<CalendarResponseVO> calendarList;


}
