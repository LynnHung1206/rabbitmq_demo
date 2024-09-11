package com.lynn.rabbitmq_demo.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.DIRECT_QUEUE_1_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.DIRECT_QUEUE_2_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.FANOUT_QUEUE_1_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.FANOUT_QUEUE_2_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.SIMPLE_QUEUE_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.TOPIC_QUEUE_1_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.TOPIC_QUEUE_2_NAME;
import static com.lynn.rabbitmq_demo.properties.RabbitQueueProperties.WORK_QUEUE_NAME;

/**
 * @Author: Lynn on 2024/9/5
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class RabbitTestListener {

  @RabbitListener(queues = SIMPLE_QUEUE_NAME)
  public void consumeSimpleMsg(String requestPayload) {
    System.out.println("this is simple queue requestPayload = " + requestPayload);
  }

  @RabbitListener(queues = WORK_QUEUE_NAME)
  public void consumeWorkMsg1(String requestPayload) throws InterruptedException {
    System.out.println("this is work queue 1 requestPayload = " + requestPayload);
    Thread.sleep(50);
  }

  @RabbitListener(queues = WORK_QUEUE_NAME)
  public void consumeWorkMsg2(String requestPayload) {
    System.err.println("this is work queue 2 requestPayload = " + requestPayload);
  }

  @RabbitListener(queues = FANOUT_QUEUE_1_NAME)
  public void consumeFanout1(String requestPayload) {
    System.out.println("this is fanout queue 1 requestPayload = " + requestPayload);
  }

  @RabbitListener(queues = FANOUT_QUEUE_2_NAME)
  public void consumeFanout2(String requestPayload) {
    System.out.println("this is fanout queue 2 requestPayload = " + requestPayload);
  }

  @RabbitListener(queues = DIRECT_QUEUE_1_NAME)
  public void consumeDirect1(String requestPayload) {
    System.out.println("this is direct queue 1 requestPayload = " + requestPayload);
  }
  @RabbitListener(queues = DIRECT_QUEUE_2_NAME)
  public void consumeDirect2(String requestPayload) {
    System.out.println("this is direct queue 2 requestPayload = " + requestPayload);
  }

  @RabbitListener(queues = TOPIC_QUEUE_1_NAME)
  public void consumeTopic1(String requestPayload) {
    System.out.println("this is topic queue 1 requestPayload = " + requestPayload);
  }
  @RabbitListener(queues = TOPIC_QUEUE_2_NAME)
  public void consumeTopic2(String requestPayload) {
    System.out.println("this is topic queue 2 requestPayload = " + requestPayload);
  }
}
