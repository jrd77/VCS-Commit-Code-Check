package com.atzuche.order.photo.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class should be the default multi-thread implement to receive aliyun mns message. Created by
 * andy on 16/6/17.
 */
public class AliyunMnsListenerContainerFactory implements DisposableBean {
    private final static Logger logger = LoggerFactory.getLogger(AliyunMnsListenerContainerFactory.class);

    private ExecutorService taskExecutor;

    private AliyunMnsService aliyunMnsService;

    private Map<String, AliyunMnsListenerContainer> containerMap = new ConcurrentHashMap<>();

    public ExecutorService getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(ExecutorService taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public AliyunMnsService getAliyunMnsService() {
        return aliyunMnsService;
    }

    public void setAliyunMnsService(AliyunMnsService aliyunMnsService) {
        this.aliyunMnsService = aliyunMnsService;
    }

    public void init() {

    }

    public synchronized AliyunMnsListenerContainer createMessageListenerContainer(String queueName) {
        if (containerMap.get(queueName) != null) {
            return containerMap.get(queueName);
        }
        AliyunMnsListenerContainer container = new AliyunMnsListenerContainer();
        container.setQueueName(queueName);
        container.setTaskExecutor(taskExecutor);
        container.setAliyunMnsService(aliyunMnsService);
        containerMap.put(queueName, container);
        return container;
    }

    @Override
    public void destroy() throws Exception {
        logger.info("AliyunMnsListenerContainerFactory stopping");
        Iterator<AliyunMnsListenerContainer> containerIterator = containerMap.values().iterator();

        while (containerIterator.hasNext()) {
            try {
                containerIterator.next().destroy();
            } catch (Exception e) {
                logger.error("close aliyun mns listener container error", e);
            }
        }
        try {
            taskExecutor.shutdown();
            taskExecutor.awaitTermination(30, TimeUnit.SECONDS);
        }catch (InterruptedException e){
             logger.error("teminated taskExecutor interrupted");
        }finally {
           if(taskExecutor.isTerminated()){
               taskExecutor.shutdownNow();
           }
        }

        aliyunMnsService.close();

        logger.info("AliyunMnsListenerContainerFactory stopped");

    }
}
