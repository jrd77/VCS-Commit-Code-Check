package com.atzuche.order.photo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class SysConfig {

	private SysConfig(){}//私有构造函数（不允许实例化）

	/**
	 * oss,bucket
	 */
	public static String ossBucket;

	/**
	 * 违章凭证mq
	 */
	public static String voucherQueue;

	/**
	 * oss,accessId
	 */
	public static String ossAccessId;

	/**
	 * oss,ossAccessKey
	 */
	public static String ossAccessKey;

	/**
	 * oss,ossAccessKey
	 */
	public static String ossEndpoint;



	@Value("${oss.bucket}")
	public void setOssBucket(String bucket) {
		SysConfig.ossBucket = bucket;
	}

	@Value("${renter.voucher.queue}")
	public void setVoucherQueue(String queue) {
		SysConfig.voucherQueue = queue;
	}

	@Value("${oss.access.id}")
	public void setOssAccessId(String accessId) {
		SysConfig.ossAccessId = accessId;
	}

	@Value("${oss.access.key}")
	public void setOssAccessKey(String accessKey) {
		SysConfig.ossAccessKey = accessKey;
	}

	@Value("${oss.endpoint}")
	public void setOssEndpoint(String endpoint) {
		SysConfig.ossEndpoint = endpoint;
	}



}
