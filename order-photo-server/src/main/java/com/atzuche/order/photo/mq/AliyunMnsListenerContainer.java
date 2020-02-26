package com.atzuche.order.photo.mq;

import com.aliyun.mns.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * Created by andy on 16/6/17.
 */
public class AliyunMnsListenerContainer{
    private final static Logger logger = LoggerFactory.getLogger(AliyunMnsListenerContainer.class);

    private String queueName;

    private AliyunMessageListener messageListener;

    private Executor taskExecutor;

    private AliyunMnsService aliyunMnsService;

    private List<AsyncMessageListenerInvoker> invokerList = new CopyOnWriteArrayList<>();



    public AliyunMnsService getAliyunMnsService() {
        return aliyunMnsService;
    }

    public void setAliyunMnsService(AliyunMnsService aliyunMnsService) {
        this.aliyunMnsService = aliyunMnsService;
    }

    public Executor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public AliyunMessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(AliyunMessageListener messageListener) {
        this.messageListener = messageListener;
    }


    protected void invokeMnsListener(Message message) throws AliyunMessageException {
        AliyunMessageListener listener = getMessageListener();
        if (listener != null)
            listener.onMessage(message);
        else {
            throw new IllegalStateException("No Aliyun Message listener specified:- set property 'messageListener'");
        }
    }

    protected void scheduleNewInvoker(){

    }

    protected void doShutdown()throws Exception{
        logger.info("Waiting for shutdown of mns message listener invokers");

    }

    public void initNow() {
        AsyncMessageListenerInvoker r = new AsyncMessageListenerInvoker();
        taskExecutor.execute(r);
        invokerList.add(r);
    }


    public void destroy() throws Exception {
        logger.info("close aliyunListener Containner...");
        for(AsyncMessageListenerInvoker invoker:invokerList){
            invoker.stop();
        }
        logger.info("close all aliyun listener invoker");
    }


    private class AsyncMessageListenerInvoker implements Runnable {

        private volatile boolean stop =false;

        @Override
        public void run() {
             while(!Thread.currentThread().isInterrupted()&&!stop){
                 logger.info("receive from queueName: {}",queueName);
                 try {
                     Message message = aliyunMnsService.receiveMessage(queueName);
                     if (message != null) {
                         try {
                             messageListener.onMessage(message);
                             aliyunMnsService.delMessageFromQueue(queueName, message.getReceiptHandle());
                         } catch (Exception e) {
                             logger.error("handle message error ,message is {}", message, e);
                         }
                     }
                 }catch (Exception e){
                     logger.error("receive from aliyun mns:{}",queueName,e);
                     try {
                         Thread.sleep(30*1000);
                     } catch (InterruptedException e1) {
                         e1.printStackTrace();
                     }
                 }
             }
        }



        public void stop(){
            logger.info("set stop flag to true");
            stop =true;
        }

    }
}
