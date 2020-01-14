package com.atzuche.order.admin.mapper.log;

import lombok.Data;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/14 3:50 下午
 **/
@Data
public class QueryVO {
    private String orderNo;
    private String operator;
    private String content;
    private String subType;

}
