package com.atzuche.order.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.util.Properties;

/**
 * @author pengcheng.fu
 * @version v1.0
 * @comments 邮件发送工具类
 * @date 2016年3月1日
 */
public class EmailUtils {

    private static Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    /**
     * 系统属性
     */
    private static Properties props = System.getProperties();

    /**
     * 邮件会话对象
     */
    private static Session session;

    /**
     * MIME邮件对象
     */
    private static MimeMessage mimeMsg;
    private static Multipart mp = null;
    private static final String HOST_NAME = "smtp.qiye.163.com";
    private static final String FROM_ADDR = "service@atzuche.com";
    private static final String FROM_NAME = "service@atzuche.com";
    private static final String FROM_PWD = "Ab135792468";

    private static final String SMTP = "smtp";

    /**
     * 发送邮件
     *
     * @param tos     收件人
     * @param copyTos 抄送人
     * @param subject 主题（标题）
     * @param content 正文(内容)
     * @throws Exception 异常
     */
    public static void sendMail(String[] tos, String[] copyTos, String subject, String content) throws Exception {
        // Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象
        logger.info("send a email to {}, copy to {}, that subject is {}", tos, copyTos, subject);
        BodyPart bp = setBodyPart(tos, subject, content);
        send(copyTos, bp);
    }

    /**
     * 发送邮件(带附件)
     *
     * @param tos     收件人
     * @param copyTos 抄送人
     * @param subject 主题（标题）
     * @param content 正文(内容)
     * @throws Exception 异常
     */
    public static void sendMailAttachment(String[] tos, String[] copyTos, String subject, String content, ByteArrayInputStream stream, String fileName, String type) throws Exception {
        // Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象
        BodyPart bp = setBodyPart(tos, subject, content);
        // 邮件内容部分2---附件1
        MimeBodyPart body1 = new MimeBodyPart();
        DataSource dataSource1 = new ByteArrayDataSource(stream, type);
        // 代表项目根目录下
        body1.setDataHandler(new DataHandler(dataSource1));
        // 中文附件名，解决乱码
        body1.setFileName(MimeUtility.encodeText(fileName));
        mp.addBodyPart(bp);
        send(copyTos, body1);
    }

    /**
     * 构建body
     *
     * @param tos
     * @param subject
     * @param content
     * @return
     * @throws MessagingException
     */
    private static BodyPart setBodyPart(String[] tos, String subject, String content) throws MessagingException {
        mp = new MimeMultipart();
        mimeMsg = new MimeMessage(session);
        session = Session.getDefaultInstance(props, null);
        session.setDebug(false);
        // 设置发信人
        mimeMsg.setFrom(new InternetAddress(FROM_ADDR));
        // 设置接收人
        InternetAddress[] sendTo = new InternetAddress[tos.length];
        for (int i = 0; i < tos.length; i++) {
            sendTo[i] = new InternetAddress(tos[i]);
        }
        mimeMsg.setRecipients(Message.RecipientType.TO, sendTo);
        // 设置主题
        mimeMsg.setSubject(subject);
        // 设置正文
        BodyPart bp = new MimeBodyPart();
        bp.setContent(content, "text/html;charset=utf-8");
        return bp;
    }

    /**
     * body追加部分
     *
     * @param copyTos
     * @param bp
     * @throws MessagingException
     */
    private static void send(String[] copyTos, BodyPart bp) throws MessagingException {
        mp.addBodyPart(bp);
        mimeMsg.setContent(mp);
        mimeMsg.saveChanges();
        // 发送邮件
        Transport transport = session.getTransport(SMTP);
        transport.connect(HOST_NAME, FROM_NAME, FROM_PWD);
        transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.TO));
        if (copyTos != null && copyTos.length > 0) {
            // 设置抄送人
            for (int i = 0; i < copyTos.length; i++) {
                mimeMsg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(copyTos[i]));
            }
            transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.CC));
        }
        transport.close();
    }

    public static void main(String... args) throws Exception {
        String[] tos = {"song.shi@atzuche.com"};
        String errorMsg = "123\n223";
        ByteArrayInputStream stream = new ByteArrayInputStream(errorMsg.getBytes());
        // sendMailAttachment(tos, null, "测试", "Dear ALL：<br><br>        ", stream, "文件.txt", "application/txt");
        /*String content = SendEmailService.getContent(list);
        System.out.println(content);
        sendMail(tos, null, "长租收款提醒", content);*/
    }

}