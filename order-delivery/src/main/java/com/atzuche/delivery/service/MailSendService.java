package com.atzuche.delivery.service;

import com.atzuche.delivery.utils.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * @author yi.liu
 */
@Service
public class MailSendService {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(MailSendService.class);

    @Autowired
    @Qualifier("smsExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    public void sendSimpleEmail(String[] tos, String subject, String content) {
        taskExecutor.execute(() -> {
            try {
                Email.sendSimpleEmail(tos, subject, content);
            } catch (Exception e) {
                logger.error("发用邮件失败:subject:{}", subject, e);
            }
        });
    }
}
