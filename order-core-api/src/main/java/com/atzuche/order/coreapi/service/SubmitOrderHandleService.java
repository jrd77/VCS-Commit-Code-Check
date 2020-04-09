package com.atzuche.order.coreapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 提交订单数据落库操作
 * <p><font color=red>主订单、租客订单、车主订单、押金(违章押金、车辆押金)、违章信息初始化、还车记录初始化等</font></p>
 *
 * @author pengcheng.fu
 * @date 2020/4/9 18:10
 */

@Service
public class SubmitOrderHandleService {

    private static Logger logger = LoggerFactory.getLogger(SubmitOrderHandleService.class);



    @Transactional(rollbackFor = Exception.class)
    public void save() {



    }
}
