package com.atzuche.order.coreapi.config;

import com.atzuche.order.coreapi.common.RabbitConstants;
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

	public static final String DEFAULT_EXCHANGE = "order-oil-mq-exchange";

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

	@Bean
	public Queue wzDeRunCitiesQueue() {
		return new Queue(RabbitMqEnums.WZ_DE_RUN_CITIES.getQueueName(), true);
	}

	@Bean
	public DirectExchange wzDeRunCitiesExchange() {
		return new DirectExchange(RabbitMqEnums.WZ_DE_RUN_CITIES.getExchange());
	}

	@Bean
	public Binding wzDeRunCitiesBind() {
		return BindingBuilder.bind(wzDeRunCitiesQueue()).to(wzDeRunCitiesExchange()).with(RabbitMqEnums.WZ_DE_RUN_CITIES.getRoutingKey());
	}

	@Bean
	public Queue wzCheLeHangInfoQueue() {
		return new Queue(RabbitMqEnums.WZ_CHE_LE_HANG_INFO.getQueueName(), true);
	}

	@Bean
	public DirectExchange wzCheLeHangInfoExchange() {
		return new DirectExchange(RabbitMqEnums.WZ_CHE_LE_HANG_INFO.getExchange());
	}

	@Bean
	public Binding wzCheLeHangInfoBind() {
		return BindingBuilder.bind(wzCheLeHangInfoQueue()).to(wzCheLeHangInfoExchange()).with(RabbitMqEnums.WZ_CHE_LE_HANG_INFO.getRoutingKey());
	}

    @Bean
	public Queue handoverCarMilegeQueue(){
		return new Queue("handover_car_oil_queue",true);
	}

	@Bean
	public Queue handoverCarRenYunMilegeQueue(){
		return new Queue("handover_car_renYun_oil_queue",true);
	}


	@Bean
	public Queue orderPreReturnCarQueue() {
		return new Queue(RabbitMqEnums.ORDER_PRERETURNCAR.getQueueName(), true);
	}

	@Bean
	public TopicExchange orderPreReturnCarExchange() {
		return new TopicExchange(RabbitMqEnums.ORDER_PRERETURNCAR.getExchange());
	}

	@Bean
	public Binding orderPreReturnCarBind() {
		return BindingBuilder.bind(orderPreReturnCarQueue()).to(orderPreReturnCarExchange()).with(RabbitMqEnums.ORDER_PRERETURNCAR.getRoutingKey());
	}

	@Bean
	public Queue orderPreSettlementQueue() {
		return new Queue(RabbitMqEnums.ORDER_PRESETTLEMENT.getQueueName(), true);
	}

	@Bean
	public TopicExchange orderPreSettlementExchange() {
		return new TopicExchange(RabbitMqEnums.ORDER_PRESETTLEMENT.getExchange());
	}

	@Bean
	public Binding orderPreSettlementBind() {
		return BindingBuilder.bind(orderPreSettlementQueue()).to(orderPreSettlementExchange()).with(RabbitMqEnums.ORDER_PRESETTLEMENT.getRoutingKey());
	}

	@Bean
	public Queue orderEndQueue() {
		return new Queue(RabbitMqEnums.ORDER_END.getQueueName(), true);
	}

	@Bean
	public TopicExchange orderEndExchange() {
		return new TopicExchange(RabbitMqEnums.ORDER_END.getExchange());
	}

	@Bean
	public Binding orderEndBind() {
		return BindingBuilder.bind(orderEndQueue()).to(orderEndExchange()).with(RabbitMqEnums.ORDER_END.getRoutingKey());
	}

	@Bean
	public Queue orderWzSettlementSuccessQueue() {
		return new Queue(RabbitMqEnums.ORDER_WZ_SETTLEMENT_SUCCESS.getQueueName(), true);
	}

	@Bean
	public TopicExchange orderWzSettlementSuccessExchange() {
		return new TopicExchange(RabbitMqEnums.ORDER_WZ_SETTLEMENT_SUCCESS.getExchange());
	}

	@Bean
	public Binding orderWzSettlementSuccessBind() {
		return BindingBuilder.bind(orderWzSettlementSuccessQueue()).to(orderWzSettlementSuccessExchange()).with(RabbitMqEnums.ORDER_WZ_SETTLEMENT_SUCCESS.getRoutingKey());
	}

	@Bean
	public Queue orderWzSettlementFailQueue() {
		return new Queue(RabbitMqEnums.ORDER_WZ_SETTLEMENT_FAIL.getQueueName(), true);
	}

	@Bean
	public TopicExchange orderWzSettlementFailExchange() {
		return new TopicExchange(RabbitMqEnums.ORDER_WZ_SETTLEMENT_FAIL.getExchange());
	}

	@Bean
	public Binding orderWzSettlementFailBind() {
		return BindingBuilder.bind(orderWzSettlementFailQueue()).to(orderWzSettlementFailExchange()).with(RabbitMqEnums.ORDER_WZ_SETTLEMENT_FAIL.getRoutingKey());
	}



    /**
     * 配置交换机实例
     *
     * @return DirectExchange
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(RabbitConstants.SEND_SECONDARY_NOTICE_EXCHANGE);
    }

    /**
     * 配置队列实例(影像资料审核)
     *
     * @return Queue
     */
    @Bean
    public Queue impactDataAuditNoticeQueue() {
        return new Queue(RabbitConstants.SEND_IMPACT_DATA_AUDIT_NOTICE_QUEUE);
    }

    /**
     * 将队列绑定到交换机上，并设置消息分发的路由键
     *
     * @return Binding
     */
    @Bean
    public Binding impactDataAuditNoticeBinding() {
        //链式写法: 用指定的路由键将队列绑定到交换机
        return BindingBuilder.bind(impactDataAuditNoticeQueue()).to(directExchange()).with(RabbitConstants.SEND_IMPACT_DATA_AUDIT_NOTICE_ROUTEKEY);
    }

    /**
     * 配置队列实例(提现结果通知)
     *
     * @return Queue
     */
    @Bean
    public Queue withdrawalsNoticeQueue() {
        return new Queue(RabbitConstants.SEND_WITHDRAWALS_NOTICE_QUEUE);
    }

    /**
     * 将队列绑定到交换机上，并设置消息分发的路由键
     *
     * @return Binding
     */
    @Bean
    public Binding withdrawalsNoticeBinding() {
        //链式写法: 用指定的路由键将队列绑定到交换机
        return BindingBuilder.bind(withdrawalsNoticeQueue()).to(directExchange()).with(RabbitConstants.SEND_WITHDRAWALS_NOTICE_ROUTEKEY);
    }

    /**
     * 配置队列实例(收益到账通知)
     *
     * @return Queue
     */
    @Bean
    public Queue incomeToAccountNoticeQueue() {
        return new Queue(RabbitConstants.SEND_INCOMEAMT_TOACCOUNT_NOTICE_QUEUE);
    }

    /**
     * 将队列绑定到交换机上，并设置消息分发的路由键
     *
     * @return Binding
     */
    @Bean
    public Binding incomeToAccountNoticeBinding() {
        //链式写法: 用指定的路由键将队列绑定到交换机
        return BindingBuilder.bind(incomeToAccountNoticeQueue()).to(directExchange()).with(RabbitConstants.SEND_INCOMEAMT_TOACCOUNT_NOTICE_ROUTEKEY);
    }

}
