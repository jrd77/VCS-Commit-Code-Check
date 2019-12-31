package com.atzuche.delivery.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

/** 
 * @comments
 * @author  zg 
 * @date 2013年12月9日  
 */
public class Email {
	/*private static String HOST_NAME ="smtp.ym.163.com";
	private static String FROM_ADDR = "service@atzuche.com";
	private static String FROM_NAME = "AtzucheService";
	private static String FROM_PWD = "6BnXi5qZs8";*/
	
	private static String HOST_NAME ="smtp.qiye.163.com";
	private static String FROM_ADDR = "service@atzuche.com";
	private static String FROM_NAME = "AtzucheService";
	private static String FROM_PWD = "Ab135792468";

	/**
	 * 发送简单邮件
	 * @param to  目标邮箱
	 * @param subject
	 * @param msg
	 * @return
	 * @throws EmailException
	 */
	public static String sendSimpleEmail(String[] to, String subject, String msg) throws EmailException{
		SimpleEmail email = new SimpleEmail();
		email.setHostName(HOST_NAME);
		//email.setTLS(true);//email.set
		//email.setSslSmtpPort("");
		email.setAuthentication(FROM_ADDR, FROM_PWD);
		email.addTo(to);
		email.setFrom(FROM_ADDR, FROM_NAME);
		email.setSubject(subject);
		email.setMsg(msg); 
		return email.send();
	}
	
	public static String sendHtmlEmail(String to, String subject, String msg) throws EmailException{
		
		HtmlEmail email = new HtmlEmail();
		email.setHostName(HOST_NAME);
		email.setAuthentication(FROM_ADDR, FROM_PWD);
		email.addTo(to);
		email.setFrom(FROM_ADDR, FROM_NAME);
		email.setSubject(subject);
		email.setMsg(msg); 
		return email.send();
	}

	/**
	 * 满油补贴计算金额异常邮件
	 * @param toEmial
	 * @param subject
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String sendCsOilSubsidyRemindEmail(String[] toEmial,String[] toCcEmial,String subject,String content) throws Exception {
		HtmlEmail simpleEmail = new HtmlEmail();
		simpleEmail.setHostName(HOST_NAME);
		simpleEmail.setAuthentication(FROM_ADDR, FROM_PWD);
		simpleEmail.setFrom(FROM_ADDR, FROM_NAME);
		simpleEmail.addTo(toEmial);
		simpleEmail.addCc(toCcEmial);
		simpleEmail.setSubject(subject);
		simpleEmail.setMsg(content);
		return simpleEmail.send();
	}
	public static void main(String[] args) throws EmailException {
		String s = null;
		//sendSimpleEmail("smtp.126.com", "autoyol@126.com", "Me", "autoyol2013", "bruce198@163.com", "你好，", "邮件内容");
		s = sendSimpleEmail(new String[] {"yi.liu@atzuche.com"}, "你好，", "<a href=\"http://115.28.54.153:8080/autoyol/mem/resetPwd/308880462oGLmP4AS\" target=\"_blank\">http://115.28.54.153:8080/autoyol/mem/resetPwd/308880462oGLmP4AS</a>");
		System.out.println(s);
		System.out.println("done!");
	}

}
 