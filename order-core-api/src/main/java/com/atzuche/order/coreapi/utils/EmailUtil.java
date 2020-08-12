package com.atzuche.order.coreapi.utils;

import com.atzuche.order.coreapi.common.EmailCommon;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

/**
 * 邮件发送Util
 *
 * @author pengcheng.fu
 * @date 2020/7/31 15:15
 */
public class EmailUtil {

    public static String HOST_NAME = "smtp.qiye.163.com";
    public static String FROM_ADDR = "service@atzuche.com";
    public static String FROM_NAME = "AtzucheService";
    public static String FROM_PWD = "Ab135792468";

    /**
     * 发送简单邮件
     *
     * @param common  邮箱参数
     * @param to      目标邮箱
     * @param subject 邮件主题
     * @param msg     邮件内容
     * @return String
     */
    public static String sendSimpleEmail(EmailCommon common, String[] to, String subject, String msg) throws EmailException {
        SimpleEmail email = new SimpleEmail();
        email.setHostName(common.getHostName());
        email.setAuthentication(common.getFromAddr(), common.getFromPwd());
        email.addTo(to);
        email.setFrom(common.getFromAddr(), common.getFromName());
        email.setSubject(subject);
        email.setMsg(msg);
        return email.send();
    }

    /**
     * 发送简单邮件(html)
     *
     * @param common  邮箱参数
     * @param to      目标邮箱
     * @param subject 邮件主题
     * @param msg     邮件内容
     * @return String
     */
    public static String sendHtmlEmail(EmailCommon common, String to, String subject, String msg) throws EmailException {

        HtmlEmail email = new HtmlEmail();
        email.setHostName(common.getHostName());
        email.setAuthentication(common.getFromAddr(), common.getFromPwd());
        email.addTo(to);
        email.setFrom(common.getFromAddr(), common.getFromName());
        email.setSubject(subject);
        email.setMsg(msg);
        return email.send();
    }


    public static void main(String[] args) throws EmailException {
        EmailCommon common = new EmailCommon();
        common.setHostName(HOST_NAME);
        common.setFromAddr(FROM_ADDR);
        common.setFromName(FROM_NAME);
        common.setFromPwd(FROM_PWD);

        String s = sendSimpleEmail(common, new String[]{"pengcheng.fu@atzuche.com"}, "你好，", "<a href=\"http://115.28.54" +
                ".153:8080/autoyol/mem/resetPwd/308880462oGLmP4AS\" target=\"_blank\">http://115.28.54.153:8080/autoyol/mem/resetPwd/308880462oGLmP4AS</a>");
        System.out.println(s);
        System.out.println("done!");
    }

}
 