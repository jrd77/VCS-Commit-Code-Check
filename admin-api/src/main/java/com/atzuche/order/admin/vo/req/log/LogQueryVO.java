package com.atzuche.order.admin.vo.req.log;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/15 3:13 下午
 **/
@Data
@ToString
public class LogQueryVO {
    @AutoDocProperty(value = "订单号")
    private String orderNo;
    @AutoDocProperty(value = "开始时间，格式是yyyyMMddHHmmss")
    private String startTime;
    @AutoDocProperty(value = "结束时间，格式是yyyyMMddHHmmss")
    private String endTime;
    @AutoDocProperty(value = "费用类型")
    private Integer opTypeCode;
    @AutoDocProperty(value = "操作内容")
    private String content;
    @AutoDocProperty(value = "操作人")
    private String operatorName;

}
