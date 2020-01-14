package com.atzuche.order.photo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.ResourceBundle;

/**  
 * @Author zg 
 * @Date 2013-10-9
 * @Comments
 */
public class SysConf {


	/************************接口key********************************/
	public static final String SECRET_KEY = "E6iJPSXSxbC+pCwSvF3T6H0uVMkYCY1iwV9+MQtYv0evxsJOBj2K5+iDl+xwDgth2dIs3pdcvWa8BdMNDHVXtg==";
	/********************************************************/
	
	private static Logger logger = LoggerFactory.getLogger(SysConf.class);
	
	private static final String BUNDLE_NAME = "conf/conf";
	
	/** 1:正式环境，2：测试环境   */
	public static final String ENV = getEnv(); //getEnv();
	

	//app真格服务访问地址
    public static final String APP_SERVER_ADDRZG = getBundleString("app_server_addzg");

	//仁云取还车接口地址
    public static final String HROCLOUD_SERVER_URL = getBundleString("HrocloudServerUrl");
    //web 地址
    public static final String WEB_ADDR = getBundleString("web_addr");
    //H5地址
    public static final String H5_ADDR = getBundleString("h5_addr");
    //短链获取
    public static final String APP_SERVER_SHORTLINK = getBundleString("short_url");
    
	public static final String APP_SERVER_ADDR = getAppServerAddr();
	public static final String APP_TEMP_SERVER_ADDR =ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINA).getString("appTempServer");
	public static final String IM_CENTER_ADDR = getImCenterAddr();
	
	public static final String[] APP_SERVER_IPS = getAppServerIps();
	
	public static final String CON_SERVER_ADDR = getConServerAddr();
	public static final String CON_IMAGE_DIR = getConImageDir();
	
	//===================== OSS 相关配置  ========================
	/** OSS end point  */
	public static final String OSS_END_POINT = "http://oss-cn-hangzhou.aliyuncs.com"; 
	
	public static final String OSS_BASE_URL = "oss-cn-hangzhou.aliyuncs.com"; 
	
	public static final String OSS_BUCKET = getOSSBucket();
	public static final String OSS_BUCKET_AUTH = getOSSBucketAuth();
	public static final String OSS_BUCKET_CAR_AUTH = getOSSBucketCarAuth();
	
	//====================== 短信 ==================================
	public static final String SMS_MARKET_MSG_URL =  getBundleString("smsMarketMsgUrl");
	public static final String SMS_MARKET_MT_URL =getBundleString("smsMarketMtUrl");//短信营销
	public static final String SMS_ACCESS_KEY = getBundleString("smsAccessKey");
	
	//======================异常报警邮件发送地址 ==================================
	public static final String[] EXCEPTION_MAIL_LIST = getExceptionWarnMailList();

	//===================== 车辆审核通知URL ========================
	public static final String CAR_APPROVE_NOTIFY_URL = getApproveNotifyUrl();
	
	//===================== 人员审核通知URL ========================
	public static final String MEM_APPROVE_NOTIFY_URL = getMemApproveNotifyUrl();
	
	private SysConf(){}//私有构造函数（不允许实例化）

	//===================== 保险相关         ============================================================
	/** 保单存放目录  */
	public static final String INSUR_POLICY_PATH = getInsurPolicyPath(); 
	
	public static final String INTERFACE_CALLBACK_URL = getBundleString("interface_callback_url");
	
	//======================关于营销活动的Url========================================================
	public static final String BASE_EVENT_SHORT_URL =getBundleString("event_short_url");
	
	public static final String BASE_EVENT_CHANNEL_URL =getBundleString("event_channel_url");
	
	//======================= 后台找车之订单调度相关接口 地址==========================================
	public static final String TRANS_UPGRADE_DISPOSE_URL=getBundleString("trans_upgrade_dispose_url");
	public static final String COUPON_URL=getBundleString("coupon_url");
	public static final String CPIC_COUPON_SERVICE_URL=getBundleString("cpic_coupon_service_url");
	//======================= private method ==========================================
	public static  final String DEREN_REQ_URL =getBundleString("deren_Url");
	public static  final String DEREN_USERID =getBundleString("deren_userid");
	public static  final String DEREN_USERPWD =getBundleString("deren_userpwd");
	public static  final String DEREN_EMAIL =getBundleString("deren_email");
	public static  final String DEREN_COPYEMAIL =getBundleString("derenCopy_email");
	//========================MQ ==================//
	public static  final String MQ_ACCESS_KEY =getBundleString("mq_access_key");
	public static  final String MQ_ACCESS_ID =getBundleString("mq_access_id");
	public static  final String MQ_ACCESS_END_POINT =getBundleString("mq_access_end_point");
	public static  final String MQ_CRM_CAR_QUEUE_NAME =getBundleString("mq_crm_car_queue_name");
	public static  final String MQ_AUTO_UPDATE_MEMBER_QUEUE =getBundleString("mq_auto_update_member_queue");
	public static  final String MQ_AUTO_BLACKLIST_UPDATE_QUEUE =getBundleString("mq_auto_blacklist_update_queue");
	//mq_auto_hro_cpic_application_queue 代步车29期需求
	public static  final String MQ_AUTO_HRO_CPIC_APPLICATION_QUEUE =getBundleString("mq_auto_hro_cpic_application_queue");
	
	public static  final String MQ_AUTO_BEHAVIOR_QUEUE =getBundleString("mq_auto_behavior_queue");
	public static  final String MQ_AUTO_ORDER_SETTLE_RECORD_QUEUE =getBundleString("mq_auto_order_settle_record_queue");
	public static  final String MQ_AUTO_PLATFORM_MESSAGE_QUEUE =getBundleString("mq_auto_platform_message_queue");

	public static  final String MQ_AUTO_INVITE_GIFT_HANDLE_QUEUE = getBundleString("auto-invite-gift-handle-queue");
	
	public static final String SEARCH_INDEX_CAR_QUEUE = getBundleString("search_index_car_queue");
	public static final String SEARCH_INDEX_PACKAGE_QUEUE = getBundleString("search_index_package_queue");

	//========================太保车主招募=======================//
	public static final String CAR_OWNER_RECRUIT_CPIC_PATH=getBundleString("car_owner_recruit_cpic_path");
	//========================车辆检测 接收邮箱=======================//
	public static final String CARDETECT_EMAIL_TOS=getBundleString("cardetect_email_tos");

	// 上汽chexiang开放平台appKey
	public static final String CHEXIANG_API_APPKEY = getBundleString("chexiang.api.appKey");
	public static final String CHEXIANG_API_SECURITYKEY = getBundleString("chexiang.api.securityKey");
	public static final String CHEXIANG_API_FIND_ORDER_CHECK_URL = getBundleString("chexiang.api.find.order.check.url");
	public static final String AUTOCARPRICEURL =getBundleString("auto.car.price.url");

	public static final String HTML5_PACKAGE_PROMOTE_URL = getBundleString("html5_package_promote_url");


	public static final String HTML5_LONG_RENT_PROMOTE_LINK_URL = getBundleString("html5_long_rent_promote_link_url");
	public static final String APP_LONG_RENT_PROMOTE_LINK_URL = getBundleString("app_long_rent_promote_link_url");

	public static  final String MQ_AUTO_RENTER_VOUCHER_QUEUE =getBundleString("mq_auto_renter_voucher_queue");
	// 会员认证服务
	public static final String AUTO_MEMBER_AUTHENTICATION_URL = getBundleString("auto.member.authentication.url");
	
	public static  final String DADI_STATUS_TRANS_CANCEL =getBundleString("com.autoyol.mns.queue.dadi-status-trans-cancel");

	public static  final String AUTO_TIME_SHARE_TRANS_MODIFY_QUEUE =getBundleString("mq_auto_time_share_trans_modify_queue");
	// 上下线车需要刷新车辆缓存信息,推送队列
	public static final String CAR_CACHEABLE_REFRESH_MQ_QUEUE = getBundleString("car_cacheable_refresh_mq_queue");
	// 购买保险服务
	public static final String AUTO_INSURANCE_PURCHASE_URL = getBundleString("auto.insurance.purchase.url");
	
	//代步车链接(注意事项)
    public static final String CPIC_SHORT_URL = getBundleString("cpicShortUrl");
    //附赠套餐的链接
    public static final String CPICPKG_SHORT_URL = getBundleString("cpicPkgShortUrl");
    // 新管理后台地址
    public static final String CAR_INFO_MGMT_URL = getBundleString("carInfoMgmtUrl");
	/** 仁云接口异常，提醒邮箱 */
	public static final String HROCLOUD_NOTICE_EMAIL =getBundleString("hrocloud.notice.email");

	/** 礼品卡服务 **/
	public static final String AUTO_GIF_CARD_URL = getBundleString("auto.gift.card.url");

	public static final String REMIND_PUSH_URL = getBundleString("remindPushUrl");

	public static final String SHORT_SERVICE_URL = getBundleString("shortServiceUrl");
	/**pms 二线城市*/
	public static final String PMS_PACKAGE_ORDER_URL = getBundleString("auto.pms.package.order.url");

	private static String getRequestUrl(){
        if("1".equals(ENV)){
            //正式环境
            return "http://apps.aotuzuche.com:7064";
        }
        //测试环境
		return "http://10.0.3.203:8080";
    }
	
	private static String getApproveNotifyUrl(){
		if("1".equals(ENV)){
			return "http://120.55.245.190:9527/trans/addExcutor";//正式环境
		}
		return "http://115.29.233.175:9527/trans/addExcutor";//测试环境
	}
	
	private static String getAppServerAddr(){
		String webSite = ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINA).getString("appServer");
		if(StringUtils.hasText(webSite)){
			if(!webSite.endsWith("/")){
				webSite =  webSite + "/";
			}
			return webSite;
		}else{
			return "http://www.aotuzuche.com:7064/autoyol/";
		}
	}
	
	private static String[] getAppServerIps() {
		String appIps = ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINA).getString("appServerIps");
		return appIps.split(",");
	}
	
	private static String getConServerAddr(){
		String webSite = ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINA).getString("conServer");
		if(StringUtils.hasText(webSite)){
			if(!webSite.endsWith("/")){
				webSite =  webSite + "/";
			}
			return webSite;
		}else{
			return "http://www.aotuzuche.com:9031/autoyolConsole/";
		}
	}
	
	private static String getConImageDir(){
		String webSite = ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINA).getString("conImageDir");
		if(StringUtils.hasText(webSite)){
			if(!webSite.endsWith("/")){
				webSite =  webSite + "/";
			}
			return webSite;
		}else{
			return "/dd/autoCon/webapps/autoyolConsole/images/";
		}
	}

	/**
	 * “env”为 1 时返回 "at-images"，其他返回"at-images-test"
	 * @return
	 */
	private static String getOSSBucket(){
		if("1".equals(ENV)){
			//正式环境
			return "at-images";
		}
		//测试环境
		return "at-images-test";
	}
	
	/**
	 * “env”为 1 时返回 "veri-images"，其他返回"veri-images-test"
	 * @return
	 */
	private static String getOSSBucketAuth(){
		if("1".equals(ENV)){
			//正式环境
			return "veri-images";
		}
		//测试环境
		return "veri-images-test";
	}
	
	private static String getOSSBucketCarAuth(){
		if("1".equals(ENV)){
			//正式环境
			return "veri-images-car";
		}
		//测试环境
		return "veri-images-car-test";
	}
	
	/**
	 * 获取环境配置参数
	 * @return
	 */
	private static String getEnv(){
		String env = null;
		try {
			env = ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINA).getString("env");
			logger.info("SysConf.env={}",env);
			if(StringUtils.hasText(env)){
				if(!env.matches("[1-3]")){
					env =  "1";
					logger.error("环境参数“env”配置错误，使用默认配置（1）");
				}
			}else{
				env = "1"; 
				logger.error("没有配置环境参数“env”配置参数出错，使用默认配置（1）");
			}
		} catch (Exception e) {
			env = "1";
			logger.error("获取环境配置参数“env”出错，使用默认配置（1）", e);
		}
		return env;
	}
	private static String getBundleString(String key){
		return ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINA).getString(key);
	}
	public static String getBundleStrNoCache(String key){
		ResourceBundle.clearCache();
		return ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINA).getString(key);
	}
	//======================= private method ==========================================
		private static String getInsurPolicyPath(){
			String insurPolicyPath = getBundleString("insurPolicyPath");
			if(StringUtils.hasText(insurPolicyPath)){
				if(!insurPolicyPath.endsWith("/")){
					insurPolicyPath = insurPolicyPath + "/";
				}
			}else{
				insurPolicyPath = "/usr/insurPolicy/";
			}
			return insurPolicyPath;
		}
	
	    private static String[] getExceptionWarnMailList() {
			String[] mails = null;
			try {
				String mailListStr = getBundleString("exceptionWarnMailList");
				mails = mailListStr.split(",");
			} catch (Exception e) {
				logger.error("获取异常报警邮件发送地址失败：",e);
			}
			return mails;
		}

	private static String getMemApproveNotifyUrl()
	{
		return INTERFACE_CALLBACK_URL+"/ctrip/notifyRcv/manage/auth";
	}


	public static String getImCenterAddr() {
		String webSite = ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINA).getString("imCenter");
		if(StringUtils.hasText(webSite)){
			if(!webSite.endsWith("/")){
				webSite =  webSite + "/";
			}
			return webSite;
		}else{
			return "http://dev-web.autozuche.com/imcenter/";
		}
	}
}
