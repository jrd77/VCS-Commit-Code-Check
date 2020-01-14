package com.atzuche.order.photo.mq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.client.AsyncCallback;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.atzuche.order.photo.util.SysConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

/**
 * Created by andy on 16/6/16.
 */
public class AliyunMnsService {
    private final static Logger logger = LoggerFactory.getLogger(AliyunMnsService.class);
    private static final String accessKey= SysConf.MQ_ACCESS_KEY;
    private static final String accessId=SysConf.MQ_ACCESS_ID;
    private static final String accountEndPoint=SysConf.MQ_ACCESS_END_POINT;
    private boolean inited = false;


    private MNSClient client;
    /**
     * init the mns client or re-open the mns client
     *
     * @author andy
     */
    public synchronized void init() {

        if (inited && client != null && client.isOpen()) {
            throw new IllegalStateException("AliyunMns already started,please check");
        }
        if (client != null && !client.isOpen()) {
            try {
                client.open();
                inited = true;
            } catch (Exception e) {
                inited = false;
                logger.error("open msn client failure,please check", e);
            }
        } else {
            try {
                Assert.notNull(accessKey, "accessKey cannot be null,please check");
                Assert.notNull(accessId, "accessId cannot be null,please check");
                Assert.notNull(accountEndPoint, "accountEndPoint cannot be null,please check");
                CloudAccount account = new CloudAccount(accessId, accessKey, accountEndPoint);
                client = account.getMNSClient();
                inited = true;
            } catch (Exception e) {
                inited = false;
                logger.error("some error happened in init aliyun mns service,please check", e);
            }
        }
    }


    /**
     * async send message to mns
     */
    public void asyncSend̨MessageToQueue(String messageBody, String queueName) {
        asyncSend̨MessageToQueue(messageBody,queueName,false);

    }

    @SuppressWarnings("unchecked")
	public void asyncSend̨MessageToQueue(String messageBody, String queueName,boolean compress) {
        if (inited) {
            if (client != null && !client.isOpen()) {
                openMnsClient();
            }
            Message message = createMessage(messageBody,compress);
            CloudQueue queue = client.getQueueRef(queueName);
            try {
                queue.asyncPutMessage(message, new AsyncSendMessageCallback(messageBody, queueName));
            } catch (Exception e) {
                logger.error("async send message to queueKey failure: queueName is {},messageBody is {}", queueName, messageBody, e);
            }
        } else {
            throw new IllegalStateException("mns service didn't start,please check");
        }
    }

    /**
     * create mns's message object
     */
    private Message createMessage(String messageBody,boolean compress) {
        Message messge = new Message();
        if(compress){
            messge.setMessageBody(compress(messageBody));
        }else {
            messge.setMessageBody(messageBody);
        }
        return messge;
    }

    private static byte[] compress(String str)
    {
        if (null == str)
        {
            return null;
        }
        // ByteArrayOutputStream不需要关闭
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try
        {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(StandardCharsets.UTF_8));

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                gzip.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return out.toByteArray();
    }

    /**
     * receive message from mns's queueKey
     */
    public Message receiveMessage(String queueName) {
        Assert.notNull(queueName, "queueName cann't be null");
        if (inited) {
            if (client != null && !client.isOpen()) {
                openMnsClient();
            }
            try {
                CloudQueue cloudQueue = client.getQueueRef(queueName);
                logger.trace("begin to pop message from cloudQueue: {}",cloudQueue);
                return cloudQueue.popMessage(30);
            } catch (Exception e) {
                logger.error("receive message error from queueName {}", queueName, e);
                throw new RuntimeException("cannot receive message from msn queueKey,queueName is " + queueName);
            }
        } else {
            throw new IllegalStateException("mns service didn't start,please check");
        }
    }

    private void openMnsClient() {
        try {
            client.open();
        } catch (Exception ex) {
            logger.error("open client error ", ex);
        }
    }

    /**
     * delete message from queueKey
     * @param queue
     * @param handler
     */
    public void delMessageFromQueue(String queue,String handler) {
        if(inited){
            if (client != null && !client.isOpen()) {
                openMnsClient();
            }
            try {
                CloudQueue cloudQueue = client.getQueueRef(queue);
                cloudQueue.deleteMessage(handler);
            } catch (Exception e) {
                logger.error("delete message error from queueName {},message handler is {}", queue,handler, e);
                throw new RuntimeException("cannot delete message from msn queueKey,queueName is " + queue);
            }

        }else{
            throw new IllegalStateException("mns service didn't start,please check");
        }

    }

    @SuppressWarnings("rawtypes")
	private class AsyncSendMessageCallback implements AsyncCallback {

        private final String messageBody;
        private final String queueName;


        public AsyncSendMessageCallback(String messageBody, String queueName) {
            this.messageBody = messageBody;
            this.queueName = queueName;
        }

        @Override
        public void onSuccess(Object result) {
            logger.info("send message:{} to queueKey:{} success,resutl is {}", messageBody, queueName, result);
        }

        @Override
        public void onFail(Exception ex) {
            logger.error("send message:{} to queueKey:{} failure", messageBody, queueName, ex);
        }
    }


}
