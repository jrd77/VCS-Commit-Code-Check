package com.atzuche.order.admin.vo.req.calendar;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by qincai.lin on 2019/12/30.
 */
@Data
@ToString
public class CalendarRequestVO implements Serializable {
    @AutoDocProperty(value = "车辆注册号")
    private String carNo;

}
