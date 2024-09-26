package com.lynn.rabbitmq_demo.config;

import com.lynn.rabbitmq_demo.dto.CustomCorrelationData;
import com.lynn.rabbitmq_demo.helper.MessageResendHelper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.lynn.rabbitmq_demo.properties.RabbitExchangeProperties.DIRECT_EXCHANGE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitExchangeProperties.FANOUT_EXCHANGE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitExchangeProperties.TOPIC_EXCHANGE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.DIRECT_QUEUE_1_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.DIRECT_QUEUE_2_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.FANOUT_QUEUE_1_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.FANOUT_QUEUE_2_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.SIMPLE_QUEUE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.TOPIC_QUEUE_1_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.TOPIC_QUEUE_2_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.WORK_QUEUE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitRoutingKeyProperties.ROUTING_KEY_BLUE;
import static com.lynn.rabbitmq_demo.properties.RabbitRoutingKeyProperties.ROUTING_KEY_BOY;
import static com.lynn.rabbitmq_demo.properties.RabbitRoutingKeyProperties.ROUTING_KEY_CHILD;
import static com.lynn.rabbitmq_demo.properties.RabbitRoutingKeyProperties.ROUTING_KEY_RED;
import static com.lynn.rabbitmq_demo.properties.RabbitRoutingKeyProperties.ROUTING_KEY_YELLOW;

/**
 * @Author: Lynn on 2024/9/4
 */
@Configuration
@Slf4j
@EnableAsync
public class RabbitmqConfig {
  @Value("${spring.rabbitmq.host}")
  private String HOST;

  @Value("${spring.rabbitmq.port}")
  private int POST;

  @Value("${spring.rabbitmq.username}")
  private String USERNAME;

  @Value("${spring.rabbitmq.password}")
  private String PASSWORD;

  @Bean
  ConnectionFactory rabbitConnectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setHost(HOST);
    connectionFactory.setPort(POST);
    connectionFactory.setUsername(USERNAME);
    connectionFactory.setPassword(PASSWORD);
    connectionFactory.setPublisherReturns(true);
    connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
    return connectionFactory;
  }

  @Bean
  RabbitTemplate rabbitTemplate() {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory());
    rabbitTemplate.setMandatory(true);// 會把消息還給生產者
    rabbitTemplate.setReturnsCallback(returned -> {
      System.out.println("ReturnCallback:" + "msg：" + returned.getMessage());
      System.out.println("ReturnCallback:" + "replyCode：" + returned.getReplyCode());
      System.out.println("ReturnCallback:" + "text：" + returned.getReplyText());
      System.out.println("ReturnCallback:" + "exchange：" + returned.getExchange());
      System.out.println("ReturnCallback:" + "key：" + returned.getRoutingKey());
    });

    rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
      if (correlationData instanceof CustomCorrelationData data) {
        if (ack) {
          log.info("Message confirmed");
          log.info("correlationData={}", data);
          log.info("cause={}", cause);
        } else {
          log.error("Message not confirmed: " + cause);
          log.info("start to retry data={}", data);
          messageResendHelper().resendMessage(data);
        }
      }

    });

    return rabbitTemplate;
  }

  @Bean
  AmqpAdmin amqpAdmin() {
    return new RabbitAdmin(rabbitConnectionFactory());
  }

  @Bean
  public MessageResendHelper messageResendHelper() {
    return new MessageResendHelper(rabbitTemplate());
  }


  /**
   * ==================== declare queue ====================
   */
  @Bean
  public Queue simpleQueue() {
    amqpAdmin().deleteQueue(SIMPLE_QUEUE_NAME);
    return new Queue(SIMPLE_QUEUE_NAME);
  }

  @Bean
  public Queue workQueue() {
    amqpAdmin().deleteQueue(WORK_QUEUE_NAME);
    return new Queue(WORK_QUEUE_NAME);
  }

  /**
   * ======= fanout =======
   */


  @Bean
  public Queue fanoutQueue1() {
    amqpAdmin().deleteQueue(FANOUT_QUEUE_1_NAME);
    return new Queue(FANOUT_QUEUE_1_NAME);
  }

  @Bean
  public Queue fanoutQueue2() {
    amqpAdmin().deleteQueue(FANOUT_QUEUE_2_NAME);
    return new Queue(FANOUT_QUEUE_2_NAME);
  }

  @Bean
  public FanoutExchange fanoutExchange() {
    amqpAdmin().deleteExchange(FANOUT_EXCHANGE_NAME);
    return new FanoutExchange(FANOUT_EXCHANGE_NAME);
  }

  @Bean
  public Binding fanoutBinding1() {
    return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
  }

  @Bean
  public Binding fanoutBinding2() {
    return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
  }


  /**
   * ======= direct =======
   */

  @Bean
  public Queue directQueue1() {
    return new Queue(DIRECT_QUEUE_1_NAME);
  }

  @Bean
  public Queue directQueue2() {
    return new Queue(DIRECT_QUEUE_2_NAME);
  }

  @Bean
  public DirectExchange directExchange() {
    return new DirectExchange(DIRECT_EXCHANGE_NAME);
  }


  /**
   * spring 無法處理 List<Binding> 所以手動宣告
   */
  @PostConstruct
  public void declareBindings() {
    bindDirectQueue1().forEach(bind -> amqpAdmin().declareBinding(bind));
    bindDirectQueue2().forEach(bind -> amqpAdmin().declareBinding(bind));
  }

  @Bean
  public List<Binding> bindDirectQueue1() {
    List<String> routingKeys = Arrays.asList(ROUTING_KEY_BLUE, ROUTING_KEY_RED);
    return routingKeys.stream()
        .map(routingKey -> BindingBuilder.bind(directQueue1())
            .to(directExchange())
            .with(routingKey))
        .collect(Collectors.toList());
  }

  @Bean
  public List<Binding> bindDirectQueue2() {
    List<String> routingKeys = Arrays.asList(ROUTING_KEY_YELLOW, ROUTING_KEY_RED);
    return routingKeys.stream()
        .map(routingKey -> BindingBuilder.bind(directQueue2())
            .to(directExchange())
            .with(routingKey))
        .collect(Collectors.toList());
  }

  /**
   * ======= topic =======
   */

  @Bean
  public Queue topicQueue1() {
    return new Queue(TOPIC_QUEUE_1_NAME);
  }

  @Bean
  public Queue topicQueue2() {
//    return QueueBuilder.durable(TOPIC_QUEUE_2_NAME).build(); 宣告持久化隊列
    return new Queue(TOPIC_QUEUE_2_NAME);// 默認為持久化隊列
  }

  @Bean
  public TopicExchange topicExchange() {
//    return ExchangeBuilder.topicExchange(TOPIC_EXCHANGE_NAME).build();
    return new TopicExchange(TOPIC_EXCHANGE_NAME);
  }

  @Bean
  public Binding topicBinding1() {
    return BindingBuilder
        .bind(topicQueue1())
        .to(topicExchange())
        .with(ROUTING_KEY_CHILD);
  }

  @Bean
  public Binding topicBinding2() {
    return BindingBuilder
        .bind(topicQueue2())
        .to(topicExchange())
        .with(ROUTING_KEY_BOY);
  }


}
