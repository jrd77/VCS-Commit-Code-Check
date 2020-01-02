package com.atzuche.order.coreapi.config;

import com.atzuche.order.coreapi.enums.RabbitMqEnums;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitConfiguration
 *
 * @author shisong
 * @date 2019/12/28
 */
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

	@Value("${spring.rabbitmq.consumer.count}")
	private Integer consumerCount;

	@Bean
	public ConnectionFactory connectionFactory() {        
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);        
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
        factory.setConcurrentConsumers(consumerCount);
        return factory;
    }


	@Bean
	public Queue wzInfoQueue() {
		return new Queue(RabbitMqEnums.WZ_INFO.getQueueName(), true);
	}

	@Bean
	public DirectExchange wzInfoExchange() {
		return new DirectExchange(RabbitMqEnums.WZ_INFO.getExchange());
	}

	@Bean
	public Binding wzInfoBind() {
		return BindingBuilder.bind(wzInfoQueue()).to(wzInfoExchange()).with(RabbitMqEnums.WZ_INFO.getRoutingKey());
	}

	@Bean
	public Queue wzPriceQueue() {
		return new Queue(RabbitMqEnums.WZ_PRICE.getQueueName(), true);
	}

	@Bean
	public DirectExchange wzPriceExchange() {
		return new DirectExchange(RabbitMqEnums.WZ_PRICE.getExchange());
	}

	@Bean
	public Binding wzPriceBind() {
		return BindingBuilder.bind(wzPriceQueue()).to(wzPriceExchange()).with(RabbitMqEnums.WZ_PRICE.getRoutingKey());
	}

	@Bean
	public Queue wzVoucherQueue() {
		return new Queue(RabbitMqEnums.WZ_VOUCHER.getQueueName(), true);
	}

	@Bean
	public DirectExchange wzVoucherExchange() {
		return new DirectExchange(RabbitMqEnums.WZ_VOUCHER.getExchange());
	}

	@Bean
	public Binding wzVoucherBind() {
		return BindingBuilder.bind(wzVoucherQueue()).to(wzVoucherExchange()).with(RabbitMqEnums.WZ_VOUCHER.getRoutingKey());
	}

	@Bean
	public Queue wzFeedBackQueue() {
		return new Queue(RabbitMqEnums.WZ_FEEDBACK.getQueueName(), true);
	}

	@Bean
	public DirectExchange wzFeedBackExchange() {
		return new DirectExchange(RabbitMqEnums.WZ_FEEDBACK.getExchange());
	}

	@Bean
	public Binding wzFeedBackBind() {
		return BindingBuilder.bind(wzFeedBackQueue()).to(wzFeedBackExchange()).with(RabbitMqEnums.WZ_FEEDBACK.getRoutingKey());
	}

	@Bean
	public Queue wzResultFeedBackQueue() {
		return new Queue(RabbitMqEnums.WZ_RESULT_FEEDBACK.getQueueName(), true);
	}

	@Bean
	public DirectExchange wzResultFeedBackExchange() {
		return new DirectExchange(RabbitMqEnums.WZ_RESULT_FEEDBACK.getExchange());
	}

	@Bean
	public Binding wzResultFeedBackBind() {
		return BindingBuilder.bind(wzResultFeedBackQueue()).to(wzResultFeedBackExchange()).with(RabbitMqEnums.WZ_RESULT_FEEDBACK.getRoutingKey());
	}

	@Bean
	public Queue handoverCarQueue() {
		return new Queue("handover_car_queue", true);
	}

}
