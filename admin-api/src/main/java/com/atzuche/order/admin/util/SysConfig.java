package com.atzuche.order.admin.util;

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
