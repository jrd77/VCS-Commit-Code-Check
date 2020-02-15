package com.atzuche.order.coreapi.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import com.atzuche.order.commons.CommonUtils;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * @comments 异常报警邮件
 * @author  zg 
 * @version v1.0 
 * @date 2015年4月27日 
 */
public class ExceptionGPSMail extends Thread {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private  String[] MAIL_LIST;
	
	private  String HOST_NAME;
	private  String FROM_ADDR;
	private  String FROM_NAME;
	private  String FROM_PWD;
	
	private Exception ex;
	private String title;
	private String content;

	public ExceptionGPSMail(String HOST_NAME,String FROM_ADDR,String FROM_NAME,String FROM_PWD,String[] MAIL_LIST){
		this.HOST_NAME = HOST_NAME;
		this.FROM_ADDR = FROM_ADDR;
		this.FROM_NAME = FROM_NAME;
		this.FROM_PWD = FROM_PWD;
		this.MAIL_LIST = MAIL_LIST;
	}
	/**
	 * 发送异常信息到邮箱
	 * @param ex
	 */
	public void send(Exception ex){
		this.ex = ex;
		this.start();
	}
	
	/**
	 * 发送异常信息（包括标题）到邮箱
	 * @param title
	 * @param ex
	 */
	public void send(String title, Exception ex){
		this.title = title;
		this.ex = ex;
		this.start();
	}
	
	/**
	 * 发送异常信息（包括标题、内容）到邮箱
	 * @param title
	 * @param content
	 * @param ex
	 */
	public void send(String title, String content, Exception ex){
		this.title = title;
		this.content = content;
		this.ex = ex;
		this.start();
	}
	
	/**
	 * 
	 * @param title
	 * @param content
	 * @param ex
	 * @param currThread 调用此方法的线程（Thread.currentThread(),获取此方法的调用者信息时传此参数）
	 */
	public void send(String title, String content, Exception ex, Thread currThread){
		this.title = title;
		this.content = content;
		try {
			StackTraceElement stack[] = currThread.getStackTrace();
			StackTraceElement invokerStack = stack[2];
			String className = invokerStack.getClassName();
			String methodName = invokerStack.getMethodName();
			this.content = className + "." + methodName + "invoke Error\n" + this.content;
		} catch (Exception e) {
			logger.error("",e);
		}
		this.start();
	}
	
	/**
	 * 发送错误信息（包括标题、内容）到邮箱
	 * @param title
	 * @param content
	 */
	public void send(String title, String content){
		this.title = title;
		this.content = content;
		this.start();
	}
	
	@Override
	public void run() {
		if(MAIL_LIST != null){
			String exceptionMsg = "";
			if(ex != null){
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				ex.printStackTrace(new PrintWriter(buf, true));
				exceptionMsg = buf.toString();
			}
			String localIp = CommonUtils.getLocalAddress();
			SimpleEmail simpleEmail = new SimpleEmail();
			simpleEmail.setHostName(HOST_NAME);
			String res = null;
			try {
				simpleEmail.setAuthentication(FROM_ADDR, FROM_PWD);
				simpleEmail.setFrom(FROM_ADDR, FROM_NAME);
				simpleEmail.addTo(MAIL_LIST);
				String subject = (title != null) ? "异常报警！" + title : "异常报警！";
				simpleEmail.setSubject(localIp + subject);
				String msg = (content != null) ? content + ":\n" + exceptionMsg : exceptionMsg;
				simpleEmail.setMsg(msg);
				simpleEmail.setCharset(StandardCharsets.UTF_8.name());
					res = simpleEmail.send();
			} catch (Exception e) {
				logger.error("",e); 	
			}
			logger.info("send mail response:{}",res);
		}
	}
}
 