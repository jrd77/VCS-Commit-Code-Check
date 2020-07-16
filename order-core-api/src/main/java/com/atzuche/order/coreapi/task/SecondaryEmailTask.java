/**
 * 
 */
package com.atzuche.order.coreapi.task;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.atzuche.order.commons.CatConstants;
import com.atzuche.order.coreapi.entity.OrderSecondOpenEntity;
import com.atzuche.order.coreapi.entity.vo.ExceptionEmailServerVo;
import com.atzuche.order.coreapi.service.ExceptionEmailService;
import com.atzuche.order.coreapi.service.ExceptionGPSMail;
import com.atzuche.order.coreapi.service.OrderSecondOpenService;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;

/**
 * @author jing.huang
 *
 */
@Component
@JobHandler("secondaryEmailTask")
public class SecondaryEmailTask extends IJobHandler {

	private Logger logger = LoggerFactory.getLogger(TransIllegalSettleTask.class);
	@Resource
    private ExceptionEmailService exceptionEmailService;
	//clsj@atzuche.com,jianfang.ye@atzuche.com,sys.center@atzuche.com,jing.huang@atzuche.com
    @Value("${second.openvir.emails}")
    private String emails; 
    @Autowired
    OrderSecondOpenService orderSecondOpenService;
    
	/**
	 * 以逗号分割,获取邮件名单列表
	 * @param emails
	 * @return
	 */
    private String[] exceptionEmail(String emails) {
        return emails.split(",");
    }
    
	@Override
	public ReturnT<String> execute(String arg0) throws Exception {
		Transaction t = Cat.getProducer().newTransaction(CatConstants.XXL_JOB_CALL, "二清车主未开户，");
		try {
			Cat.logEvent(CatConstants.XXL_JOB_METHOD, "SecondaryEmailTask.execute");
			Cat.logEvent(CatConstants.XXL_JOB_PARAM, null);
			logger.info("开始执行 二清车主未开户，  定时器");
			XxlJobLogger.log("开始执行 二清车主未开户， 定时器");
			
			/**
			 * 业务逻辑处理
			 */
			ExceptionEmailServerVo email = exceptionEmailService.getEmailServer();
            String[] emailsArray = this.exceptionEmail(emails);
            String title = "上行二清未开户提醒邮件";
            StringBuffer content = new StringBuffer();
            
            //组装内容
            List<OrderSecondOpenEntity> list = orderSecondOpenService.query();
            if(list != null && list.size() > 0) {
            	content.append("车辆号\t车牌\t车辆类型\t手机号码\t订单号\t起租时间\t还车时间\t触发点\t车主会员号\n");
            	//获取文本字符串
            	list.stream().forEach(x -> get(x,content));
	            //发送邮件
	            new ExceptionGPSMail(email.getHostName(),email.getFromAddr(),email.getFromName(),email.getFromPwd(),emailsArray).send(title,content.toString());
	            //邮件发送成功，修改flag
	            List<Integer> ids = list.stream().map(OrderSecondOpenEntity::getId).collect(Collectors.toList());
	            int index = orderSecondOpenService.updateFlag(ids);
	            logger.info("更新index=[{}],params=[{}]",index,ids.stream().map(x -> String.valueOf(x)).collect(Collectors.joining(",","(",")")));
	            
            }else {
            	logger.info("无订单内容发送");
            }
			
			logger.info("结束执行 二清车主未开户， ");
			XxlJobLogger.log("结束执行 二清车主未开户， ");
			t.setStatus(Transaction.SUCCESS);
			return SUCCESS;
		} catch (Exception e) {
			XxlJobLogger.log("执行 二清车主未开户， 异常:" + e);
			logger.error("执行 二清车主未开户， 异常", e);
			Cat.logError("执行 二清车主未开户， 异常", e);
			t.setStatus(e);
			return new ReturnT(FAIL.getCode(), e.toString());
		} finally {
			if (t != null) {
				t.complete();
			}
		}
	}
	
	/**
	 * 拼接字符串
	 * @param x
	 * @param content
	 * @return
	 */
	private Object get(OrderSecondOpenEntity x, StringBuffer content) {
		content.append(x.toString()+"\n");
		return x;
	}

}
