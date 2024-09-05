package com.lynn.rabbitmq_demo.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author: Lynn on 2024/9/5
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class RabbitTestListener {

  @RabbitListener(queues = "test.queue")
  public void consumeMsg(String requestPayload) {
    System.out.println("requestPayload = " + requestPayload);
  }
}
