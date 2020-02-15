package com.atzuche.order.photo.common;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;
    //
//	@Value("${spring.rabbitmq.consumer.count}")
//	private Integer consumerCount;
    public static final String DEFAULT_EXCHANGE = "order-mq-exchange";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(10);
        return factory;
    }

    @Bean
    public Queue renYunDeliveryCarPhotoQueue() {
        return new Queue("ren_yun_delivery_car_photo_queue1", true);
    }

    @Bean
    public DirectExchange renYunDeliveryCarPhotoExchange() {
        return new DirectExchange(DEFAULT_EXCHANGE);
    }

    @Bean
    public Binding renYunDeliveryCarPhotoBind() {
        return BindingBuilder.bind(renYunDeliveryCarPhotoQueue()).to(renYunDeliveryCarPhotoExchange()).with("routingKey_"+renYunDeliveryCarPhotoQueue().getName());
    }

}
