package com.atzuche.order.coreapi.service;

import com.atzuche.order.coreapi.entity.vo.ExceptionEmailServerVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *<p>Title:ExceptionEmailService</p>
 *<p>Description:系统中出现异常发送email服务</p>
 *<p>Company:atzuche</p>
 * @author zhiping.li
 * @date 2015年11月18日上午11:22:49
 */
@Service
public class ExceptionEmailService{

	@Value("${exception.email.hostName}")
    private String hostName;
	@Value("${exception.email.fromAddr}")
    private String fromAddr;
	@Value("${exception.email.fromName}")
    private String fromName;
	@Value("${exception.email.mailPassword}")
    private String mailPassword;

	/**
	 * 
	 *@Title: getEmailServer 
	 *@Description: 获取异常email服务发送信息
	 *@param 
	 *@return ExceptionEmailServerVo 
	 *@throws 
	 *@auther zhiping.li
	 *@date 2015年11月25日下午2:13:51
	 */
	public ExceptionEmailServerVo getEmailServer(){
		ExceptionEmailServerVo emailServerVo = new ExceptionEmailServerVo();
		emailServerVo.setHostName(hostName);
		emailServerVo.setFromAddr(fromAddr);
		emailServerVo.setFromName(fromName);
		emailServerVo.setFromPwd(mailPassword);
		return emailServerVo;
	}

}
