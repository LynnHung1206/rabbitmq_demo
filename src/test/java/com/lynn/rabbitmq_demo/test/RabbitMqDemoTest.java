package com.lynn.rabbitmq_demo.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.SIMPLE_QUEUE_NAME;

/**
 * @Author: Lynn on 2024/12/3
 */
@SpringBootTest
@Slf4j
public class RabbitMqDemoTest {

  @Autowired
  private  RabbitTemplate rabbitTemplate;

  @Test
  void test() {
    log.error("???????????");
      rabbitTemplate.convertAndSend(SIMPLE_QUEUE_NAME,"hi hi");
  }
}
