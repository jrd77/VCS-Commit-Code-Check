package com.atzuche.order.admin.vo.req.log;

import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/15 3:13 下午
 **/
@Data
@ToString
public class LogQueryVO {
    private String orderNo;
    private String startTime;
    private String endTime;
    private Integer opTypeCode;
    private String content;
    private String operatorName;

}
