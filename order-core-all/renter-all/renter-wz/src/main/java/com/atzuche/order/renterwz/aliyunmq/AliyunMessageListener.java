package com.atzuche.order.renterwz.aliyunmq;

import com.aliyun.mns.model.Message;

/**
 * Created by andy on 16/6/17.
 */
public interface AliyunMessageListener {
    /**
     * handle aliyun mns message
     * @param message
     * @throws AliyunMessageException
     */
    void onMessage(Message message)throws AliyunMessageException;
}
