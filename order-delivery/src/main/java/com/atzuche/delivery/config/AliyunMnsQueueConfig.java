package com.atzuche.delivery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by andy on 16/6/16.
 */
@ConfigurationProperties(prefix = "com.autoyol.mns.queue")
@Configuration
public class AliyunMnsQueueConfig {


    public String simpleCreateMemQueue;


    private String autoPointChangeQueue;

    private String distAutoPointQueue;
    
    private String autoMemSmsCreate;

    public String getDistAutoPointQueue() {
        return distAutoPointQueue;
    }

    public void setDistAutoPointQueue(String distAutoPointQueue) {
        this.distAutoPointQueue = distAutoPointQueue;
    }

    public String getSimpleCreateMemQueue() {
        return simpleCreateMemQueue;
    }

    public void setSimpleCreateMemQueue(String simpleCreateMemQueue) {
        this.simpleCreateMemQueue = simpleCreateMemQueue;
    }

    public String getAutoPointChangeQueue() {
        return autoPointChangeQueue;
    }

    public void setAutoPointChangeQueue(String autoPointChangeQueue) {
        this.autoPointChangeQueue = autoPointChangeQueue;
    }

	public String getAutoMemSmsCreate() {
		return autoMemSmsCreate;
	}

	public void setAutoMemSmsCreate(String autoMemSmsCreate) {
		this.autoMemSmsCreate = autoMemSmsCreate;
	}
}
