package com.atzuche.order.renterwz.aliyunmq.annotations;

import com.aliyun.mns.model.Message;
import com.atzuche.order.renterwz.aliyunmq.AliyunMessageException;
import com.atzuche.order.renterwz.aliyunmq.AliyunMessageListener;
import com.atzuche.order.renterwz.aliyunmq.AliyunMnsListenerContainer;
import com.atzuche.order.renterwz.aliyunmq.AliyunMnsListenerContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Created by andy on 16/6/17.
 */
public class AliyunMnsAnnotationBeanPostProcessor implements BeanPostProcessor,ApplicationContextAware,EnvironmentAware{

    public static final String PROPERTY_PREFIX="com.autoyol.mns.coupon.queue.";
    private final static Logger logger = LoggerFactory.getLogger(AliyunMnsAnnotationBeanPostProcessor.class);

    private ApplicationContext applictionContext;

    private Environment environment;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass= AopUtils.getTargetClass(bean);
        Map<Method,Set<AliyunMnsListener>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
            new MethodIntrospector.MetadataLookup<Set<AliyunMnsListener>>(){

                @Override
                public Set<AliyunMnsListener> inspect(Method method) {
                    Set<AliyunMnsListener> listenerMethods = AnnotationUtils.getRepeatableAnnotations(method,AliyunMnsListener.class);
                    return (!listenerMethods.isEmpty()?listenerMethods:null);
                }
            });
        if(annotatedMethods.isEmpty()){
            logger.trace("No @AliyunMsgListener found on bean type :"+bean.getClass());
        }else{
            for(Map.Entry<Method,Set<AliyunMnsListener>> entry:annotatedMethods.entrySet()){
                Method method = entry.getKey();
                for(AliyunMnsListener listener:entry.getValue()){
                    processAliyunMsgListener(listener,method,bean);
                }
            }
        }
        return bean;
    }

    private void processAliyunMsgListener(AliyunMnsListener listener, Method method, Object bean) {
        logger.trace("method is :{},class is {}",method.getName(),bean.getClass());
        String name = listener.queueKey();

        String queueName = environment.getProperty(PROPERTY_PREFIX+name);
        logger.trace("queueName is {}",queueName);
        if(!StringUtils.hasText(queueName)){
            throw new IllegalArgumentException("queueName for queueKey "+PROPERTY_PREFIX+name+" doest't exist");
        }
        AliyunMnsListenerContainerFactory factory = applictionContext.getBean(AliyunMnsListenerContainerFactory.class);

        AliyunMnsListenerContainer container = factory.createMessageListenerContainer(queueName);
        //TODO
        container.setMessageListener(new AliyunMessageListener() {
            @Override
            public void onMessage(Message message) throws AliyunMessageException {
                try {
                    logger.info("message is :{}",message);
                    logger.info("method is {},bean is {}",method,bean);
                    method.invoke(bean,message);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

        container.initNow();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applictionContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


}
