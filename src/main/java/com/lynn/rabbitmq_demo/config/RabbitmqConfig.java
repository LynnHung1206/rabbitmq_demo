package com.lynn.rabbitmq_demo.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import static com.lynn.rabbitmq_demo.properties.RabbitProperties.FANOUT_EXCHANGE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitProperties.FANOUT_QUEUE_1_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitProperties.FANOUT_QUEUE_2_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitProperties.SIMPLE_QUEUE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitProperties.WORK_QUEUE_NAME;

/**
 * @Author: Lynn on 2024/9/4
 */
@Configuration
@Slf4j
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
    return connectionFactory;
  }

  @Bean
  RabbitTemplate rabbitTemplate() {
    return new RabbitTemplate(rabbitConnectionFactory());
  }

  @Bean
  AmqpAdmin amqpAdmin() {
    return new RabbitAdmin(rabbitConnectionFactory());
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

}
