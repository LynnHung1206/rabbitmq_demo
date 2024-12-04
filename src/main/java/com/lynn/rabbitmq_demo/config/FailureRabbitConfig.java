package com.lynn.rabbitmq_demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author: Lynn on 2024/12/4
 */
@Configuration
public class FailureRabbitConfig {

  @Bean
  public DirectExchange errorExchange() {
    return new DirectExchange("error.direct");
  }

  @Bean
  public Queue errorQueue() {
    return new Queue("error.queue");
  }

  @Bean
  public Binding errorBinding() {
    return BindingBuilder.bind(errorQueue())
        .to(errorExchange())
        .with("error");
  }


  @Bean
  public MessageRecoverer rePublishMessageRecover(RabbitTemplate rabbitTemplate) {
    return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
  }

}
