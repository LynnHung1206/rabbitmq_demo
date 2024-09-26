package com.lynn.rabbitmq_demo.helper;

import com.lynn.rabbitmq_demo.dto.CustomCorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * @Author: Lynn on 2024/9/26
 */
public class MessageResendHelper {

  private final RabbitTemplate rabbitTemplate;

  @Autowired
  public MessageResendHelper(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Async
  public void resendMessage(CustomCorrelationData data) {
    rabbitTemplate.convertAndSend(data.getExchange(), data.getRoutingKey(), data.getMessage(), data);
  }

}
