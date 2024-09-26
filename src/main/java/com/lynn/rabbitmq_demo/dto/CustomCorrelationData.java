package com.lynn.rabbitmq_demo.dto;

import lombok.Getter;
import lombok.ToString;
import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 * @Author: Lynn on 2024/9/26
 */
@Getter
@ToString(callSuper = true)
public class CustomCorrelationData extends CorrelationData {
  private final String exchange;
  private final String routingKey;
  private final Object message;

  public CustomCorrelationData(String id, String exchange, String routingKey, Object message) {
    super(id);
    this.exchange = exchange;
    this.routingKey = routingKey;
    this.message = message;
  }

}
