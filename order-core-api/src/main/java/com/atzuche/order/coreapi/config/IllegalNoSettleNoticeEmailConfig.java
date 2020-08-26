package com.atzuche.order.coreapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 违章押金结算失败发送邮件 配置
 *
 * @author pengcheng.fu
 * @date 2020/7/31 16:26
 */

@Configuration
public class IllegalNoSettleNoticeEmailConfig {

    @Value("${illegal.settle.fail.notice.host_name}")
    public String hostName;

    @Value("${illegal.settle.fail.notice.from_addr}")
    public String fromAddr;

    @Value("${illegal.settle.fail.notice.from_name}")
    public String fromName;

    @Value("${illegal.settle.fail.notice.from_pwd}")
    public String fromPwd;

    @Value("${illegal.settle.fail.notice.to_addr}")
    public String toAddrs;


}
