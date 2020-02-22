package com.atzuche.order.coreapi.filter;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.OrderReqContext;
import com.atzuche.order.commons.filter.OrderFilter;
import com.atzuche.order.commons.filter.OrderFilterException;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import org.springframework.stereotype.Service;
/**
 * @ Author        :  ZhangBin
 * @ CreateDate    :  2019/10/14 16:34
 * @ Description   :  15分钟内重复下单校验
 *
 */
@Service("waitOwner15minuteReplyFilter")
public class WaitOwner15minuteReplyFilter implements OrderFilter {
    @Override
    public void validate(OrderReqContext context) throws OrderFilterException {

    }
}
