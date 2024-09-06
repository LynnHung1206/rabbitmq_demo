package com.lynn.rabbitmq_demo.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
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

//  @EventListener(ApplicationReadyEvent.class)
  @PostConstruct
  public void insideRabbitInit() {
    amqpAdmin().declareQueue(new Queue(SIMPLE_QUEUE_NAME));
    amqpAdmin().declareQueue(new Queue(WORK_QUEUE_NAME));
  }


}
