package com.atzuche.order.delivery.service;

import com.atzuche.order.delivery.utils.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yi.liu
 */
@Service
public class MailSendService {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(MailSendService.class);

    @Resource(name = "smsExecutor")
    private ThreadPoolTaskExecutor smsExecutor;

    public void sendSimpleEmail(String[] tos, String subject, String content) {
        smsExecutor.execute(() -> {
            try {
                Email.sendSimpleEmail(tos, subject, content);
            } catch (Exception e) {
                logger.error("发用邮件失败:subject:{}", subject, e);
            }
        });
    }
}
