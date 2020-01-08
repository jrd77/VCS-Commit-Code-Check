package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.service.ShortUrlService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.renterwz.dto.RenterWzPushMessageBody;
import com.atzuche.order.renterwz.entity.WzQueryDayConfEntity;
import com.atzuche.order.renterwz.service.RenterOrderWzDetailService;
import com.atzuche.order.renterwz.service.TransIllegalSendAliYunMq;
import com.atzuche.order.renterwz.service.WzQueryDayConfService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * JPushService
 *
 * @author shisong
 * @date 2020/1/2
 */
@Service
public class JPushService {

	private static final String SMS_TYPE_RENTER = "1";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private OrderService orderService;

	@Resource
	private WzQueryDayConfService wzQueryDayConfService;
	
	@Value("${com.autoyol.sms.jpushUrl}")
	private String jpushUrl;
	
	@Value("${com.autoyol.sms.appServerUrl}")
	private String appServerUrl;
	
	@Value("${com.autoyol.sms.illgalUrl}")
	private String illgalUrl;
    @Value("${com.autoyol.sms.illgalDetailAppUrl}")
    private String illgalDetailAppUrl;

	@Resource
	private ShortUrlService shortUrlService;

	@Resource
	private RenterOrderWzDetailService renterOrderWzDetailService;

	@Resource
	private SendPlatformSmsService sendPlatformSmsService;

	@Resource
	private TransIllegalSendAliYunMq transIllegalSendAliYunMq;

	private String jpushContentTxt = "您的订单（订单号：#{orderNo}）查出有新的违章，立即点击查看。";

	/**
	 * 租客的违章
	 */
	@Value("${renter_wz_renter_message}")
	private String contentTxt;

	@Value("${renter_wz_owner_message}")
	private String ownerContentTxt;

	@Value("${renter_wz_ctrip_message}")
	private String ctripContentTxt;

	@Value("${renter_wz_push_event}")
	private String renterWzPushEvent;

	/**
	 * 发送违章短信、推送
	 *  @param orderNo 订单号
	 * @param renterNo 会员号
	 * @param renterPhone 手机号
	 * @param type 1:租客 2:车主
	 * @param isCtripOrder 是否是携程订单
	 */
	public void updateIllegalMessage(String orderNo, String renterNo, String renterPhone, String type, boolean isCtripOrder) {
		Map<String, Object> paramMap = new HashMap<>(4);
		RenterWzPushMessageBody renterWzPushMessageBody = new RenterWzPushMessageBody();
		if (isCtripOrder && SMS_TYPE_RENTER.equals(type)) {
			paramMap.put("textCode", ctripContentTxt);
		} else {
			if(SMS_TYPE_RENTER.equals(type)) {
				paramMap.put("textCode", contentTxt);
			}else {
				String cityCode = "";
				OrderEntity order = orderService.getOrderEntity(orderNo);
				if(order != null){
					cityCode = order.getCityCode();
				}
				//获取城市违章天数配置
				List<WzQueryDayConfEntity> configs = wzQueryDayConfService.queryAll();
				Integer dealDays = 33;
				if(configs != null && configs.size() > 0) {
					for(WzQueryDayConfEntity illegalQueryDayConf : configs) {
						Integer nowCity = illegalQueryDayConf.getCityCode();
						if(nowCity != null && nowCity.equals(111111)) {
							dealDays = illegalQueryDayConf.getTransProcessDay();
						}
						if(StringUtils.isNotBlank(cityCode) && cityCode.equals(String.valueOf(nowCity))) {
							dealDays = illegalQueryDayConf.getTransProcessDay();
							break;
						}
					}
				}
				paramMap.put("textCode", ownerContentTxt);
				paramMap.put("dealDays",dealDays);
			}
		}
		String url = appServerUrl + illgalUrl + "?type=1001&orderNo=" + orderNo;
		paramMap.put("orderNo",String.valueOf(orderNo));
		paramMap.put("url",shortUrlService.getShortUrlNew(url));

		logger.info("发送orderNo is {}违章短信成功", orderNo);
		sendPlatformSmsService.sendNormalSms(paramMap);
		renterWzPushMessageBody.setOrderNo(orderNo);
		renterWzPushMessageBody.setMemNo(renterPhone);
		renterWzPushMessageBody.setEvent(renterWzPushEvent);
		transIllegalSendAliYunMq.sendPushMsg(renterWzPushMessageBody);
		if (SMS_TYPE_RENTER.equals(type)) {
			renterOrderWzDetailService.updateSmsStatus(orderNo);
		} else {
			renterOrderWzDetailService.updateOwnerSmsStatus(orderNo);
		}
	}
	
}
